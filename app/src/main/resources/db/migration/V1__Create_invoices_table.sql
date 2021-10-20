CREATE TABLE invoices
(
id uuid UNIQUE NOT NULL,
issue_date timestamp NOT NULL,
PRIMARY KEY (id)
);
