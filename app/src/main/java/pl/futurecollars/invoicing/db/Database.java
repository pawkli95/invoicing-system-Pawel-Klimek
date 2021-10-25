package pl.futurecollars.invoicing.db;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import pl.futurecollars.invoicing.dto.InvoiceDto;
import pl.futurecollars.invoicing.model.Invoice;

public interface Database<T> {

    T save(T invoice);

    T getById(UUID id) throws NoSuchElementException;

    List<T> getAll();

    T update(T updatedEntity);

    void delete(UUID id) throws NoSuchElementException;
}
