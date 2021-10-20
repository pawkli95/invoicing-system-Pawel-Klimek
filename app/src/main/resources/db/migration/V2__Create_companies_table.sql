CREATE TABLE companies
(
tax_id varchar(10) UNIQUE NOT NULL,
company_name varchar(50) NOT NULL,
address varchar(100) NOT NULL,
health_insurance numeric(50, 2) NOT NULL,
pension_insurance numeric(50, 2) NOT NULL,
PRIMARY KEY (tax_id)
);

