package pl.futurecollars.invoicing.service;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;
import pl.futurecollars.invoicing.model.TaxCalculation;

@Service
@AllArgsConstructor
public class TaxCalculatorService {

    private Database database;

    public TaxCalculation getTaxCalculation(long taxId) throws NoSuchElementException {
        if (checkForTaxId(taxId)) {
            return TaxCalculation.builder()
                    .income(income(taxId))
                    .costs(costs(taxId))
                    .earnings(earnings(taxId))
                    .incomingVat(incomingVat(taxId))
                    .outgoingVat(outgoingVat(taxId))
                    .vatToReturn(vatToReturn(taxId))
                    .build();
        }
        throw new NoSuchElementException("No company with such tax id exists in database");

    }

    private BigDecimal calculate(Predicate<Invoice> predicate, Function<InvoiceEntry, BigDecimal> calculationFunction) {
        BigDecimal result = database.getAll().stream()
                .filter(predicate)
                .flatMap(invoice -> invoice.getInvoiceEntries().stream())
                .map(calculationFunction)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return result;
    }

    private BigDecimal income(long taxId) {
        Predicate<Invoice> predicate = invoice -> invoice.getFrom().getTaxIdentificationNumber() == taxId;
        return calculate(predicate, InvoiceEntry::getPrice);
    }

    private BigDecimal costs(long taxId) {
        Predicate<Invoice> predicate = invoice -> invoice.getTo().getTaxIdentificationNumber() == taxId;
        return calculate(predicate, InvoiceEntry::getPrice);
    }

    private BigDecimal incomingVat(long taxId) {
        Predicate<Invoice> predicate = invoice -> invoice.getFrom().getTaxIdentificationNumber() == taxId;
        return calculate(predicate, InvoiceEntry::getVatValue);
    }

    private BigDecimal outgoingVat(long taxId) {
        Predicate<Invoice> predicate = invoice -> invoice.getTo().getTaxIdentificationNumber() == taxId;
        return calculate(predicate, InvoiceEntry::getVatValue);
    }

    private BigDecimal earnings(long taxId) {
        return income(taxId).subtract(costs(taxId));
    }

    private BigDecimal vatToReturn(long taxId) {
        return incomingVat(taxId).subtract(outgoingVat(taxId));
    }

    private boolean checkForTaxId(long taxId) {
        Predicate<Invoice> sellerPredicate = invoice -> invoice.getFrom().getTaxIdentificationNumber() == taxId;
        Predicate<Invoice> invoicePredicate = sellerPredicate.or(invoice -> invoice.getTo().getTaxIdentificationNumber() == taxId);
        Optional<Invoice> optional = database
                .getAll().stream()
                .filter(invoicePredicate)
                .findAny();
        return optional.isPresent();
    }
}
