package com.powerfin.actions.inventory;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;
import org.openxava.model.*;
import org.openxava.util.*;
import org.openxava.validators.*;

import com.powerfin.helper.*;
import com.powerfin.model.*;

public class AccountItemSaveAction extends SaveAction {

	
	@SuppressWarnings("unchecked")
	public void execute() throws Exception {
		
		String accountId = getView().getRoot().getValueString("accountId");
		String code = getView().getRoot().getValueString("code");
		String name = getView().getRoot().getValueString("name");
		String alternateCode=getView().getRoot().getValueString("alternateCode");
		String productId = getView().getSubview("product").getValueString("productId");

		validate();
		
		if (getView().isKeyEditable()) {

			Account accountParent = AccountHelper.createAccount(productId, null, null, name, code, alternateCode);

			getView().setValue("accountId", accountParent.getAccountId());

			addMessage("account_created", accountParent.getClass().getName());
			
			TransactionModule transactionModule = XPersistence.getManager().find(TransactionModule.class,
					"INVOICE_PURCHASE");

			TransactionModuleAccount accountModule = new TransactionModuleAccount();
			accountModule.setAccount(accountParent);
			accountModule.setTransactionModule(transactionModule);
			accountModule.setName(accountParent.getName());
			XPersistence.getManager().persist(accountModule);

			transactionModule = XPersistence.getManager().find(TransactionModule.class, "INVOICE_SALE");

			accountModule = new TransactionModuleAccount();
			accountModule.setAccount(accountParent);
			accountModule.setTransactionModule(transactionModule);
			accountModule.setName(accountParent.getName());
			XPersistence.getManager().persist(accountModule);
			
		} else {
			Account accountModifi = XPersistence.getManager().find(Account.class, accountId);
			accountModifi.setCode(code);
			accountModifi.setName(name);
			accountModifi.setAlternateCode(alternateCode);
			Product product = XPersistence.getManager().find(Product.class, productId);
			accountModifi.setProduct(product);
			accountModifi.setTransactionalName(name);
			AccountHelper.updateAccount(accountModifi);

			addMessage("account_modified", accountModifi.getClass().getName());
			
			List<TransactionModuleAccount> accounts_module = XPersistence.getManager()
					.createQuery("" + "from TransactionModuleAccount where account.accountId=:accountItemId")
					.setParameter("accountItemId", accountModifi.getAccountId()).getResultList();
			for (TransactionModuleAccount accountModuleMod : accounts_module) {
				accountModuleMod.setName(name);
				XPersistence.getManager().merge(accountModuleMod);
			}
		}
		super.setResetAfter(false);
		super.execute();

	}
	
	private void validate() throws Exception {
		
		Messages errors = MapFacade.validate(getModelName(), getValuesToSave());
		if (errors.contains()) throw new ValidationException(errors);
		
	}
}
