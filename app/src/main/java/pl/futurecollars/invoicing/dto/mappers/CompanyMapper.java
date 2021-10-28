package pl.futurecollars.invoicing.dto.mappers;

import org.mapstruct.Mapper;
import pl.futurecollars.invoicing.dto.CompanyDto;
import pl.futurecollars.invoicing.model.Company;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    Company toEntity(CompanyDto companyDto);

    CompanyDto toDto(Company company);

}
