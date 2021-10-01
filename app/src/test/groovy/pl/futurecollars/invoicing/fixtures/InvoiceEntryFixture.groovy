package pl.futurecollars.invoicing.fixtures

import pl.futurecollars.invoicing.model.InvoiceEntry
import pl.futurecollars.invoicing.model.Vat

class InvoiceEntryFixture {

    static InvoiceEntry getInvoiceEntry(int id) {
        return new InvoiceEntry("Product $id", new BigDecimal(200 * id), Vat.VAT_23);
    }

    static List<InvoiceEntry> getInvoiceEntryList(int number) {
        List<InvoiceEntry> list = new ArrayList<>();
        for(int i = 1; i <= number; i++) {
            list.add(getInvoiceEntry(i))
        }
        return list;
    }
}
