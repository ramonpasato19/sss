package com.powerfin.actions.inventory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.openxava.actions.SaveAction;
import org.openxava.jpa.XPersistence;
import com.powerfin.model.Account;
import com.powerfin.model.AccountItemLotsInvoice;

public class AccountItemLotsSaveAction extends SaveAction {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void execute() throws Exception {
		List<AccountItemLotsInvoice> accountItemLotsInvoices=(List<AccountItemLotsInvoice>) getView().getRoot().getValue("accountItemLotsInvoice");
		BigDecimal quantity=BigDecimal.ZERO;
		BigDecimal quantityCurrent=BigDecimal.ZERO;
		Iterator accountItemsLots_it=accountItemLotsInvoices.iterator();
		if(accountItemsLots_it==null)
			return;
		HashMap item, itemMap;
		Account accountItem;
		while(accountItemsLots_it.hasNext()){
			item = (HashMap) accountItemsLots_it.next();
			itemMap=(HashMap) item.values().toArray()[2];
			accountItem=XPersistence.getManager().find(Account.class, itemMap.values().toArray()[1]);
			if(accountItem.getProduct().getProductClass().getProductClassId().equals("02")){
				quantity=quantity.add((BigDecimal) item.values().toArray()[0]);
				quantityCurrent=quantityCurrent.add((BigDecimal) item.values().toArray()[0]);
			}else{
				quantityCurrent=quantityCurrent.subtract((BigDecimal) item.values().toArray()[0]);
			}
		}
		getView().getRoot().setValue("quantity", quantity);
		getView().getRoot().setValue("currentQuantity", quantityCurrent);
		super.setResetAfter(false);
		super.execute();
	}

}
