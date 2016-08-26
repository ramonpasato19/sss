package com.powerfin.actions.account;

import org.openxava.actions.*;

public class SearchProductWhenCreatingAccount extends ReferenceSearchAction { 

	private String productType;
	
	public void execute() throws Exception {
		super.execute(); 
		getTab().setBaseCondition("${productType} = '"+getProductType()+"'");
		getTab().setPropertiesNames("productId, name");
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}
	
	
}
