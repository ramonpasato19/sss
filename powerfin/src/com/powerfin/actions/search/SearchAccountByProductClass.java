package com.powerfin.actions.search;

import org.openxava.actions.*;

public class SearchAccountByProductClass extends ReferenceSearchAction { 

	private String productClass;
	
	public void execute() throws Exception {
		super.execute();
		getTab().setPropertiesNames("accountId, name, code, currency.currencyId");
		getTab().setBaseCondition("${product.productType.productClass} in ("+getProductClass()+")");
	}

	public String getProductClass() {
		return productClass;
	}

	public void setProductClass(String productClass) {
		this.productClass = productClass;
	}

}
