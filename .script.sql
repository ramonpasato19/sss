CREATE SEQUENCE portfolio_recovery_management_sequence
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;

CREATE TABLE portfolio_recovery_management_type (
	portfolio_recovery_management_type_id CHARACTER VARYING(3) NOT NULL,
	name CHARACTER VARYING(50) NOT NULL,
	minimum_expired_days INTEGER NULL,
	maximum_expired_days INTEGER NULL,
	minimum_amount numeric(11,2) NULL,
	PRIMARY KEY (portfolio_recovery_management_type_id)
);


CREATE TABLE portfolio_recovery_management_status (
	portfolio_recovery_management_status_id CHARACTER VARYING(3) NOT NULL,
	name CHARACTER VARYING(50) NOT NULL,
	PRIMARY KEY (portfolio_recovery_management_status_id)
);

CREATE TABLE portfolio_recovery_management_detail_result (
	portfolio_recovery_management_detail_result_id CHARACTER VARYING(3) NOT NULL,
	description CHARACTER VARYING(50) NOT NULL,
	PRIMARY KEY (portfolio_recovery_management_detail_result_id)
);

CREATE TABLE portfolio_recovery_management (
	portfolio_recovery_management_id INTEGER NOT NULL,
	account_loan_id CHARACTER VARYING(20) NULL,
	portfolio_recovery_management_status_id CHARACTER VARYING(3) NULL,
	portfolio_recovery_management_type_id CHARACTER VARYING(3) NULL,
	begin_date date NULL,
	final_date date NULL,
	PRIMARY KEY (portfolio_recovery_management_id),
	FOREIGN KEY (portfolio_recovery_management_type_id) REFERENCES portfolio_recovery_management_type(portfolio_recovery_management_type_id),
	FOREIGN KEY (account_loan_id) REFERENCES account_loan(account_id),
	FOREIGN KEY (portfolio_recovery_management_status_id) REFERENCES portfolio_recovery_management_status(portfolio_recovery_management_status_id)
);

CREATE TABLE portfolio_recovery_management_detail (
	portfolio_recovery_management_detail_id CHARACTER VARYING(32) NOT NULL,
	management CHARACTER VARYING(4000) NOT NULL,
	overdue_quotas INTEGER NULL,
	overdue_balances numeric(11,2) NULL,
	overdue_from date NULL,
	capital_reduced numeric(11,2) NULL,
	default_interest numeric(11,2) NULL,
	interest numeric(11,2) NULL,
	vehicle_insurance numeric(11,2) NULL,
	mortgage_insurance numeric(11,2) NULL,
	receivable_fee numeric(11,2) NULL,
	collection_fee numeric(11,2) NULL,
	result_date date NULL,
	user_create CHARACTER VARYING(50) NULL,
	user_update CHARACTER VARYING(50) NULL,
	user_supervisor CHARACTER VARYING(50) NULL,
	date_create timestamp NULL,
	date_update timestamp NULL,
	portfolio_recovery_management_detail_result_id CHARACTER VARYING(3) NULL,
	portfolio_recovery_management_id CHARACTER VARYING(32) NULL,
	number_detail INTEGER NULL,
	PRIMARY KEY (portfolio_recovery_management_detail_id),
	FOREIGN KEY (portfolio_recovery_management_detail_result_id) REFERENCES portfolio_recovery_management_detail_result(portfolio_recovery_management_detail_result_id),
	FOREIGN KEY (portfolio_recovery_management_id) REFERENCES portfolio_recovery_management(portfolio_recovery_management_id)
);


INSERT INTO portfolio_recovery_management_status(portfolio_recovery_management_status_id, "name")VALUES('PEN', 'PENDIENTE');
INSERT INTO portfolio_recovery_management_status(portfolio_recovery_management_status_id, "name")VALUES('GES', 'GESTIONADA');
INSERT INTO portfolio_recovery_management_status(portfolio_recovery_management_status_id, "name")VALUES('ABO', 'ABONADA');
INSERT INTO portfolio_recovery_management_status(portfolio_recovery_management_status_id, "name")VALUES('PAG', 'PAGADA');

INSERT INTO portfolio_recovery_management_detail_result(portfolio_recovery_management_detail_result_id, description)VALUES('PAG', 'PAGADO');
INSERT INTO portfolio_recovery_management_detail_result(portfolio_recovery_management_detail_result_id, description)VALUES('ABO', 'ABONADO');
INSERT INTO portfolio_recovery_management_detail_result(portfolio_recovery_management_detail_result_id, description)VALUES('PPA', 'PROMESA DE PAGO');
INSERT INTO portfolio_recovery_management_detail_result(portfolio_recovery_management_detail_result_id, description)VALUES('MTE', 'MENSAJE CON TERCEROS');
INSERT INTO portfolio_recovery_management_detail_result(portfolio_recovery_management_detail_result_id, description)VALUES('MFA', 'MENSAJE CON FAMILIAR');
INSERT INTO portfolio_recovery_management_detail_result(portfolio_recovery_management_detail_result_id, description)VALUES('MWA', 'MENSAJE WHATSAPP');
INSERT INTO portfolio_recovery_management_detail_result(portfolio_recovery_management_detail_result_id, description)VALUES('SMS', 'MENSAJE DE TEXTO');
INSERT INTO portfolio_recovery_management_detail_result(portfolio_recovery_management_detail_result_id, description)VALUES('MCE', 'MENSAJE DE CORREO ELECTRONICO');
INSERT INTO portfolio_recovery_management_detail_result(portfolio_recovery_management_detail_result_id, description)VALUES('NPA', 'NEGACIÓN DE PAGO');
INSERT INTO portfolio_recovery_management_detail_result(portfolio_recovery_management_detail_result_id, description)VALUES('FAL', 'FALLECIDO');
INSERT INTO portfolio_recovery_management_detail_result(portfolio_recovery_management_detail_result_id, description)VALUES('VIS', 'VISITA');

INSERT INTO portfolio_recovery_management_type(portfolio_recovery_management_type_id, name, minimum_expired_days, maximum_expired_days, minimum_amount)VALUES('PRE', 'PREVENTIVO', 0, 29, 0.00);
INSERT INTO portfolio_recovery_management_type(portfolio_recovery_management_type_id, name, minimum_expired_days, maximum_expired_days, minimum_amount)VALUES('VEN', 'VENCIDO', 30, 120, 0.00);
INSERT INTO portfolio_recovery_management_type(portfolio_recovery_management_type_id, name, minimum_expired_days, maximum_expired_days, minimum_amount)VALUES('JUD', 'JUDICIAL', 121, 10000, 3000.00);

INSERT INTO report(report_id, "name", file, format)VALUES('PORTFOLIO_RECOVERY_MANAGEMENT', 'PORTFOLIO_RECOVERY_MANAGEMENT', 'xxx', 'EXCEL');
INSERT INTO action_report(action_report_id, reports)VALUES('COM.POWERFIN.ACTIONS.ACCOUNTLOAN.PRINTPORTFOLIORECOVERYMANAGEMENTDETAIL', 'PORTFOLIO_RECOVERY_MANAGEMENT');
