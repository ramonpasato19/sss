package com.powerfin.helper;

import java.math.*;
import java.util.*;

import org.openxava.jpa.*;

import com.powerfin.model.*;

public class BalanceHelper {

	public static BigDecimal getBalance(String accountId)
	{
		return getBalance(accountId, 0, CategoryHelper.BALANCE_CATEGORY);
	}
	
	public static BigDecimal getBalance(Account account)
	{
		return getBalance(account.getAccountId(), 0, CategoryHelper.BALANCE_CATEGORY);
	}
	
	public static BigDecimal getBalance(Account account, Integer subaccount, Category category)
	{
		return getBalance(account.getAccountId(), subaccount, category.getCategoryId());
	}
	
	@SuppressWarnings("unchecked")
	public static BigDecimal getBalance(String account, Integer subaccount, String category)
	{
		BigDecimal value = null;
		List<Balance> balances = (List<Balance>) XPersistence.getManager().createQuery("select o from Balance o "
				+ "where o.account.accountId = :account "
				+ "and o.subaccount = :subaccount "
				+ "and o.category.categoryId = :category "
				+ "and o.toDate = :toDate")
				.setParameter("account", account)
				.setParameter("category", category)
				.setParameter("subaccount", subaccount)
				.setParameter("toDate", com.powerfin.util.UtilApp.DEFAULT_EXPIRY_DATE)
				.getResultList();
		if (balances!=null && !balances.isEmpty())
		{
			Balance balance = balances.get(0);
			value = balance.getBalance();
		}
		return value;
	}
	
	@SuppressWarnings("unchecked")
	public static BigDecimal getOfficialBalance(Account account, Integer subaccount, Category category) throws Exception
	{
		BigDecimal value = null;
		List<Balance> balances = (List<Balance>) XPersistence.getManager().createQuery("select o from Balance o "
				+ "where o.account.accountId = :account "
				+ "and o.subaccount = :subaccount "
				+ "and o.category.categoryId = :category "
				+ "and o.toDate = :toDate")
				.setParameter("account", account.getAccountId())
				.setParameter("category", category.getCategoryId())
				.setParameter("subaccount", subaccount)
				.setParameter("toDate", com.powerfin.util.UtilApp.DEFAULT_EXPIRY_DATE)
				.getResultList();
		if (balances!=null && !balances.isEmpty())
		{
			Balance balance = balances.get(0);
			BigDecimal exchangeRate = ExchangeRateHelper.getExchangeRate(balance.getAccount().getCurrency());
			value = balance.getBalance().multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP).abs();
		}
		return value;
	}
}
