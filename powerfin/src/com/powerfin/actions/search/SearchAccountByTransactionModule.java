package com.powerfin.actions.search;

import java.util.*;

import org.openxava.actions.*;

import com.powerfin.exception.*;

public class SearchAccountByTransactionModule extends ReferenceSearchAction { 

	private String transactionModuleId;
	
	@SuppressWarnings("unchecked")
	public void execute() throws Exception {
		Map<String, String> transactionCurrency = (Map<String, String>) getView().getRoot().getValue("currency");
		if (transactionCurrency==null || 
				transactionCurrency.get("currencyId")==null ||
				transactionCurrency.get("currencyId").isEmpty()) {
			throw new OperativeException("the_transaction_currency_is_required");
		}
		
		super.execute();

		getTab().setPropertiesNames("accountId, transactionalName, code, currency.currencyId");
			
		getTab().setBaseCondition("${currency.currencyId} = '"+(String)transactionCurrency.get("currencyId")+"' "
				+ "AND ${accountId} IN (SELECT tma.account.accountId "
				+ "FROM TransactionModuleAccount tma "
				+ "WHERE tma.transactionModule.transactionModuleId = '"+getTransactionModuleId()+"') ");
		
		
	}

	public String getTransactionModuleId() {
		return transactionModuleId;
	}

	public void setTransactionModuleId(String transactionModuleId) {
		this.transactionModuleId = transactionModuleId;
	}
	
}