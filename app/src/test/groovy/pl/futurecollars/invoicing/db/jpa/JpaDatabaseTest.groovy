package pl.futurecollars.invoicing.db.jpa

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.db.DatabaseTest

@SpringBootTest
@ActiveProfiles("jpaTest")
class JpaDatabaseTest extends DatabaseTest{

    @Autowired
    Database jpaDatabase

    @Override
    Database getDatabase() {
        return jpaDatabase
    }

}
