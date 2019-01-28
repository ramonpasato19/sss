package com.powerfin.helper;

import java.math.*;
import java.util.*;

import org.openxava.jpa.*;

import com.powerfin.model.*;
import com.powerfin.util.*;

public class BalanceHelper {

	public static BigDecimal getBalance(Account account) throws Exception
	{
		return getBalance(account.getAccountId(), 0, CategoryHelper.BALANCE_CATEGORY, account.getBranch().getBranchId(), null);
	}

	public static BigDecimal getBalance(String account, Integer subaccount, String category, Integer branch) throws Exception
	{
		return getBalance(account, subaccount, category, branch, null);
	}	
	
	@SuppressWarnings("unchecked")
	public static BigDecimal getBalance(String account, Integer subaccount, String category, Integer branch, Date accountingDate) throws Exception
	{
		
		BigDecimal value = BigDecimal.ZERO;
		List<Balance> balances = (List<Balance>) XPersistence.getManager().createQuery("select o from Balance o "
				+ "where o.account.accountId = :account "
				+ "and o.subaccount = :subaccount "
				+ "and o.category.categoryId = :category "
				+ "and o.branch.branchId = :branch "
				+ "and :accountingDate between o.fromDate and o.toDate")
				.setParameter("account", account)
				.setParameter("category", category)
				.setParameter("subaccount", subaccount)
				.setParameter("branch", branch)
				.setParameter("accountingDate", accountingDate==null?UtilApp.DEFAULT_EXPIRY_DATE:accountingDate)
				.getResultList();
		
		if (balances == null || balances.isEmpty())
	        return null;
	    
		for(Balance balance : balances)
			if (balance.getBalance()!=null)
				value = value.add(balance.getBalance());
		
		return value;
	}
	
	@SuppressWarnings("unchecked")
	public static BigDecimal getOfficialBalance(Account account, Integer subaccount, Category category, Integer branch, Date accountingDate) throws Exception
	{
		BigDecimal value = BigDecimal.ZERO;
		List<Balance> balances = (List<Balance>) XPersistence.getManager().createQuery("select o from Balance o "
				+ "where o.account.accountId = :account "
				+ "and o.subaccount = :subaccount "
				+ "and o.category.categoryId = :category "
				+ "and o.branch.branchId = :branch "
				+ "and :accountingDate between o.fromDate and o.toDate")
				.setParameter("account", account.getAccountId())
				.setParameter("category", category.getCategoryId())
				.setParameter("subaccount", subaccount)
				.setParameter("branch", branch)
				.setParameter("accountingDate", accountingDate==null?UtilApp.DEFAULT_EXPIRY_DATE:accountingDate)
				.getResultList();
		
		if (balances == null || balances.isEmpty()) 
	        return null;
		
		for(Balance balance : balances)
		{
			if (balance.getBalance()!=null)
			{
				BigDecimal exchangeRate = ExchangeRateHelper.getExchangeRate(balance.getAccount().getCurrency());
				value = value.add(balance.getBalance().multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP));
			}
		}
		return value;
	}
	
	@SuppressWarnings("unchecked")
	public static BigDecimal getStock(String account, Integer subaccount, String category, Integer branch, Date accountingDate) throws Exception
	{
		BigDecimal value = BigDecimal.ZERO;
		List<Balance> balances = (List<Balance>) XPersistence.getManager().createQuery("select o from Balance o "
				+ "where o.account.accountId = :account "
				+ "and o.subaccount = :subaccount "
				+ "and o.category.categoryId = :category "
				+ "and o.branch.branchId = :branch "
				+ "and :accountingDate between o.fromDate and o.toDate")
				.setParameter("account", account)
				.setParameter("category", category)
				.setParameter("subaccount", subaccount)
				.setParameter("branch", branch)
				.setParameter("accountingDate", accountingDate==null?UtilApp.DEFAULT_EXPIRY_DATE:accountingDate)
				.getResultList();
		
		if (balances == null || balances.isEmpty())
	        return null;
	    
		for(Balance balance : balances)
			if (balance.getStock()!=null)
				value = value.add(balance.getStock());
		
		return value;
	}
}
