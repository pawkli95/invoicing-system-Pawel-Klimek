package pl.futurecollars.invoicing.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.futurecollars.invoicing.model.Invoice;

import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
}
