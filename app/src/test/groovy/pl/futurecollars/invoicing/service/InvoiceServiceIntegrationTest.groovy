package pl.futurecollars.invoicing.service

import org.springframework.boot.test.context.SpringBootTest
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase
import pl.futurecollars.invoicing.fixtures.InvoiceFixture
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification
import java.util.function.Predicate

@SpringBootTest
class InvoiceServiceIntegrationTest extends Specification {

    Database inMemoryDatabase;
    InvoiceService invoiceService;
    Invoice invoice = InvoiceFixture.getInvoice()
    def setup() {
        inMemoryDatabase = new InMemoryDatabase()
        invoiceService = new InvoiceService(inMemoryDatabase)
    }

    def "should save invoice to database"() {
        when: "we ask invoice service to save invoice"
        invoiceService.saveInvoice(invoice)

        then: "invoice is saved in database"
        inMemoryDatabase.getById(invoice.getId()) == invoice
    }

    def "should get invoice by id from database"() {
        given: "invoice saved to database"
        inMemoryDatabase.save(invoice)

        when: "we ask invoice service for invoice by id"
        Invoice returnedInvoice = invoiceService.getById(invoice.getId())

        then: "invoice is returned"
        returnedInvoice == invoice
    }

    def "should throw exception when id is not used"() {
        when:"we ask invoice service to get nonexistent invoice"
        invoiceService.getById(UUID.randomUUID())

        then:"exception is thrown"
        thrown(NoSuchElementException)
    }

    def "should get list of invoices"() {
        given: "invoice saved to database"
        invoiceService.saveInvoice(invoice)

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
        inMemoryDatabase.save(invoice)
        Predicate<Invoice> invoicePredicate = (Invoice invoice) -> invoice.getFrom().getTaxIdentificationNumber() == 1L

        when: "we ask invoice service to filter the database based on Predicate"
        List<Invoice> invoiceList = invoiceService.filter(invoicePredicate)

        then: "database is filtered"
        invoiceList == [invoice]
    }

    def "should update invoice in database"() {
        given: "invoice saved to database"
        inMemoryDatabase.save(invoice)

        and: "updated invoice"
        Invoice updatedInvoice = InvoiceFixture.getInvoice()
        updatedInvoice.setId(invoice.getId())

        when: "we ask invoice service to update database"
        invoiceService.updateInvoice(updatedInvoice)

        then: "database is updated"
        inMemoryDatabase.getById(updatedInvoice.getId()) == updatedInvoice

    }

    def "should delete invoice from database"() {
        given: "invoice saved to database"
        inMemoryDatabase.save(invoice)

        when: "we ask invoice service to delete invoice"
        invoiceService.deleteInvoice(invoice.getId())

        then: "database is empty"
        inMemoryDatabase.getAll().isEmpty()
    }

    def "should throw exception when deleting nonexistent invoice"() {
        when:"we ask invoice service to delete nonexistent invoice"
        invoiceService.deleteInvoice(UUID.randomUUID())

        then: "exception is thrown"
        thrown(NoSuchElementException)
    }
}
