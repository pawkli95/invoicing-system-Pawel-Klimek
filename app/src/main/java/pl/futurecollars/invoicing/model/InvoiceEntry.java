package pl.futurecollars.invoicing.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @ApiModelProperty(value = "Flag to mark car used for personal reasons", required = true, example = "true")
    private final boolean personalCar;

    @JsonCreator
    public InvoiceEntry(@JsonProperty("description") String description,
                        @JsonProperty("price") BigDecimal price,
                        @JsonProperty("vatRate") Vat vatRate,
                        @JsonProperty("personalCar") boolean personalCar) {
        this.description = description;
        this.price = price;
        this.vatRate = vatRate;
        this.personalCar = personalCar;
        this.vatValue = price.multiply(BigDecimal.valueOf(vatRate.rate)).setScale(2, RoundingMode.HALF_UP);
    }
}
