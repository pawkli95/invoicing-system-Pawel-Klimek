package pl.futurecollars.invoicing.db.memory;

import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;
import java.util.*;

public class InMemoryDatabase implements Database {


    public final Map<UUID, Invoice> database = new HashMap<>();

    @Override
    public Invoice save(Invoice invoice) {
        Optional<Invoice> invoiceOptional = Optional.ofNullable(invoice);
        if(invoiceOptional.isPresent()) {
            while (database.containsKey(invoice.getId())) {
                invoice.setId(UUID.randomUUID());
            }
            database.put(invoice.getId(), invoice);
            return invoice;
        }
        return null;
    }

    @Override
    public Optional<Invoice> getById(UUID id) {
        return Optional.ofNullable(database.get(id));
    }

    @Override
    public List<Invoice> getAll() {
        return new ArrayList<>(database.values());
    }

    @Override
    public Invoice update(Invoice updatedInvoice) {
        Optional<Invoice> optionalInvoice = Optional.ofNullable(updatedInvoice);
        if(optionalInvoice.isPresent()) {
            database.put(updatedInvoice.getId(), updatedInvoice);
            return updatedInvoice;
        }
            return null;
    }

    @Override
    public boolean delete(UUID id) {
        return database.remove(id, database.get(id));
    }
}
