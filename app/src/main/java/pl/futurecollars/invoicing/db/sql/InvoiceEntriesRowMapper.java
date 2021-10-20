package pl.futurecollars.invoicing.db.sql;
import org.springframework.jdbc.core.RowMapper;
import pl.futurecollars.invoicing.model.InvoiceEntry;
import pl.futurecollars.invoicing.model.Vat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;


public class InvoiceEntriesRowMapper implements RowMapper<InvoiceEntry> {


    @Override
    public InvoiceEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
        return InvoiceEntry.builder()
                .id((UUID) rs.getObject("id"))
                .description(rs.getString("description"))
                .personalCar(rs.getBoolean("personal_car"))
                .price(rs.getBigDecimal("price"))
                .vatRate(Vat.valueOf(rs.getString("vat_rate")))
                .vatValue((rs.getBigDecimal("vat_value")))
                .build();

    }
}
