package com.powerfin.actions.inventory;

import org.openxava.actions.SaveAction;
import org.openxava.jpa.XPersistence;
import com.powerfin.helper.AccountHelper;
import com.powerfin.model.Account;
import com.powerfin.model.TransactionModule;
import com.powerfin.model.TransactionModuleAccount;

public class AccountItemSaveAction extends SaveAction {
	public void execute() throws Exception {
		String accountId= getView().getRoot().getValueString("accountId");
		String code = getView().getRoot().getValueString("code");
		String name= getView().getRoot().getValueString("name");
		String productId = getView().getSubview("product").getValueString("productId");

		if (getView().isKeyEditable()) {

			Account accountParent = AccountHelper.createAccount(productId, null, null, name, code, null);

			getView().setValue("accountId", accountParent.getAccountId());

			TransactionModule transactionModule=XPersistence.getManager().find(TransactionModule.class, "INVOICE_PURCHASE");

			TransactionModuleAccount accountModule=new TransactionModuleAccount();
			accountModule.setAccount(accountParent);
			accountModule.setTransactionModule(transactionModule);
			accountModule.setName(accountParent.getName());
			XPersistence.getManager().persist(accountModule);

			transactionModule=XPersistence.getManager().find(TransactionModule.class, "INVOICE_SALE");

			accountModule=new TransactionModuleAccount();
			accountModule.setAccount(accountParent);
			accountModule.setTransactionModule(transactionModule);
			accountModule.setName(accountParent.getName());
			XPersistence.getManager().persist(accountModule);
		}else{
			Account accountModifi = XPersistence.getManager().find(Account.class, accountId);
			accountModifi.setCode(code);
			accountModifi.setName(name);
			accountModifi.setTransactionalName(name);
			AccountHelper.updateAccount(accountModifi);
			TransactionModule transactionModule=XPersistence.getManager().find(TransactionModule.class, "INVOICE_PURCHASE");
			TransactionModuleAccount accountModuleMod=(TransactionModuleAccount) XPersistence.getManager()
			.createQuery("from TransactionModuleAccount where account.accountId='"+accountModifi.getAccountId()+"' and transactionModule.transactionModuleId='"+transactionModule.getTransactionModuleId()+"') ").getSingleResult();
			accountModuleMod.setName(name);
			XPersistence.getManager().persist(accountModuleMod);

			transactionModule=XPersistence.getManager().find(TransactionModule.class, "INVOICE_SALE");
			accountModuleMod=(TransactionModuleAccount) XPersistence.getManager()
			.createQuery("from TransactionModuleAccount where account.accountId='"+accountModifi.getAccountId()+"' and transactionModule.transactionModuleId='"+transactionModule.getTransactionModuleId()+"') ").getSingleResult();
			accountModuleMod.setName(name);
			XPersistence.getManager().persist(accountModuleMod);
		}
		super.setResetAfter(false);
		super.execute();

	}
}
