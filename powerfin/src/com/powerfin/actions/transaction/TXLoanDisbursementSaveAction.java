package com.powerfin.actions.transaction;

import java.math.*;
import java.util.*;

import org.openxava.jpa.*;
import org.openxava.util.*;

import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.model.*;

public class TXLoanDisbursementSaveAction extends TXSaveAction {

	public void extraValidations() throws Exception {}
	
	@SuppressWarnings("unchecked")
	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		Account account = getDebitAccount();
		AccountLoan loan = XPersistence.getManager().find(AccountLoan.class, account.getAccountId());
		TransactionAccount ta = null;
		
		List<AccountPaytable> quotas = XPersistence.getManager()
				.createQuery("SELECT o FROM AccountPaytable o "
						+ "WHERE o.accountId = :accountId "
						+ "ORDER BY o.subaccount")
				.setParameter("accountId", account.getAccountId())
				.getResultList();
		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		
		if (quotas==null || quotas.isEmpty() || loan.getAmount().compareTo(BigDecimal.ZERO)==0)
    		throw new OperativeException("loan_not_processed_with_out_paytable");
    	
		
		
		for (AccountPaytable quota: quotas)
		{
			ta = TransactionAccountHelper.createCustomDebitTransactionAccount(account, quota.getSubaccount(), quota.getCapital(), transaction, CategoryHelper.getCategoryById(CategoryHelper.CAPITAL_CATEGORY), quota.getDueDate());
			ta.setRemark(XavaResources.getString("quota_number", quota.getSubaccount()));
			transactionAccounts.add(ta);
			
			ta = TransactionAccountHelper.createCustomCreditTransactionAccount(loan.getDisbursementAccount(), quota.getCapital(), transaction);
			ta.setRemark(XavaResources.getString("quota_number", quota.getSubaccount()));
			transactionAccounts.add(ta);
			
		}

		return transactionAccounts;
	}
	
	public void postSaveAction(Transaction transaction) throws Exception
	{
		if (TransactionHelper.isFinancialSaved(transaction))
		{
			Account a = transaction.getDebitAccount();
			a.setAccountStatus(AccountStatusHelper.getAccountStatus(AccountLoanHelper.STATUS_LOAN_ACTIVE));
			AccountHelper.updateAccount(a);
		}
	}
}
