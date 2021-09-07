package pl.futurecollars.invoicing.db;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import pl.futurecollars.invoicing.model.Invoice;

public interface Database {

    Invoice save(Invoice invoice);

    Invoice getById(UUID id) throws NoSuchElementException;

    List<Invoice> getAll();

    Invoice update(Invoice updatedInvoice);

    boolean delete(UUID id) throws NoSuchElementException;
}
