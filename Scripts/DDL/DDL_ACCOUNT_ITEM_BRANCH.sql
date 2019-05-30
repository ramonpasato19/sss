/******************************************************************************************/
/* Se agregan las columnas de información de la ultima factura de compra                  */ 
/******************************************************************************************/
ALTER TABLE account_item_branch ADD last_cost_purchase numeric(15,6) NULL;
ALTER TABLE account_item_branch ADD last_account_invoice_id varchar NULL;
ALTER TABLE account_item_branch ADD CONSTRAINT account_invoice_branch_fk FOREIGN KEY (last_account_invoice_id) REFERENCES account(account_id);



