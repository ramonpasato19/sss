package com.powerfin.actions.bookAccount;

import org.openxava.actions.*;

public class SearchBookAccount extends ReferenceSearchAction { 

	private String condition;
	
	public void execute() throws Exception {
		super.execute(); 
		getTab().setPropertiesNames("groupAccount.name, bookAccountId, name");
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
