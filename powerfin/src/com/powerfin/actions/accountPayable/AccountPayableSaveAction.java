package com.powerfin.actions.accountPayable;

import org.openxava.actions.*;

import com.powerfin.helper.*;
import com.powerfin.model.*;

public class AccountPayableSaveAction extends SaveAction{

	public void execute() throws Exception {

		String productId = getView().getSubview("product").getValueString("productId");
		Integer personId = getView().getSubview("person").getValueInt("personId");
				
		if (getView().isKeyEditable()) { // On Create Account

			Account account = AccountHelper.createAccount(null, productId, personId,
					null, null, null, null);
			getView().setValue("accountId", account.getAccountId());
		}		
		super.execute();
		
	}
}
