package pl.futurecollars.invoicing.db.sql;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.db.sql.rowmapper.InvoiceRowMapper;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;

@Slf4j
@RequiredArgsConstructor
public class SqlDatabase implements Database<Invoice> {

    public static final String SELECT_QUERY = "select i.id, i.issue_date, i.number as number, "
            + "c1.id as seller_id, c1.tax_id as seller_tax_id, c1.company_name as seller_company_name, c1.address as seller_address, "
            + "c1.pension_insurance as seller_pension_insurance, c1.health_insurance as seller_health_insurance, "
            + "c2.id as buyer_id, c2.tax_id as buyer_tax_id, c2.company_name as buyer_company_name, c2.address as buyer_address, "
            + "c2.pension_insurance as buyer_pension_insurance, c2.health_insurance as buyer_health_insurance "
            + "from invoices i "
            + "inner join companies c1 on i.seller_id = c1.id "
            + "inner join companies c2 on i.buyer_id = c2.id";
    private final JdbcTemplate jdbcTemplate;
    private final InvoiceRowMapper invoiceRowMapper;

    @Override
    @Transactional
    public Invoice save(Invoice invoice) {
        if (invoice != null) {
            UUID buyerId = insertCompany(invoice.getBuyer());
            UUID sellerId = insertCompany(invoice.getSeller());

            invoice.getBuyer().setId(buyerId);
            invoice.getSeller().setId(sellerId);

            UUID invoiceId = insertInvoice(invoice, sellerId, buyerId);
            invoice.setId(invoiceId);

            addEntriesRelatedToInvoice(invoice);

            return invoice;
        }
        return null;
    }

    private UUID insertCompany(Company company) {
        UUID id = UUID.randomUUID();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "insert into companies "
                            + "(id, tax_id, address, company_name, health_insurance, pension_insurance) "
                            + "values (?, ?, ?, ?, ?, ?);");
            ps.setObject(1, id);
            ps.setString(2, company.getTaxIdentificationNumber());
            ps.setString(3, company.getAddress());
            ps.setString(4, company.getName());
            ps.setBigDecimal(5, company.getHealthInsurance());
            ps.setBigDecimal(6, company.getPensionInsurance());
            log.info("inserted company taxid {}", company.getTaxIdentificationNumber());
            return ps;
        });

        return id;
    }

    private UUID insertInvoice(Invoice invoice, UUID sellerId, UUID buyerId) {
        UUID invoiceId = UUID.randomUUID();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps =
                    connection.prepareStatement("insert into invoices"
                            + " (id, issue_date, buyer_id, seller_id, number) values (?, ?, ?, ?, ?);");
            ps.setObject(1, invoiceId);
            ps.setTimestamp(2, Timestamp.valueOf(invoice.getDate()));
            ps.setObject(3, buyerId);
            ps.setObject(4, sellerId);
            ps.setString(5, invoice.getNumber());
            log.info("inserted invoice id {}", invoice.getId().toString());
            return ps;
        });
        return invoiceId;
    }

    private void addEntriesRelatedToInvoice(Invoice invoice) {
        invoice.getInvoiceEntries().forEach(invoiceEntry -> {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement("insert into invoice_entries "
                        + "(id, description, invoice_id, personal_car, price, vat_rate, vat_value) "
                        + "values (?, ?, ?, ?, ? ,? ,?);");
                UUID id = UUID.randomUUID();
                invoiceEntry.setId(id);
                ps.setObject(1, invoiceEntry.getId());
                ps.setString(2, invoiceEntry.getDescription());
                ps.setObject(3, invoice.getId());
                ps.setBoolean(4, invoiceEntry.isPersonalCar());
                ps.setBigDecimal(5, invoiceEntry.getPrice());
                ps.setString(6, invoiceEntry.getVatRate().toString());
                ps.setBigDecimal(7, invoiceEntry.getVatValue());
                log.info("Added invoice entry id {}", invoiceEntry.getId());
                return ps;
            });
        });
    }

    @Override
    public Invoice getById(UUID id) throws NoSuchElementException {
        List<Invoice> invoice = jdbcTemplate.query(SELECT_QUERY + " where i.id = '" + id + "';", invoiceRowMapper);
        if (invoice.isEmpty()) {
            throw new NoSuchElementException();
        }
        return invoice.get(0);
    }

    @Override
    public List<Invoice> getAll() {
        return jdbcTemplate.query(SELECT_QUERY + ";", invoiceRowMapper);
    }

    @Override
    public Invoice update(Invoice updatedInvoice) {
        Invoice invoiceToUpdate = getById(updatedInvoice.getId());
        UUID sellerId = invoiceToUpdate.getSeller().getId();
        UUID buyerId = invoiceToUpdate.getBuyer().getId();

        updateCompany(updatedInvoice.getSeller(), sellerId);
        updateCompany(updatedInvoice.getBuyer(), buyerId);

        updatedInvoice.getSeller().setId(sellerId);
        updatedInvoice.getBuyer().setId(buyerId);

        updateInvoice(updatedInvoice);

        deleteRelatedInvoiceEntries(updatedInvoice.getId());
        addEntriesRelatedToInvoice(updatedInvoice);

        return updatedInvoice;
    }

    private void updateCompany(Company updatedCompany, UUID id) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps =
                    connection.prepareStatement("update companies "
                            + "set company_name = ?, "
                            + "address = ?, "
                            + "tax_id = ?, "
                            + "health_insurance = ?, "
                            + "pension_insurance = ? "
                            + "where id = ?");
            ps.setString(1, updatedCompany.getName());
            ps.setString(2, updatedCompany.getAddress());
            ps.setString(3, updatedCompany.getTaxIdentificationNumber());
            ps.setBigDecimal(4, updatedCompany.getHealthInsurance());
            ps.setBigDecimal(5, updatedCompany.getPensionInsurance());
            ps.setObject(6, id);
            return ps;
        });
    }

    private void updateInvoice(Invoice updatedInvoice) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps =
                    connection.prepareStatement("update invoices "
                            + "set issue_date = ?, "
                            + "number = ? "
                            + "where id = ?");
            ps.setTimestamp(1, Timestamp.valueOf(updatedInvoice.getDate()));
            ps.setString(2, updatedInvoice.getNumber());
            ps.setObject(3, updatedInvoice.getId());
            return ps;
        });
    }

    @Override
    @Transactional
    public void delete(UUID id) throws NoSuchElementException {
        if (id != null) {
            getById(id);
            deleteRelatedInvoiceEntries(id);
            deleteInvoice(id);
        }
    }

    private void deleteInvoice(UUID id) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps =
                    connection.prepareStatement("delete from invoices "
                            + "where id = ?;");
            ps.setObject(1, id);
            return ps;
        });
    }

    private void deleteRelatedInvoiceEntries(UUID id) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps =
                    connection.prepareStatement("delete from invoice_entries "
                            + "where invoice_id = ?;");
            ps.setObject(1, id);
            return ps;
        });
    }
}
