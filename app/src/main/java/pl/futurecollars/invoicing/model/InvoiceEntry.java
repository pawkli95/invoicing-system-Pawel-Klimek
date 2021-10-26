package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "invoice_entries")
public class InvoiceEntry {

    @Id
    @GeneratedValue
    @ApiModelProperty(value = "Id", example = "f77de595-58a1-4533-b96b-e493aee29e8a")
    private UUID id;

    @ApiModelProperty(value = "Description of product", required = true, example = "Paliwo na Orlenie")
    private String description;

    @ApiModelProperty(value = "Price of product", required = true, example = "200")
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @ApiModelProperty(value = "VAT rate", required = true, example = "VAT_23")
    private Vat vatRate;

    @Builder.Default
    @ApiModelProperty(value = "Value of VAT", required = true, example = "46")
    private BigDecimal vatValue = BigDecimal.ZERO.setScale(2);

    @ApiModelProperty(value = "Flag to mark car used for personal reasons", required = true, example = "true")
    private boolean personalCar;

    public void calculateVatValue() {
        vatValue = price.multiply(BigDecimal.valueOf(vatRate.rate)).setScale(2, RoundingMode.HALF_UP);
    }
}
