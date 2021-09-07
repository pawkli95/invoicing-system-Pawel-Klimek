package pl.futurecollars.invoicing.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Invoice {

    private UUID id;
    private LocalDateTime date;
    private Company from;
    private Company to;
    private List<InvoiceEntry> invoiceEntries;

    public Invoice(LocalDateTime date, Company from, Company to, List<InvoiceEntry> invoiceEntries) {
        this.id = UUID.randomUUID();
        this.date = date;
        this.from = from;
        this.to = to;
        this.invoiceEntries = invoiceEntries;
    }
}
