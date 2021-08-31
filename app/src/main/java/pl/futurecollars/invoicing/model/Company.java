package pl.futurecollars.invoicing.model;

import java.util.UUID;
import lombok.Data;

@Data
public class Company {

    private final UUID id;
    private final long taxIdentificationNumber;
    private final String address;

    public Company(long taxIdentificationNumber, String address) {
        this.taxIdentificationNumber = taxIdentificationNumber;
        this.address = address;
        this.id = UUID.randomUUID();
    }
}

