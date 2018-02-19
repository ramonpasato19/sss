package com.powerfin.actions.inventory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.openxava.actions.OnChangePropertyBaseAction;
import com.powerfin.model.AccountItemTax;

public class AccountItemChangeValues extends OnChangePropertyBaseAction {

	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	public void execute() throws Exception {
		List<AccountItemTax> taxes=(List<AccountItemTax>) getView().getRoot().getValue("accountItemTax");
		BigDecimal total_percentage=BigDecimal.ZERO;
		Iterator tax_it=taxes.iterator();
		if(taxes==null)
			return;
		HashMap item;
		while(tax_it.hasNext()){
			item = (HashMap) tax_it.next();
			total_percentage=total_percentage.add((BigDecimal) item.values().toArray()[3]);
		}

		BigDecimal price=(BigDecimal) getView().getValue("price");
		if(price==null)
			return;

		BigDecimal total_taxes=BigDecimal.ZERO;
		if(total_percentage.compareTo(BigDecimal.ZERO)>0){
			total_taxes=price.multiply(total_percentage).divide(new BigDecimal("100"));
		}
		BigDecimal total=price.add(total_taxes);
		getView().getRoot().setValue("taxPrice", total_taxes);
		getView().getRoot().setValue("retailPrice",total);
		getView().getRoot().setValue("retailPriceAux",total);

	}
}
