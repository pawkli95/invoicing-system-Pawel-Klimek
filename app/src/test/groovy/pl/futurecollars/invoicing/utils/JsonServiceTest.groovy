package pl.futurecollars.invoicing.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification

import java.time.LocalDateTime

class JsonServiceTest extends Specification {

    JsonService jsonService = new JsonService()
    Company from = new Company(1L, "address1");
    Company to = new Company(2L, "address2");
    Invoice invoice = new Invoice(LocalDateTime.now(), from, to, new ArrayList<>());
    ObjectMapper mapper = new ObjectMapper();

    def setup() {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
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
        Invoice invoiceDeserialized = jsonService.toObject(jsonString)

        then:
        invoiceDeserialized == invoice
    }
}
