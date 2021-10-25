package pl.futurecollars.invoicing.dto.mappers;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import pl.futurecollars.invoicing.dto.InvoiceDto;
import pl.futurecollars.invoicing.model.Invoice;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    public InvoiceDto toDto(Invoice invoice);

    public Invoice toEntity(InvoiceDto invoiceDto);
}
