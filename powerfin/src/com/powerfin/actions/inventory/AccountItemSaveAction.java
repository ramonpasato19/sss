package com.powerfin.actions.inventory;

import java.util.List;

import org.openxava.actions.SaveAction;
import org.openxava.jpa.XPersistence;
import com.powerfin.helper.AccountHelper;
import com.powerfin.model.Account;
import com.powerfin.model.Product;
import com.powerfin.model.Stock;
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
			Product product = XPersistence.getManager().find(Product.class, productId);
			accountModifi.setProduct(product);
			accountModifi.setTransactionalName(name);
			AccountHelper.updateAccount(accountModifi);


			List<TransactionModuleAccount> accounts_module = XPersistence.getManager().createQuery(""
					+ "from TransactionModuleAccount where account.accountId=:accountItemId"
					)
					.setParameter("accountItemId", accountModifi.getAccountId())
					.getResultList();
			for (TransactionModuleAccount accountModuleMod : accounts_module){
				accountModuleMod.setName(name);
				XPersistence.getManager().merge(accountModuleMod);
			}
		}
		super.setResetAfter(false);
		super.execute();

	}
}
