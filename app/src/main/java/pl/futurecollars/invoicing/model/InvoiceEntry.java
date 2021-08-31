package pl.futurecollars.invoicing.model;

import java.math.BigDecimal;
<<<<<<< HEAD
import java.math.RoundingMode;
=======
>>>>>>> origin/TASK2
import lombok.Data;

@Data
public class InvoiceEntry {

    private final String description;
    private final BigDecimal price;
    private final Vat vatRate;
    private final BigDecimal vatValue;

    public InvoiceEntry(String description, BigDecimal price, Vat vatRate) {
        this.description = description;
        this.price = price;
        this.vatRate = vatRate;
        this.vatValue = price.multiply(BigDecimal.valueOf(vatRate.rate)).setScale(2, RoundingMode.HALF_UP);
    }
}
