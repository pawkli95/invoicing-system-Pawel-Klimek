package pl.futurecollars.invoicing.controller.testConfig

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pl.futurecollars.invoicing.config.FilePathConfig
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.db.filebased.FileBasedDatabase
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.utils.FileService
import pl.futurecollars.invoicing.utils.JsonService

@TestConfiguration
class FileBasedDatabaseTestConfig {

    @Bean("fileBasedDatabase")
    Database getFileBasedDatabaseTestInstance() {
        FileService jsonFileService = new FileService(FilePathConfig.TEST_JSON_FILE)
        FileService idsFileService = new FileService(FilePathConfig.TEST_IDS_FILE)
        return new FileBasedDatabase(new JsonService<Invoice>(), jsonFileService, idsFileService)
    }
}
