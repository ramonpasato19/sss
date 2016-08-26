package com.powerfin.actions.search;

import org.openxava.actions.*;

import com.powerfin.exception.*;

public class SearchFileType extends ReferenceSearchAction { 

	private String condition;
	
	@SuppressWarnings("unused")
	public void execute() throws Exception {
		Integer n = getPreviousView().getRoot().getValueInt("negotiationId");
	
		if (n==null) {
			throw new OperativeException("the_negotiation_not_found");
		}
		super.execute();
		getTab().setPropertiesNames("negotiationFileTypeId, name");
		
		//Set condition of the Negotiation File Type
		getTab().setBaseCondition( "${negotiationFileTypeId} NOT IN "
				+ "(select nf.negotiationFileType.negotiationFileTypeId "
				+ "from NegotiationFile nf "
				+ "where nf.negotiation.negotiationId = "+n+")");	
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	
		
	
}
