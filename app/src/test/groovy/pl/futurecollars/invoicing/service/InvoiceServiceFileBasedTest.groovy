package pl.futurecollars.invoicing.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

import pl.futurecollars.invoicing.db.filebased.FileBasedDatabase

@ActiveProfiles("fileTest")
@SpringBootTest
class InvoiceServiceFileBasedTest extends InvoiceServiceAbstractIntegrationTest {

    @Autowired
    FileBasedDatabase fileBasedDatabase

    FileBasedDatabase getDatabase() {
        return fileBasedDatabase
    }

    def setup() {
        database = getDatabase()
        database.getJsonFileService().eraseFile()
        database.getIdsFileService().eraseFile()
        invoiceService = new InvoiceService(database)
        }
}
