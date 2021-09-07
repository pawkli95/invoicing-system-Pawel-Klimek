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

    private final Database fileBasedDatabase;

    public Invoice saveInvoice(Invoice invoice) {
        return fileBasedDatabase.save(invoice);
    }

    public Invoice getById(UUID id) throws NoSuchElementException {
        return fileBasedDatabase.getById(id);
    }

    public List<Invoice> getAll() {
        return fileBasedDatabase.getAll();
    }

    public List<Invoice> filter(Predicate<Invoice> predicate) {
        return fileBasedDatabase.getAll()
                .stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public Invoice updateInvoice(Invoice updatedInvoice) {
        return fileBasedDatabase.update(updatedInvoice);
    }

    public boolean deleteInvoice(UUID id) throws NoSuchElementException {
        return fileBasedDatabase.delete(id);
    }
}
