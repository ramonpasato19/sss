package com.powerfin.actions.search;

import org.openxava.actions.*;

import com.powerfin.exception.*;

public class SearchAccountDetail extends ReferenceSearchAction { 

	private String transactionModuleId;
	
	public void execute() throws Exception {
		super.execute();

		String accountId = (String)getViewInfo().getParent().getParent().getValue("accountId");
		String productId = (String) getViewInfo().getParent().getParent().getSubview("product").getValue("productId");
		
		if (accountId==null || accountId.isEmpty())
			throw new OperativeException("account_invoice_most_be_saved");
		
		if (productId==null || productId.isEmpty())
			throw new OperativeException("product_most_be_selected");

		getTab().setPropertiesNames("accountId, name, code, currency.currencyId");
		getTab().setBaseCondition("${accountId} IN (SELECT tma.account.accountId "
				+ "FROM TransactionModuleAccount tma "
				+ "WHERE tma.transactionModule.transactionModuleId = '"+getTransactionModuleId()+"') "
				+ "AND ${currency.currencyId} IN (SELECT p.currency.currencyId "
				+ "FROM Product p WHERE p.productId = '"+productId+"')");
	}

	public String getTransactionModuleId() {
		return transactionModuleId;
	}

	public void setTransactionModuleId(String transactionModuleId) {
		this.transactionModuleId = transactionModuleId;
	}
	
}