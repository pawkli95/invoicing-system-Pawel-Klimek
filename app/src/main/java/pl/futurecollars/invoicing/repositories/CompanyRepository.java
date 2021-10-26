package pl.futurecollars.invoicing.repositories;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.futurecollars.invoicing.model.Company;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
}
