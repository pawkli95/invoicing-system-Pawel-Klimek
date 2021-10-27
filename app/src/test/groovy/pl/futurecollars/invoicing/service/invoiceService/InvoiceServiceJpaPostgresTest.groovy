package pl.futurecollars.invoicing.service.invoiceService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.PostgreSQLContainer
import pl.futurecollars.invoicing.db.Database
import spock.lang.Subject

@ActiveProfiles("testcontainer")
@SpringBootTest
class InvoiceServiceJpaPostgresTest extends InvoiceServiceAbstractIntegrationTest{

    @Subject.Container
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test")

    static {
        postgreSQLContainer.start()
        System.setProperty("DB_PORT", String.valueOf(postgreSQLContainer.getFirstMappedPort()))
    }

    @Autowired
    Database jpaDatabase

    @Override
    Database getDatabase() {
        return jpaDatabase
    }
}
