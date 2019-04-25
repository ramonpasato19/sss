package com.powerfin.helper;

import java.math.*;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.*;

import org.openxava.jpa.*;
import org.openxava.util.DataSourceConnectionProvider;
import org.openxava.util.SystemException;

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
	
	public static void generateBalance(Date accountingDate, String accountId, int subaccount, String categoryId, int branchId) {
		Connection con = null;
		String schema = XPersistence.getDefaultSchema().toLowerCase();
		try {
			con = DataSourceConnectionProvider.getByComponent("Balance")
					.getConnection();
			CallableStatement cs = con.prepareCall("{ call "+schema+".generate_balance(?, ?, ?, ?, ?)}");
			cs.setDate(1, new java.sql.Date(accountingDate.getTime()));
			cs.setString(2, accountId);
			cs.setInt(3, subaccount);
			cs.setString(4, categoryId);
			cs.setInt(5, branchId);
			cs.executeUpdate();
			cs.close();

		} catch (Exception ex) {
			throw new SystemException(ex);
		} finally {
			try {
				con.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
