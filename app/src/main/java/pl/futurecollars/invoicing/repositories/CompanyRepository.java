package pl.futurecollars.invoicing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.futurecollars.invoicing.model.Company;

import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
}
