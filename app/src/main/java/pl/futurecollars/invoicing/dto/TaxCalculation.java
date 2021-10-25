package pl.futurecollars.invoicing.dto;

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

    @ApiModelProperty(value = "Total income of company", required = true, example = "3000")
    private BigDecimal income;

    @ApiModelProperty(value = "Expenses of company", required = true, example = "2000")
    private BigDecimal costs;

    @ApiModelProperty(value = "Net earnings of company", required = true, example = "1000")
    private BigDecimal incomeMinusCosts;

    @ApiModelProperty(value = "Pension insurance", required = true, example = "1000")
    private BigDecimal pensionInsurance;

    @ApiModelProperty(value = "Income - costs - pension insurance", required = true, example = "1000")
    private BigDecimal incomeMinusCostsMinusPensionInsurance;

    @ApiModelProperty(value = "Tax calculation base(above rounded)", required = true, example = "6000")
    private BigDecimal taxCalculationBase;

    @ApiModelProperty(value = "Income tax", required = true, example = "2045.67")
    private BigDecimal incomeTax;

    @ApiModelProperty(value = "Health insurance at 9% rate", required = true, example = "450.2")
    private BigDecimal healthInsurance9;

    @ApiModelProperty(value = "Health insurance at 7.75% rate", required = true, example = "540.2")
    private BigDecimal healthInsurance775;

    @ApiModelProperty(value = "Income tax - health insurance at 7,75%", required = true, example = "9892.4")
    private BigDecimal incomeTaxMinusHealthInsurance;

    @ApiModelProperty(value = "Final income tax value", required = true, example = "4320.1")
    private BigDecimal finalIncomeTaxValue;

    @ApiModelProperty(value = "Incoming Vat tax of company", required = true, example = "500")
    private BigDecimal incomingVat;

    @ApiModelProperty(value = "Outgoing Vat tax of company", required = true, example = "1000")
    private BigDecimal outgoingVat;

    @ApiModelProperty(value = "Vat tax to return by company", required = true, example = "500")
    private BigDecimal vatToReturn;
}

