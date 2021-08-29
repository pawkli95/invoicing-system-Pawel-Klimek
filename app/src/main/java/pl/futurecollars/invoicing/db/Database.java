package pl.futurecollars.invoicing.db;

import pl.futurecollars.invoicing.model.Invoice;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Database {

    Invoice save(Invoice invoice);

    Optional<Invoice> getById(UUID id);

    List<Invoice> getAll();

    Invoice update(Invoice updatedInvoice);

    boolean delete(UUID id);
}
