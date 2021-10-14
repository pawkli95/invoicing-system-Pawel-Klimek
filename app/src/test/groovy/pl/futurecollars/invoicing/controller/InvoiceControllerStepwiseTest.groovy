package pl.futurecollars.invoicing.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.json.JacksonTester
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
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
@AutoConfigureJsonTesters
@Stepwise
@ActiveProfiles("fileTest")
class InvoiceControllerStepwiseTest extends Specification {

    @Autowired
    private MockMvc mockMvc

    @Autowired
    JacksonTester<Invoice> jsonInvoiceService

    @Autowired
    JacksonTester<List<Invoice>> jsonInvoiceListService

    @Shared Invoice invoice = InvoiceFixture.getInvoice()
    @Shared Invoice updatedInvoice = InvoiceFixture.getInvoice()

    def "should return empty list"() {
        given:
        deleteAllInvoices()

        when:
        def response = mockMvc
                .perform(get("/api/invoices"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        response == "[]"

    }

    def "should save invoice"() {
        given:
        String jsonString = jsonInvoiceService.write(invoice).getJson()

        when:
        def response = mockMvc
                .perform(post("/api/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        jsonInvoiceService.parseObject(response) == invoice
    }

    def "should return invoice by id"() {
        given:
        UUID id = invoice.getId()

        when:
        def response = mockMvc
                .perform(get("/api/invoices/" + id.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        jsonInvoiceService.parseObject(response) == invoice

    }

    def "should return list of invoices"() {
        when:
        def response = mockMvc
                .perform(get("/api/invoices"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        def invoices = jsonInvoiceListService.parseObject(response)
        invoices.size() == 1
        invoices[0] == invoice
    }

    def "should return 404 NotFound status when getting invoice by id which doesn't exist"() {
        given:
        UUID invalidId = UUID.randomUUID()

        when:
        def response = mockMvc
                .perform(get("/api/invoices/" + invalidId))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        response.isEmpty()
    }

    def "should filter the database"() {
        given:
        String sellerTaxId = invoice.getSeller().getTaxIdentificationNumber()
        String buyerTaxId = invoice.getBuyer().getTaxIdentificationNumber()

        when:
        def response = mockMvc
                .perform(get("/api/invoices")
                        .queryParam("sellerTaxId", sellerTaxId)
                        .queryParam("buyerTaxId", buyerTaxId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        def invoices = jsonInvoiceListService.parseObject(response)
        invoices.size() == 1
        invoices[0] == invoice
    }

    def "should update the invoice"() {
        given:
        updatedInvoice.setId(invoice.getId())
        String updatedJsonString = jsonInvoiceService.write(updatedInvoice).getJson()

        expect:
        mockMvc
                .perform(put("/api/invoices/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJsonString))
                .andExpect(status().isOk())
    }

    def "should return updated invoice by id"() {
        given:
        UUID id = invoice.getId()

        when:
        def response = mockMvc
                .perform(get("/api/invoices/" + id.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        jsonInvoiceService.parseObject(response) == updatedInvoice
    }

    def "should return 404 Not Found when trying to update nonexistent invoice"() {
        given:
        Invoice invalidInvoice = InvoiceFixture.getInvoice()
        String jsonString = jsonInvoiceService.write(invalidInvoice).getJson()

        when:
        def response = mockMvc
                    .perform(put("/api/invoices")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonString))
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
        mockMvc
                .perform(delete("/api/invoices/" + id.toString()))
                .andExpect(status().isAccepted())

        and:
        mockMvc
                .perform(delete("/api/invoices/" + id.toString()))
                .andExpect(status().isNotFound())

        and:
        mockMvc
                .perform(get("/api/invoices/" + id.toString()))
                .andExpect(status().isNotFound())
    }

    private List<Invoice> getAllInvoices() {
        def list = mockMvc
                .perform(get("/api/invoices"))
                .andReturn()
                .getResponse()
                .getContentAsString()
        return jsonInvoiceListService.parseObject(list)
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
