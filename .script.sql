ALTER TABLE default_interest_rate ADD COLUMN daily_value numeric(11,2) DEFAULT 0;
ALTER TABLE default_interest_rate ADD COLUMN product_id CHARACTER VARYING (20);
ALTER TABLE default_interest_rate ADD CONSTRAINT def_int_rate_product_fk FOREIGN KEY (product_id) REFERENCES product (product_id);
ALTER TABLE product ADD COLUMN default_interest_type CHARACTER VARYING (1) default 'R' not null;
ALTER TABLE product ADD COLUMN days_grace INTEGER default '0' not null ;
ALTER TABLE product ADD COLUMN days_grace_collection_fee INTEGER default '0' not null ;
ALTER TABLE product ADD COLUMN apply_default_interest_accrued integer default '1' not null;
ALTER TABLE product DROP COLUMN default_interest_rate;

ALTER TABLE account_loan ADD COLUMN apply_default_interest_accrued integer default '1' not null;
ALTER TABLE account_loan DROP COLUMN default_interest_rate;
ALTER TABLE account_loan ALTER COLUMN day_grace SET DEFAULT 0;
ALTER TABLE account_loan ALTER COLUMN day_grace_collection_fee SET DEFAULT 0;
ALTER TABLE account_loan RENAME COLUMN day_grace TO days_grace;
ALTER TABLE account_loan RENAME COLUMN day_grace_collection_fee TO days_grace_collection_fee;
    
CREATE TABLE prelation_order
(
   prelation_order_id character varying(32), 
   product_id character varying(20) not null, 
   category_id character varying(10) not null, 
   allow_partial_payment integer not null,
   prelation_order integer not null default 0, 
   PRIMARY KEY (prelation_order_id), 
   FOREIGN KEY (product_id) REFERENCES product (product_id) ON UPDATE NO ACTION ON DELETE NO ACTION, 
   FOREIGN KEY (category_id) REFERENCES category (category_id) ON UPDATE NO ACTION ON DELETE NO ACTION
);
ALTER TABLE prelation_order ADD CONSTRAINT prelation_order_uk UNIQUE (product_id, category_id);
INSERT INTO transaction_module (transaction_module_id, name, prefix, sequence_db_name, financial_transaction_status_id, default_transaction_status_id, sufix, lpad, rpad) VALUES ('GENERALTRANSACTION', 'TRANSACCION GENERAL', 'TX', 'VOUCHER', '002', '001', '', '000000', '');
