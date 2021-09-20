package pl.futurecollars.invoicing.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoicing.fixtures.InvoiceFixture
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Specification
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@AutoConfigureMockMvc
@SpringBootTest
class InvoiceControllerIntegrationTest extends Specification{

    @Autowired
    MockMvc mockMvc

    @Autowired
    JsonService<Invoice> jsonService

    JsonService<Invoice[]> jsonListService = new JsonService<>()

    Invoice invoice = InvoiceFixture.getInvoice()

    def setup() {
        deleteAllInvoices()
    }

    def "should return empty list"() {
        when:
        def response = mockMvc.perform(get("/api/invoices")).andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString()

        then:
        response == "[]"
    }

    def "should save invoice to database"() {
        given:
        String jsonString = jsonService.toJsonString(invoice)

        when:
        def response = mockMvc.perform(post("/api/invoices").contentType(MediaType.APPLICATION_JSON).content(jsonString))
        .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString()

        then:
        jsonService.toObject(response, Invoice.class) == invoice
    }

    def "should return list of all invoices"() {
        given:
        int numberOfInvoicesAdded = 10
        def expectedInvoices = addInvoices(numberOfInvoicesAdded)

        when:
        def response = mockMvc.perform(get("/api/invoices"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        def invoices = jsonListService.toObject(response, Invoice[])
        invoices.size() == numberOfInvoicesAdded
        invoices.sort() == expectedInvoices.sort()
    }

    def "should return invoice by id"() {
        given:
        def invoices = addInvoices(1)
        Invoice invoice = invoices[0]
        UUID id = invoice.getId()

        when:
        def response = mockMvc.perform(get("/api/invoices/" + id.toString())).andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString()

        then:
        jsonService.toObject(response, Invoice.class) == invoice
    }

    def "should return 404 NotFound status when asking for nonexistent invoice by id"() {
        given:
        UUID invalidId = UUID.randomUUID()

        when:
        def response = mockMvc
                .perform(get("/api/invoices/" + invalidId.toString()))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        response.isEmpty()
    }

    def "should update invoice"() {
        given:
        def invoices = addInvoices(3)
        Invoice invoiceToUpdate = invoices[1]
        Invoice updatedInvoice = InvoiceFixture.getInvoice()
        updatedInvoice.setId(invoiceToUpdate.getId())
        String jsonString = jsonService.toJsonString(updatedInvoice)

        when:
        def response = mockMvc
                .perform(put("/api/invoices/").contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        getInvoiceById(invoiceToUpdate.getId()) == updatedInvoice
        jsonService.toObject(response, Invoice.class) == updatedInvoice
    }

    def "should return 404 NotFound when updating nonexistent invoice"() {
        given:
        String jsonString = jsonService.toJsonString(invoice)

        expect:
        mockMvc.perform(put("/api/invoices").contentType(MediaType.APPLICATION_JSON).content(jsonString))
                .andExpect(status().isNotFound())
    }

    def "should return invoices created before given date"() {
        given:
        addInvoices(3)

        when:
        def response = mockMvc.perform(get("/api/invoices").queryParam("before", "2020-10-10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        response == "[]"
    }

    def "should return invoices created after given date"() {
        given:
        def numberOfInvoices = 10
        def invoices = addInvoices(numberOfInvoices)

        when:
        def response = mockMvc.perform(get("/api/invoices").queryParam("after", "2020-10-10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        def returnedInvoices = jsonListService.toObject(response, Invoice[])
        returnedInvoices.size() == numberOfInvoices
        returnedInvoices.sort() == invoices.sort()
    }

    def "should filter invoices by sellerId"() {
        given:
        def invoices = addInvoices(10)
        def invoice = invoices[0]
        UUID sellerId = invoice.getFrom().getId()

        when:
        def response = mockMvc.perform(get("/api/invoices").queryParam("sellerId", sellerId.toString()))
                              .andExpect(status().isOk())
                              .andReturn()
                              .getResponse()
                              .getContentAsString()

        then:
        def filteredInvoices = jsonListService.toObject(response, Invoice[])
        filteredInvoices.size() == 1
        filteredInvoices[0] == invoice
    }

    def "should filter invoices by buyerId"() {
        given:
        def invoices = addInvoices(10)
        def invoice = invoices[0]
        UUID buyerId = invoice.getTo().getId()

        when:
        def response = mockMvc.perform(get("/api/invoices").queryParam("buyerId", buyerId.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        def filteredInvoices = jsonListService.toObject(response, Invoice[])
        filteredInvoices.size() == 1
        filteredInvoices[0] == invoice
    }

    def "should delete invoice"() {
        given:
        def numberOfInvoices = 3
        def invoices = addInvoices(numberOfInvoices)
        Invoice toDelete = invoices[0]
        UUID id = toDelete.getId()

        when:
        mockMvc.perform(delete("/api/invoices/" + id.toString()))
                .andExpect(status().isAccepted())

        then:
        def invoicesLeft = getAllInvoices()
        getAllInvoices().size() == numberOfInvoices - 1
    }

    def "should return 404 NotFound when deleting nonexistent invoice"() {
        given:
        UUID id = invoice.getId()

        expect:
        mockMvc.perform(delete("/api/invoices/" + id.toString()))
                .andExpect(status().isNotFound())
    }



    private List<Invoice> addInvoices(int number) {
        List<Invoice> invoiceList = new ArrayList<>()
        for(int i = 0; i < number; i++) {
            Invoice invoice = InvoiceFixture.getInvoice()
            invoiceList.add(invoice)
            String jsonString = jsonService.toJsonString(invoice)
            mockMvc.perform(post("/api/invoices").contentType(MediaType.APPLICATION_JSON).content(jsonString))
        }
        return invoiceList
    }

    private List<Invoice> getAllInvoices() {
        def list = mockMvc.perform(get("/api/invoices")).andReturn().getResponse().getContentAsString()
        return jsonListService.toObject(list, Invoice[])
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

    private Invoice getInvoiceById(UUID id) {
        def response = mockMvc
                .perform(get("/api/invoices/" + id.toString()))
                .andReturn()
                .getResponse()
                .getContentAsString()
        return jsonService.toObject(response, Invoice.class)
    }
}
