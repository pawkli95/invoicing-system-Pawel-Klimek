package pl.futurecollars.invoicing.service.taxCalculatorService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.futurecollars.invoicing.db.Database

@SpringBootTest
@ActiveProfiles("fileTest")
class TaxCalculatorServiceFileDatabaseTest extends TaxCalculatorServiceIntegrationTest {

    @Autowired
    Database filebasedDb

    @Override
    Database getDatabase() {
        return filebasedDb
    }
}
