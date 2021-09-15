package pl.futurecollars.invoicing.db.filebased

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.db.DatabaseTest
import pl.futurecollars.invoicing.utils.FileService
import pl.futurecollars.invoicing.utils.JsonService

@SpringBootTest
class FileBasedDatabaseTest extends DatabaseTest {

    @Autowired
    JsonService jsonService

    @Autowired
    FileService jsonFileService

    @Autowired
    FileService idsFileService

    @Override
    Database getDatabase() {
        Database fileBasedDatabase = new FileBasedDatabase(jsonService, jsonFileService, idsFileService)
        fileBasedDatabase.getJsonFileService().eraseFile()
        fileBasedDatabase.getIdsFileService().eraseFile()
        return fileBasedDatabase
    }
}
