CREATE TABLE paytable_type
    (
	paytable_type_id CHARACTER VARYING(3) NOT NULL,
	name CHARACTER VARYING(100) NOT NULL,
	PRIMARY KEY (paytable_type_id)
    );

INSERT INTO paytable_type (paytable_type_id, name) VALUES ('001', 'PAGO INTERES PERIODICO');
INSERT INTO paytable_type (paytable_type_id, name) VALUES ('002', 'PAGO CAPITAL/INTERES PERIODICO');


CREATE TABLE operating_condition
(
   operating_condition_id character varying(3), 
   name character varying(100), 
   PRIMARY KEY (operating_condition_id)
);
insert into operating_condition values ('001','NORMAL');

ALTER TABLE account ADD COLUMN operating_condition_id CHARACTER VARYING (3);
UPDATE account set operating_condition_id = '001';
ALTER TABLE account ALTER COLUMN operating_condition_id SET NOT NULL;
ALTER TABLE account ADD CONSTRAINT account_operating_condition_fk FOREIGN KEY (operating_condition_id) REFERENCES operating_condition (operating_condition_id);

ALTER TABLE account_loan ADD COLUMN apply_automatic_debit INTEGER;
UPDATE account_loan set apply_automatic_debit = 0;
ALTER TABLE account_loan ALTER COLUMN apply_automatic_debit SET NOT NULL;

ALTER TABLE product ADD COLUMN apply_automatic_debit INTEGER default '0' not null;

ALTER TABLE product ADD COLUMN operating_condition_id CHARACTER VARYING (3);
UPDATE product set operating_condition_id = '001';
ALTER TABLE product ALTER COLUMN operating_condition_id SET NOT NULL;
ALTER TABLE product ADD CONSTRAINT product_operating_condition_fk FOREIGN KEY (operating_condition_id) REFERENCES operating_condition (operating_condition_id);

CREATE SEQUENCE product_account_term
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
CREATE TABLE account_term
(
	account_id CHARACTER VARYING(20) NOT NULL,
	previous_account_id CHARACTER VARYING(20),
	amount NUMERIC(11,2),
	paytable_type_id CHARACTER VARYING(3) NOT NULL,
	issue_date DATE,
	disbursement_date DATE,
	frecuency_id INTEGER,
	interest_rate NUMERIC(5,2),
	start_date_payment DATE,
	period INTEGER,
	contract_number CHARACTER VARYING(30),
	daily_rate NUMERIC(11,7),
	quotas_number INTEGER,
	payment_day INTEGER,
	disbursement_account_id CHARACTER VARYING(20),
	automatic_renewal INTEGER,
	number_renewals integer,
	user_registering CHARACTER VARYING(12) NOT NULL,
	registration_date TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
	CONSTRAINT account_term_pk PRIMARY KEY (account_id),
	CONSTRAINT frecuency_account_term_fk FOREIGN KEY (frecuency_id) REFERENCES frecuency (frecuency_id),
	CONSTRAINT account_term_fk FOREIGN KEY (account_id) REFERENCES account (account_id),
	CONSTRAINT paytable_type_term_fk FOREIGN KEY (paytable_type_id) REFERENCES paytable_type (paytable_type_id),
	CONSTRAINT account_term_previous_fk FOREIGN KEY (previous_account_id) REFERENCES account (account_id),
	CONSTRAINT account_term_disbursement_fk FOREIGN KEY (disbursement_account_id) REFERENCES account (account_id)
);

INSERT INTO transaction_module (transaction_module_id, name, prefix, sequence_db_name, financial_transaction_status_id, default_transaction_status_id, sufix, lpad, rpad) VALUES ('BATCHTERMINTERESTPROVISION', 'CONTABILIZACION DE PROVISION DE INTERES DE INVERSIONES', 'BTX', 'BATCH_VOUCHER', '002', '001', '', '0000000', '');
INSERT INTO batch_process_type (batch_process_type_id, name, activated, action_class, transaction_module_id, start_end_day) VALUES ('TERM_INTEREST_PROVISION', 'CONTABILIZACION DE INTERES DE INVERSIONES', 0, 'com.powerfin.actions.batch.TermInterestProvisionBatchSaveAction', 'BATCHTERMINTERESTPROVISION', 'S');

INSERT INTO transaction_module (transaction_module_id, name, prefix, sequence_db_name, financial_transaction_status_id, default_transaction_status_id, sufix, lpad, rpad) VALUES ('BATCHTERMPAYMENT', 'PAGO DE CAPITAL E INTERES DE INVERSIONES', 'BTX', 'BATCH_VOUCHER', '002', '001', '', '0000000', '');
INSERT INTO batch_process_type (batch_process_type_id, name, activated, action_class, transaction_module_id, start_end_day) VALUES ('TERM_PAYMENT', 'PAGO DE CAPITAL E INTERES DE INVERSIONES', 0, 'com.powerfin.actions.batch.TermPaymentBatchSaveAction', 'BATCHTERMPAYMENT', 'S');

INSERT INTO transaction_module (transaction_module_id, name, prefix, sequence_db_name, financial_transaction_status_id, default_transaction_status_id, sufix, lpad, rpad) VALUES ('BATCHTERMRENEWAL', 'RENOVACION AUTOMATICA DE INVERSION', 'BTX', 'BATCH_VOUCHER', '002', '001', '', '0000000', '');
INSERT INTO transaction_module (transaction_module_id, name, prefix, sequence_db_name, financial_transaction_status_id, default_transaction_status_id, sufix, lpad, rpad) VALUES ('TERMRENEWAL', 'RENOVACION DE INVERSION', 'TX', 'VOUCHER', '002', '001', '', '000000', '');

