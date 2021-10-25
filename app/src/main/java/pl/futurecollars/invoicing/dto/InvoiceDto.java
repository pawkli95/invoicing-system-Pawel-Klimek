package pl.futurecollars.invoicing.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.InvoiceEntry;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDto {

    private UUID id;

    private String number;
    @ApiModelProperty(value = "Date of invoice creation", required = true, example = "2021-09-10T14:49:35.9239111")
    private LocalDateTime date;
    @ApiModelProperty(value = "Company who sold the product", required = true)
    private Company seller;
    @ApiModelProperty(value = "Company who bought the product", required = true)
    private Company buyer;
    @ApiModelProperty(value = "List of products", required = true)
    private List<InvoiceEntry> invoiceEntries;
}
