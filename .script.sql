ALTER TABLE product ADD COLUMN sale_portfolio_utility_distribution CHARACTER VARYING(10);
ALTER TABLE account_portfolio ADD COLUMN sale_portfolio_utility_distribution CHARACTER VARYING(10);
UPDATE negotiation_file_type SET name = 'ARCHIVO DE PRESTAMOS VENTA' WHERE negotiation_file_type_id = '101';
INSERT INTO negotiation_file_type (negotiation_file_type_id, name) VALUES ('102', 'ARCHIVO DE VENTA');
CREATE INDEX idx_balance_inverse ON balance (account_id, subaccount, category_id, from_date, to_date DESC);