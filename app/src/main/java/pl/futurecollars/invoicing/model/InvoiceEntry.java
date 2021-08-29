package pl.futurecollars.invoicing.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InvoiceEntry {

    private final String description;
    private final BigDecimal price;
    private final BigDecimal vatValue;
    private final Vat vatRate;
}
