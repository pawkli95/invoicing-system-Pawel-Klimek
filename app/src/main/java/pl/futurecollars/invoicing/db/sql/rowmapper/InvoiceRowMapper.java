package pl.futurecollars.invoicing.db.sql.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import pl.futurecollars.invoicing.db.sql.rowmapper.InvoiceEntriesRowMapper;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;

@AllArgsConstructor
@Component
public class InvoiceRowMapper implements RowMapper<Invoice> {

    private final JdbcTemplate jdbcTemplate;
    private final InvoiceEntriesRowMapper invoiceEntriesRowMapper;

    @Override
    public Invoice mapRow(ResultSet rs, int rowNum) throws SQLException {
        UUID invoiceId = (UUID) rs.getObject("id");
        List<InvoiceEntry> invoiceEntryList = jdbcTemplate.query("select ie.id as ie_id, ie.description, ie.personal_car, "
                + "ie.price, ie.vat_rate, ie.vat_value from invoice_entries ie "
                + "where ie.invoice_id = '" + invoiceId + "';",
                invoiceEntriesRowMapper
                );

        return Invoice.builder()
                .id((UUID) rs.getObject("id"))
                .seller(Company.builder()
                        .id((UUID) rs.getObject("seller_id"))
                        .taxIdentificationNumber(rs.getString("seller_tax_id"))
                        .address(rs.getString("seller_address"))
                        .name(rs.getString("seller_company_name"))
                        .healthInsurance(rs.getBigDecimal("seller_health_insurance"))
                        .pensionInsurance(rs.getBigDecimal("seller_pension_insurance"))
                        .build())
                .buyer(Company.builder()
                        .id((UUID) rs.getObject("buyer_id"))
                        .taxIdentificationNumber(rs.getString("buyer_tax_id"))
                        .address(rs.getString("buyer_address"))
                        .name(rs.getString("buyer_company_name"))
                        .healthInsurance(rs.getBigDecimal("buyer_health_insurance"))
                        .pensionInsurance(rs.getBigDecimal("buyer_pension_insurance"))
                        .build())
                .number(rs.getString("number"))
                .date(rs.getTimestamp("issue_date").toLocalDateTime())
                .invoiceEntries(invoiceEntryList)
                .build();
    }
}

