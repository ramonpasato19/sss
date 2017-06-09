CREATE TABLE
    account_invoice_tax
    (
        account_invoice_tax_id CHARACTER VARYING(32) NOT NULL,
        account_invoice_id CHARACTER VARYING(20) NOT NULL,
        tax_id CHARACTER VARYING(10) NOT NULL,
        tax_percentage NUMERIC(5,2) NOT NULL,
        tax_base NUMERIC(11,2) NOT NULL,
        tax_amount NUMERIC(11,2) NOT NULL,
        CONSTRAINT account_invoice_tax_pk PRIMARY KEY (account_invoice_tax_id),
        CONSTRAINT account_inv_tax_account_inv_fk FOREIGN KEY (account_invoice_id) REFERENCES account_invoice (account_id),
        CONSTRAINT account_inv_tax_tax_fk FOREIGN KEY (tax_id) REFERENCES tax (tax_id)
    );

ALTER TABLE account_portfolio ADD COLUMN purchase_status_id CHARACTER VARYING(3);
ALTER TABLE account_portfolio ADD COLUMN sale_status_id CHARACTER VARYING(3);
update account_portfolio set purchase_status_id = '002', sale_status_id = '002';
ALTER TABLE account_portfolio ADD CONSTRAINT acc_por_pur_status_fk FOREIGN KEY (purchase_status_id) REFERENCES account_status (account_status_id);
ALTER TABLE account_portfolio ADD CONSTRAINT acc_por_sale_status_fk FOREIGN KEY (sale_status_id) REFERENCES account_status (account_status_id);
ALTER TABLE account_portfolio ALTER COLUMN purchase_status_id SET NOT NULL;
ALTER TABLE account_portfolio ALTER COLUMN sale_status_id SET NOT NULL;

drop table account_invoice_detail_tax;