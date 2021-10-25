package pl.futurecollars.invoicing.model;

import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue
    @ApiModelProperty(value = "Id", example = "f77de595-58a1-4533-b96b-e493aee29e8a")
    private UUID id;

    @Column(unique = true)
    @ApiModelProperty(value = "Invoice number(given by user)", required = true, example = "12/07/19999329")
    private String number;

    @Column(name = "issue_date", nullable = false)
    @ApiModelProperty(value = "Date of invoice creation", required = true, example = "2021-09-10T14:49:35.9239111")
    private LocalDateTime date;

    @ManyToOne(cascade ={CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "seller_id", nullable = false)
    @ApiModelProperty(value = "Company who sold the product", required = true)
    private Company seller;

    @ManyToOne(cascade ={CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "buyer_id", nullable = false)
    @ApiModelProperty(value = "Company who bought the product", required = true)
    private Company buyer;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL,
            orphanRemoval = true)
    @JoinColumn(name = "invoice_id", nullable = false)
    @ApiModelProperty(value = "List of products", required = true)
    private List<InvoiceEntry> invoiceEntries;
}
