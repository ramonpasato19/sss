package com.powerfin.actions.search;

import org.openxava.actions.*;

public class SearchPerson extends ReferenceSearchAction { 

private String condition;
	
	public void execute() throws Exception {
		super.execute();
		getTab().setPropertiesNames("personId, identification, name");
		getTab().setBaseCondition(getCondition());		
				
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	
		
	
}
