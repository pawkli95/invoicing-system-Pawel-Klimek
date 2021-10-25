package pl.futurecollars.invoicing.service

import org.mapstruct.factory.Mappers
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.dto.InvoiceDto
import pl.futurecollars.invoicing.dto.mappers.InvoiceMapper
import pl.futurecollars.invoicing.fixtures.InvoiceFixture
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification
import java.util.function.Predicate

abstract class InvoiceServiceAbstractIntegrationTest extends Specification{

    Database database

    InvoiceService invoiceService

    InvoiceDto invoiceDto = InvoiceFixture.getInvoiceDto()

    InvoiceMapper invoiceMapper = Mappers.getMapper(InvoiceMapper.class)

    abstract Database getDatabase();

    def setup() {
        database = getDatabase()
        invoiceService = new InvoiceService(database, invoiceMapper)
        clearDatabase()
    }

    def "should save invoice to database"() {
        when: "we ask invoice service to save invoice"
        invoiceService.saveInvoice(invoiceDto)

        then: "invoice is saved in database"
        database.getById(invoiceDto.getId()) == invoiceMapper.toEntity(invoiceDto)
    }

    def "should get invoice by id from database"() {
        given: "invoice saved to database"
        Invoice invoice = invoiceMapper.toEntity(invoiceDto)
        database.save(invoice)

        when: "we ask invoice service for invoice by id"
        InvoiceDto returnedInvoice = invoiceService.getById(invoice.getId())

        then: "invoice is returned"
        returnedInvoice == invoiceDto
    }

    def "should throw exception when id is not used"() {
        when:"we ask invoice service to get nonexistent invoice"
        invoiceService.getById(UUID.randomUUID())

        then:"exception is thrown"
        thrown(NoSuchElementException)
    }

    def "should get list of invoices"() {
        given: "invoice saved to database"
        invoiceService.saveInvoice(invoiceDto)

        when: "we ask invoice service for a list of all invoices"
        List<InvoiceDto> invoiceList = invoiceService.getAll()

        then: "list od invoices is returned"
        invoiceList == [invoiceDto]
    }

    def "should get an empty list of invoices"() {
        expect:
        invoiceService.getAll().isEmpty()
    }

    def "should filter database"() {
        given: "a list of invoices and a Predicate"
        Invoice invoice = invoiceMapper.toEntity(invoiceDto)
        database.save(invoice)
        String taxId = invoice.getSeller().getTaxIdentificationNumber()
        Predicate<Invoice> invoicePredicate = (Invoice i) -> i.getSeller().getTaxIdentificationNumber().equals(taxId)

        when: "we ask invoice service to filter the database based on Predicate"
        List<InvoiceDto> invoiceList = invoiceService.filter(invoicePredicate)

        then: "database is filtered"
        invoiceList == [invoiceDto]
    }

    def "should update invoice in database"() {
        given: "invoice saved to database"
        Invoice invoice = invoiceMapper.toEntity(invoiceDto)
        database.save(invoice)

        and: "updated invoice"
        InvoiceDto updatedInvoice = InvoiceFixture.getInvoiceDto()
        updatedInvoice.setId(invoice.getId())

        when: "we ask invoice service to update database"
        invoiceService.updateInvoice(updatedInvoice)

        then: "database is updated"
        invoiceService.getById(updatedInvoice.getId()) == updatedInvoice

    }

    def "should throw exception when updating nonexistent invoice"() {
        when: "we ask invoice service to update nonexistent invoice"
        invoiceService.updateInvoice(invoiceDto)

        then: "exception is thrown"
        thrown(NoSuchElementException)
    }

    def "should delete invoice from database"() {
        given: "invoice saved to database"
        Invoice invoice = InvoiceFixture.getInvoice()
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

    def clearDatabase() {
        for(Invoice invoice : database.getAll()) {
            database.delete(invoice.getId())
        }
    }
}
