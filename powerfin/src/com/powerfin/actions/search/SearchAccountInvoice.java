package com.powerfin.actions.search;

import org.openxava.actions.*;

public class SearchAccountInvoice extends ReferenceSearchAction { 

	private String condition;
	
	public void execute() throws Exception {
		super.execute();
		getTab().setPropertiesNames("account.accountId, account.code, account.person.name, account.accountStatus.name, issueDate, subtotal, vat, total, account.currency.currencyId");
		if(getCondition()!=null && !getCondition().isEmpty())
			getTab().setBaseCondition(getCondition());
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

}
