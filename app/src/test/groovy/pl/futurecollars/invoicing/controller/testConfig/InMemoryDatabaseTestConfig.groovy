package pl.futurecollars.invoicing.controller.testConfig

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase

@TestConfiguration
class InMemoryDatabaseTestConfig {

    @Bean("fileBasedDatabase")
    Database getInMemoryDatabaseTestInstance() {
        return new InMemoryDatabase()
    }
}
