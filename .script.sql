INSERT INTO operating_condition (operating_condition_id, "name") VALUES('ELE', 'COMPROBANTE EMITIDO ELECTRONICAMENTE');

ALTER TABLE account_retention ALTER COLUMN authorization_code TYPE varchar(50) USING authorization_code::varchar;

ALTER TABLE tax_type ADD COLUMN external_code character varying (10);
UPDATE tax_type SET external_code = '2' WHERE tax_type_id = '001';

ALTER TABLE account ADD COLUMN electronic_document_file character varying (32);
ALTER TABLE account ADD COLUMN physical_document_file character varying (32);
ALTER TABLE company ADD COLUMN electronic_signature_file character varying (32);


INSERT INTO parameter (parameter_id, value) VALUES ('mail.smtp.auth', 'true');
INSERT INTO parameter (parameter_id, value) VALUES ('mail.smtp.host', 'mail.grisbi.com.ec');
INSERT INTO parameter (parameter_id, value) VALUES ('mail.smtp.mail.sender', 'facturacion@grisbi.com.ec');
INSERT INTO parameter (parameter_id, value) VALUES ('mail.smtp.password', 'Facturacion2018');
INSERT INTO parameter (parameter_id, value) VALUES ('mail.smtp.port', '465');
INSERT INTO parameter (parameter_id, value) VALUES ('mail.smtp.socketFactory.class', 'javax.net.ssl.SSLSocketFactory');
INSERT INTO parameter (parameter_id, value) VALUES ('mail.smtp.socketFactory.fallback', 'false');
INSERT INTO parameter (parameter_id, value) VALUES ('mail.smtp.socketFactory.port', '465');
INSERT INTO parameter (parameter_id, value) VALUES ('mail.smtp.starttls.enable', 'true');
INSERT INTO parameter (parameter_id, value) VALUES ('mail.smtp.user', 'Facturacion');

INSERT INTO parameter (parameter_id, value) VALUES ('ELECTRONIC_VOUCHER_ENVIROMENT', '1');
INSERT INTO parameter (parameter_id, value, description, type) VALUES ('ELECTRONIC_RETENTION_PURCHASE_PDF', 'RETENTION_PURCHASE', null, null);
INSERT INTO parameter (parameter_id, value, description, type) VALUES ('ELECTRONIC_INVOICE_SALE_PDF', 'INVOICE_SALE', null, null);

INSERT INTO parameter (parameter_id, value, description, type) VALUES ('PASSWORD_ELECTRONIC_SIGNATURE', 'xxxxxx', '', '');

UPDATE "tax" SET "external_code" = '0' WHERE "tax_id" = 'IVA0';
UPDATE "tax" SET "external_code" = '7' WHERE "tax_id" = 'EXCENTIVA';
UPDATE "tax" SET "external_code" = '2' WHERE "tax_id" = 'IVA12';
UPDATE "tax" SET "external_code" = '2' WHERE "tax_id" = 'IVA14';
UPDATE "tax" SET "external_code" = '6' WHERE "tax_id" = 'NOOBJIVA';

ALTER TABLE account_loan ADD COLUMN codebtor_person_id integer;
ALTER TABLE account_loan ADD CONSTRAINT acc_loan_codebtor_person FOREIGN KEY (codebtor_person_id) REFERENCES person (person_id);
ALTER TABLE transaction_module ADD COLUMN allows_batch_process integer;
ALTER TABLE transaction_module ADD COLUMN allows_reverse_transaction integer;

ALTER TABLE transaction ADD COLUMN subaccount integer;
ALTER TABLE account ADD COLUMN remark CHARACTER VARYING(4000);
ALTER TABLE account_loan ALTER COLUMN days_grace DROP DEFAULT;
ALTER TABLE account_sold_paytable ADD COLUMN sale_subaccount INTEGER;
update account_sold_paytable set sale_subaccount = 0;
ALTER TABLE account_sold_paytable ALTER COLUMN sale_subaccount SET NOT NULL;