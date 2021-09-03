package pl.futurecollars.invoicing.model

import spock.lang.Specification

class VatTest extends Specification {
    def "Vat rates should be adequate"() {
        expect:
        Vat.VAT_23.rate == 0.23f
        Vat.VAT_8.rate == 0.08f
        Vat.VAT_5.rate == 0.05f
        Vat.VAT_0.rate == 0
    }
}
