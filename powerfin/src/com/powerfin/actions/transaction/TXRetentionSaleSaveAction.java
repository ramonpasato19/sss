package com.powerfin.actions.transaction;

import java.math.*;
import java.util.*;

import org.openxava.jpa.*;

import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.model.*;

public class TXRetentionSaleSaveAction extends TXSaveAction {

	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		Account account = getDebitAccount();
		AccountRetention retention = XPersistence.getManager().find(AccountRetention.class, account.getAccountId());

		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		
		if (retention.getDetails()==null || retention.getDetails().isEmpty() || retention.getTotal().compareTo(BigDecimal.ZERO)==0)
    		throw new OperativeException("retention_not_processed_with_out_detail");
    	
		transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(retention.getAccountInvoice().getAccount(), retention.getTotal(), transaction));
		
		for (AccountRetentionDetail detail: retention.getDetails())
			transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(retention.getAccount(), detail.getFinalAmount(), transaction, detail.getRetentionConcept().getCategory()));

		return transactionAccounts;
	}
	
	public void postSaveAction(Transaction transaction) throws Exception
	{
		if (TransactionHelper.isFinancialSaved(transaction))
		{
			Account account = transaction.getDebitAccount();
			account.setAccountStatus(AccountStatusHelper.getAccountStatus(AccountInvoiceHelper.STATUS_RETENTION_ACTIVE));
			AccountHelper.updateAccount(account);
			
			AccountRetention retention = XPersistence.getManager().find(AccountRetention.class, account.getAccountId());
			account = retention.getAccountInvoice().getAccount();
			account.setAccountStatus(AccountStatusHelper.getAccountStatus(AccountInvoiceHelper.STATUS_INVOICE_WITH_RETENTION));
			AccountHelper.updateAccount(account);
		}
	}
}
