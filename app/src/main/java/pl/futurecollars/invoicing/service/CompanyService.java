package pl.futurecollars.invoicing.service;

import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.repositories.CompanyRepository;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public Company saveCompany(Company company) {
        if (company != null) {
            return companyRepository.save(company);
        }
        return null;
    }

    public void delete(UUID id) throws NoSuchElementException {
        if (id != null && companyRepository.existsById(id)) {
            companyRepository.deleteById(id);
        } else {
            throw new NoSuchElementException();
        }

    }
}
