package pl.futurecollars.invoicing.service.invoiceService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.futurecollars.invoicing.db.Database

@SpringBootTest
@ActiveProfiles("mongo")
class InvoiceServiceMongoTest extends InvoiceServiceAbstractIntegrationTest{

    @Autowired
    Database mongoDatabase
    @Override
    Database getDatabase() {
        return mongoDatabase
    }
}
