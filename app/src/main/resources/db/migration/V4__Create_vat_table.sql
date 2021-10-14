CREATE TABLE vat
(
name varchar(10) NOT NULL,
rate numeric(3,2) NOT NULL,
PRIMARY KEY (name)
);

INSERT INTO vat
(name, rate)
VALUES
('VAT_23', 0.23),
('VAT_8', 0.08),
('VAT_5', 0.05),
('VAT_0', 0.00);
