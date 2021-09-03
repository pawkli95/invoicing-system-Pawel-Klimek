package pl.futurecollars.invoicing.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InvoiceEntry {

    private final String description;
    private final BigDecimal price;
    private final Vat vatRate;
    private final BigDecimal vatValue;

    @JsonCreator
    public InvoiceEntry(@JsonProperty("description") String description,
                        @JsonProperty("price") BigDecimal price,
                        @JsonProperty("vatRate") Vat vatRate) {
        this.description = description;
        this.price = price;
        this.vatRate = vatRate;
        this.vatValue = price.multiply(BigDecimal.valueOf(vatRate.rate)).setScale(2, RoundingMode.HALF_UP);
    }
}
