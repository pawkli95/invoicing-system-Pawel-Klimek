package pl.futurecollars.invoicing.db.filebased

import org.springframework.boot.test.context.SpringBootTest
import pl.futurecollars.invoicing.db.DatabaseTest

@SpringBootTest
class FileBasedDatabaseTest extends DatabaseTest {

    def setup() {
        database = new FileBasedDatabase()
        database.getJsonFileService().eraseFile()
        database.getIdsFileService().eraseFile()
    }
}
