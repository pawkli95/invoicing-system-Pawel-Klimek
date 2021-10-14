CREATE TABLE invoice_entries
(
id uuid DEFAULT gen_random_uuid(),
description varchar(50) NOT NULL,
price numeric(50, 2) NOT NULL,
vat_rate varchar(6) NOT NULL,
vat_value numeric(50, 2) NOT NULL,
personal_car boolean NOT NULL,
invoice_id uuid NOT NULL,
PRIMARY KEY (id)
);

ALTER TABLE invoice_entries
ADD CONSTRAINT fk_vat
FOREIGN KEY (vat_rate)
REFERENCES vat(name);

ALTER TABLE invoice_entries
ADD CONSTRAINT fk_invoice_id
FOREIGN KEY (invoice_id)
REFERENCES invoices(id)
ON DELETE CASCADE;


