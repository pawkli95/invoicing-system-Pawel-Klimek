package pl.futurecollars.invoicing.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.json.JacksonTester
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoicing.dto.InvoiceDto
import pl.futurecollars.invoicing.fixtures.CompanyFixture
import pl.futurecollars.invoicing.fixtures.InvoiceEntryFixture
import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.dto.TaxCalculation
import spock.lang.Shared
import spock.lang.Specification
import java.time.LocalDateTime
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
abstract class TaxCalculatorControllerTest extends Specification {

    @Autowired
    MockMvc mockMvc

    @Autowired
    JacksonTester<InvoiceDto> invoiceJsonService

    @Autowired
    JacksonTester<TaxCalculation> taxCalculationJsonService

    @Autowired
    JacksonTester<List<InvoiceDto>> invoiceListService

    @Autowired
    JacksonTester<Company> companyJsonService

    @Shared
    Company company1 = CompanyFixture.getCompany()

    def "should calculate tax without personal car expenses"() {
        given:
        deleteInvoices()
        addInvoicesWithoutPersonalCarEntries()
        String companyJson = companyJsonService.write(company1).getJson()

        when:
        def response = mockMvc
                .perform(post("/api/tax")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(companyJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        TaxCalculation taxCalculation = taxCalculationJsonService.parseObject(response)
        taxCalculation.getIncome() == BigDecimal.valueOf(4200)
        taxCalculation.getCosts() == BigDecimal.valueOf(2000)
        taxCalculation.getIncomeMinusCosts() == BigDecimal.valueOf(2200)
        taxCalculation.getIncomingVat() == BigDecimal.valueOf(966)
        taxCalculation.getOutgoingVat() == BigDecimal.valueOf(460)
        taxCalculation.getVatToReturn() == BigDecimal.valueOf(506)
        taxCalculation.getPensionInsurance() == BigDecimal.valueOf(500)
        taxCalculation.getIncomeMinusCostsMinusPensionInsurance() == BigDecimal.valueOf(1700)
        taxCalculation.getTaxCalculationBase() == BigDecimal.valueOf(1700)
        taxCalculation.getIncomeTax() == BigDecimal.valueOf(323)
        taxCalculation.getHealthInsurance9() == BigDecimal.valueOf(90)
        taxCalculation.getHealthInsurance775() == BigDecimal.valueOf(77.5)
        taxCalculation.getIncomeTaxMinusHealthInsurance() == BigDecimal.valueOf(245.5)
        taxCalculation.getFinalIncomeTaxValue() == BigDecimal.valueOf(245)
    }

    def "should calculate tax with personal car expenses"() {
        given:
        deleteInvoices()
        addInvoicesWithPersonalCarEntries()
        String companyJson = companyJsonService.write(company1).getJson()

        when:
        def response = mockMvc
                .perform(post("/api/tax")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(companyJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()

        then:
        TaxCalculation taxCalculation = taxCalculationJsonService.parseObject(response)
        taxCalculation.getIncome() == BigDecimal.valueOf(4200)
        taxCalculation.getCosts() == BigDecimal.valueOf(2138)
        taxCalculation.getIncomeMinusCosts() == BigDecimal.valueOf(2062)
        taxCalculation.getIncomingVat() == BigDecimal.valueOf(966)
        taxCalculation.getOutgoingVat() == BigDecimal.valueOf(322)
        taxCalculation.getVatToReturn() == BigDecimal.valueOf(644)
        taxCalculation.getPensionInsurance() == BigDecimal.valueOf(500)
        taxCalculation.getIncomeMinusCostsMinusPensionInsurance() == BigDecimal.valueOf(1562)
        taxCalculation.getTaxCalculationBase() == BigDecimal.valueOf(1562)
        taxCalculation.getIncomeTax() == BigDecimal.valueOf(296.78)
        taxCalculation.getHealthInsurance9() == BigDecimal.valueOf(90)
        taxCalculation.getHealthInsurance775() == BigDecimal.valueOf(77.5)
        taxCalculation.getIncomeTaxMinusHealthInsurance() == BigDecimal.valueOf(219.28)
        taxCalculation.getFinalIncomeTaxValue() == BigDecimal.valueOf(219)

    }

    def "should return 404 NotFound http status when tax id doesn't exist"() {
        given:
        deleteInvoices()
        String companyJson = companyJsonService.write(company1).getJson()

        expect:
                 mockMvc
                .perform(post("/api/tax/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(companyJson))
                .andExpect(status().isNotFound())
    }

    void addInvoicesWithPersonalCarEntries() {
        Company company2 = CompanyFixture.getCompany()
        InvoiceDto invoice1 = new InvoiceDto(UUID.randomUUID(), "number1", LocalDateTime.now(), company1, company2, InvoiceEntryFixture.getInvoiceEntryListWithPersonalCar(6))
        InvoiceDto invoice2 = new InvoiceDto(UUID.randomUUID(), "number2", LocalDateTime.now(), company2, company1, InvoiceEntryFixture.getInvoiceEntryListWithPersonalCar(4))
        addInvoice(invoice1)
        addInvoice(invoice2)
    }

    void addInvoicesWithoutPersonalCarEntries() {
        Company company2 = CompanyFixture.getCompany()
        InvoiceDto invoice1 = new InvoiceDto(UUID.randomUUID(), "number1", LocalDateTime.now(), company1, company2, InvoiceEntryFixture.getInvoiceEntryListWithoutPersonalCar(6))
        InvoiceDto invoice2 = new InvoiceDto(UUID.randomUUID(), "number2", LocalDateTime.now(), company2, company1, InvoiceEntryFixture.getInvoiceEntryListWithoutPersonalCar(4))
        addInvoice(invoice1)
        addInvoice(invoice2)
    }

    void addInvoice(InvoiceDto invoice) {
        String jsonString = invoiceJsonService.write(invoice).getJson()
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
        List<InvoiceDto> list = invoiceListService.parseObject(response)
        for(InvoiceDto i : list) {
            deleteInvoice(i)
        }
    }

    void deleteInvoice(InvoiceDto invoice) {
        String id = invoice.getId().toString()
        mockMvc
                .perform(delete("/api/invoices/" + id))
    }
}
