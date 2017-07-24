package com.powerfin.actions.accountInvoice;

import java.util.*;

import org.openxava.actions.SaveAction;
import org.openxava.jpa.XPersistence;

import com.powerfin.exception.*;
import com.powerfin.helper.AccountHelper;
import com.powerfin.model.*;

public class AccountInvoiceOrderPurchaseSaveAction extends SaveAction{
	private String transactionModuleId;
	private String accountStatusId;
	Integer branchId = null;
	
	@SuppressWarnings("unchecked")
	public void execute() throws Exception {
		
		String accountId = getView().getValueString("accountId");
		accountStatusId = getView().getSubview("accountStatus").getValueString("accountStatusId");
		String productId = getView().getSubview("product").getValueString("productId");
		Integer personId = getView().getSubview("person").getValueInt("personId");
		
		Map<String, Integer> branchMap = (Map<String, Integer>) getView().getRoot().getValue("branch");
		branchId = (Integer)branchMap.get("branchId");
		
		if (branchId == null)
			throw new OperativeException("branch_is_required");
		
		if (getView().isKeyEditable()) { //Create Account
			Account account = AccountHelper.createAccount(productId, personId, accountStatusId, null, "", null, branchId);
			getView().setValue("accountId", account.getAccountId());
			addMessage("account_created", account.getClass().getName());
		}else
		{
			Account account = XPersistence.getManager().find(Account.class, accountId);
			account.setPerson(XPersistence.getManager().find(Person.class, personId));
			account.setBranch(XPersistence.getManager().find(Branch.class, branchId));
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