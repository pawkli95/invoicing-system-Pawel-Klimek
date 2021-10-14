CREATE TABLE invoices
(
id uuid DEFAULT gen_random_uuid(),
issue_date timestamp NOT NULL,
PRIMARY KEY (id)
);
