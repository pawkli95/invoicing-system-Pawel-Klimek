package pl.futurecollars.invoicing.service

import org.checkerframework.checker.units.qual.C
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.model.InvoiceEntry
import spock.lang.Specification

import java.time.LocalDateTime
import java.util.function.Predicate

class InvoiceServiceUnitTest extends Specification {

    Database database;
    Invoice invoice;
    InvoiceService invoiceService;

    def setup() {
        database = Mock()
        Company from = new Company(1L, 1L, "")
        Company to = new Company(2L, 2L, "")
        invoice = new Invoice(LocalDateTime.now(), from , to, new ArrayList<InvoiceEntry>())
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
        database.getById(invoice.getId()) >> Optional.ofNullable(invoice)

        when: "we ask invoice service for invoice by id"
        Optional<Invoice> invoiceOptional = invoiceService.getById(invoice.getId())

        then: "invoice is returned"
        invoiceOptional.get() == invoice
    }

    def "calling getAll() should delegate to database getAll()"() {
        when: "we ask invoice service for list of all invoices"
        invoiceService.getAll()

        then: "database getAll() is called"
        1 * database.getAll()
    }

    def "should filter database" () {
        given: "a list of invoices and a Predicate"
        database.getAll() >> List.of(invoice)
        Predicate<Invoice> invoicePredicate = (Invoice invoice) -> invoice.getFrom().getId() == 1L

        when: "we ask invoice service to filter the database based on Predicate"
        List<Invoice> invoiceList = invoiceService.filter(invoicePredicate)

        then: "database is filtered"
        invoiceList == [invoice]
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
}
