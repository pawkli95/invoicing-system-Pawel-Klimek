package pl.futurecollars.invoicing.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.fixtures.CompanyFixture
import pl.futurecollars.invoicing.fixtures.InvoiceEntryFixture
import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.model.TaxCalculation
import pl.futurecollars.invoicing.service.TaxCalculatorService
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Specification
import java.time.LocalDateTime
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
abstract class TaxCalculatorControllerTest extends Specification {

    @Autowired
    TaxCalculatorService taxCalculatorService

    @Autowired
    MockMvc mockMvc

    @Autowired
    Database database

    JsonService<Invoice> invoiceJsonService = new JsonService<>()

    JsonService<TaxCalculation> taxCalculationJsonService = new JsonService<>()

    JsonService<Invoice[]> invoiceListService = new JsonService<>()

    Company company1 = CompanyFixture.getCompany()

    Company company2 = CompanyFixture.getCompany()

    Invoice invoice1

    Invoice invoice2

    def "should calculate tax"() {
        given:
        deleteInvoices()
        addInvoices()
        long taxId = company1.getTaxIdentificationNumber()

        when:
        def response = mockMvc
                .perform(get("/api/tax/" + taxId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        TaxCalculation taxCalculation = taxCalculationJsonService.toObject(response, TaxCalculation.class)
        taxCalculation.getIncome() == new BigDecimal(4200)
        taxCalculation.getCosts() == new BigDecimal(2000)
        taxCalculation.getEarnings() == new BigDecimal(2200)
        taxCalculation.getIncomingVat() == new BigDecimal(966)
        taxCalculation.getOutgoingVat() == new BigDecimal(460)
        taxCalculation.getVatToReturn() == new BigDecimal(506)

    }

    def "should return 404 http status when tax id doesn't exist"() {
        given:
        deleteInvoices()
        long taxId = 1

        expect:
        def response = mockMvc
                .perform(get("/api/tax/" + taxId))
                .andExpect(status().isNotFound())
    }

    def void addInvoices() {
        invoice1 = new Invoice(LocalDateTime.now(), company1, company2, InvoiceEntryFixture.getInvoiceEntryList(6))
        invoice2 = new Invoice(LocalDateTime.now(), company2, company1, InvoiceEntryFixture.getInvoiceEntryList(4))
        addInvoice(invoice1)
        addInvoice(invoice2)
    }

    void addInvoice(Invoice invoice) {
        String jsonString = invoiceJsonService.toJsonString(invoice)
        mockMvc
                .perform(post("/api/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonString))
    }

    void deleteInvoices() {
        def response = mockMvc
                .perform(get("/api/invoices"))
                .andReturn()
                .getResponse()
                .getContentAsString()
        List<Invoice> list = invoiceListService.toObject(response, Invoice[])
        for(Invoice i : list) {
            deleteInvoice(i)
        }
    }

    void deleteInvoice(Invoice invoice) {
        String id = invoice.getId().toString()
        mockMvc
                .perform(delete("/api/invoices/" + id))
    }
}
