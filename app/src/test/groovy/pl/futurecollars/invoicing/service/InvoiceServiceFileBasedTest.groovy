package pl.futurecollars.invoicing.service


import pl.futurecollars.invoicing.config.FilePathConfig
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.db.filebased.FileBasedDatabase
import pl.futurecollars.invoicing.utils.FileService
import pl.futurecollars.invoicing.utils.JsonService

class InvoiceServiceFileBasedTest extends InvoiceServiceAbstractIntegrationTest{

    Database getDatabase() {
        return new FileBasedDatabase(new JsonService(), new FileService(FilePathConfig.JSON_FILE), new FileService(FilePathConfig.IDS_FILE))
    }
}
