package pl.futurecollars.invoicing.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.json.JacksonTester
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoicing.dto.InvoiceDto
import pl.futurecollars.invoicing.fixtures.InvoiceFixture
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@AutoConfigureJsonTesters
@AutoConfigureMockMvc
@SpringBootTest
abstract class InvoiceControllerIntegrationTest extends Specification{

    @Autowired
    MockMvc mockMvc

    @Autowired
    JacksonTester<InvoiceDto> jsonService

    @Autowired
    JacksonTester<List<InvoiceDto>> jsonListService

    InvoiceDto invoiceDto = InvoiceFixture.getInvoiceDto()

    def setup() {
        deleteAllInvoices()
    }

    def "should return empty list"() {
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

    def "should save invoice to database"() {
        given:
        String jsonString = jsonService.write(invoiceDto).getJson()

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
        jsonService.parseObject(response) == invoiceDto
    }

    def "should return list of all invoices"() {
        given:
        int numberOfInvoicesAdded = 10
        def expectedInvoices = addInvoices(numberOfInvoicesAdded)

        when:
        def response = mockMvc
                .perform(get("/api/invoices"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        def invoices = jsonListService.parseObject(response)
        invoices.size() == numberOfInvoicesAdded
        invoices.sort() == expectedInvoices.sort()
    }

    def "should return invoice by id"() {
        given:
        def invoices = addInvoices(1)
        InvoiceDto invoice = invoices[0]
        UUID id = invoice.getId()

        when:
        def response = mockMvc
                .perform(get("/api/invoices/" + id.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        jsonService.parseObject(response) == invoice
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
        InvoiceDto invoiceToUpdate = invoices[1]
        InvoiceDto updatedInvoice = InvoiceFixture.getInvoiceDto()
        updatedInvoice.setId(invoiceToUpdate.getId())
        String jsonString = jsonService.write(updatedInvoice).getJson()

        when:
        def response = mockMvc
                .perform(put("/api/invoices/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        getInvoiceById(invoiceToUpdate.getId()) == updatedInvoice
        jsonService.parseObject(response) == updatedInvoice
    }

    def "should return 404 NotFound when updating nonexistent invoice"() {
        given:
        String jsonString = jsonService.write(invoiceDto).getJson()

        expect:
        mockMvc
                .perform(put("/api/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
                .andExpect(status().isNotFound())
    }

    def "should return invoices created before given date"() {
        given:
        addInvoices(3)

        when:
        def response = mockMvc
                .perform(get("/api/invoices")
                        .queryParam("before", "2020-10-10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        response == "[]"
    }

    def "should filter and return invoices created after given date"() {
        given:
        def numberOfInvoices = 10
        def invoices = addInvoices(numberOfInvoices)

        when:
        def response = mockMvc
                .perform(get("/api/invoices")
                        .queryParam("after", "2020-10-10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        def returnedInvoices = jsonListService.parseObject(response)
        returnedInvoices.size() == numberOfInvoices
        returnedInvoices.sort() == invoices.sort()
    }

    def "should filter invoices by sellerTaxId"() {
        given:
        def invoices = addInvoices(10)
        def invoice = invoices[0]
        String sellerTaxId = invoice.getSeller().getTaxIdentificationNumber()

        when:
        def response = mockMvc
                .perform(get("/api/invoices")
                        .queryParam("sellerTaxId", sellerTaxId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        def filteredInvoices = jsonListService.parseObject(response)
        filteredInvoices.size() == 1
        filteredInvoices[0] == invoice
    }

    def "should filter invoices by buyerId"() {
        given:
        def invoices = addInvoices(10)
        def invoice = invoices[0]
        String buyerTaxId = invoice.getBuyer().getTaxIdentificationNumber()

        when:
        def response = mockMvc
                .perform(get("/api/invoices")
                        .queryParam("buyerTaxId", buyerTaxId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        def filteredInvoices = jsonListService.parseObject(response)
        filteredInvoices.size() == 1
        filteredInvoices[0] == invoice
    }

    def "should delete invoice"() {
        given:
        def numberOfInvoices = 3
        def invoices = addInvoices(numberOfInvoices)
        InvoiceDto toDelete = invoices[0]
        UUID id = toDelete.getId()

        when:
        mockMvc
                .perform(delete("/api/invoices/" + id.toString()))
                .andExpect(status().isAccepted())

        then:
        getAllInvoices().size() == numberOfInvoices - 1
    }

    def "should return 404 NotFound when deleting nonexistent invoice"() {
        given:
        UUID id = invoiceDto.getId()

        expect:
        mockMvc
                .perform(delete("/api/invoices/" + id.toString()))
                .andExpect(status().isNotFound())
    }

    private List<InvoiceDto> addInvoices(int number) {
        List<InvoiceDto> invoiceList = new ArrayList<>()
        for(int i = 0; i < number; i++) {
            InvoiceDto invoiceDto = InvoiceFixture.getInvoiceDto()
            invoiceList.add(invoiceDto)
            String jsonString = jsonService.write(invoiceDto).getJson()
            mockMvc.perform(post("/api/invoices").contentType(MediaType.APPLICATION_JSON).content(jsonString))
        }
        return invoiceList
    }

    private List<InvoiceDto> getAllInvoices() {
        def list = mockMvc
                .perform(get("/api/invoices"))
                .andReturn()
                .getResponse()
                .getContentAsString()
        return jsonListService.parseObject(list)
    }

    private void deleteInvoice(UUID id) {
        mockMvc.perform(delete("/api/invoices/" + id.toString()))
    }

    private void deleteAllInvoices() {
        List<InvoiceDto> invoiceList = getAllInvoices()
        for(InvoiceDto invoice : invoiceList) {
            UUID id = invoice.getId()
            deleteInvoice(id)
        }
    }

    private InvoiceDto getInvoiceById(UUID id) {
        def response = mockMvc
                .perform(get("/api/invoices/" + id.toString()))
                .andReturn()
                .getResponse()
                .getContentAsString()
        return jsonService.parseObject(response)
    }
}
