package com.powerfin.actions.search;

import org.openxava.actions.*;

public class SearchAccountItem extends ReferenceSearchAction {
	private String condition;

	public void execute() throws Exception {
		super.execute();
		getTab().setPropertiesNames("accountId, code, alternateCode, name");

		if (getCondition() != null && !getCondition().isEmpty())
			getTab().setBaseCondition(getCondition());
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

}
