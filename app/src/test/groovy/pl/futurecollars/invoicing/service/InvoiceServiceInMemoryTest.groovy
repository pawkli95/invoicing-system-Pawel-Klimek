package pl.futurecollars.invoicing.service

import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase

class InvoiceServiceInMemoryTest extends InvoiceServiceAbstractIntegrationTest{

    Database getDatabase() {
        return new InMemoryDatabase();
    }
}
