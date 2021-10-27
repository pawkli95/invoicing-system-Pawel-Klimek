package pl.futurecollars.invoicing.db.mongo;

import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;
import pl.futurecollars.invoicing.model.Invoice;

public interface MongoDbRepository extends MongoRepository<Invoice, UUID> {
}
