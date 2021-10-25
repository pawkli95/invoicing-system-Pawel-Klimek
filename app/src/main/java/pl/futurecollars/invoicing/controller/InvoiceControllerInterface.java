package pl.futurecollars.invoicing.controller;

import io.swagger.annotations.ApiOperation;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import pl.futurecollars.invoicing.dto.InvoiceDto;
import pl.futurecollars.invoicing.model.Invoice;

public interface InvoiceControllerInterface {

    @ApiOperation(value = "Add new invoice")
    @PostMapping
    ResponseEntity<InvoiceDto> saveInvoice(@RequestBody InvoiceDto invoice);

    @ApiOperation(value = "Get invoices based on parameters")
    @GetMapping(produces = {"application/json;charset=UTF-8"})
    ResponseEntity<List<InvoiceDto>> getAll(@RequestParam(value = "before", required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate before,
                                         @RequestParam(value = "after", required = false)
                                         @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate after,
                                         @RequestParam(value = "sellerTaxId", required = false) String sellerTaxId,
                                         @RequestParam(value = "buyerTaxId", required = false) String buyerTaxId);

    @ApiOperation(value = "Get invoice by id")
    @GetMapping("/{id}")
    ResponseEntity<InvoiceDto> getById(@PathVariable UUID id) throws NoSuchElementException;

    @ApiOperation(value = "Update invoice")
    @PutMapping
    ResponseEntity<InvoiceDto> update(@RequestBody InvoiceDto updatedInvoice) throws NoSuchElementException;

    @ApiOperation(value = "Delete invoice")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable UUID id) throws NoSuchElementException;
}
