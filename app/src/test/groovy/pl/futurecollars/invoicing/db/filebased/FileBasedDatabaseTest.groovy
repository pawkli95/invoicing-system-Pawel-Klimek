package pl.futurecollars.invoicing.db.filebased

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.apache.commons.io.FileUtils
import pl.futurecollars.invoicing.config.FilePathConfig
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
    String jsonString
    String jsonTest = "invoicesTest.json"
    String idTest = "idsTest.txt"

    def setup() {
        jsonService = new JsonService()
        fileBasedDatabase = new FileBasedDatabase(jsonService)
        fileBasedDatabase.getJsonFileService().eraseFile()
        fileBasedDatabase.getIdsFileService().eraseFile()
        FileUtils.write(new File(jsonTest), "", "UTF-8", false)
        FileUtils.write(new File(idTest), "", "UTF-8", false)
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        jsonString = mapper.writeValueAsString(invoice)
    }

    def "should save invoice to file"() {
        given:
        FileUtils.write(new File(jsonTest), jsonString + "\n", "UTF-8")

        when:
        fileBasedDatabase.save(invoice)

        then:
        FileUtils.contentEquals(new File(FilePathConfig.JSON_FILE), new File(jsonTest))
    }

    def "should get invoice by id"() {
        given:
        FileUtils.write(new File(FilePathConfig.JSON_FILE), jsonString, "UTF-8");
        FileUtils.write(new File(FilePathConfig.IDS_FILE),invoice.getId().toString(), "UTF-8");

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
        FileUtils.write(new File(FilePathConfig.JSON_FILE), jsonString, "UTF-8")

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
        FileUtils.write(new File(FilePathConfig.JSON_FILE), jsonString + "\n", "UTF-8")
        FileUtils.write(new File(FilePathConfig.IDS_FILE), invoice.getId().toString() + "\n", "UTF-8")
        Invoice updatedInvoice = new Invoice(LocalDateTime.now(), from, to, new ArrayList<InvoiceEntry>())
        updatedInvoice.setId(invoice.getId())
        String updatedJsonString = mapper.writeValueAsString(updatedInvoice)
        FileUtils.write(new File(jsonTest), updatedJsonString + "\n", "UTF-8")

        when:
        fileBasedDatabase.update(updatedInvoice)

        then:
        FileUtils.contentEquals(new File(FilePathConfig.JSON_FILE), new File(jsonTest))
    }

    def "should delete invoice from database"() {
        given:
        FileUtils.write(new File(FilePathConfig.JSON_FILE), jsonString, "UTF-8")
        FileUtils.write(new File(FilePathConfig.IDS_FILE), invoice.getId().toString(), "UTF-8")

        when:
        fileBasedDatabase.delete(invoice.getId())

        then:
       new File(FilePathConfig.JSON_FILE).length() == 0
    }

    def "should throw NoSuchElementException when deleting nonexistent invoice"() {
        when:
        fileBasedDatabase.delete(UUID.randomUUID())

        then:
        thrown(NoSuchElementException)
    }
}
