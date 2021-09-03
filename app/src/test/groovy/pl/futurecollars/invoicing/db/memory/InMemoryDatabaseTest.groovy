package pl.futurecollars.invoicing.db.memory

import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.model.InvoiceEntry
import spock.lang.Specification
import java.time.LocalDateTime

class InMemoryDatabaseTest extends Specification {

    Company from = new Company(1L, "address1");
    Company to = new Company(2L, "address2");
    InMemoryDatabase inMemoryDatabase;
    Invoice invoice;

    def setup() {
        inMemoryDatabase = new InMemoryDatabase();
        invoice = new Invoice(LocalDateTime.now(), from, to, new ArrayList<InvoiceEntry>());
    }

    def "should save invoice to database"() {
        when: "we ask database to save invoice"
        inMemoryDatabase.save(invoice);

        then: "invoice is in database"
        inMemoryDatabase.getDatabase().get(invoice.getId()) == invoice;
    }

    def "should return invoice after saving it to database"() {
        when: "we ask database to save invoice"
        Invoice returnedInvoice = inMemoryDatabase.save(invoice)

        then: "saved invoice is returned"
        returnedInvoice == invoice
    }

    def "should return null if invoice is null"() {
        expect:
        inMemoryDatabase.save(null) == null

    }

    def "should return invoice by id"() {
        given: "an example invoice in database"
        inMemoryDatabase.getDatabase().put(invoice.getId(), invoice)

        when: "we ask for an invoice by id"
        def returnedInvoice = inMemoryDatabase.getById(invoice.getId())

        then: "invoice is returned"
        returnedInvoice == invoice
    }

    def "should throw exception when there is no invoice with this id"() {
        when: "we ask database for an nonexistent invoice"
        def returnedInvoice = inMemoryDatabase.getById(UUID.randomUUID());

        then: "exception is thrwon"
        thrown(NoSuchElementException)
    }

    def "should return list of all invoices from database"() {
        given: "an example invoice in database"
        inMemoryDatabase.getDatabase().put(invoice.getId(), invoice)

        when: "we ask database for list of all invoices"
        ArrayList<Invoice> invoiceList = inMemoryDatabase.getAll();

        then: "list of all invoices is returned"
        invoiceList == [invoice]
    }

    def "should return empty list if there are no invoices in database"() {
        expect: "empty database produces empty list of invoices"
        inMemoryDatabase.getAll().isEmpty()
    }

    def "should update invoice in database"() {
        given: "an example invoice in database"
        inMemoryDatabase.getDatabase().put(invoice.getId(), invoice)

        and: "an updated invoice"
        Company updatedFromCompany = new Company(3L, "address3")
        Company updatedToCompany = new Company(4L, "address4")
        Invoice updatedInvoice = new Invoice(LocalDateTime.now(), updatedFromCompany, updatedToCompany, new ArrayList<InvoiceEntry>())
        updatedInvoice.setId(invoice.getId())

        when: "we ask database to update invoice"
        inMemoryDatabase.update(updatedInvoice)

        then: "invoice is updated"
        inMemoryDatabase.getDatabase().get(invoice.getId()) == updatedInvoice
    }

    def "should return null if updated invoice is null"() {
        expect:
        inMemoryDatabase.update(null) == null
    }

    def "should delete invoice from database"() {
        given: "an example invoice in database"
        inMemoryDatabase.getDatabase().put(invoice.getId(), invoice)

        when: "we ask database to delete invoice"
        boolean wasDeleted = inMemoryDatabase.delete(invoice.getId())

        then: "invoice is deleted"
        inMemoryDatabase.database.isEmpty()
        wasDeleted
    }

    def "should throw exception if when deleting nonexistent invoice"() {
        when: "we ask database to delete nonexistent invoice"
        def returnedInvoice = inMemoryDatabase.delete(UUID.randomUUID())

        then: "exception is thrown"
        thrown(NoSuchElementException)
    }
}
