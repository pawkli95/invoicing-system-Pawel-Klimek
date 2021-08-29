package pl.futurecollars.invoicing.model

import spock.lang.Specification

class VatTest extends Specification {
    def "Vat rates should be adequate"() {
        expect:
        Vat.V_23.rate == new BigDecimal("0.23")
        Vat.V_8.rate == new BigDecimal("0.08")
        Vat.V_5.rate == new BigDecimal("0.05")
        Vat.V_0.rate == new BigDecimal(BigDecimal.ZERO)
    }
}
