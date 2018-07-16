package com.powerfin.actions.transaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.openxava.jpa.XPersistence;

import com.powerfin.exception.OperativeException;
import com.powerfin.helper.TransactionAccountHelper;
import com.powerfin.model.Account;
import com.powerfin.model.AccountLoan;
import com.powerfin.model.AccountPaytable;
import com.powerfin.model.Transaction;
import com.powerfin.model.TransactionAccount;
import com.powerfin.util.UtilApp;

public class TXAdjustmentInsuranceBalancesSaveAction extends TXSaveAction{

	@SuppressWarnings("unchecked")
	public void extraValidations() throws Exception {
		getDebitAccount();
		
		AccountLoan loanAccount = XPersistence.getManager().find(AccountLoan.class, getDebitAccount().getAccountId());
		
		BigDecimal value = (BigDecimal) getView().getRoot().getValue("value");
		
		if (value == null)
			throw new OperativeException("value_is_required");
		
		getSubaccount();
		
		String categoryId = getSecondaryCategory();
		if (UtilApp.fieldIsEmpty(categoryId))
			throw new OperativeException("insurance_type_is_required");
		
		Account insuranceAccount = categoryId.equals("INSURANRE")?loanAccount.getInsuranceAccount():loanAccount.getMortgageAccount();
		if (insuranceAccount == null)
			throw new OperativeException("insurance_account_is_required");
		
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
		AccountLoan loanAccount = XPersistence.getManager().find(AccountLoan.class, debitAccount.getAccountId());
		Account insuranceAccount = null;
		
		BigDecimal value = (BigDecimal) getView().getRoot().getValue("value");
		if (value == null)
			throw new OperativeException("value_is_required");
		
		if (transaction.getSecondaryCategory().getCategoryId().equals("INSURANRE"))
			insuranceAccount = loanAccount.getInsuranceAccount();
		else if (transaction.getSecondaryCategory().getCategoryId().equals("MORTGAGERE"))
			insuranceAccount = loanAccount.getMortgageAccount();
		
		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		
		if (UtilApp.isGreaterThanZero(value))
		{
			transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(debitAccount, 
					getSubaccount(),
					value, 
					transaction, 
					transaction.getSecondaryCategory()));
			transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(insuranceAccount, value, transaction));
		}
		else
		{
			transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(debitAccount, 
					getSubaccount(),
					value.abs(), 
					transaction, 
					transaction.getSecondaryCategory()));
			transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(insuranceAccount, value.abs(), transaction));
		}
		
		return transactionAccounts;
	}
}
