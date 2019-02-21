package com.powerfin.actions.transaction;

import java.util.List;

import org.openxava.jpa.XPersistence;

import com.powerfin.helper.AccountInvoiceHelper;
import com.powerfin.model.Transaction;
import com.powerfin.model.TransactionAccount;

public class TXConversionItemSaveAction extends TXSaveAction {

	public void extraValidations() throws Exception {}
	
	@SuppressWarnings("unchecked")
	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		return XPersistence.getManager().createQuery("SELECT ta FROM TransactionAccount ta "
				+ "WHERE ta.transaction.transactionId = :transactionId ")
			.setParameter("transactionId", transaction.getTransactionId())
			.getResultList();
	}
	
	public void postSaveAction(Transaction transaction) throws Exception
	{
		AccountInvoiceHelper.postTransferItemSaveAction(transaction);
	}
}
