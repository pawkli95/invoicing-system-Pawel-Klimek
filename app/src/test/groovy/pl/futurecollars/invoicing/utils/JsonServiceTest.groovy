package pl.futurecollars.invoicing.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pl.futurecollars.invoicing.fixtures.InvoiceFixture
import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification

import java.time.LocalDateTime

@SpringBootTest
class JsonServiceTest extends Specification {

    @Autowired
    JsonService<Invoice> jsonService
    Invoice invoice = InvoiceFixture.getInvoice()
    ObjectMapper mapper = new ObjectMapper();

    def setup() {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    def "should serialize invoice to json string"() {
        given:
        String jsonString1 = mapper.writeValueAsString(invoice);

        when:
        String jsonString2 = jsonService.toJsonString(invoice)

        then:
        jsonString1 == jsonString2
    }

    def "should deserialize json string to invoice"() {
        given:
        String jsonString = mapper.writeValueAsString(invoice)

        when:
        Invoice invoiceDeserialized = jsonService.toObject(jsonString, Invoice.class)

        then:
        invoiceDeserialized == invoice
    }
}
