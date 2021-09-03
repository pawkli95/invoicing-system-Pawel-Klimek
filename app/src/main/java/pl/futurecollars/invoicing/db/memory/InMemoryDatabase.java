package pl.futurecollars.invoicing.db.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.Getter;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;

public class InMemoryDatabase implements Database {

    @Getter
    private final Map<UUID, Invoice> database = new HashMap<>();

    @Override
    public Invoice save(Invoice invoice) {
        if (invoice != null) {
            while (database.containsKey(invoice.getId())) {
                invoice.setId(UUID.randomUUID());
            }
            database.put(invoice.getId(), invoice);
        }
        return invoice;

    }

    @Override
    public Invoice getById(UUID id) throws NoSuchElementException {
        if (database.containsKey(id)) {
            return database.get(id);
        }
        throw new NoSuchElementException();
    }

    @Override
    public List<Invoice> getAll() {
        return new ArrayList<>(database.values());
    }

    @Override
    public Invoice update(Invoice updatedInvoice) {
        if (updatedInvoice != null) {
            database.put(updatedInvoice.getId(), updatedInvoice);
        }
        return updatedInvoice;
    }

    @Override
    public boolean delete(UUID id) {
        if (database.containsKey(id)) {
            try {
                database.remove(id);
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        throw new NoSuchElementException();
    }
}
