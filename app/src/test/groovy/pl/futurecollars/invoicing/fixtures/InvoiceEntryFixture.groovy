package pl.futurecollars.invoicing.fixtures

import pl.futurecollars.invoicing.model.InvoiceEntry
import pl.futurecollars.invoicing.model.Vat

class InvoiceEntryFixture {

    static InvoiceEntry getInvoiceEntryWithPersonalCar(int id) {
        boolean isPersonalCar = false;
        if(id % 2 == 0) {
            isPersonalCar = true;
        }
        return InvoiceEntry.builder()
                .id(UUID.randomUUID())
                .description("Product $id")
                .price(BigDecimal.valueOf(200 * id))
                .vatRate(Vat.VAT_23)
                .personalCar(isPersonalCar)
                .build()
    }

    static List<InvoiceEntry> getInvoiceEntryListWithPersonalCar(int number) {
        List<InvoiceEntry> list = new ArrayList<>();
        for(int i = 1; i <= number; i++) {
            list.add(getInvoiceEntryWithPersonalCar(i))
        }
        return list;
    }

    static InvoiceEntry getInvoiceEntry(int id) {
        return InvoiceEntry.builder()
                .id(UUID.randomUUID())
                .description("Product $id")
                .price(BigDecimal.valueOf(200 * id))
                .vatRate(Vat.VAT_23)
                .personalCar(false)
                .build()
    }

    static List<InvoiceEntry> getInvoiceEntryListWithoutPersonalCar(int number) {
        List<InvoiceEntry> list = new ArrayList<>();
        for(int i = 1; i <= number; i++) {
            list.add(getInvoiceEntry(i))
        }
        return list;
    }
}
