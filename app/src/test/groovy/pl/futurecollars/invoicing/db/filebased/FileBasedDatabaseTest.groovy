package pl.futurecollars.invoicing.db.filebased

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pl.futurecollars.invoicing.config.FilePathConfig
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.db.DatabaseTest
import pl.futurecollars.invoicing.utils.FileService
import pl.futurecollars.invoicing.utils.JsonService

@SpringBootTest
class FileBasedDatabaseTest extends DatabaseTest {

    @Autowired
    FileBasedDatabase fileBasedDatabase;

    @Override
    Database getDatabase() {
        fileBasedDatabase.getJsonFileService().eraseFile()
        fileBasedDatabase.getIdsFileService().eraseFile()
        return fileBasedDatabase
    }
}

@TestConfiguration
class FileBasedDatabaseTestConfiguration {

    @Bean
    FileBasedDatabase getFileBasedDatabaseTestInstance() {
        FileService jsonFileService = new FileService(FilePathConfig.TEST_JSON_FILE)
        FileService idsFileService = new FileService(FilePathConfig.TEST_IDS_FILE)
        return new FileBasedDatabase(new JsonService(), jsonFileService, idsFileService)
    }

}
