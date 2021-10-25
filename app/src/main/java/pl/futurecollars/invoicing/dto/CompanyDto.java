package pl.futurecollars.invoicing.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyDto {

    @ApiModelProperty(value = "Id of company", example = "1002020100")
    private UUID id;

    @ApiModelProperty(value = "Tax identification number of company", required = true, example = "1002020100")
    private String taxIdentificationNumber;

    @ApiModelProperty(value = "Name of company", required = true, example = "PepsiCo")
    private String name;

    @ApiModelProperty(value = "Address of company", required = true, example = "ul. Krótka 22, Warszawa 04-988")
    private String address;

    @ApiModelProperty(value = "Health insurance", required = true, example = "4250.9")
    private BigDecimal healthInsurance;

    @ApiModelProperty(value = "Pension insurance", required = true, example = "900.3")
    private BigDecimal pensionInsurance;
}
