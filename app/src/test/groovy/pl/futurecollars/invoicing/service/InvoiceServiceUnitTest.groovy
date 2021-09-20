package pl.futurecollars.invoicing.service

import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.fixtures.InvoiceFixture
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification
import java.util.function.Predicate

class InvoiceServiceUnitTest extends Specification {

    Database database;
    Invoice invoice = InvoiceFixture.getInvoice()
    InvoiceService invoiceService;

    def setup() {
        database = Mock()
        invoiceService = new InvoiceService(database)
    }

    def "calling saveInvoice() should delegate to database save()"() {
        when: "we ask invoice service to save invoice"
        invoiceService.saveInvoice(invoice)

        then: "database save() is called"
        1 * database.save(invoice)
    }

    def "should get an invoice from database by id"() {
        given: "an invoice returned by database"
        database.getById(invoice.getId()) >> invoice

        when: "we ask invoice service for invoice by id"
        Invoice returnedInvoice = invoiceService.getById(invoice.getId())

        then: "invoice is returned"
        returnedInvoice == invoice
    }

    def "calling getAll() should delegate to database getAll()"() {
        when: "we ask invoice service for list of all invoices"
        invoiceService.getAll()

        then: "database getAll() is called"
        1 * database.getAll()
    }

    def "calling updateInvoice() should delegate to database update()"() {
        when: "we ask invoice service to update invoice"
        invoiceService.updateInvoice(invoice)

        then: "invoice is updated"
        1 * database.update(invoice)
    }

    def "calling deleteInvoice() should delegate to database delete()"() {
        when: "we ask invoice service to delete invoice"
        invoiceService.deleteInvoice(null)

        then: "database delete() is called"
        1 * database.delete(null)
    }

    def "should filter database"() {
        given: "a list of invoices and a Predicate"
        database.getAll() >> [invoice]
        long taxNumber = invoice.getFrom().getTaxIdentificationNumber()
        Predicate<Invoice> invoicePredicate = (Invoice invoice) -> invoice.getFrom().getTaxIdentificationNumber() == taxNumber

        when: "we ask invoice service to filter the database based on Predicate"
        List<Invoice> invoiceList = invoiceService.filter(invoicePredicate)

        then: "database is filtered"
        invoiceList == [invoice]
    }
}
