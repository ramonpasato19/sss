package com.powerfin.actions.transaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.openxava.jpa.XPersistence;

import com.powerfin.exception.OperativeException;
import com.powerfin.helper.TransactionAccountHelper;
import com.powerfin.model.Account;
import com.powerfin.model.AccountPaytable;
import com.powerfin.model.Category;
import com.powerfin.model.Transaction;
import com.powerfin.model.TransactionAccount;
import com.powerfin.util.UtilApp;

public class TXAdjustmentOtherBalancesSaveAction extends TXSaveAction{

	@SuppressWarnings("unchecked")
	public void extraValidations() throws Exception {
		getDebitAccount();
		
		BigDecimal value = (BigDecimal) getView().getRoot().getValue("value");
		
		if (value == null)
			throw new OperativeException("value_is_required");
		
		getSubaccount();
		
		String categoryId = getSecondaryCategory();
		if (UtilApp.fieldIsEmpty(categoryId))
			throw new OperativeException("balance_type_is_required");
		
		List<AccountPaytable> quotas = XPersistence.getManager().createQuery("SELECT o FROM AccountPaytable o "
				+ "WHERE o.account.accountId = :accountId "
				+ "AND o.subaccount = :subaccount")
				.setParameter("accountId", getDebitAccount().getAccountId())
				.setParameter("subaccount", getSubaccount())
				.getResultList();
		
		if (quotas == null || quotas.isEmpty())
			throw new OperativeException("quota_not_found");
		
		AccountPaytable ap = (AccountPaytable)quotas.get(0);
		
		if (ap.getPaymentDate() != null)
			throw new OperativeException("quota_is_already_paid",ap.getSubaccount(),ap.getPaymentDate());
	}
	
	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		Account debitAccount = getDebitAccount();

		String otherSecondaryCategoryId = null;
		
		BigDecimal value = (BigDecimal) getView().getRoot().getValue("value");
		if (value == null)
			throw new OperativeException("value_is_required");
		
		if (transaction.getSecondaryCategory().getCategoryId().equals("RECEIFEERE"))
			otherSecondaryCategoryId = "RECEIFEEIN";
		else if (transaction.getSecondaryCategory().getCategoryId().equals("LEGALFEERE"))
			otherSecondaryCategoryId = "LEGALFEEIN";
		else if (transaction.getSecondaryCategory().getCategoryId().equals("INTERESTPR"))
		{
			if (debitAccount.getProduct().getProductId().equals("108") || debitAccount.getProduct().getProductId().equals("109"))
				otherSecondaryCategoryId = "INTDIF";
			else
				otherSecondaryCategoryId = "INTERESTIN";
		}
		
		Category otherSecondaryCategory = XPersistence.getManager().find(Category.class, otherSecondaryCategoryId);
		
		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		
		if (UtilApp.isGreaterThanZero(value))
		{
			transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(debitAccount, 
					getSubaccount(),
					value, 
					transaction, 
					transaction.getSecondaryCategory()));
			transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(debitAccount, 
					getSubaccount(),
					value, 
					transaction, 
					otherSecondaryCategory));
		}
		else
		{
			transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(debitAccount, 
					getSubaccount(),
					value.abs(), 
					transaction, 
					transaction.getSecondaryCategory()));
			transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(debitAccount, 
					getSubaccount(),
					value.abs(), 
					transaction, 
					otherSecondaryCategory));
		}
		
		return transactionAccounts;
	}
}
