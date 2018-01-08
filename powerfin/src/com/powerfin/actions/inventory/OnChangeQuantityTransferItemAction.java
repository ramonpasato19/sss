package com.powerfin.actions.inventory;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.openxava.actions.OnChangePropertyBaseAction;

public class OnChangeQuantityTransferItemAction  extends OnChangePropertyBaseAction{

	@Override
	public void execute() throws Exception {
		
		if (getNewValue() == null)
			return;

		BigDecimal cost = (BigDecimal)getView().getValue("averageCost");
		BigDecimal quantity = (BigDecimal)getView().getValue("quantity");
		BigDecimal total = null;
		
		if (cost!=null && quantity!=null)
			total = quantity.multiply(cost).setScale(6, RoundingMode.HALF_UP);
		
		if (total!=null && total.compareTo(BigDecimal.ZERO)>0)
			getView().setValue("value", total);
		else
			getView().setValue("value", null);
	}
}
