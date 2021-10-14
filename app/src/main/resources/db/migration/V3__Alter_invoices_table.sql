ALTER TABLE invoices 
ADD COLUMN buyer varchar(10) NOT NULL,
ADD COLUMN seller varchar(10) NOT NULL;

ALTER TABLE invoices
ADD CONSTRAINT buyer_fk FOREIGN KEY (buyer)
REFERENCES companies(tax_id);

ALTER TABLE invoices
ADD CONSTRAINT seller_fk FOREIGN KEY (seller)
REFERENCES companies(tax_id);


