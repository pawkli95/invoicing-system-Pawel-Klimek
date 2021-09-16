package pl.futurecollars.invoicing.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.service.InvoiceService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<Invoice> saveInvoice(@RequestBody Invoice invoice) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.saveInvoice(invoice));
    }

    @GetMapping
    public ResponseEntity<List<Invoice>> getAll(@RequestParam(value = "before", required = false)
                                                    @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate before,
                                                @RequestParam(value = "after", required = false)
                                                @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate after,
                                                @RequestParam(value = "sellerId", required = false) UUID sellerId,
                                                @RequestParam(value = "buyerId", required = false) UUID buyerId) {
        Predicate<Invoice> invoicePredicate = Objects::nonNull;
        if (before != null) {
            invoicePredicate = invoicePredicate.and(invoice -> invoice.getDate().toLocalDate().isBefore(before));
        }
        if (after != null) {
            invoicePredicate = invoicePredicate.and(invoice -> invoice.getDate().toLocalDate().isAfter(after));
        }
        if (sellerId != null) {
            invoicePredicate = invoicePredicate.and(invoice -> invoice.getFrom().getId().equals(sellerId));
        }
        if (buyerId != null) {
            invoicePredicate = invoicePredicate.and(invoice -> invoice.getTo().getId().equals(buyerId));
        }
        return ResponseEntity.ok().body(invoiceService.filter(invoicePredicate));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getById(@PathVariable UUID id) throws NoSuchElementException {
        return ResponseEntity.ok().body(invoiceService.getById(id));
    }

    @PutMapping
    public ResponseEntity<Invoice> update(@RequestBody Invoice updatedInvoice) {
        return ResponseEntity.ok().body(invoiceService.updateInvoice(updatedInvoice));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) throws NoSuchElementException {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
