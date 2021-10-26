package pl.futurecollars.invoicing.controller.invoiceController

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.json.JacksonTester
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoicing.dto.InvoiceDto
import pl.futurecollars.invoicing.fixtures.InvoiceFixture
import pl.futurecollars.invoicing.model.Invoice
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
    JacksonTester<InvoiceDto> jsonInvoiceService

    @Autowired
    JacksonTester<List<InvoiceDto>> jsonInvoiceListService

    @Shared InvoiceDto invoiceDto = InvoiceFixture.getInvoiceDto()
    @Shared InvoiceDto updatedInvoiceDto = InvoiceFixture.getInvoiceDto()

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
        String jsonString = jsonInvoiceService.write(invoiceDto).getJson()

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
        jsonInvoiceService.parseObject(response) == invoiceDto
    }

    def "should return invoice by id"() {
        given:
        UUID id = invoiceDto.getId()

        when:
        def response = mockMvc
                .perform(get("/api/invoices/" + id.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        jsonInvoiceService.parseObject(response) == invoiceDto

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
        invoices[0] == invoiceDto
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
        String sellerTaxId = invoiceDto.getSeller().getTaxIdentificationNumber()
        String buyerTaxId = invoiceDto.getBuyer().getTaxIdentificationNumber()

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
        invoices[0] == invoiceDto
    }

    def "should update the invoice"() {
        given:
        updatedInvoiceDto.setId(invoiceDto.getId())
        String updatedJsonString = jsonInvoiceService.write(updatedInvoiceDto).getJson()

        expect:
        mockMvc
                .perform(put("/api/invoices/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJsonString))
                .andExpect(status().isOk())
    }

    def "should return updated invoice by id"() {
        given:
        UUID id = invoiceDto.getId()

        when:
        def response = mockMvc
                .perform(get("/api/invoices/" + id.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        jsonInvoiceService.parseObject(response) == updatedInvoiceDto
    }

    def "should return 404 Not Found when trying to update nonexistent invoice"() {
        given:
        InvoiceDto invalidInvoiceDto = InvoiceFixture.getInvoiceDto()
        String jsonString = jsonInvoiceService.write(invalidInvoiceDto).getJson()

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
        UUID id = invoiceDto.getId()

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

    private List<InvoiceDto> getAllInvoices() {
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
        List<InvoiceDto> invoiceList = getAllInvoices()
        for(InvoiceDto invoice : invoiceList) {
            UUID id = invoice.getId()
            deleteInvoice(id)
        }
    }
}
