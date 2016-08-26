package com.powerfin.actions.search;

import org.openxava.actions.*;

import com.powerfin.helper.*;

public class SearchOfficialAccount extends ReferenceSearchAction { 

	private String condition;
	
	public void execute() throws Exception {
		super.execute();
		getTab().setPropertiesNames("accountId, name, code, currency.currencyId");
		
		//Set condition of the Transaction Currency
		getTab().setBaseCondition("${currency.currencyId} = '"+CompanyHelper.getDefaultCurrency().getCurrencyId()+"'");
				
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
