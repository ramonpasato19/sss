package com.powerfin.actions.search;

import org.openxava.actions.*;

public class SearchAccountByProductType extends ReferenceSearchAction { 

	private String productType;
	
	public void execute() throws Exception {
		super.execute();
		getTab().setPropertiesNames("accountId, name, code, currency.currencyId");
		getTab().setBaseCondition("${product.productType.productTypeId} in ("+getProductType()+")");
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}
	
	
}
