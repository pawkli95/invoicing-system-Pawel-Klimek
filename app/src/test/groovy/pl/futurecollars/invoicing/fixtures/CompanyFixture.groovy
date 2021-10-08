package pl.futurecollars.invoicing.fixtures

import pl.futurecollars.invoicing.model.Company

class CompanyFixture {

    static Random random = new Random()

    static Company getCompany() {
        StringBuilder taxId = new StringBuilder();
        for(int i = 0; i < 10; i++) {
            int r = random.nextInt(10)
            taxId.append(r);
        }
       return Company
               .builder()
               .name("Firma")
               .taxIdentificationNumber(taxId.toString())
               .address("Firmowa 1")
               .pensionInsurance(BigDecimal.valueOf(500))
               .healthInsurance(BigDecimal.valueOf(1000))
               .build()
    }
}
