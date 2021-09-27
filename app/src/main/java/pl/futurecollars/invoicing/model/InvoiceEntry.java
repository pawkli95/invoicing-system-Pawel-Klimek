package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.Data;

@Data
public class InvoiceEntry {

    @ApiModelProperty(value = "Description of product", required = true, example = "Paliwo na Orlenie")
    private final String description;
    @ApiModelProperty(value = "Price of product", required = true, example = "200")
    private final BigDecimal price;
    @ApiModelProperty(value = "VAT rate", required = true, example = "VAT_23")
    private final Vat vatRate;
    @ApiModelProperty(value = "Value of VAT", required = true, example = "46")
    private final BigDecimal vatValue;

    public InvoiceEntry(String description, BigDecimal price, Vat vatRate) {
        this.description = description;
        this.price = price;
        this.vatRate = vatRate;
        this.vatValue = price.multiply(BigDecimal.valueOf(vatRate.rate)).setScale(2, RoundingMode.HALF_UP);
    }
}
