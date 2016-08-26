package com.powerfin.actions.transaction;

import java.math.*;
import java.util.*;

import org.openxava.jpa.*;
import org.openxava.util.*;

import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.model.*;

public class TXPurchasePortfolioSaveAction extends TXSaveAction {

	@SuppressWarnings("unchecked")
	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		BigDecimal totalCapital = BigDecimal.ZERO;
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
    	
		AccountPortfolio portfolio = XPersistence.getManager().find(AccountPortfolio.class, account.getAccountId());
		if (portfolio == null)
    		throw new OperativeException("account_portfolio_not_found", account.getAccountId());
		

		//capital
		for (AccountPaytable quota: quotas)
		{
			ta = TransactionAccountHelper.createCustomDebitTransactionAccount(account, quota.getSubaccount(), quota.getCapital(), transaction, CategoryHelper.getCategoryById(CategoryHelper.CAPITAL_CATEGORY), quota.getDueDate());
			ta.setRemark(XavaResources.getString("quota_number", quota.getSubaccount()));
			transactionAccounts.add(ta);
			
			ta = TransactionAccountHelper.createCustomCreditTransactionAccount(loan.getDisbursementAccount(), quota.getCapital(), transaction);
			ta.setRemark(XavaResources.getString("quota_number", quota.getSubaccount()));
			transactionAccounts.add(ta);
			totalCapital = totalCapital.add(quota.getCapital());
		}
		
		//spread_purchase
		if (portfolio.getPurchaseSpread()!=null)
		{
			if (portfolio.getPurchaseSpread().compareTo(BigDecimal.ZERO)>0)
			{
				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(account, portfolio.getPurchaseSpread(), transaction, CategoryHelper.getCategoryById(CategoryHelper.PURCHASE_SPREAD_PR_CATEGORY));
				ta.setRemark(XavaResources.getString("purchase_spread"));
				transactionAccounts.add(ta);
				
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(portfolio.getPurchaseNegotiation().getDebitCreditAccount(), portfolio.getPurchaseSpread(), transaction);
				ta.setRemark(XavaResources.getString("purchase_spread"));
				transactionAccounts.add(ta);
			}
			if (portfolio.getPurchaseSpread().compareTo(BigDecimal.ZERO)<0)
			{
				//TODO: Falta realizar el proceso cuando el spread es negativo, ganacia en spread de compra. 
			}
		}
		
		//transfer capital to broker
		ta = TransactionAccountHelper.createCustomDebitTransactionAccount(loan.getDisbursementAccount(), totalCapital, transaction);
		ta.setRemark(XavaResources.getString("transfer_loan_amount_to_broker"));
		transactionAccounts.add(ta);
		
		ta = TransactionAccountHelper.createCustomCreditTransactionAccount(portfolio.getPurchaseNegotiation().getDebitCreditAccount(), totalCapital, transaction);
		ta.setRemark(XavaResources.getString("transfer_loan_amount_from_customer"));
		transactionAccounts.add(ta);
		
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
