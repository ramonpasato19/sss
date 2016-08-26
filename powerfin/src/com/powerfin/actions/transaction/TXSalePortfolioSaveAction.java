package com.powerfin.actions.transaction;

import java.math.*;
import java.util.*;

import org.openxava.jpa.*;
import org.openxava.util.*;

import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.model.*;
import com.powerfin.util.*;

public class TXSalePortfolioSaveAction extends TXSaveAction {

	@SuppressWarnings("unchecked")
	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		BigDecimal capitalBalance = BigDecimal.ZERO;
		BigDecimal spreadPurchaseBalance = BigDecimal.ZERO;
		BigDecimal utilitySalePortfolio = BigDecimal.ZERO;
		Account account = getDebitAccount();
		
		AccountPortfolio accountPortfolio = XPersistence.getManager().find(AccountPortfolio.class, account.getAccountId());
		AccountLoan accountLoan = XPersistence.getManager().find(AccountLoan.class, account.getAccountId());
		
		Category capitalCategory = CategoryHelper.getCategoryById(CategoryHelper.CAPITAL_CATEGORY);
		Category purchaseSpreadCategory = CategoryHelper.getCategoryById(CategoryHelper.PURCHASE_SPREAD_PR_CATEGORY);
		TransactionAccount ta = null;
			
		if (accountPortfolio == null)
    		throw new OperativeException("account_portfolio_not_found", account.getAccountId());
		
		List<Balance> balances = XPersistence.getManager()
				.createQuery("SELECT o FROM Balance o "
						+ "WHERE o.account.accountId = :accountId "
						+ "AND o.category.categoryId = :categoryId "
						+ "AND o.toDate = :toDate "
						+ "ORDER BY o.subaccount DESC")
				.setParameter("accountId", account.getAccountId())
				.setParameter("categoryId", capitalCategory.getCategoryId())
				.setParameter("toDate", UtilApp.DEFAULT_EXPIRY_DATE)
				.getResultList();
		
		List<Balance> spreadBalances = XPersistence.getManager()
				.createQuery("SELECT o FROM Balance o "
						+ "WHERE o.account.accountId = :accountId "
						+ "AND o.category.categoryId = :categoryId "
						+ "AND o.toDate = :toDate ")
				.setParameter("accountId", account.getAccountId())
				.setParameter("categoryId", purchaseSpreadCategory.getCategoryId())
				.setParameter("toDate", UtilApp.DEFAULT_EXPIRY_DATE)
				.getResultList();
		
		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		
		if (balances==null || balances.isEmpty())
    		throw new OperativeException("sale_not_processed_with_out_balances");
		else
			for (Balance balance : balances)
				capitalBalance = capitalBalance.add(balance.getBalance());
		
		if (capitalBalance.compareTo(BigDecimal.ZERO)<=0)
    		throw new OperativeException("sale_not_processed_with_balance_zero");
		
		if (capitalBalance.compareTo(accountPortfolio.getSaleAmount())!=0)
    		throw new OperativeException("sale_not_processed_balance_not_equal_sale_amount");
		
		if (spreadBalances!=null && !spreadBalances.isEmpty())
			for (Balance spreadBalance : spreadBalances)
				spreadPurchaseBalance = spreadPurchaseBalance.add(spreadBalance.getBalance());
		
		if (accountPortfolio.getSaleSpread().compareTo(spreadPurchaseBalance)<0)
			throw new OperativeException("sale_not_processed_sale_spread_is_less_than_spread_purchase_balance");
		
		//transfer amount broker to customer
		ta = TransactionAccountHelper.createCustomDebitTransactionAccount(accountPortfolio.getSaleNegotiation().getDebitCreditAccount(), accountPortfolio.getSaleAmount(), transaction);
		ta.setRemark(XavaResources.getString("transfer_to_customer_for_sale_portfolio"));
		transactionAccounts.add(ta);
		ta = TransactionAccountHelper.createCustomCreditTransactionAccount(accountLoan.getDisbursementAccount(), accountPortfolio.getSaleAmount(), transaction);
		ta.setRemark(XavaResources.getString("transfer_from_broker_for_sale_portfolio"));
		transactionAccounts.add(ta);
		
		//capital
		for (Balance balance : balances)
		{
			ta = TransactionAccountHelper.createCustomCreditTransactionAccount(account, balance.getSubaccount(), balance.getBalance(), transaction, capitalCategory, balance.getDueDate());
			ta.setRemark(XavaResources.getString("quota_number", balance.getSubaccount()));
			transactionAccounts.add(ta);
			
			ta = TransactionAccountHelper.createCustomDebitTransactionAccount(accountLoan.getDisbursementAccount(), balance.getBalance(), transaction);
			ta.setRemark(XavaResources.getString("quota_number", balance.getSubaccount()));
			transactionAccounts.add(ta);
			
			/*order accountant register capital*/
			/*
			ta = TransactionAccountHelper.createCustomDebitTransactionAccount(account, balance.getSubaccount(), balance.getBalance(), transaction, CategoryHelper.getCategoryById(CategoryHelper.ORDER_CAPITAL_CATEGORY));
			ta.setRemark(XavaResources.getString("quota_number", balance.getSubaccount()));
			transactionAccounts.add(ta);
			
			ta = TransactionAccountHelper.createCustomCreditTransactionAccount(account, balance.getSubaccount(), balance.getBalance(), transaction, CategoryHelper.getCategoryById(CategoryHelper.ORDER_SALE_CAPITAL_CATEGORY));
			ta.setRemark(XavaResources.getString("quota_number", balance.getSubaccount()));
			transactionAccounts.add(ta);
			*/
		}
		
		//spread_sale
		//cancel spread purchase
		if (spreadPurchaseBalance.compareTo(BigDecimal.ZERO)>0)
		{
			ta = TransactionAccountHelper.createCustomCreditTransactionAccount(account, spreadPurchaseBalance, transaction, purchaseSpreadCategory);
			ta.setRemark(XavaResources.getString("cancel_purchase_spread"));
			transactionAccounts.add(ta);
			
			ta = TransactionAccountHelper.createCustomDebitTransactionAccount(accountPortfolio.getSaleNegotiation().getDebitCreditAccount(), spreadPurchaseBalance, transaction);
			ta.setRemark(XavaResources.getString("cancel_purchase_spread"));
			transactionAccounts.add(ta);
			
			utilitySalePortfolio = accountPortfolio.getSaleSpread().subtract(spreadPurchaseBalance);
		}
		
		//utility log
		if (utilitySalePortfolio.compareTo(BigDecimal.ZERO)>0)
		{
			ta = TransactionAccountHelper.createCustomCreditTransactionAccount(account, utilitySalePortfolio, transaction, CategoryHelper.getCategoryById(CategoryHelper.UTILITY_SALE_PORTFOLIO_PR_CATEGORY));
			ta.setRemark(XavaResources.getString("utility_sale_portfolio"));
			transactionAccounts.add(ta);
			
			ta = TransactionAccountHelper.createCustomDebitTransactionAccount(accountPortfolio.getSaleNegotiation().getDebitCreditAccount(), utilitySalePortfolio, transaction);
			ta.setRemark(XavaResources.getString("utility_sale_portfolio"));
			transactionAccounts.add(ta);
		}

		return transactionAccounts;
	}
	
}
