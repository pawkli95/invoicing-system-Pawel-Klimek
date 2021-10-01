package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaxCalculation {

    @ApiModelProperty(value = "Expenses of company", required = true, example = "2000")
    private BigDecimal costs;
    @ApiModelProperty(value = "Net earnings of company", required = true, example = "1000")
    private BigDecimal earnings;
    @ApiModelProperty(value = "Total income of company", required = true, example = "3000")
    private BigDecimal income;
    @ApiModelProperty(value = "Incoming Vat tax of company", required = true, example = "500")
    private BigDecimal incomingVat;
    @ApiModelProperty(value = "Outgoing Vat tax of company", required = true, example = "1000")
    private BigDecimal outgoingVat;
    @ApiModelProperty(value = "Vat tax to return by company", required = true, example = "500")
    private BigDecimal vatToReturn;
}
