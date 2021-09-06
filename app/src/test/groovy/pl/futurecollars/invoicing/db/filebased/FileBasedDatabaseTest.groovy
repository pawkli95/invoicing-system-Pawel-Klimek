package pl.futurecollars.invoicing.db.filebased

import pl.futurecollars.invoicing.db.DatabaseTest
import pl.futurecollars.invoicing.utils.JsonService

class FileBasedDatabaseTest extends DatabaseTest {

    def setup() {
        database = new FileBasedDatabase(new JsonService())
        database.getJsonFileService().eraseFile()
        database.getIdsFileService().eraseFile()
    }
}
