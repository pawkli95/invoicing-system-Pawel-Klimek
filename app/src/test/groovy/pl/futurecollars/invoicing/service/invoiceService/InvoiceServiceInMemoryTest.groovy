package pl.futurecollars.invoicing.service.invoiceService

import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase
import pl.futurecollars.invoicing.service.invoiceService.InvoiceServiceAbstractIntegrationTest

class InvoiceServiceInMemoryTest extends InvoiceServiceAbstractIntegrationTest{

    Database getDatabase() {
        return new InMemoryDatabase();
    }
}
