package com.powerfin.actions.search;

import java.util.*;

import org.openxava.actions.*;

import com.powerfin.exception.*;

public class SearchAccount extends ReferenceSearchAction { 

	private String condition;
	
	@SuppressWarnings("unchecked")
	public void execute() throws Exception {
		Map<String, String> transactionCurrency = (Map<String, String>) getView().getRoot().getValue("currency");
		if (transactionCurrency==null || 
				transactionCurrency.get("currencyId")==null ||
				transactionCurrency.get("currencyId").isEmpty()) {
			throw new OperativeException("the_transaction_currency_is_required");
		}
		super.execute();
		getTab().setPropertiesNames("accountId, name, code, currency.currencyId");
		
		//Set condition of the Transaction Currency
		getTab().setBaseCondition("${currency.currencyId} = '"+(String)transactionCurrency.get("currencyId")+"'");
				
		if(getCondition()!=null && !getCondition().isEmpty())
			getTab().setBaseCondition(getTab().getBaseCondition()+" AND "+getCondition());
		
				
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	
		
	
}
