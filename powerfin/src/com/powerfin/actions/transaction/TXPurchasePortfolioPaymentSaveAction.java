package com.powerfin.actions.transaction;

import java.math.*;
import java.util.*;

import org.openxava.jpa.*;

import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.model.*;

public class TXPurchasePortfolioPaymentSaveAction extends TXSaveAction {

	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		Account debitAccount = getDebitAccount();
		AccountLoan accountLoan = XPersistence.getManager().find(AccountLoan.class, getCreditAccount().getAccountId());
		BigDecimal transactionValue = getValue();
			
		List<TransactionAccount> transactionAccounts = AccountLoanHelper
				.getTransactionAccountsForPymentPurchasePortfolio(transaction, accountLoan, debitAccount, transactionValue);
				
		return transactionAccounts;

	}
	
	public void extraValidations() throws Exception
	{
		List<AccountOverdueBalance> overdueBalances = AccountLoanHelper.getOverdueBalances(getCreditAccount());
		BigDecimal totalOverdue = BigDecimal.ZERO;
		BigDecimal minToPayment = BigDecimal.ZERO;
		BigDecimal transactionValue = getValue();
		
		for (AccountOverdueBalance balance:overdueBalances)
		{
			totalOverdue=totalOverdue.add(balance.getTotal());
			minToPayment=minToPayment.add(balance.getReceivableFee())
					.add(balance.getLegalFee())
					.add(balance.getCollectionFee())
					.add(balance.getDefaultInterest());
		}
		
		if (totalOverdue.compareTo(BigDecimal.ZERO)<=0)
			throw new OperativeException("balance_due_is_zero");
		
		if (transactionValue.compareTo(totalOverdue)>0)
			throw new OperativeException("amount_to_be_paid_is_greater_than_the_overdue_balance");
		/*
		if (transactionValue.compareTo(minToPayment)<0)
			throw new OperativeException("the_minimum_value_to_pay_is", minToPayment);
			*/
	}
	
	public void postSaveAction(Transaction transaction) throws Exception
	{
		AccountLoanHelper.postPurchasePortfolioPaymentSaveAction(transaction);
	}
}
