package com.powerfin.actions.search;

import org.openxava.actions.*;

public class SearchTax extends ReferenceSearchAction { 

	private String condition;
	
	public void execute() throws Exception {
		super.execute();
		getTab().setPropertiesNames("taxId, name, percentage");
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
