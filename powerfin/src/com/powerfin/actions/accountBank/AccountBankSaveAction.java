package com.powerfin.actions.accountBank;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;
import org.openxava.model.*;

import com.powerfin.helper.*;
import com.powerfin.model.*;
import com.powerfin.model.types.*;

public class AccountBankSaveAction extends SaveAction {

	@SuppressWarnings("rawtypes")
	public void execute() throws Exception {

		String productId = getView().getSubview("product").getValueString(
				"productId");
		String code = getView().getRoot().getValueString("code");
		String name = getView().getRoot().getValueString("name");
		String bookAccountId = getView().getSubview("bookAccount")
				.getValueString("bookAccountId");
		BookAccount bookAccount = XPersistence.getManager().find(
				BookAccount.class, bookAccountId);

		String accountId = getView().getValueString("accountId");
		
		if (getView().isKeyEditable()) { // On Create Account

			Account account = AccountHelper.createAccount(accountId, productId, null,
					null, name, code, null);
			getView().setValue("accountId", account.getAccountId());
		}
		super.execute();

		if (getErrors().isEmpty()) {

			Map keyValues = getView().getKeyValues();
			AccountBank accountBank = (AccountBank) MapFacade
					.findEntity(getView().getModelName(), keyValues);

			Category category = XPersistence.getManager().find(Category.class,
					CategoryHelper.BALANCE_CATEGORY);

			CategoryAccount categoryAccount = new CategoryAccount();
			categoryAccount.setAccount(accountBank.getAccount());
			categoryAccount.setBookAccount(bookAccount.getBookAccountId());
			categoryAccount.setCategory(category);
			categoryAccount.setAllowsNegativeBalance(Types.YesNoIntegerType.NO);
			XPersistence.getManager().persist(categoryAccount);
		}

	}
}
