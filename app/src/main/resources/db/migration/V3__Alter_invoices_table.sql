ALTER TABLE invoices 
ADD COLUMN buyer_tax_id varchar(10) NOT NULL,
ADD COLUMN seller_tax_id varchar(10) NOT NULL;

ALTER TABLE invoices
ADD CONSTRAINT buyer_fk FOREIGN KEY (buyer_tax_id)
REFERENCES companies(tax_id);

ALTER TABLE invoices
ADD CONSTRAINT seller_fk FOREIGN KEY (seller_tax_id)
REFERENCES companies(tax_id);


