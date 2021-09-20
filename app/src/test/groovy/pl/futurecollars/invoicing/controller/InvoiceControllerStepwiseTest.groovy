package pl.futurecollars.invoicing.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoicing.controller.testConfig.FileBasedDatabaseTestConfig
import pl.futurecollars.invoicing.fixtures.InvoiceFixture
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
@Stepwise
@Import(FileBasedDatabaseTestConfig.class)
class InvoiceControllerStepwiseTest extends Specification {

    @Autowired
    private MockMvc mockMvc

    @Autowired
    JsonService<Invoice> jsonInvoiceService

    JsonService<Invoice[]> jsonInvoiceListService = new JsonService<>()

    @Shared Invoice invoice = InvoiceFixture.getInvoice()
    @Shared Invoice updatedInvoice = InvoiceFixture.getInvoice()

    def "should return empty list"() {
        given:
        deleteAllInvoices()

        when:
        def response = mockMvc.perform(get("/api/invoices"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        response == "[]"

    }

    def "should save invoice"() {
        given:
        String jsonString = jsonInvoiceService.toJsonString(invoice)

        when:
        def response = mockMvc.perform(post("/api/invoices").contentType(MediaType.APPLICATION_JSON)
                .content(jsonString))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        jsonInvoiceService.toObject(response, Invoice.class) == invoice
    }

    def "should return invoice by id"() {
        given:
        UUID id = invoice.getId()

        when:
        def response = mockMvc.perform(get("/api/invoices/" + id.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        jsonInvoiceService.toObject(response, Invoice.class) == invoice

    }

    def "should return list of invoices"() {
        when:
        def response = mockMvc.perform(get("/api/invoices"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        def invoices = jsonInvoiceListService.toObject(response, Invoice[])
        invoices.size() == 1
        invoices[0] == invoice
    }

    def "should return 404 NotFound status when getting invoice by id which doesn't exist"() {
        given:
        UUID invalidId = UUID.randomUUID()

        when:
        def response = mockMvc.perform(get("/api/invoices/" + invalidId))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        response.isEmpty()
    }

    def "should filter the database"() {
        given:
        UUID sellerId = invoice.getFrom().getId()
        UUID buyerId = invoice.getTo().getId()

        when:
        def response = mockMvc.perform(get("/api/invoices").queryParam("sellerId", sellerId.toString())
                .queryParam("buyerId", buyerId.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        def invoices = jsonInvoiceListService.toObject(response, Invoice[])
        invoices.size() == 1
        invoices[0] == invoice
    }

    def "should update the invoice"() {
        given:
        updatedInvoice.setId(invoice.getId())
        String updatedJsonString = jsonInvoiceService.toJsonString(updatedInvoice)

        expect:
        mockMvc.perform(put("/api/invoices/").contentType(MediaType.APPLICATION_JSON).content(updatedJsonString))
        .andExpect(status().isOk())
    }

    def "should return updated invoice by id"() {
        given:
        UUID id = invoice.getId()

        when:
        def response = mockMvc.perform(get("/api/invoices/" + id.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        jsonInvoiceService.toObject(response, Invoice.class) == updatedInvoice
    }

    def "should return 404 Not Found when trying to update nonexistent invoice"() {
        given:
        Invoice invalidInvoice = InvoiceFixture.getInvoice()
        String jsonString = jsonInvoiceService.toJsonString(invalidInvoice)

        when:
        def response = mockMvc
                    .perform(put("/api/invoices").contentType(MediaType.APPLICATION_JSON).content(jsonString))
                    .andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse()
                    .getContentAsString()

        then:
        response.isEmpty()
    }

    def "should delete invoice by id"() {
        given:
        UUID id = invoice.getId()

        expect:
        mockMvc.perform(delete("/api/invoices/" + id.toString())).andExpect(status().isAccepted())

        and:
        mockMvc.perform(delete("/api/invoices/" + id.toString())).andExpect(status().isNotFound())

        and:
        mockMvc.perform(get("/api/invoices/" + id.toString())).andExpect(status().isNotFound())
    }

    private List<Invoice> getAllInvoices() {
        def list = mockMvc.perform(get("/api/invoices")).andReturn().getResponse().getContentAsString()
        return jsonInvoiceListService.toObject(list, Invoice[])
    }

    private void deleteInvoice(UUID id) {
        mockMvc.perform(delete("/api/invoices/" + id.toString()))
    }

    private void deleteAllInvoices() {
        List<Invoice> invoiceList = getAllInvoices()
        for(Invoice invoice : invoiceList) {
            UUID id = invoice.getId()
            deleteInvoice(id)
        }
    }
}
