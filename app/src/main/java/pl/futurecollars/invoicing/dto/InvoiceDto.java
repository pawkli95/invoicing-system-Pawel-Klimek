package pl.futurecollars.invoicing.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.InvoiceEntry;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvoiceDto {

    @ApiModelProperty(value = "Id", example = "f77de595-58a1-4533-b96b-e493aee29e8a")
    private UUID id;

    @ApiModelProperty(value = "Invoice number(given by user)", required = true, example = "12/07/19999329")
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
