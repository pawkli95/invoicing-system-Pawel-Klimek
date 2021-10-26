package pl.futurecollars.invoicing.db

import lombok.extern.slf4j.Slf4j
import pl.futurecollars.invoicing.fixtures.InvoiceFixture
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Shared
import spock.lang.Specification

@Slf4j
abstract class DatabaseTest extends Specification {


    Database<Invoice> database

    Invoice invoice = InvoiceFixture.getInvoice()

    abstract Database getDatabase();

    def setup() {
        database = getDatabase()
    }

    def cleanup() {
        clearDatabase()
    }


    def "should save invoice to database"() {
        when: "we ask database to save invoice"
        Invoice returnedInvoice = database.save(invoice)
        Invoice response = database.getById(returnedInvoice.getId())

        then: "invoice is saved to database"
        response.getId() == returnedInvoice.getId()
        response.getBuyer().getTaxIdentificationNumber() == returnedInvoice.getBuyer().getTaxIdentificationNumber()
        response.getSeller().getTaxIdentificationNumber() == returnedInvoice.getSeller().getTaxIdentificationNumber()
    }

    def "should return null if saved invoice is null"() {
        expect:
        database.save(null) == null
    }


    def "should get invoice by id"() {
        given: "invoice saved in database"
        Invoice returnedInvoice = database.save(invoice)

        when: "we ask database to get invoice by id"
        def response = database.getById(returnedInvoice.getId())

        then: "invoice is returned"
        returnedInvoice.getId() == response.getId()
        response.getBuyer().getTaxIdentificationNumber() == returnedInvoice.getBuyer().getTaxIdentificationNumber()
        response.getSeller().getTaxIdentificationNumber() == returnedInvoice.getSeller().getTaxIdentificationNumber()
    }

    def "should throw exception when asking for nonexistent invoice"() {
        when: "we ask database for nonexistent invoice"
        database.getById(UUID.randomUUID())

        then: "exception is thrown"
        thrown(NoSuchElementException)
    }


    def "should get list of saved invoices"() {
        given: "invoice saved to database"
        Invoice returnedInvoice = database.save(invoice)

        when: "we ask database for a list of all invoices"
        def invoiceList = database.getAll()

        then: "list is returned"
        invoiceList.size() == 1
        invoiceList.get(0).getId() == returnedInvoice.getId()
        invoiceList.get(0).getBuyer().getTaxIdentificationNumber() == returnedInvoice.getBuyer().getTaxIdentificationNumber()
        invoiceList.get(0).getSeller().getTaxIdentificationNumber() == returnedInvoice.getSeller().getTaxIdentificationNumber()
    }

    def "should get empty list when no invoices were saved"() {
        expect:
        database.getAll().isEmpty()
    }


    def "should update invoice in database"() {
        given: "invoice saved to database"
        Invoice returnedInvoice = database.save(invoice)

        and: "updated invoice"
        Invoice updatedInvoice = InvoiceFixture.getInvoice()
        updatedInvoice.setId(returnedInvoice.getId())

        when: "we ask database to update invoice"
        database.update(updatedInvoice)
        Invoice response = database.getById(returnedInvoice.getId())

        then: "invoice is updated"
        response.getId() == updatedInvoice.getId()
        response.getBuyer().getTaxIdentificationNumber() == updatedInvoice.getBuyer().getTaxIdentificationNumber()
        response.getSeller().getTaxIdentificationNumber() == updatedInvoice.getSeller().getTaxIdentificationNumber()

    }

    def "should throw exception when updating nonexistent invoice"() {
        given:
        Invoice invoice = InvoiceFixture.getInvoice()

        when:
        database.update(invoice)

        then:
        thrown(NoSuchElementException)
    }

    def "should delete invoice"() {
        given: "invoice saved to database"
        Invoice returnedInvoice = database.save(invoice)

        when: "we ask database to delete invoice"
        database.delete(returnedInvoice.getId())

        then: "invoice is deleted"
        database.getAll().isEmpty()
    }

    def "should throw exception when deleting nonexistent invoice"() {
        when: "we ask database to delete nonexistent invoice"
        database.delete(UUID.randomUUID())

        then: "exception is thrown"
        thrown(NoSuchElementException)
    }

    def clearDatabase() {
        def list = database.getAll()
        if(!list.isEmpty()) {
            for(Invoice i : list) {
                database.delete(i.getId())
            }
        }
    }
}
