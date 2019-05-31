package com.powerfin.actions.search;


import org.openxava.actions.ReferenceSearchAction;


public class SearchCategoryLocation extends ReferenceSearchAction{
	
	
	String condition = "";
	@SuppressWarnings("unchecked")
	public void execute() throws Exception {
		super.execute();
		getTab().setPropertiesNames("categoryId, name");
		getTab().setBaseCondition("${categoryId} IN (SELECT cat.categoryId "
				+ "FROM Category cat "
				+ "WHERE cat.categoryId in ('EXPIRED','SPOLIT'))");				
	}

}
