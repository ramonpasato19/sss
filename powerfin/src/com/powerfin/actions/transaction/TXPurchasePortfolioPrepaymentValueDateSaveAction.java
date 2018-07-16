package com.powerfin.actions.transaction;

import java.math.BigDecimal;
import java.util.List;

import org.openxava.jpa.XPersistence;

import com.powerfin.helper.AccountLoanHelper;
import com.powerfin.model.Account;
import com.powerfin.model.AccountLoan;
import com.powerfin.model.Transaction;
import com.powerfin.model.TransactionAccount;

public class TXPurchasePortfolioPrepaymentValueDateSaveAction extends TXSaveAction {

	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		Account debitAccount = getDebitAccount();
		AccountLoan accountLoan = XPersistence.getManager().find(AccountLoan.class, getCreditAccount().getAccountId());
		BigDecimal spreadValue = (BigDecimal) getView().getRoot().getValue("spreadValue");
		if (spreadValue == null || spreadValue.compareTo(BigDecimal.ZERO)<0)
			spreadValue = BigDecimal.ZERO;
			
		List<TransactionAccount> transactionAccounts = AccountLoanHelper
				.getTAForAccountLoanPrepayment(transaction, accountLoan, debitAccount, spreadValue);
				
		return transactionAccounts;

	}
	
	public void extraValidations() throws Exception
	{
		getCreditAccount();
		getDebitAccount();
	}
	
	public void postSaveAction(Transaction transaction) throws Exception
	{
		AccountLoanHelper.postAccountLoanPrepaymentSaveAction(transaction);
	}
}
