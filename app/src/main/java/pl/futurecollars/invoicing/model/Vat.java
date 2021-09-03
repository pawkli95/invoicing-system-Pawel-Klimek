package pl.futurecollars.invoicing.model;

public enum Vat {

    VAT_23(0.23f),
    VAT_8(0.08f),
    VAT_5(0.05f),
    VAT_0(0);

    public final float rate;

    Vat(float rate) {
        this.rate = rate;
    }
}
