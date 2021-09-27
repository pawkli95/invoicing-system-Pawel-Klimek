package pl.futurecollars.invoicing.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.UUID;
import lombok.Data;

@Data
public class Company {

    @ApiModelProperty(value = "Id of company", required = true, example = "f77de595-58a1-4533-b96b-e493aee29e8a")
    private final UUID id;
    @ApiModelProperty(value = "Tax identification number of company", required = true, example = "1")
    private final long taxIdentificationNumber;
    @ApiModelProperty(value = "Address of company", required = true, example = "ul. Krótka 22, Warszawa 04-988")
    private final String address;

    @JsonCreator
    public Company(@JsonProperty("taxIdentificationNumber") long taxIdentificationNumber,
                   @JsonProperty("address") String address) {
        this.taxIdentificationNumber = taxIdentificationNumber;
        this.address = address;
        this.id = UUID.randomUUID();
    }
}
