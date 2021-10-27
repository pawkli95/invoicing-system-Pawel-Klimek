package pl.futurecollars.invoicing.db.jpa;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.futurecollars.invoicing.model.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    @Override
    @Query("select distinct i from Invoice i left join fetch i.invoiceEntries")
    List<Invoice> findAll();
}
