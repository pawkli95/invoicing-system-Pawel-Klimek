package pl.futurecollars.invoicing.service

import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.fixtures.CompanyFixture
import pl.futurecollars.invoicing.fixtures.InvoiceEntryFixture
import pl.futurecollars.invoicing.fixtures.InvoiceFixture
import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.dto.TaxCalculation
import spock.lang.Specification

import java.time.LocalDateTime

class TaxCalculatorServiceUnitTest extends Specification {

    Database database = Mock()

    TaxCalculatorService taxCalculatorService = new TaxCalculatorService(database)

    def "should calculate tax without personal car expenses"() {
        given: "invoices in database without personal car expenses"
        Company company1 = CompanyFixture.getCompany()
        Company company2 = CompanyFixture.getCompany()
        Invoice invoice1 = new Invoice(UUID.randomUUID(),"number1", LocalDateTime.now(), company1, company2, InvoiceEntryFixture.getInvoiceEntryListWithoutPersonalCar(6))
        Invoice invoice2 = new Invoice(UUID.randomUUID(), "number2", LocalDateTime.now(), company2, company1, InvoiceEntryFixture.getInvoiceEntryListWithoutPersonalCar(4))
        database.getAll() >> [invoice1, invoice2]

        when:"we ask tax calculator service to calculate tax"
        TaxCalculation taxCalculation = taxCalculatorService.getTaxCalculation(company1)

        then:"tax is calculated accurately"
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
        given:"invoices in database with personal car expenses"
        Company company1 = CompanyFixture.getCompany()
        Company company2 = CompanyFixture.getCompany()
        Invoice invoice1 = new Invoice(UUID.randomUUID(), "number1", LocalDateTime.now(), company1, company2, InvoiceEntryFixture.getInvoiceEntryListWithPersonalCar(6))
        Invoice invoice2 = new Invoice(UUID.randomUUID(), "number2", LocalDateTime.now(), company2, company1, InvoiceEntryFixture.getInvoiceEntryListWithPersonalCar(4))
        database.getAll() >> [invoice1, invoice2]

        when:"we ask tax calculator service to calculate tax"
        TaxCalculation taxCalculation = taxCalculatorService.getTaxCalculation(company1)

        then:"tax is calculated accurately"
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

    def "should throw NoSuchElement exception when tax id is not in database"() {
        given:"an empty database"
        database.getAll() >> []
        Company company = CompanyFixture.getCompany()

        when:"we ask tax calculator service to calculate tax"
        taxCalculatorService.getTaxCalculation(company)

        then:"NoSuchElementException is thrown"
        thrown(NoSuchElementException)
    }
}
