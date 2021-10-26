package pl.futurecollars.invoicing.service.taxCalculatorService

import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase

class TaxCalculatorServiceInMemoryTest extends TaxCalculatorServiceIntegrationTest {

    @Override
    Database getDatabase() {
        return new InMemoryDatabase();
    }
}
