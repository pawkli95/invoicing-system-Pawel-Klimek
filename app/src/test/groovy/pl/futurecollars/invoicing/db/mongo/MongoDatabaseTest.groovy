package pl.futurecollars.invoicing.db.mongo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.db.DatabaseTest

@SpringBootTest
@ActiveProfiles("mongo")
class MongoDatabaseTest extends DatabaseTest{

    @Autowired
    Database mongoDatabase

    @Override
    Database getDatabase() {
        return mongoDatabase
    }
}
