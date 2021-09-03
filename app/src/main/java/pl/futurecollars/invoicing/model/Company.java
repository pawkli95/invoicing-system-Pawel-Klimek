package pl.futurecollars.invoicing.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Company {

    private final UUID id;
    private final long taxIdentificationNumber;
    private final String address;

    @JsonCreator
    public Company(@JsonProperty("taxIdentificationNumber") long taxIdentificationNumber,
                   @JsonProperty("address") String address) {
        this.taxIdentificationNumber = taxIdentificationNumber;
        this.address = address;
        this.id = UUID.randomUUID();
    }
}
