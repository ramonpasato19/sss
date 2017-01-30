package com.powerfin.actions.inventory;


import org.openxava.actions.CreateNewElementInCollectionAction;
import com.powerfin.exception.OperativeException;

public class AccountItemPreSaveDetails extends
CreateNewElementInCollectionAction{

	@Override
	public void execute() throws Exception {

		String accountId = (String) getView().getValue("accountId");
		if (accountId==null || accountId.isEmpty()){
			if(getCollectionElementView().getModelName().compareTo("AccountItem.AccountItemTax")==0)
				throw new OperativeException("account_item_most_be_saved_tax");
			else
				throw new OperativeException("account_item_most_be_saved_category");
		}

		super.execute();
	}

}
