package pl.futurecollars.invoicing.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class Invoice {

    private UUID id = UUID.randomUUID();
    private LocalDateTime date;
    private Company from;
    private Company to;
    private List<InvoiceEntry> invoiceEntries;

    public Invoice(LocalDateTime date, Company from, Company to, List<InvoiceEntry> invoiceEntries) {
        this.date = date;
        this.from = from;
        this.to = to;
        this.invoiceEntries = invoiceEntries;
    }
}
