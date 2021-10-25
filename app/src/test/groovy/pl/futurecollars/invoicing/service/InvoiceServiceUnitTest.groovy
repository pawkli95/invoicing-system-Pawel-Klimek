package pl.futurecollars.invoicing.service

import org.mapstruct.factory.Mappers
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.dto.InvoiceDto
import pl.futurecollars.invoicing.dto.mappers.InvoiceMapper
import pl.futurecollars.invoicing.fixtures.InvoiceFixture
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification
import java.util.function.Predicate

class InvoiceServiceUnitTest extends Specification {

    Database database;
    InvoiceDto invoiceDto = InvoiceFixture.getInvoiceDto()
    InvoiceService invoiceService
    InvoiceMapper invoiceMapper = Mappers.getMapper(InvoiceMapper.class)

    def setup() {
        database = Mock()
        invoiceService = new InvoiceService(database, invoiceMapper)
    }

    def "calling saveInvoice() should delegate to database save()"() {
        when: "we ask invoice service to save invoice"
        invoiceService.saveInvoice(invoiceDto)

        then: "database save() is called"
        1 * database.save(invoiceMapper.toEntity(invoiceDto))
    }

    def "should get an invoice from database by id"() {
        given: "an invoice returned by database"
        Invoice invoice = invoiceMapper.toEntity(invoiceDto)
        database.getById(invoice.getId()) >> invoice

        when: "we ask invoice service for invoice by id"
        InvoiceDto returnedInvoiceDto = invoiceService.getById(invoiceDto.getId())

        then: "invoice is returned"
        returnedInvoiceDto == invoiceDto
    }

    def "calling getAll() should return list of InvoiceDto"() {
        given:
        database.getAll() >> List.of(invoiceMapper.toEntity(invoiceDto))

        when: "we ask invoice service for list of all invoices"
        def list = invoiceService.getAll()

        then: "database getAll() is called"
        list == [invoiceDto]
    }

    def "calling updateInvoice() should delegate to database update()"() {
        when: "we ask invoice service to update invoice"
        invoiceService.updateInvoice(invoiceDto)

        then: "invoice is updated"
        1 * database.update(invoiceMapper.toEntity(invoiceDto))
    }

    def "calling deleteInvoice() should delegate to database delete()"() {
        when: "we ask invoice service to delete invoice"
        invoiceService.deleteInvoice(null)

        then: "database delete() is called"
        1 * database.delete(null)
    }

    def "should filter database"() {
        given: "a list of invoices and a Predicate"
        Invoice invoice = invoiceMapper.toEntity(invoiceDto)
        database.getAll() >> [invoice]
        String taxId = invoice.getSeller().getTaxIdentificationNumber()
        Predicate<Invoice> invoicePredicate = (Invoice i) -> i.getSeller().getTaxIdentificationNumber().equals(taxId)

        when: "we ask invoice service to filter the database based on Predicate"
        List<InvoiceDto> invoiceList = invoiceService.filter(invoicePredicate)

        then: "database is filtered"
        invoiceList == [invoiceDto]
    }
}
