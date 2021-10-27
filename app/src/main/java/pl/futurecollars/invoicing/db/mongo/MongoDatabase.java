package pl.futurecollars.invoicing.db.mongo;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;

@RequiredArgsConstructor
public class MongoDatabase implements Database<Invoice> {

    private final MongoDbRepository mongoRepository;

    @Override
    public Invoice save(Invoice invoice) {
        if (invoice != null) {
            invoice.setId(UUID.randomUUID());
            return mongoRepository.save(invoice);
        }
        return null;
    }

    @Override
    public Invoice getById(UUID id) throws NoSuchElementException {
        Optional<Invoice> optional = mongoRepository.findById(id);
        return optional.orElseThrow(NoSuchElementException::new);
    }

    @Override
    public List<Invoice> getAll() {
        return mongoRepository.findAll();
    }

    @Override
    public Invoice update(Invoice updatedEntity) {
        if (mongoRepository.existsById(updatedEntity.getId())) {
            return mongoRepository.save(updatedEntity);
        }
        throw new NoSuchElementException();
    }

    @Override
    public void delete(UUID id) throws NoSuchElementException {
        if (mongoRepository.existsById(id)) {
            mongoRepository.deleteById(id);
            return;
        }
        throw new NoSuchElementException();

    }
}
