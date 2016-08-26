package com.powerfin.actions.accountAccountant;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;
import org.openxava.model.*;

import com.powerfin.helper.*;
import com.powerfin.model.*;
import com.powerfin.model.types.*;

public class AccountAccountantSaveAction extends SaveAction {

	@SuppressWarnings("rawtypes")
	public void execute() throws Exception {

		String transactionalName = getView().getRoot()
				.getValueString("transactionalName");
		String productId = getView().getSubview("product").getValueString(
				"productId");
		String bookAccountId = getView().getSubview("bookAccount")
				.getValueString("bookAccountId");
		BookAccount bookAccount = XPersistence.getManager().find(
				BookAccount.class, bookAccountId);

		String accountId = getView().getValueString("accountId");
		
		if (getView().isKeyEditable()) { // On Create Account

			Account account = AccountHelper.createAccount(accountId, productId, null,
					null, bookAccount.getName(),
					bookAccount.getBookAccountId(), null);
			account.setTransactionalName(transactionalName);
			getView().setValue("accountId", account.getAccountId());
		}
		super.execute();

		if (getErrors().isEmpty()) {

			Map keyValues = getView().getKeyValues();
			AccountAccountant accountAccountant = (AccountAccountant) MapFacade
					.findEntity(getView().getModelName(), keyValues);

			Category category = XPersistence.getManager().find(Category.class,
					CategoryHelper.BALANCE_CATEGORY);

			CategoryAccount categoryAccount = new CategoryAccount();
			categoryAccount.setAccount(accountAccountant.getAccount());
			categoryAccount.setBookAccount(bookAccount.getBookAccountId());
			categoryAccount.setCategory(category);
			categoryAccount.setAllowsNegativeBalance(Types.YesNoIntegerType.YES);
			XPersistence.getManager().persist(categoryAccount);
		}

	}
}
