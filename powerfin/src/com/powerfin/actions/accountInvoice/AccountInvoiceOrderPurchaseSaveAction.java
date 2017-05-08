package com.powerfin.actions.accountInvoice;

import org.openxava.actions.SaveAction;
import org.openxava.jpa.XPersistence;

import com.powerfin.helper.AccountHelper;
import com.powerfin.model.Account;
import com.powerfin.model.AccountStatus;
import com.powerfin.model.Person;

public class AccountInvoiceOrderPurchaseSaveAction extends SaveAction{
	private String transactionModuleId;
	private String accountStatusId;
	
	public void execute() throws Exception {
		
		String accountId = getView().getValueString("accountId");
		accountStatusId = getView().getSubview("accountStatus").getValueString("accountStatusId");
		String productId = getView().getSubview("product").getValueString("productId");
		Integer personId = getView().getSubview("person").getValueInt("personId");
		if (getView().isKeyEditable()) { //Create Account
			Account account = AccountHelper.createAccount(productId, personId, accountStatusId, null, "", null);
			getView().setValue("accountId", account.getAccountId());
			addMessage("account_created", account.getClass().getName());
		}else
		{
			Account account = XPersistence.getManager().find(Account.class, accountId);
			account.setPerson(XPersistence.getManager().find(Person.class, personId));
			account.setAccountStatus(XPersistence.getManager().find(AccountStatus.class, accountStatusId));
			account = AccountHelper.updateAccount(account);
			addMessage("account_modified", account.getClass().getName());
		}
		
		super.execute();
	}

	
	public String getAccountStatusId() {
		return accountStatusId;
	}

	public void setAccountStatusId(String accountStatusId) {
		this.accountStatusId = accountStatusId;
	}


	public String getTransactionModuleId() {
		return transactionModuleId;
	}


	public void setTransactionModuleId(String transactionModuleId) {
		this.transactionModuleId = transactionModuleId;
	}
	
}