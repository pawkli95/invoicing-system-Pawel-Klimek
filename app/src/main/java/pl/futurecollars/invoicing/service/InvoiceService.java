package pl.futurecollars.invoicing.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.dto.InvoiceDto;
import pl.futurecollars.invoicing.dto.mappers.InvoiceMapper;
import pl.futurecollars.invoicing.model.Invoice;

@RequiredArgsConstructor
@Service
public class InvoiceService {

    private final Database<Invoice> fileBasedDatabase;
    private final InvoiceMapper invoiceMapper;

    public InvoiceDto saveInvoice(InvoiceDto invoiceDto) {
        Invoice invoice = invoiceMapper.toEntity(invoiceDto);
        Invoice returnedInvoice = fileBasedDatabase.save(invoice);
        return invoiceMapper.toDto(returnedInvoice);
    }

    public InvoiceDto getById(UUID id) throws NoSuchElementException {
        return invoiceMapper.toDto(fileBasedDatabase.getById(id));
    }

    public List<InvoiceDto> getAll() {
        return fileBasedDatabase.getAll().stream()
                .map(invoiceMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<InvoiceDto> filter(Predicate<Invoice> predicate) {
        return fileBasedDatabase.getAll()
                .stream()
                .filter(predicate)
                .map(invoiceMapper::toDto)
                .collect(Collectors.toList());
    }

    public InvoiceDto updateInvoice(InvoiceDto updatedInvoice) throws NoSuchElementException {
        Invoice returnedInvoice = fileBasedDatabase.update(invoiceMapper.toEntity(updatedInvoice));
        return invoiceMapper.toDto(returnedInvoice);
    }

    public void deleteInvoice(UUID id) throws NoSuchElementException {
        fileBasedDatabase.delete(id);
    }
}
