package pl.futurecollars.invoicing.db.jpa;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.repositories.InvoiceRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class JpaDatabase implements Database<Invoice> {

    private final InvoiceRepository invoiceRepository;

    @Override
    public Invoice save(Invoice invoice) {
        if(invoice != null) {
            return invoiceRepository.save(invoice);
        }
        return null;
    }

    @Override
    public Invoice getById(UUID id) throws NoSuchElementException {
        Optional<Invoice> optional = invoiceRepository.findById(id);
        if(optional.isPresent()) {
            return optional.get();
        }
        throw new NoSuchElementException();
    }

    @Override
    public List<Invoice> getAll() {
        return invoiceRepository.findAll();
    }

    @Override
    public Invoice update(Invoice updatedInvoice) throws NoSuchElementException {
        if(updatedInvoice != null && invoiceRepository.existsById(updatedInvoice.getId())) {
            return invoiceRepository.save(updatedInvoice);
        }
        throw new NoSuchElementException();
    }

    @Override
    public void delete(UUID id) throws NoSuchElementException {
        if(id != null && invoiceRepository.existsById(id)) {
            invoiceRepository.deleteById(id);
        } else {
            throw new NoSuchElementException();
        }
    }
}


