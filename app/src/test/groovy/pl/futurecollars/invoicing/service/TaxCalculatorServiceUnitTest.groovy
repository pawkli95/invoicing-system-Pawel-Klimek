package pl.futurecollars.invoicing.service

import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.fixtures.InvoiceFixture
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.model.TaxCalculation
import spock.lang.Specification

class TaxCalculatorServiceUnitTest extends Specification {

    Database database = Mock()

    TaxCalculatorService taxCalculatorService = new TaxCalculatorService(database)

    Invoice invoice = InvoiceFixture.getInvoice()

    def "should calculate tax"() {
        given:
        database.getAll() >> [invoice]
        long taxId = invoice.getFrom().getTaxIdentificationNumber()

        when:
        TaxCalculation taxCalculation = taxCalculatorService.getTaxCalculation(taxId)

        then:
        taxCalculation.getCosts() == BigDecimal.ZERO
        taxCalculation.getIncome() == new BigDecimal(2000)
        taxCalculation.getEarnings() == new BigDecimal(2000)
        taxCalculation.getIncomingVat() ==  new BigDecimal(460)
        taxCalculation.getOutgoingVat() == BigDecimal.ZERO
        taxCalculation.getVatToReturn() == new BigDecimal(460)
    }

    def "should throw NoSuchElementException when tax id doesn't exist"() {
        given:
        database.getAll() >> [invoice]
        long taxId = invoice.getFrom().getTaxIdentificationNumber() + 1

        when:
        taxCalculatorService.getTaxCalculation(taxId)

        then:
        thrown(NoSuchElementException)
    }
}
