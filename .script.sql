CREATE TABLE
    pos
    (
        pos_id character VARYING (10) NOT NULL,
        name CHARACTER VARYING (100) not null,
        branch_id INTEGER NOT NULL,
        establishment_code CHARACTER VARYING(10),
        emission_point_code CHARACTER VARYING(10),
        sequential_name CHARACTER VARYING(100),
        authorization_code CHARACTER VARYING(100),
        CONSTRAINT pos_pk PRIMARY KEY (pos_id),
        CONSTRAINT pos_branch_fk FOREIGN KEY (branch_id) REFERENCES branch (branch_id)
    );

CREATE TABLE
    pos_user
    (
        pos_user_id CHARACTER VARYING(32) NOT NULL,
        user_name CHARACTER VARYING(30) NOT NULL,
        pos_id character VARYING (10) NOT NULL,
        CONSTRAINT pos_user_pk PRIMARY KEY (pos_user_id),
        CONSTRAINT pos_user_oxuser_fk FOREIGN KEY (user_name) REFERENCES "oxusers" ("name"),
        CONSTRAINT pos_user_pos_fk FOREIGN KEY (pos_id) REFERENCES "pos" ("pos_id"),
        UNIQUE (user_name)
    );

CREATE SEQUENCE pos002_sequence
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;

CREATE SEQUENCE pos003_sequence
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
  
ALTER TABLE account_item ADD COLUMN vat_tax INTEGER;
update account_item set vat_tax = 0;
ALTER TABLE account_item ALTER COLUMN vat_tax SET NOT NULL;

ALTER TABLE account_invoice ADD COLUMN pos_id CHARACTER VARYING(10);
ALTER TABLE account_invoice ADD CONSTRAINT acc_inv_pos_fk FOREIGN KEY (pos_id) REFERENCES pos (pos_id);

ALTER TABLE account_invoice_payment ADD COLUMN detail CHARACTER VARYING(400);
    
INSERT INTO operating_condition (operating_condition_id, name) VALUES ('E02', 'ENVIADO POR CORREO');
INSERT INTO operating_condition (operating_condition_id, name) VALUES ('E01', 'FIRMADO DIGITALMENTE');
