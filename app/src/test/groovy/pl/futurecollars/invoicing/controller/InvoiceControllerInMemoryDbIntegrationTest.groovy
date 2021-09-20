package pl.futurecollars.invoicing.controller

import org.springframework.context.annotation.Import
import pl.futurecollars.invoicing.controller.testConfig.InMemoryDatabaseTestConfig

@Import(InMemoryDatabaseTestConfig.class)
class InvoiceControllerInMemoryDbIntegrationTest extends InvoiceControllerIntegrationTest {
}
