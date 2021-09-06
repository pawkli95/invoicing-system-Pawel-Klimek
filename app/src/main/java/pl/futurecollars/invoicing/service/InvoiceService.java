package pl.futurecollars.invoicing.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;

@RequiredArgsConstructor
@Service
public class InvoiceService {

    private final Database database;

    public Invoice saveInvoice(Invoice invoice) {
        return database.save(invoice);
    }

    public Invoice getById(UUID id) throws NoSuchElementException {
        return database.getById(id);
    }

    public List<Invoice> getAll() {
        return database.getAll();
    }

    public List<Invoice> filter(Predicate<Invoice> predicate) {
        return database.getAll()
                .stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public Invoice updateInvoice(Invoice updatedInvoice) {
        return database.update(updatedInvoice);
    }

    public boolean deleteInvoice(UUID id) throws NoSuchElementException {
        return database.delete(id);
    }
}
