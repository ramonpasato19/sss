ALTER TABLE account_invoice_payment ADD COLUMN value NUMERIC(11,2);
ALTER TABLE pos ADD COLUMN retention_sequential_name CHARACTER VARYING (100);
ALTER TABLE pos ADD COLUMN account_id CHARACTER VARYING(20);
ALTER TABLE pos ADD CONSTRAINT pos_account_fk FOREIGN KEY (account_id) REFERENCES account (account_id);
ALTER TABLE person ADD COLUMN credit_limit NUMERIC(11,2);

ALTER TABLE account ADD COLUMN updated_on TIMESTAMP without TIME zone;
ALTER TABLE account ADD COLUMN updated_by CHARACTER VARYING(30);

ALTER TABLE account_invoice_detail ADD COLUMN tax_special_consumption NUMERIC(15,6);
ALTER TABLE account_invoice_detail ADD COLUMN tax_redeemable NUMERIC(15,6);

ALTER TABLE account_invoice_payment ADD COLUMN change NUMERIC(11,2);

drop view v_ats_sales;
ALTER TABLE account_invoice_detail ALTER COLUMN amount TYPE NUMERIC(15,6);
ALTER TABLE account_invoice_detail ALTER COLUMN tax_amount TYPE NUMERIC(15,6);

ALTER TABLE account_invoice ALTER COLUMN issue_date TYPE TIMESTAMP without TIME zone;

drop view view_control_inventory_detail;
drop view view_control_inventory;
ALTER TABLE account_item ALTER COLUMN average_value TYPE NUMERIC(15,6);
ALTER TABLE pos RENAME COLUMN account_id TO cash_account_id;
ALTER TABLE pos ADD COLUMN credit_card_account_id CHARACTER VARYING(20);
ALTER TABLE pos ADD COLUMN check_account_id CHARACTER VARYING(20);

CREATE TABLE
    account_item_branch
    (
        account_item_branch_id CHARACTER VARYING(32) NOT NULL,
        account_id CHARACTER VARYING(20) not null,
        branch_id INTEGER NOT NULL,
        average_cost NUMERIC(15,6),
        minimum_stock NUMERIC(13,4),
        maximum_stock NUMERIC(13,4),
        CONSTRAINT account_item_branch_pk PRIMARY KEY (account_item_branch_id),
        CONSTRAINT acc_item_br_item_fk FOREIGN KEY (account_id) REFERENCES account_item(account_id),
        CONSTRAINT acc_item_br_branch_fk FOREIGN KEY (branch_id) REFERENCES branch(branch_id),
        CONSTRAINT acc_item_br_uk UNIQUE (account_id, branch_id)
    );
    
CREATE TABLE
    price_list
    (
        price_list_id CHARACTER VARYING(1) NOT NULL,
        name CHARACTER VARYING(100) not null,
        CONSTRAINT price_list_pk PRIMARY KEY (price_list_id)
    );
    
CREATE TABLE
    account_item_price
    (
        account_item_price_id CHARACTER VARYING(32) NOT NULL,
        account_id CHARACTER VARYING(20) not null,
        price_list_id CHARACTER VARYING(1) NOT NULL,
        price numeric (13,4),
        CONSTRAINT acc_item_price_pk PRIMARY KEY (account_item_price_id),
        CONSTRAINT acc_item_br_item_fk FOREIGN KEY (account_id) REFERENCES account_item(account_id),
        CONSTRAINT acc_item_pr_list_fk FOREIGN KEY (price_list_id) REFERENCES price_list(price_list_id),
        CONSTRAINT acc_item_price_uk UNIQUE (account_id, price_list_id)
    );
    
ALTER TABLE pos ADD COLUMN price_list_id CHARACTER VARYING(1);
ALTER TABLE pos ADD CONSTRAINT pos_price_list_fk FOREIGN KEY (price_list_id) REFERENCES price_list (price_list_id);

ALTER TABLE account_item ALTER COLUMN cost DROP NOT NULL;
ALTER TABLE account_item ALTER COLUMN price DROP NOT NULL;
ALTER TABLE account_item ALTER COLUMN inventoried DROP NOT NULL;
ALTER TABLE account_item ALTER COLUMN unit_measure DROP NOT NULL;
ALTER TABLE account_invoice_detail ADD COLUMN due_date date;
--ok

---
UPDATE person set credit_limit = 0;
INSERT INTO parameter (parameter_id, value, description, type) VALUES ('DISCOUNT_VOUCHER_PRODUCT_ID', '208', null, null);
INSERT INTO parameter (parameter_id, value, description, type) VALUES ('DEFAULT_PRICE_LIST_ID', 'A', null, null);
INSERT INTO product (product_id, product_type_id, currency_id, name, interest_rate, prefix, sufix, sequence_db_name, single_account, own_product, lpad, rpad, default_interest_type, days_grace, days_grace_collection_fee, apply_default_interest_accrued, apply_automatic_debit, operating_condition_id, sale_portfolio_utility_distribution) VALUES ('209', '200', 'USD', 'BONOS VARIOS', 0.00, 'NID', '', 'PRODUCT_ACCOUNT_DISCOUNT_VOUCHER', 1, 0, '000000', '', 'R', 0, 0, 1, 0, '001', null);

CREATE SEQUENCE product_account_discount_voucher INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START WITH 1 NO CYCLE;