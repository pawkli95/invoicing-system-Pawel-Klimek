package pl.futurecollars.invoicing.service

import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase
import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.model.InvoiceEntry
import spock.lang.Specification
import java.time.LocalDateTime
import java.util.function.Predicate

class InvoiceServiceIntegrationTest extends Specification {

    Database database;
    InvoiceService invoiceService;
    Invoice invoice;
    Company from = new Company(1L, "adress")
    Company to = new Company(2L, "address")

    def setup() {
        database = new InMemoryDatabase()
        invoiceService = new InvoiceService(database)
        invoice = new Invoice(LocalDateTime.now(), from, to, new ArrayList<InvoiceEntry>())
    }

    def "should save invoice to database"() {
        when: "we ask invoice service to save invoice"
        invoiceService.saveInvoice(invoice)

        then: "invoice is saved in database"
        database.getById(invoice.getId()) == invoice
    }

    def "should get invoice by id from database"() {
        given: "invoice saved to database"
        database.save(invoice)

        when: "we ask invoice service for invoice by id"
        Invoice returnedInvoice = invoiceService.getById(invoice.getId())

        then: "invoice is returned"
        returnedInvoice == invoice
    }

    def "should throw exception when id is not used"() {
        when:"we ask invoice service to get nonexistent invoice"
        def returnedInvoice = invoiceService.getById(UUID.randomUUID())

        then:"exception is thrown"
        thrown(NoSuchElementException)
    }

    def "should get list of invoices"() {
        given: "invoice saved to database"
        database.save(invoice)

        when: "we ask invoice service for a list of all invoices"
        List<Invoice> invoiceList = invoiceService.getAll()

        then: "list od invoices is returned"
        invoiceList == [invoice]
    }

    def "should get an empty list of invoices"() {
        expect:
        invoiceService.getAll().isEmpty()
    }

    def "should filter database"() {
        given: "a list of invoices and a Predicate"
        database.save(invoice)
        Predicate<Invoice> invoicePredicate = (Invoice invoice) -> invoice.getFrom().getTaxIdentificationNumber() == 1L

        when: "we ask invoice service to filter the database based on Predicate"
        List<Invoice> invoiceList = invoiceService.filter(invoicePredicate)

        then: "database is filtered"
        invoiceList == [invoice]
    }

    def "should update invoice in database"() {
        given: "invoice saved to database"
        database.save(invoice)

        and: "updated invoice"
        Invoice updatedInvoice = new Invoice(LocalDateTime.now(), from, to, new ArrayList<InvoiceEntry>())
        updatedInvoice.setId(invoice.getId())

        when: "we ask invoice service to update database"
        invoiceService.updateInvoice(updatedInvoice)

        then: "database is updated"
        database.getById(updatedInvoice.getId()) == updatedInvoice

    }

    def "should delete invoice from database"() {
        given: "invoice saved to database"
        database.save(invoice)

        when: "we ask invoice service to delete invoice"
        invoiceService.deleteInvoice(invoice.getId())

        then: "database is empty"
        database.getAll().isEmpty()
    }

    def "should throw exception when deleting nonexistent invoice"() {
        when:"we ask invoice service to delete nonexistent invoice"
        invoiceService.deleteInvoice(UUID.randomUUID())

        then: "exception is thrown"
        thrown(NoSuchElementException)
    }
}
