package pl.futurecollars.invoicing.service

import org.mapstruct.factory.Mappers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

import pl.futurecollars.invoicing.db.filebased.FileBasedDatabase
import pl.futurecollars.invoicing.dto.mappers.InvoiceMapper

@ActiveProfiles("fileTest")
@SpringBootTest
class InvoiceServiceFileBasedTest extends InvoiceServiceAbstractIntegrationTest {

    @Autowired
    FileBasedDatabase fileBasedDatabase

    FileBasedDatabase getDatabase() {
        return fileBasedDatabase
    }
}
