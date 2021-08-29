package pl.futurecollars.invoicing.model;

import lombok.Data;

import java.util.UUID;

@Data
public class Company {

    private final long id;
    private final long taxIdentificationNumber;
    private final String address;

}
