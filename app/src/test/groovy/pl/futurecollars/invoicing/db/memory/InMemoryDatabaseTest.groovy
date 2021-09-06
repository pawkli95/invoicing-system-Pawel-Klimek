package pl.futurecollars.invoicing.db.memory

import pl.futurecollars.invoicing.db.DatabaseTest

class InMemoryDatabaseTest extends DatabaseTest{
    def setup() {
        database = new InMemoryDatabase()
    }
}
