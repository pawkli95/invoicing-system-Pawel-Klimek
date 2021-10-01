package pl.futurecollars.invoicing.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.db.filebased.FileBasedDatabase;
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.utils.FileService;
import pl.futurecollars.invoicing.utils.JsonService;

@Slf4j
@Configuration
public class DatabaseConfiguration {

    @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "file")
    @Bean
    public Database getFileBasedDatabase(@Value("${invoicing-system.invoicesFile}") String invoicesFile,
                                         @Value("${invoicing-system.idsFile}") String idsFile) {
        FileService invoices = new FileService(invoicesFile);
        FileService ids = new FileService(idsFile);
        log.info("Created file database");
        return new FileBasedDatabase(new JsonService<Invoice>(), invoices, ids);
    }

    @Bean
    @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "inmemory")
    public Database getInMemoryDatabase() {
        log.info("Created inmemory database");
        return new InMemoryDatabase();
    }

    @Bean
    @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "fileTest")
    public Database getTestFileBasedDatabase(@Value("${invoicing-system.invoicesFile}") String invoicesFile,
                                             @Value("${invoicing-system.idsFile}") String idsFile) {
        FileService invoices = new FileService(invoicesFile);
        FileService ids = new FileService(idsFile);
        log.info("Created test file database");
        return new FileBasedDatabase(new JsonService<Invoice>(), invoices, ids);
    }

    @Bean
    @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "inmemoryTest")
    public Database getTestInMemoryDatabase() {
        log.info("Created test inmemory database");
        return new InMemoryDatabase();
    }
}
