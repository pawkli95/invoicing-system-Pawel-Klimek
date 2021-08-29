package pl.futurecollars.invoicing.model;

import java.math.BigDecimal;

public enum Vat {
    V_23(new BigDecimal("0.23")),
    V_8(new BigDecimal("0.08")),
    V_5(new BigDecimal("0.05")),
    V_0(BigDecimal.ZERO);

    public final BigDecimal rate;


    private Vat(BigDecimal rate) {
        this.rate = rate;
    }
}
