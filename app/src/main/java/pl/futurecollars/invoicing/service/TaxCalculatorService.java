package pl.futurecollars.invoicing.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;
import pl.futurecollars.invoicing.dto.TaxCalculation;

@Slf4j
@Service
@AllArgsConstructor
public class TaxCalculatorService {

    private final Database<Invoice> database;

    public TaxCalculation getTaxCalculation(Company company) throws NoSuchElementException {
        String taxId = company.getTaxIdentificationNumber();
        if (checkForTaxId(taxId)) {
            return TaxCalculation.builder()
                    .income(income(taxId))
                    .costs(costs(taxId))
                    .incomeMinusCosts(incomeMinusCosts(taxId))
                    .incomingVat(incomingVat(taxId))
                    .outgoingVat(outgoingVat(taxId))
                    .vatToReturn(vatToReturn(taxId))
                    .pensionInsurance(company.getPensionInsurance())
                    .incomeMinusCostsMinusPensionInsurance(incomeMinusCostsMinusPensionInsurance(company))
                    .taxCalculationBase(taxCalculationBase(company))
                    .incomeTax(incomeTax(company))
                    .healthInsurance9(healthInsurance_9(company))
                    .healthInsurance775(healthInsurance_7_75(company))
                    .incomeTaxMinusHealthInsurance(incomeTaxMinusHealthInsurance(company))
                    .finalIncomeTaxValue(finalIncomeTaxValue(company))
                    .build();
        }
        throw new NoSuchElementException("No company with such tax id exists in database");
    }

    private BigDecimal calculate(Predicate<Invoice> predicate, Function<InvoiceEntry, BigDecimal> calculationFunction) {
        return database.getAll().stream()
                .filter(predicate)
                .flatMap(invoice -> invoice.getInvoiceEntries().stream())
                .peek(InvoiceEntry::calculateVatValue)
                .map(calculationFunction)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal income(String taxId) {
        Predicate<Invoice> predicate = invoice -> invoice.getSeller().getTaxIdentificationNumber().equals(taxId);
        return calculate(predicate, InvoiceEntry::getPrice);
    }

    private BigDecimal costs(String taxId) {
        Predicate<Invoice> predicate = invoice -> invoice.getBuyer().getTaxIdentificationNumber().equals(taxId);
        return calculate(predicate, InvoiceEntry::getPrice).add(personalCarCosts(predicate));
    }

    private BigDecimal incomingVat(String taxId) {
        Predicate<Invoice> predicate = invoice -> invoice.getSeller().getTaxIdentificationNumber().equals(taxId);
        return calculate(predicate, InvoiceEntry::getVatValue);
    }

    private BigDecimal outgoingVat(String taxId) {
        Predicate<Invoice> predicate = invoice -> invoice.getBuyer().getTaxIdentificationNumber().equals(taxId);
        return personalCarRelatedVat(predicate).add(notPersonalCarRelatedVat(predicate));
    }

    private BigDecimal personalCarRelatedValue(Predicate<Invoice> predicate) {
        return database.getAll()
                .stream()
                .filter(predicate)
                .flatMap(i -> i.getInvoiceEntries().stream())
                .peek(InvoiceEntry::calculateVatValue)
                .filter(InvoiceEntry::isPersonalCar)
                .map(InvoiceEntry::getVatValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal notPersonalCarRelatedVat(Predicate<Invoice> predicate) {
        return database.getAll()
                .stream()
                .filter(predicate)
                .flatMap(i -> i.getInvoiceEntries().stream())
                .peek(InvoiceEntry::calculateVatValue)
                .filter(entry -> !entry.isPersonalCar())
                .map(InvoiceEntry::getVatValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal personalCarRelatedVat(Predicate<Invoice> predicate) {
        return (personalCarRelatedValue(predicate).divide(BigDecimal.valueOf(2))).setScale(2, RoundingMode.HALF_DOWN);
    }

    private BigDecimal personalCarCosts(Predicate<Invoice> predicate) {
        return (personalCarRelatedValue(predicate).divide(BigDecimal.valueOf(2))).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal incomeMinusCosts(String taxId) {
        return income(taxId).subtract(costs(taxId));
    }

    private BigDecimal vatToReturn(String taxId) {
        return incomingVat(taxId).subtract(outgoingVat(taxId));
    }

    private BigDecimal incomeMinusCostsMinusPensionInsurance(Company company) {
        String taxId = company.getTaxIdentificationNumber();
        BigDecimal pensionInsurance = company.getPensionInsurance();
        return incomeMinusCosts(taxId).subtract(pensionInsurance);
    }

    private BigDecimal taxCalculationBase(Company company) {
        return incomeMinusCostsMinusPensionInsurance(company).setScale(0, RoundingMode.HALF_DOWN);
    }

    private BigDecimal incomeTax(Company company) {
        return taxCalculationBase(company).multiply(BigDecimal.valueOf(0.19))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal healthInsurance_9(Company company) {
        return company.getHealthInsurance().multiply(BigDecimal.valueOf(0.09)).setScale(2, RoundingMode.HALF_DOWN);
    }

    private BigDecimal healthInsurance_7_75(Company company) {
        return company.getHealthInsurance().multiply(BigDecimal.valueOf(0.0775)).setScale(2, RoundingMode.HALF_DOWN);
    }

    private BigDecimal incomeTaxMinusHealthInsurance(Company company) {
        return incomeTax(company).subtract(healthInsurance_7_75(company));
    }

    private BigDecimal finalIncomeTaxValue(Company company) {
        return incomeTaxMinusHealthInsurance(company).setScale(0, RoundingMode.HALF_DOWN);
    }

    private boolean checkForTaxId(String taxId) {
        Predicate<Invoice> sellerPredicate = invoice -> invoice.getSeller().getTaxIdentificationNumber().equals(taxId);
        Predicate<Invoice> invoicePredicate = sellerPredicate.or(invoice -> invoice.getBuyer().getTaxIdentificationNumber().equals(taxId));
        Optional<Invoice> optional = database
                .getAll().stream()
                .filter(invoicePredicate)
                .findAny();
        return optional.isPresent();
    }
}
