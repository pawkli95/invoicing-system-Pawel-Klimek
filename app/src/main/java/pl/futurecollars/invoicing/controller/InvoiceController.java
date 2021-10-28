package pl.futurecollars.invoicing.controller;

import io.swagger.annotations.Api;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.dto.InvoiceDto;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.service.InvoiceService;

@Api(tags = {"invoice-controller"})
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/invoices")
public class InvoiceController implements InvoiceControllerInterface {

    private final InvoiceService invoiceService;

    @Override
    public ResponseEntity<InvoiceDto> saveInvoice(@RequestBody InvoiceDto invoice) {
        log.debug("Request to save invoice");
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.saveInvoice(invoice));
    }

    @Override
    public ResponseEntity<List<InvoiceDto>> getAll(@RequestParam(value = "before", required = false)
                                                @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate before,
                                                @RequestParam(value = "after", required = false)
                                                @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate after,
                                                @RequestParam(value = "sellerTaxId", required = false) String sellerTaxId,
                                                @RequestParam(value = "buyerTaxId", required = false) String buyerTaxId) {
        log.debug("Request to return invoices");
        Predicate<Invoice> invoicePredicate = null;
        if (before != null || after != null || sellerTaxId != null || buyerTaxId != null) {
            invoicePredicate = Objects::nonNull;
        }
        if (before != null) {
            invoicePredicate = invoicePredicate.and(invoice -> invoice.getDate().toLocalDate().isBefore(before));
        }
        if (after != null) {
            invoicePredicate = invoicePredicate.and(invoice -> invoice.getDate().toLocalDate().isAfter(after));
        }
        if (sellerTaxId != null) {
            invoicePredicate = invoicePredicate.and(invoice -> invoice.getSeller().getTaxIdentificationNumber().equals(sellerTaxId));
        }
        if (buyerTaxId != null) {
            invoicePredicate = invoicePredicate.and(invoice -> invoice.getBuyer().getTaxIdentificationNumber().equals(buyerTaxId));
        }
        if (invoicePredicate == null) {
            return ResponseEntity.ok().body(invoiceService.getAll());
        } else {
            return ResponseEntity.ok().body(invoiceService.filter(invoicePredicate));
        }
    }

    @Override
    public ResponseEntity<InvoiceDto> getById(@PathVariable UUID id) throws NoSuchElementException {
        log.debug("Request to return invoice by id: " + id.toString());
        return ResponseEntity.ok().body(invoiceService.getById(id));
    }

    @Override
    public ResponseEntity<InvoiceDto> update(@RequestBody InvoiceDto updatedInvoice) throws NoSuchElementException {
        log.debug("Request to update invoice");
        return ResponseEntity.ok().body(invoiceService.updateInvoice(updatedInvoice));
    }

    @Override
    public ResponseEntity<Void> delete(@PathVariable UUID id) throws NoSuchElementException {
        log.debug("Request to delete invoice with id: " + id.toString());
        invoiceService.deleteInvoice(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
