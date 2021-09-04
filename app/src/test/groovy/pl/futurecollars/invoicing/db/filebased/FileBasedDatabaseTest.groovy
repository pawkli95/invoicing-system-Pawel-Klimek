package pl.futurecollars.invoicing.db.filebased

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.apache.commons.io.FileUtils
import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.model.InvoiceEntry
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Specification
import java.time.LocalDateTime

class FileBasedDatabaseTest extends Specification {

    JsonService jsonService
    FileBasedDatabase fileBasedDatabase
    Company from = new Company(1L, "address1")
    Company to = new Company(2L, "address2")
    Invoice invoice = new Invoice(LocalDateTime.now(), from, to, new ArrayList<>())
    ObjectMapper mapper = new ObjectMapper();
    String jsonString;
    String jsonTest1 = "invoices.json"
    String jsonTest2 = "jsonTest2.txt"
    String idTest1 = "ids.txt"
    String idTest2 = "idTest2.txt"

    def setup() {
        jsonService = new JsonService()
        fileBasedDatabase = new FileBasedDatabase(jsonService)
        fileBasedDatabase.getJsonFileService().eraseFile()
        fileBasedDatabase.getIdsFileService().eraseFile()
        FileUtils.write(new File(jsonTest2), "", "UTF-8", false)
        FileUtils.write(new File(idTest2), "", "UTF-8", false)
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        jsonString = mapper.writeValueAsString(invoice)
    }

    def cleaup() {
        fileBasedDatabase.getJsonFileService().eraseFile()
        fileBasedDatabase.getIdsFileService().eraseFile()
    }

    def "should save invoice to file"() {
        given:
        FileUtils.write(new File(jsonTest2), jsonString + "\n", "UTF-8")

        when:
        fileBasedDatabase.save(invoice)

        then:
        FileUtils.contentEquals(new File(jsonTest1), new File(jsonTest2))
    }

    def "should get invoice by id"() {
        given:
        FileUtils.write(new File(jsonTest1), jsonString + "\n", "UtF-8");
        FileUtils.write(new File(idTest1),invoice.getId().toString() + "\n", "UTF-8");

        when:
        Invoice returnedInvoice = fileBasedDatabase.getById(invoice.getId())

        then:
        returnedInvoice == invoice
    }

    def "should throw NoSuchElementException when asking for nonexistent invoice"() {
        when:
        fileBasedDatabase.getById(UUID.randomUUID())

        then:
        thrown(NoSuchElementException)
    }

    def "should get list of all invoices"() {
        given:
        FileUtils.write(new File(jsonTest1), jsonString, "UTF-8")

        when:
        def returnedList = fileBasedDatabase.getAll()

        then:
        returnedList == [invoice]
    }

    def "should get empty list if no invoices were added"() {
        when:
        def returnedList = fileBasedDatabase.getAll()

        then:
        returnedList.isEmpty()
    }

    def "should update invoice"() {
        given:
        FileUtils.write(new File(jsonTest1), jsonString + "\n", "UTF-8")
        FileUtils.write(new File(idTest1), invoice.getId().toString() + "\n", "UTF-8")
        Invoice updatedInvoice = new Invoice(LocalDateTime.now(), from, to, new ArrayList<InvoiceEntry>())
        updatedInvoice.setId(invoice.getId())
        String updatedJsonString = mapper.writeValueAsString(updatedInvoice)
        FileUtils.write(new File(jsonTest2), updatedJsonString + "\n", "UTF-8")

        when:
        fileBasedDatabase.update(updatedInvoice)

        then:
        FileUtils.contentEquals(new File(jsonTest1), new File(jsonTest2))
    }

    def "should delete invoice from database"() {
        given:
        FileUtils.write(new File(jsonTest1), jsonString, "UTF-8")
        FileUtils.write(new File(idTest1), invoice.getId().toString(), "UTF-8")

        when:
        fileBasedDatabase.delete(invoice.getId())

        then:
        File jsonTest1 = new File(jsonTest1)
        jsonTest1.length() == 0
        File idTest1 = new File(idTest1)
        idTest1.length() == 0
    }

    def "should throw NoSuchElementException when deleting nonexistent invoice"() {
        when:
        fileBasedDatabase.delete(UUID.randomUUID())

        then:
        thrown(NoSuchElementException)
    }
}
