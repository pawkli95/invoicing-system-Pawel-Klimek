package pl.futurecollars.invoicing.service;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;

public class InvoiceService {

    private final Database database;

    public InvoiceService(Database database) {
        this.database = database;
    }

    public Invoice saveInvoice(Invoice invoice) {
        return database.save(invoice);
    }

    public Invoice getById(UUID id) {
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

    public boolean deleteInvoice(UUID id) {
        return database.delete(id);
    }
}

