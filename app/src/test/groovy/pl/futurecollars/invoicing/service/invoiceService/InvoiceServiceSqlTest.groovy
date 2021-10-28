package pl.futurecollars.invoicing.service.invoiceService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.futurecollars.invoicing.db.Database

@SpringBootTest
@ActiveProfiles("sqlTest")
class InvoiceServiceSqlTest extends InvoiceServiceAbstractIntegrationTest{

    @Autowired
    Database sqlDatabase

    @Override
    Database getDatabase() {
        return sqlDatabase
    }
}
