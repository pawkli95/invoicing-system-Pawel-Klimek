package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
public class Invoice {

    @ApiModelProperty(value = "Invoice id", required = true, example = "f77de595-58a1-4533-b96b-e493aee29e8a")
    private UUID id;
    @ApiModelProperty(value = "Date of invoice creation", required = true, example = "2021-09-10T14:49:35.9239111")
    private LocalDateTime date;
    @ApiModelProperty(value = "Company who sold the product", required = true)
    private Company seller;
    @ApiModelProperty(value = "Company who bought the product", required = true)
    private Company buyer;
    @ApiModelProperty(value = "List of products", required = true)
    private List<InvoiceEntry> invoiceEntries;

    public Invoice(LocalDateTime date, Company seller, Company buyer, List<InvoiceEntry> invoiceEntries) {
        this.date = date;
        this.seller = seller;
        this.buyer = buyer;
        this.invoiceEntries = invoiceEntries;
    }
}
