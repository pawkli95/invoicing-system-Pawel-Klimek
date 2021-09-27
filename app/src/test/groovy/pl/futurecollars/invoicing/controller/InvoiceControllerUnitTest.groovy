package pl.futurecollars.invoicing.controller

import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoicing.fixtures.InvoiceFixture
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.service.InvoiceService
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Specification
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(InvoiceController.class)
class InvoiceControllerUnitTest extends Specification {

    @Autowired
    MockMvc mockMvc

    @SpringBean
    InvoiceService invoiceService = Mock()

    Invoice invoice = InvoiceFixture.getInvoice()

    JsonService<Invoice> jsonInvoiceService = new JsonService<>()

    JsonService<Invoice[]> jsonInvoiceListService = new JsonService<>()

    def "should save invoice to database"() {
        given:
        invoiceService.saveInvoice(invoice) >> invoice
        String jsonString = jsonInvoiceService.toJsonString(invoice)

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
        jsonInvoiceService.toObject(response, Invoice.class) == invoice
    }

    def "should return invoice by id when it exists"() {
        given:
        UUID id = invoice.getId()
        invoiceService.getById(id) >> invoice

        when:
        def response = mockMvc
                .perform(get("/api/invoices/" + id.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        jsonInvoiceService.toObject(response, Invoice.class) == invoice

    }

    def "should return 404 NotFoundStatus when invoice doesn't exist"() {
        given:
        UUID randomId = UUID.randomUUID()
        invoiceService.getById(randomId) >> {throw new NoSuchElementException()}

        when:
        def response = mockMvc
                .perform(get("/api/invoices" + randomId.toString()))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        response.isEmpty()
    }

    def "should return list of invoices"() {
        given:
        invoiceService.getAll() >> [invoice]

        when:
        def response = mockMvc
                .perform(get("/api/invoices"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        def invoices = jsonInvoiceListService.toObject(response, Invoice[])
        invoices.size() == 1
        invoices[0] == invoice
    }

    def "should update invoice"() {
        given:
        Invoice updatedInvoice = InvoiceFixture.getInvoice()
        String jsonString = jsonInvoiceService.toJsonString(updatedInvoice)
        invoiceService.updateInvoice(updatedInvoice) >> updatedInvoice

        when:
        def response = mockMvc
                .perform(put("/api/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        jsonInvoiceService.toObject(response, Invoice.class) == updatedInvoice
    }

    def "should delete invoice"() {
        given:
        UUID id = UUID.randomUUID()

        expect:
        mockMvc
                .perform(delete("/api/invoices/" + id.toString()))
                .andExpect(status().isAccepted())
    }

    def "should return 404 NotFound status when deleting nonexistent invoice"() {
        given:
        UUID invalidId = UUID.randomUUID()
        invoiceService.deleteInvoice(invalidId) >> {throw new NoSuchElementException()}

        expect:
        mockMvc
                .perform(delete("/api/invoices/" + invalidId.toString()))
                .andExpect(status().isNotFound())
    }
}
