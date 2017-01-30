package com.powerfin.actions.transaction;

import java.math.*;
import java.util.*;

import org.openxava.jpa.*;

import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.model.*;

public class TXInvoiceSaleSaveAction extends TXSaveAction {

	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		Account account = getDebitAccount();
		AccountInvoice invoice = XPersistence.getManager().find(AccountInvoice.class, account.getAccountId());
		
		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		
		if (invoice.getDetails()==null || invoice.getDetails().isEmpty() || invoice.getTotal().compareTo(BigDecimal.ZERO)==0)
    		throw new OperativeException("invoice_not_processed_with_out_detail");
    	
		transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(account, invoice.getTotal(),BigDecimal.ZERO,null, transaction));
		
		for (AccountInvoiceDetail detail: invoice.getDetails())
		{
			transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(detail.getAccountDetail(), detail.getAmount(),new BigDecimal(detail.getQuantity()),transaction.getUnityDetail(), transaction));
			transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(invoice.getAccount(), detail.getTaxAmount(),new BigDecimal(detail.getQuantity()),transaction.getUnityDetail(), transaction, detail.getTax().getCategory()));
		}

		return transactionAccounts;
	}
	
	public void postSaveAction(Transaction transaction) throws Exception
	{
		if (TransactionHelper.isFinancialSaved(transaction))
		{
			Account a = transaction.getDebitAccount();
			a.setAccountStatus(AccountStatusHelper.getAccountStatus(AccountInvoiceHelper.STATUS_INVOICE_ACTIVE));
			AccountHelper.updateAccount(a);
		}
	}
}
