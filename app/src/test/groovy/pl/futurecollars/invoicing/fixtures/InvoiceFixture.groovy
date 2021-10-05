package pl.futurecollars.invoicing.fixtures


import pl.futurecollars.invoicing.model.Invoice

import java.time.LocalDateTime

class InvoiceFixture {

    static Invoice getInvoice() {
        return new Invoice(LocalDateTime.now(),CompanyFixture.getCompany(), CompanyFixture.getCompany(),
        InvoiceEntryFixture.getInvoiceEntryListWithPersonalCar(4))
    }
}
