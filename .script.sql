
ALTER TABLE transaction ADD COLUMN origination_branch_id INTEGER;
ALTER TABLE transaction ADD CONSTRAINT tran_ori_branch_fk FOREIGN KEY (origination_branch_id) REFERENCES branch (branch_id);

ALTER TABLE transaction ADD COLUMN destination_branch_id INTEGER;
ALTER TABLE transaction ADD CONSTRAINT tran_dest_branch_fk FOREIGN KEY (destination_branch_id) REFERENCES branch (branch_id);

ALTER TABLE account ADD COLUMN branch_id INTEGER;
UPDATE account SET branch_id = 1;
ALTER TABLE account ALTER COLUMN branch_id SET NOT NULL;
ALTER TABLE account ADD CONSTRAINT account_branch_fk FOREIGN KEY (branch_id) REFERENCES branch (branch_id);

ALTER TABLE transaction_account ADD COLUMN branch_id INTEGER;
UPDATE transaction_account SET branch_id = 1;
ALTER TABLE transaction_account ALTER COLUMN branch_id SET NOT NULL;
ALTER TABLE transaction_account ADD CONSTRAINT tran_account_branch_fk FOREIGN KEY (branch_id) REFERENCES branch (branch_id);

ALTER TABLE movement ADD COLUMN branch_id INTEGER;
UPDATE movement SET branch_id = 1;
ALTER TABLE movement ALTER COLUMN branch_id SET NOT NULL;
ALTER TABLE movement ADD CONSTRAINT movement_branch_fk FOREIGN KEY (branch_id) REFERENCES branch (branch_id);

ALTER TABLE balance ADD COLUMN branch_id INTEGER;
UPDATE balance SET branch_id = 1;
ALTER TABLE balance ALTER COLUMN branch_id SET NOT NULL;
ALTER TABLE balance ADD CONSTRAINT balance_branch_fk FOREIGN KEY (branch_id) REFERENCES branch (branch_id);

ALTER TABLE balance DROP CONSTRAINT balance_uk;
ALTER TABLE balance ADD CONSTRAINT balance_uk UNIQUE (account_id, subaccount, category_id, branch_id, to_date);

ALTER TABLE balance ADD CONSTRAINT balance_ck_fd_td CHECK (FROM_DATE<=TO_DATE);


INSERT INTO parameter (parameter_id, value, description, type) VALUES ('MAIN_BRANCH_ID', '1', 'SUCURSAL PRINCIPAL', null);

INSERT INTO transaction_module (transaction_module_id, name, prefix, sequence_db_name, financial_transaction_status_id, default_transaction_status_id, sufix, lpad, rpad) VALUES ('TRANSFERITEM', 'TRANSFERENCIA DE ITEMS ENTRE SUCURSALES', 'TX', 'VOUCHER', '002', '001', '', '000000', '');

insert into branch_user
select name, name, 1 from oxusers;