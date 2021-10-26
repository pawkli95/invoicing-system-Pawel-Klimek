ALTER TABLE invoices 
ADD COLUMN buyer_id uuid NOT NULL;

ALTER TABLE invoices
ADD COLUMN seller_id uuid NOT NULL;

ALTER TABLE invoices
ADD CONSTRAINT buyer_fk FOREIGN KEY (buyer_id)
REFERENCES companies(id);

ALTER TABLE invoices
ADD CONSTRAINT seller_fk FOREIGN KEY (seller_id)
REFERENCES companies(id);


