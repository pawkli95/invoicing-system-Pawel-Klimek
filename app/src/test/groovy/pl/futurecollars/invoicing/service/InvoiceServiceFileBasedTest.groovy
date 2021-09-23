package pl.futurecollars.invoicing.service


import pl.futurecollars.invoicing.config.FilePathConfig
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.db.filebased.FileBasedDatabase
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.utils.FileService
import pl.futurecollars.invoicing.utils.JsonService

class InvoiceServiceFileBasedTest extends InvoiceServiceAbstractIntegrationTest{

    FileBasedDatabase getDatabase() {
        return new FileBasedDatabase(new JsonService<Invoice>(), new FileService(FilePathConfig.TEST_JSON_FILE), new FileService(FilePathConfig.TEST_IDS_FILE))
    }

    def setup() {
        database = getDatabase()
        database.getJsonFileService().eraseFile()
        database.getIdsFileService().eraseFile()
        invoiceService = new InvoiceService(database)
    }
}
