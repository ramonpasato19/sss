package com.powerfin.actions.transaction;

import java.util.*;

import org.openxava.jpa.*;

import com.powerfin.helper.*;
import com.powerfin.model.*;
import com.powerfin.model.types.*;

public class TXTransferItemSaveAction extends TXSaveAction {

	public void extraValidations() throws Exception {}
	
	@SuppressWarnings("unchecked")
	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		List<TransactionAccount> currentTransactionAccounts = XPersistence.getManager().createQuery("SELECT ta FROM TransactionAccount ta "
				+ "WHERE ta.transaction.transactionId = :transactionId "
				+ "AND ta.debitOrCredit = :debitOrCredit")
			.setParameter("transactionId", transaction.getTransactionId())
			.setParameter("debitOrCredit", Types.DebitOrCredit.CREDIT)
			.getResultList();
		
		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		
		for (TransactionAccount ta : currentTransactionAccounts)
		{
			TransactionAccount creditTa = new TransactionAccount();
			creditTa.setAccount(ta.getAccount());
			creditTa.setBranch(transaction.getOriginationBranch());
			creditTa.setCategory(CategoryHelper.getCostCategory());
			creditTa.setDebitOrCredit(Types.DebitOrCredit.CREDIT);
			creditTa.setQuantity(ta.getQuantity());
			creditTa.setValue(ta.getValue());
			transactionAccounts.add(creditTa);
					
			TransactionAccount debitTa = new TransactionAccount();
			debitTa.setAccount(ta.getAccount());
			debitTa.setBranch(transaction.getDestinationBranch());
			debitTa.setCategory(CategoryHelper.getCostCategory());
			debitTa.setDebitOrCredit(Types.DebitOrCredit.DEBIT);
			debitTa.setQuantity(ta.getQuantity());
			debitTa.setValue(ta.getValue());
			transactionAccounts.add(debitTa);
			
		}
		
		return transactionAccounts;
	}
}
