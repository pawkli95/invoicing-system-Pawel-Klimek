package pl.futurecollars.invoicing.db.memory

import org.springframework.boot.test.context.SpringBootTest
import pl.futurecollars.invoicing.db.DatabaseTest


@SpringBootTest
class InMemoryDatabaseTest extends DatabaseTest{

    def setup() {
        database = new InMemoryDatabase()
    }
}
