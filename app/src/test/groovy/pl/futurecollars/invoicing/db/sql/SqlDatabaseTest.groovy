package pl.futurecollars.invoicing.db.sql

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.db.DatabaseTest

@SpringBootTest
@ActiveProfiles("sqlTest")
class SqlDatabaseTest extends DatabaseTest {

    @Autowired
    SqlDatabase sqlDatabase

    @Override
    Database getDatabase() {
        return sqlDatabase
    }
}
