package pl.futurecollars.invoicing.db.sql;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class SqlDatabase implements Database {


    public static final String SELECT_QUERY = "select i.id, i.issue_date"
            + "c1.tax_id as seller_tax_id, c1.company_name as seller_company_name, c1.address as seller_address, "
            + "c1.pension_insurance as seller_pension_insurance, c1.health_insurance as seller_health_insurance, "
            + "c2.tax_id as buyer_tax_id, c2.company_name as buyer_company_name, c2.address as buyer_address, "
            + "c2.pension_insurance as buyer_pension_insurance, c2.health_insurance as buyer_health_insurance "
            + "from invoice i "
            + "inner join company c1 on i.seller_tax_id = c1.tax_id "
            + "inner join company c2 on i.buyer_tax_id = c2.tax_id";
    private final JdbcTemplate jdbcTemplate;
    private final InvoiceRowMapper invoiceRowMapper;

    @Override
    public Invoice save(Invoice invoice) {
        String buyerTaxId = insertCompany(invoice.getBuyer());
        String sellerTaxId = insertCompany(invoice.getSeller());

        UUID invoiceId = insertInvoice(invoice, sellerTaxId, buyerTaxId);
        addEntriesRelatedToInvoice(invoice, invoiceId);

        return invoice;
    }

    private String insertCompany(Company company) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "insert into company (tax_id, address, company_name, health_insurance, pension_insurance) values (?, ?, ?, ?, ?);");
            ps.setString(1, company.getTaxIdentificationNumber());
            ps.setString(2, company.getAddress());
            ps.setString(3, company.getName());
            ps.setBigDecimal(4, company.getHealthInsurance());
            ps.setBigDecimal(5, company.getPensionInsurance());
            return ps;
        });

        return company.getTaxIdentificationNumber();
    }

    private UUID insertInvoice(Invoice invoice, String sellerTaxId, String buyerTaxId) {
        UUID invoiceId = UUID.randomUUID();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps =
                    connection.prepareStatement("insert into invoice (id, issue_date, buyer_tax_id, seller_tax_id) values (?, ?, ?, ?);");
            ps.setObject(1, invoiceId);
            ps.setTimestamp(2, Timestamp.valueOf(invoice.getDate()));
            ps.setString(3, buyerTaxId);
            ps.setString(4, sellerTaxId);
            return ps;
        });
        return invoiceId;
    }

    private void addEntriesRelatedToInvoice(Invoice invoice, UUID invoiceId) {
        invoice.getInvoiceEntries().forEach(invoiceEntry -> {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement("insert into invoice_entries" +
                        "(id, description, invoice_id, personal_car, price, vat_rate, vat_value)" +
                        "values (?, ?, ?, ?, ? ,? ,?);");
                UUID invoiceEntryId = UUID.randomUUID();
                ps.setObject(1, invoiceEntryId);
                ps.setString(2, invoiceEntry.getDescription());
                ps.setObject(3, invoiceId);
                ps.setBoolean(4, invoiceEntry.isPersonalCar());
                ps.setBigDecimal(5, invoiceEntry.getPrice());
                ps.setString(6, invoiceEntry.getVatRate().toString());
                ps.setBigDecimal(7, invoiceEntry.getVatValue());
                return ps;
            });
        });
    }

    @Override
    public Invoice getById(UUID id) throws NoSuchElementException {
        List<Invoice> invoice = jdbcTemplate.query(SELECT_QUERY + "where id = " + id, invoiceRowMapper);
        return invoice.get(0);
    }

    @Override
    public List<Invoice> getAll() {
        return jdbcTemplate.query(SELECT_QUERY, invoiceRowMapper);
    }

    @Override
    public Invoice update(Invoice updatedInvoice) {
        return null;
    }

    @Override
    public boolean delete(UUID id) throws NoSuchElementException {
        deleteRelatedInvoiceEntries(id);
        deleteInvoice(id);
        if(getById(id) == null) {
            return true;
        }
        return false;
    }

    private void deleteInvoice(UUID id) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps =
                    connection.prepareStatement("delete from invoices" +
                            "where id = ?;");
            ps.setObject(1, id);
            return ps;
        });
    }

    private void deleteRelatedInvoiceEntries(UUID id) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps =
                    connection.prepareStatement("delete from invoice_entries " +
                            "where invoice_id = ?;");
            ps.setObject(1, id);
            return ps;
        });
    }
}
