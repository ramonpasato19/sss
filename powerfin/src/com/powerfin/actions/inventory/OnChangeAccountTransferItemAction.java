package com.powerfin.actions.inventory;

import java.math.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;

import com.powerfin.model.*;
import com.powerfin.model.types.*;

public class OnChangeAccountTransferItemAction  extends OnChangePropertyBaseAction{

	@Override
	public void execute() throws Exception {
		
		if (getNewValue() == null)
			return;
		
		String accountId = (String)getView().getSubview("account").getValue("accountId");
		AccountItem item = XPersistence.getManager().find(AccountItem.class , accountId);

		BigDecimal cost = item.getAverageValue();
		BigDecimal quantity = (BigDecimal)getView().getValue("quantity");
		BigDecimal total = null;
		
		if (cost!=null && quantity!=null)
			total = quantity.multiply(cost).setScale(6, RoundingMode.HALF_UP);
		
		if (total!=null && total.compareTo(BigDecimal.ZERO)>0)
			getView().setValue("value", total);
		else
			getView().setValue("value", null);
		
		getView().setValue("averageCost", cost);
		getView().setValue("debitOrCredit", Types.DebitOrCredit.CREDIT);		
	}

}
