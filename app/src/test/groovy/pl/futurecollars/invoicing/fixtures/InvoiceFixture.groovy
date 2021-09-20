package pl.futurecollars.invoicing.fixtures

import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.model.InvoiceEntry

import java.time.LocalDateTime

class InvoiceFixture {

    static Invoice getInvoice() {
        return new Invoice(LocalDateTime.now(), CompanyFixture.getCompany(), CompanyFixture.getCompany(), new ArrayList<InvoiceEntry>())
    }
}
