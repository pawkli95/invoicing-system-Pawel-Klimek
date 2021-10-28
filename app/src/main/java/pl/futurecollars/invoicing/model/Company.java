package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue
    @ApiModelProperty(value = "Id", example = "f77de595-58a1-4533-b96b-e493aee29e8a")
    private UUID id;

    @Column(name = "tax_id", unique = true)
    @ApiModelProperty(value = "Tax identification number of company", required = true, example = "1002020100")
    private String taxIdentificationNumber;

    @Column(name = "company_name", nullable = false)
    @ApiModelProperty(value = "Name of company", required = true, example = "PepsiCo")
    private String name;

    @Column(nullable = false)
    @ApiModelProperty(value = "Address of company", required = true, example = "ul. Krótka 22, Warszawa 04-988")
    private String address;

    @Column(nullable = false)
    @ApiModelProperty(value = "Health insurance", required = true, example = "4250.9")
    private BigDecimal healthInsurance;

    @Column(nullable = false)
    @ApiModelProperty(value = "Pension insurance", required = true, example = "900.3")
    private BigDecimal pensionInsurance;

}
