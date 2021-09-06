package pl.futurecollars.invoicing.db

import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.model.InvoiceEntry
import spock.lang.Specification
import java.time.LocalDateTime
import java.time.Month

abstract class DatabaseTest extends Specification {

    Database database;
    Company from = new Company(1L, "address1");
    Company to = new Company(2L, "address2");
    Invoice invoice = new Invoice(LocalDateTime.of(2021, Month.APRIL, 20, 19, 20), from ,to, new ArrayList<InvoiceEntry>());

    def "should save invoice to database"() {
        when: "we ask database to save invoice"
        database.save(invoice)

        then: "invoice is saved to database"
        database.getById(invoice.getId()) == invoice
    }

    def "should return invoice after saving it to database"() {
        when: "we ask database to save invoice"
        Invoice returnedInvoice = database.save(invoice)

        then: "saved invoice is returned"
        returnedInvoice == invoice
    }

    def "should return null if saved invoice is null"() {
        expect:
        database.save(null) == null
    }

    def "should get invoice by id"() {
        given: "invoice saved in database"
        database.save(invoice)

        when: "we ask database to get invoice by id"
        def returnedInvoice = database.getById(invoice.getId())

        then: "invoice is returned"
        returnedInvoice == invoice
    }

    def "should throw exception when asking for nonexistent invoice"() {
        when: "we ask database for nonexistent invoice"
        database.getById(UUID.randomUUID())

        then: "exception is thrown"
        thrown(NoSuchElementException)
    }

    def "should get list of saved invoices"() {
        given: "invoice saved to database"
        database.save(invoice)

        when: "we ask database for a list of all invoices"
        def invoiceList = database.getAll()

        then: "list is returned"
        invoiceList == [invoice]
    }

    def "should get empty list when no invoices were saved"() {
        expect:
        database.getAll().isEmpty()
    }

    def "should update invoice in database"() {
        given: "invoice saved to database"
        database.save(invoice)

        and: "updated invoice"
        Invoice updatedInvoice = new Invoice(LocalDateTime.now(), from, to, new ArrayList<InvoiceEntry>())
        updatedInvoice.setId(invoice.getId())

        when: "we ask database to update invoice"
        def returnedInvoice = database.update(updatedInvoice)

        then: "invoice is updated"
        database.getById(invoice.getId()) == updatedInvoice
    }

    def "should return invoice after updating it in database"() {
        given: "invoice saved to database"
        database.save(invoice)

        and:"updated invoice"
        Invoice updatedInvoice = new Invoice(LocalDateTime.now(), from, to, new ArrayList<InvoiceEntry>())
        updatedInvoice.setId(invoice.getId())

        when: "we ask database to update invoice"
        def returnedInvoice = database.update(updatedInvoice)

        then: "updated invoice is returned"
        returnedInvoice == updatedInvoice
    }

    def "should delete invoice"() {
        given: "invoice saved to database"
        database.save(invoice)

        when: "we ask database to delete invoice"
        database.delete(invoice.getId())

        then: "invoice is deleted"
        database.getAll().isEmpty()
    }

    def "should throw exception when deleting nonexistent invoice"() {
        when: "we ask database to delete nonexistent invoice"
        database.delete(UUID.randomUUID())

        then: "exception is thrown"
        thrown(NoSuchElementException)
    }
}
