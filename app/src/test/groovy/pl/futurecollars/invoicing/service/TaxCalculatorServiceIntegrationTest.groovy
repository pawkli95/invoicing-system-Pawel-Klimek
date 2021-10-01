package pl.futurecollars.invoicing.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.fixtures.CompanyFixture
import pl.futurecollars.invoicing.fixtures.InvoiceEntryFixture
import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.model.TaxCalculation
import spock.lang.Shared
import spock.lang.Specification
import java.time.LocalDateTime

@SpringBootTest
abstract class TaxCalculatorServiceIntegrationTest extends Specification {

    @Autowired
    Database database

    @Autowired
    TaxCalculatorService taxCalculatorService

    @Shared
    Company company1 = CompanyFixture.getCompany()

    @Shared
    Invoice invoice1

    @Shared
    Invoice invoice2

    def "should calculate tax"() {
        given:
        deleteInvoices()
        addInvoices()
        long taxId = company1.getTaxIdentificationNumber()

        when:
        TaxCalculation taxCalculation = taxCalculatorService.getTaxCalculation(taxId)

        then:
        taxCalculation.getIncome() == new BigDecimal(4200)
        taxCalculation.getCosts() == new BigDecimal(2000)
        taxCalculation.getEarnings() == new BigDecimal(2200)
        taxCalculation.getIncomingVat() == new BigDecimal(966)
        taxCalculation.getOutgoingVat() == new BigDecimal(460)
        taxCalculation.getVatToReturn() == new BigDecimal(506)
    }

    def "should throw NoSuchElementException when tax id doesn't exist"() {
        given:
        deleteInvoices()
        long taxId = 1

        when:
        taxCalculatorService.getTaxCalculation(taxId)

        then:
        thrown(NoSuchElementException)
    }

    void addInvoices() {
        Company company2 = CompanyFixture.getCompany()
        invoice1 = new Invoice(LocalDateTime.now(), company1, company2, InvoiceEntryFixture.getInvoiceEntryList(6))
        invoice2 = new Invoice(LocalDateTime.now(), company2, company1, InvoiceEntryFixture.getInvoiceEntryList(4))
        database.save(invoice1)
        database.save(invoice2)
    }

    void deleteInvoices() {
        List<Invoice> list = database.getAll()
        for(Invoice i : list) {
            database.delete(i.getId())
        }
    }
}
