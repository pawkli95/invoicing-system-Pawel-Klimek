package pl.futurecollars.invoicing.controller

import org.springframework.context.annotation.Import
import pl.futurecollars.invoicing.controller.testConfig.FileBasedDatabaseTestConfig

@Import(FileBasedDatabaseTestConfig.class)
class InvoiceControllerFileDbIntegrationTest extends InvoiceControllerIntegrationTest{
}
