package pl.futurecollars.invoicing.db.filebased

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.db.DatabaseTest

@ActiveProfiles("fileTest")
@SpringBootTest
class FileBasedDatabaseTest extends DatabaseTest {

    @Autowired
    FileBasedDatabase fileBasedDatabase;

    @Override
    Database getDatabase() {
        return fileBasedDatabase
    }
}

