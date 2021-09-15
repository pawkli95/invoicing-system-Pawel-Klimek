package pl.futurecollars.invoicing.db.memory

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.db.DatabaseTest


@SpringBootTest
class InMemoryDatabaseTest extends DatabaseTest{

    @Override
    Database getDatabase() {
        return new InMemoryDatabase()
    }
}
