package com.powerfin.actions.transaction;

import java.math.*;
import java.util.*;

import org.openxava.jpa.*;
import org.openxava.util.*;

import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.model.*;

public class TXTermDisbursementSaveAction extends TXSaveAction {

	public void extraValidations() throws Exception {}
	
	@SuppressWarnings("unchecked")
	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		Account account = getCreditAccount();
		AccountTerm term = XPersistence.getManager().find(AccountTerm.class, account.getAccountId());
		TransactionAccount ta = null;
		
		List<AccountPaytable> quotas = XPersistence.getManager()
				.createQuery("SELECT o FROM AccountPaytable o "
						+ "WHERE o.accountId = :accountId "
						+ "ORDER BY o.subaccount")
				.setParameter("accountId", account.getAccountId())
				.getResultList();
		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		
		if (quotas==null || quotas.isEmpty() || term.getAmount().compareTo(BigDecimal.ZERO)==0)
    		throw new OperativeException("term_not_processed_with_out_paytable");
		
		for (AccountPaytable quota: quotas)
		{
			if (quota.getCapital().compareTo(BigDecimal.ZERO)>0)
			{
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(account, quota.getSubaccount(), quota.getCapital(), transaction, CategoryHelper.getCategoryById(CategoryHelper.CAPITAL_CATEGORY), quota.getDueDate());
				ta.setRemark(XavaResources.getString("quota_number", quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(term.getDisbursementAccount(), quota.getCapital(), transaction);
				ta.setRemark(XavaResources.getString("quota_number", quota.getSubaccount()));
				transactionAccounts.add(ta);
			}
		}

		return transactionAccounts;
	}
	
	public void postSaveAction(Transaction transaction) throws Exception
	{
		if (TransactionHelper.isFinancialSaved(transaction))
		{
			Account a = transaction.getCreditAccount();
			a.setAccountStatus(AccountStatusHelper.getAccountStatus(AccountTermHelper.STATUS_TERM_ACTIVE));
			AccountHelper.updateAccount(a);
		}
	}
}
