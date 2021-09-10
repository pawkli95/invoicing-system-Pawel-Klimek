package pl.futurecollars.invoicing.fixtures

import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.model.InvoiceEntry

import java.time.LocalDateTime

class InvoiceFixture {

    static Company from = new Company(1L, "address1")
    static Company to = new Company(2L, "address2")
    static Invoice getInvoice() {
        return new Invoice(LocalDateTime.now(), from, to, new ArrayList<InvoiceEntry>())
    }
}
