package pl.futurecollars.invoicing.fixtures

import pl.futurecollars.invoicing.model.Company

class CompanyFixture {

    static Random random = new Random()

    static Company getCompany() {
        return new Company(Math.abs(random.nextLong()), "address")
    }
}
