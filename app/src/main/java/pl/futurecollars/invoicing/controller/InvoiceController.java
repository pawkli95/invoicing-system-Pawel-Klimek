package pl.futurecollars.invoicing.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
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
    public ResponseEntity<List<Invoice>> getAll() {
        return ResponseEntity.ok().body(invoiceService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok().body(invoiceService.getById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping
    public ResponseEntity<Invoice> update(@RequestBody Invoice updatedInvoice) {
        return ResponseEntity.ok().body(invoiceService.updateInvoice(updatedInvoice));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        try {
            invoiceService.deleteInvoice(id);
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/filterSeller/{companyId}")
    public ResponseEntity<List<Invoice>> filterFrom(@PathVariable UUID companyId) {
        Predicate<Invoice> predicate = invoice -> invoice.getFrom().getId().equals(companyId);
        return ResponseEntity.ok().body(invoiceService.filter(predicate));
    }

    @GetMapping("/filterBuyer/{companyId}")
    public ResponseEntity<List<Invoice>> filterTo(@PathVariable UUID companyId) {
        Predicate<Invoice> predicate = invoice -> invoice.getTo().getId().equals(companyId);
        return ResponseEntity.ok().body(invoiceService.filter(predicate));
    }

    @GetMapping("/filterBefore/{date}")
    public ResponseEntity<List<Invoice>> filterDateBefore(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        Predicate<Invoice> predicate = invoice -> invoice.getDate().toLocalDate().isBefore(date);
        return ResponseEntity.ok().body(invoiceService.filter(predicate));
    }

    @GetMapping("/filterAfter/{date}")
    public ResponseEntity<List<Invoice>> filterDateAfter(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        Predicate<Invoice> predicate = invoice -> invoice.getDate().toLocalDate().isAfter(date);
        return ResponseEntity.ok().body(invoiceService.filter(predicate));
    }

    @GetMapping("/filterBetween/{dateAfter}/{dateBefore}")
    public ResponseEntity<List<Invoice>> filterDateBetween(@PathVariable("dateAfter") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateAfter,
                                                           @PathVariable("dateBefore") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateBefore) {
        Predicate<Invoice> predicateAfter = invoice -> invoice.getDate().toLocalDate().isAfter(dateAfter);
        Predicate<Invoice> predicateBefore = invoice -> invoice.getDate().toLocalDate().isBefore(dateBefore);
        Predicate<Invoice> predicateBetween = predicateAfter.and(predicateBefore);
        return ResponseEntity.ok().body(invoiceService.filter(predicateBetween));
    }
}
