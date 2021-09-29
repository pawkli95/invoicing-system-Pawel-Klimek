package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Invoice {

    @ApiModelProperty(value = "Invoice id", required = true, example = "f77de595-58a1-4533-b96b-e493aee29e8a")
    private UUID id;
    @ApiModelProperty(value = "Date of invoice creation", required = true, example = "2021-09-10T14:49:35.9239111")
    private LocalDateTime date;
    @ApiModelProperty(value = "Company who sold the product", required = true)
    private Company from;
    @ApiModelProperty(value = "Company who bought the product", required = true)
    private Company to;
    @ApiModelProperty(value = "List of products", required = true)
    private List<InvoiceEntry> invoiceEntries;

    public Invoice(LocalDateTime date, Company from, Company to, List<InvoiceEntry> invoiceEntries) {
        this.id = UUID.randomUUID();
        this.date = date;
        this.from = from;
        this.to = to;
        this.invoiceEntries = invoiceEntries;
    }
}
