package com.powerfin.helper;

import java.math.*;
import java.util.*;

import org.openxava.jpa.*;

import com.powerfin.exception.*;
import com.powerfin.model.*;
import com.powerfin.util.*;

public class AccountLoanHelper {
	
	public final static String STATUS_LOAN_ACTIVE = "002";
	public final static String STATUS_LOAN_CANCEL = "003";
	public final static String STATUS_PROCESS_FINANCIAL = "002";
	public final static String LOAN_DISBURSEMENT_TRANSACTION_MODULE = "LOANDISBURSEMENT";
	public final static String SALE_PORTFOLIO_TRANSACTION_MODULE = "SALEPORTFOLIO";
	public final static String PURCHASE_PORTFOLIO_TRANSACTION_MODULE = "PURCHASEPORTFOLIO";
	
	@SuppressWarnings("unchecked")
	public static void getAllOverdueBalancesByBroker(Integer brokerPersonId) {
		Date accountingDate = CompanyHelper.getCurrentAccountingDate();
		String query = "SELECT DISTINCT(b.account) FROM Balance b  "
				+ "WHERE b.dueDate <= :accountingDate "
				+ "AND b.balance > 0 "
				+ "AND b.toDate = :toDate "
				+ "AND b.category.categoryId in ('CAPITAL','INTERESTPR','INSURANRE','MORTGAGERE','LEGALFEERE','RECEIFEERE') "
				+ "AND b.account.accountId IN ( "
				+ "SELECT a.accountId FROM Account a, AccountPortfolio p, Negotiation n "
				+ "WHERE a.accountId = p.accountId "
				+ "AND a.accountStatus.accountStatusId  = '002' "
				+ "AND p.purchaseNegotiation.negotiationId = n.negotiationId "
				+ "AND n.brokerPerson.personId = :brokerPersonId )";
		
		List<Account> accounts = XPersistence.getManager()
				.createQuery(query)
				.setParameter("accountingDate", accountingDate)
				.setParameter("toDate", UtilApp.DEFAULT_EXPIRY_DATE)
				.setParameter("brokerPersonId", brokerPersonId)
				.getResultList();
		if (accounts!=null && !accounts.isEmpty())
		{
			for(Account a:accounts)
				getOverdueBalances(a);
		}
	}
		
	
	@SuppressWarnings("unchecked")
	public static List<AccountOverdueBalance> getOverdueBalances(Account account) {
		String schema = CompanyHelper.getSchema().toLowerCase();
		List<AccountOverdueBalance> overdueBalances = new ArrayList<AccountOverdueBalance>();
		Date accountingDate = CompanyHelper.getCurrentAccountingDate();
		String query = "select * from ( "
				+ "select subaccount, "
				+ "due_date, "
				+ "sum(capital) capital, "
				+ "sum(interest) interest, "
				+ "sum(insurance) insurance, "
				+ "sum(insuranceMortgage) insuranceMortgage, "
				+ "sum(receivableFee) receivableFee, "
				+ "sum(legalFee) legalFee, "
				+ "days_overdue, "
				+ "real_days_overdue, "
				+ "last_payment_date, "
				+ "last_payment_date_collection, "
				+ "last_payment_date_default_int "
				+ "from ( "
				+ "select b.subaccount, ap.due_date, "
				+ "(case when b.category_id = 'CAPITAL' then COALESCE(b.balance,0) else 0 end) capital, "
				+ "(case when b.category_id = 'INTERESTPR' then COALESCE(b.balance,0) else 0 end) interest, "
				+ "(case when b.category_id = 'INSURANRE' then COALESCE(b.balance,0) else 0 end) insurance, "
				+ "(case when b.category_id = 'MORTGAGERE' then COALESCE(b.balance,0) else 0 end) insuranceMortgage, "
				+ "(case when b.category_id = 'RECEIFEERE' then COALESCE(b.balance,0) else 0 end) receivableFee, "
				+ "(case when b.category_id = 'LEGALFEERE' then COALESCE(b.balance,0) else 0 end) legalFee, "
				+ "COALESCE(:accountingDate - ap.due_date, 0) days_overdue, "
				+ "COALESCE(:accountingDate - ap.last_payment_date_default_int, 0) real_days_overdue, "
				+ "ap.last_payment_date, "
				+ "ap.last_payment_date_collection,"
				+ "ap.last_payment_date_default_int "
				+ "from "+schema+".balance b, "+schema+".account_paytable ap "
				+ "where b.account_id = :accountId "
				+ "and b.to_date = :toDate "
				+ "and b.category_id in ('CAPITAL','INTERESTPR','INSURANRE','MORTGAGERE','LEGALFEERE','RECEIFEERE') "
				+ "and b.account_id = ap.account_id "
				+ "and b.subaccount = ap.subaccount "
				+ "and ap.due_date <= :accountingDate "
				+ "and ap.payment_date is null "
				+ ") x group by subaccount, due_date, days_overdue, "
				+ " real_days_overdue, last_payment_date,last_payment_date_collection,last_payment_date_default_int  "
				+ ") z "
				+ "order by subaccount";

		List<Object[]> balances = XPersistence.getManager()
				.createNativeQuery(query)
				.setParameter("accountId", account.getAccountId())
				.setParameter("toDate", UtilApp.DEFAULT_EXPIRY_DATE)
				.setParameter("accountingDate", accountingDate)
				.getResultList();
		
		if (balances!=null && !balances.isEmpty())
		{
			for (Object[] balance : balances)
			{
				AccountOverdueBalance overdueBalance = new AccountOverdueBalance();
				overdueBalance.setAccountId(account.getAccountId());
				overdueBalance.setAccountingDate(accountingDate);
				overdueBalance.setSubaccount((Integer) balance[0]);
				overdueBalance.setDueDate((Date) balance[1]);
				overdueBalance.setCapital((BigDecimal) balance[2]);
				overdueBalance.setInterest((BigDecimal) balance[3]);
				overdueBalance.setInsurance((BigDecimal) balance[4]);
				overdueBalance.setInsuranceMortgage((BigDecimal) balance[5]);
				overdueBalance.setReceivableFee((BigDecimal) balance[6]);
				overdueBalance.setLegalFee((BigDecimal) balance[7]);
				overdueBalance.setOverdueDays((Integer) balance[8]);
				overdueBalance.setRealOverdueDays((Integer) balance[9]);
				overdueBalance.setLastPaymentDate((Date) balance[10]);
				overdueBalance.setLastPaymentDateCollection((Date) balance[11]);
				overdueBalance.setLastPaymentDateDefaultInterest((Date) balance[12]);
				overdueBalance.setDefaultInterest(getDefaultInterestByQuota(overdueBalance));
				overdueBalance.setCollectionFee(getCollectionFeeByQuota(overdueBalance));
				BigDecimal total = overdueBalance.getCapital()
						.add(overdueBalance.getInterest())
						.add(overdueBalance.getInsurance())
						.add(overdueBalance.getInsuranceMortgage())
						.add(overdueBalance.getDefaultInterest())
						.add(overdueBalance.getCollectionFee())
						.add(overdueBalance.getReceivableFee())
						.add(overdueBalance.getLegalFee());
				overdueBalance.setTotal(total);
				if (total.compareTo(BigDecimal.ZERO)>0)
					overdueBalances.add(overdueBalance);
			}
		}		
		XPersistence.getManager().createQuery("DELETE FROM AccountOverdueBalance o "
				+ "WHERE o.accountId=:accountId ")
		.setParameter("accountId", account.getAccountId())
		.executeUpdate();
		
		for (AccountOverdueBalance overdueBalance:overdueBalances)
		{
			XPersistence.getManager().persist(overdueBalance);
		}
		
		return overdueBalances;
	}
	
	@SuppressWarnings("unchecked")
	public static List<AccountOverdueBalance> getOverdueBalancesSalePortfolio(Account account) {
		String schema = CompanyHelper.getSchema().toLowerCase();
		List<AccountOverdueBalance> overdueBalances = new ArrayList<AccountOverdueBalance>();
		Date accountingDate = CompanyHelper.getCurrentAccountingDate();
		String query = "select subaccount, "
				+ "due_date, "
				+ "sum(capital) capital, "
				+ "sum(interest) interest, "
				+ "days_overdue, "
				+ "real_days_overdue, "
				+ "last_payment_date "
				+ "from ( "
				+ "select b.subaccount, ap.due_date, "
				+ "(case when b.category_id = 'SCAPITAL' then COALESCE(b.balance,0) else 0 end) capital, "
				+ "(case when b.category_id = 'INTERESTIN' then COALESCE(b.balance,0) else 0 end) interest, "
				+ "COALESCE(:accountingDate - ap.due_date, 0) days_overdue, "
				+ "COALESCE(:accountingDate - ap.last_payment_date, 0) real_days_overdue, "
				+ "ap.last_payment_date "
				+ "from "+schema+".balance b, "+schema+".account_sold_paytable ap "
				+ "where b.account_id = :accountId "
				+ "and b.to_date = :toDate "
				+ "and b.category_id in ('SCAPITAL','INTERESTIN') "
				+ "and b.account_id = ap.account_id "
				+ "and b.subaccount = ap.subaccount "
				+ "and ap.due_date <= :accountingDate "
				+ "and ap.payment_date is null "
				+ ") x group by subaccount, due_date, days_overdue "
				+ "order by subaccount";
		System.out.println(query);
		System.out.println(account.getAccountId());
		List<Object[]> balances = XPersistence.getManager()
				.createNativeQuery(query)
				.setParameter("accountId", account.getAccountId())
				.setParameter("toDate", UtilApp.DEFAULT_EXPIRY_DATE)
				.setParameter("accountingDate", accountingDate)
				.getResultList();
		
		if (balances!=null && !balances.isEmpty())
		{
			for (Object[] balance : balances)
			{
				AccountOverdueBalance overdueBalance = new AccountOverdueBalance();
				overdueBalance.setAccountId(account.getAccountId());
				overdueBalance.setAccountingDate(accountingDate);
				overdueBalance.setSubaccount((Integer) balance[0]);
				overdueBalance.setDueDate((Date) balance[1]);
				overdueBalance.setCapital((BigDecimal) balance[2]);
				overdueBalance.setInterest((BigDecimal) balance[3]);
				overdueBalance.setInsurance(BigDecimal.ZERO);
				overdueBalance.setInsuranceMortgage(BigDecimal.ZERO);
				overdueBalance.setOverdueDays((Integer) balance[4]);
				overdueBalance.setRealOverdueDays((Integer) balance[5]);
				overdueBalance.setLastPaymentDate((Date) balance[6]);
				overdueBalance.setDefaultInterest(BigDecimal.ZERO);
				overdueBalance.setCollectionFee(BigDecimal.ZERO);
				overdueBalance.setReceivableFee(BigDecimal.ZERO);
				overdueBalance.setLegalFee(BigDecimal.ZERO);
				BigDecimal total = overdueBalance.getCapital()
						.add(overdueBalance.getInterest())
						.add(overdueBalance.getInsurance())
						.add(overdueBalance.getInsuranceMortgage())
						.add(overdueBalance.getDefaultInterest())
						.add(overdueBalance.getCollectionFee())
						.add(overdueBalance.getReceivableFee())
						.add(overdueBalance.getLegalFee());
				overdueBalance.setTotal(total);
				if (total.compareTo(BigDecimal.ZERO)>0)
					overdueBalances.add(overdueBalance);
			}
		}		
		XPersistence.getManager().createQuery("DELETE FROM AccountOverdueBalance o "
				+ "WHERE o.accountId=:accountId ")
		.setParameter("accountId", account.getAccountId())
		.executeUpdate();
		
		for (AccountOverdueBalance overdueBalance:overdueBalances)
		{
			XPersistence.getManager().persist(overdueBalance);
		}
		
		return overdueBalances;
	}
	
	@SuppressWarnings("unchecked")
	public static BigDecimal getBalanceByQuota(String accountId, int subaccount)
	{
		List<Object> balances = XPersistence.getManager().createQuery("SELECT COALESCE(sum(balance),0) "
				+ "FROM Balance o "
				+ "WHERE o.toDate = :toDate "
				+ "AND o.category.categoryId in ('CAPITAL','INTERESTPR','INSURANRE','MORTGAGERE','LEGALFEERE','RECEIFEERE') "
				+ "AND o.subaccount = :subaccount "
				+ "AND o.account.accountId = :accountId ")
		.setParameter("toDate", UtilApp.DEFAULT_EXPIRY_DATE)
		.setParameter("subaccount", subaccount)
		.setParameter("accountId", accountId)
		.getResultList();
		
		if (balances!=null && !balances.isEmpty())
			return (BigDecimal)balances.get(0);
		else
			return BigDecimal.ZERO;
	}
	@SuppressWarnings("unchecked")
	public static BigDecimal getBalanceByQuotaSalePortfolio(String accountId, int subaccount)
	{
		List<Object> balances = XPersistence.getManager().createQuery("SELECT COALESCE(SUM(balance),0) "
				+ "FROM Balance o "
				+ "WHERE o.toDate = :toDate "
				+ "AND o.category.categoryId in ('SCAPITAL','INTERESTIN') "
				+ "AND o.subaccount = :subaccount "
				+ "AND o.account.accountId = :accountId ")
		.setParameter("toDate", UtilApp.DEFAULT_EXPIRY_DATE)
		.setParameter("subaccount", subaccount)
		.setParameter("accountId", accountId)
		.getResultList();
		
		if (balances!=null && !balances.isEmpty())
			return (BigDecimal)balances.get(0);
		else
			return BigDecimal.ZERO;
	}
	
	@SuppressWarnings("unchecked")
	public static BigDecimal getBalanceByQuotaAndCategory(String accountId, int subaccount, String categoryId)
	{
		List<Object> balances = XPersistence.getManager().createQuery("SELECT COALESCE(SUM(balance),0) "
				+ "FROM Balance o "
				+ "WHERE o.toDate = :toDate "
				+ "AND o.category.categoryId = :categoryId "
				+ "AND o.subaccount = :subaccount "
				+ "AND o.account.accountId = :accountId ")
		.setParameter("toDate", UtilApp.DEFAULT_EXPIRY_DATE)
		.setParameter("subaccount", subaccount)
		.setParameter("accountId", accountId)
		.setParameter("categoryId", categoryId)
		.getResultList();
		
		if (balances!=null && !balances.isEmpty())
			return (BigDecimal)balances.get(0);
		else
			return BigDecimal.ZERO;
	}
	
	@SuppressWarnings("unchecked")
	public static BigDecimal getBalanceByLoanAccount(String accountId)
	{
		List<Object> balances = XPersistence.getManager()
				.createQuery("SELECT COALESCE(sum(balance),0) "
				+ "FROM Balance o "
				+ "WHERE o.toDate = :toDate "
				+ "AND o.category.categoryId in ('CAPITAL','INTERESTPR','INSURANRE','MORTGAGERE','LEGALFEERE','RECEIFEERE') "
				+ "AND o.account.accountId = :accountId ")
		.setParameter("toDate", UtilApp.DEFAULT_EXPIRY_DATE)
		.setParameter("accountId", accountId)
		.getResultList();
		
		if (balances!=null && !balances.isEmpty())
			return (BigDecimal)balances.get(0);
		else
			return BigDecimal.ZERO;
	}
	
	@SuppressWarnings("unchecked")
	public static BigDecimal getOverdueBalanceByLoanAccount(String accountId)
	{
		List<Object> balances = XPersistence.getManager()
				.createQuery("SELECT COALESCE(SUM(o.total),0) FROM AccountOverdueBalance o "
				+ "WHERE o.accountId = :accountId ")
		.setParameter("accountId", accountId)
		.getResultList();
		
		if (balances!=null && !balances.isEmpty())
			return (BigDecimal)balances.get(0);
		else
			return BigDecimal.ZERO;
	}
	
	private static BigDecimal getCollectionFeeByQuota(AccountOverdueBalance overdueBalance)
	{
		BigDecimal amount = BigDecimal.ZERO;
		int daysGrace = 0;
		int overdueDays = 0;
		AccountLoan accountLoan = XPersistence.getManager().find(AccountLoan.class, overdueBalance.getAccountId());
		
		if (accountLoan.getDayGraceCollectionFee()!=null)
			daysGrace = accountLoan.getDayGraceCollectionFee();
		
		overdueDays = overdueBalance.getOverdueDays();
			
		if (overdueBalance.getCapital() !=null && overdueBalance.getCapital().compareTo(BigDecimal.ZERO)>0)
			amount = amount.add(overdueBalance.getCapital());
		
		if (overdueBalance.getInterest() !=null && overdueBalance.getInterest().compareTo(BigDecimal.ZERO)>0)
			amount = amount.add(overdueBalance.getInterest());
		
		if (overdueBalance.getInsurance() !=null && overdueBalance.getInsurance().compareTo(BigDecimal.ZERO)>0)
			amount = amount.add(overdueBalance.getInsurance());
		
		if (overdueBalance.getInsuranceMortgage() !=null && overdueBalance.getInsuranceMortgage().compareTo(BigDecimal.ZERO)>0)
			amount = amount.add(overdueBalance.getInsuranceMortgage());
		
		if (overdueDays<=0)
			return BigDecimal.ZERO;
		
		if (overdueDays<=daysGrace)
			return BigDecimal.ZERO;
		
		if (amount.compareTo(BigDecimal.ZERO)<=0)
			return BigDecimal.ZERO;
		
		if (overdueBalance.getLastPaymentDateCollection()!=null)
			return BigDecimal.ZERO;
		
		return getLoanCollectionFee(overdueDays, amount);
		
	}
	
	private static BigDecimal getDefaultInterestByQuota(AccountOverdueBalance overdueBalance)
	{
		BigDecimal overdueValue = BigDecimal.ZERO;
		BigDecimal defaultInterest = BigDecimal.ZERO;
		BigDecimal rate = null;
		BigDecimal dayRate = null;
		BigDecimal defaultInterestRate = null;
		int daysGrace = 0;
		int overdueDays = 0;
		
		AccountLoan accountLoan = XPersistence.getManager().find(AccountLoan.class, overdueBalance.getAccountId());
		
		if (accountLoan.getDayGrace()!=null)
			daysGrace = accountLoan.getDayGrace();
		
		if (overdueBalance.getLastPaymentDateDefaultInterest()!=null)
		{
			overdueDays = overdueBalance.getRealOverdueDays();
			daysGrace = 0;
		}
		else 
			overdueDays = overdueBalance.getOverdueDays();
		
		if (overdueBalance.getCapital()!=null)
			overdueValue = overdueValue.add(overdueBalance.getCapital());
		if (overdueBalance.getInsurance()!=null)
			overdueValue = overdueValue.add(overdueBalance.getInsurance());
		if (overdueBalance.getInsuranceMortgage()!=null)
			overdueValue = overdueValue.add(overdueBalance.getInsuranceMortgage());
		
		defaultInterestRate = getDefaultInterestRate(overdueDays);

		if (overdueValue.compareTo(BigDecimal.ZERO)<=0)
			return defaultInterest;
		
		if (overdueDays<=0)
			return defaultInterest;
		
		if (overdueDays<=daysGrace)
			return defaultInterest;

		if (defaultInterestRate==null)
			throw new OperativeException("default_interest_rate_not_found",overdueBalance.getOverdueDays());
			
		defaultInterestRate = defaultInterestRate.add(BigDecimal.ONE);
		rate = accountLoan.getInterestRate().multiply(defaultInterestRate);
		dayRate = rate.divide(new BigDecimal(100), 10, RoundingMode.HALF_UP);
		dayRate = dayRate.divide(new BigDecimal("360"), 10, RoundingMode.HALF_UP);	
		defaultInterest = overdueValue.multiply(dayRate);
		defaultInterest = defaultInterest.multiply(new BigDecimal(overdueDays)).setScale(2, RoundingMode.HALF_UP);
		
		return defaultInterest;
	}
	
	@SuppressWarnings("unchecked")
	private static BigDecimal getDefaultInterestRate(int overdueDays)
	{
		List<DefaultInterestRate> rates = XPersistence.getManager().createQuery("select o from DefaultInterestRate o "
				+ "where :overdueDays between o.fromDays and o.toDays")
				.setParameter("overdueDays", overdueDays)
				.getResultList();
		
		if (rates != null && !rates.isEmpty()){
			DefaultInterestRate rate = rates.get(0);
			return rate.getRate();
		}
		else
			return null;
		
	}
	
	@SuppressWarnings("unchecked")
	private static BigDecimal getLoanCollectionFee(int overdueDays, BigDecimal amount)
	{		
		List<LoanCollectionFee> fees = XPersistence.getManager().createQuery("select o from LoanCollectionFee o "
				+ "where :overdueDays between o.fromDays and o.toDays "
				+ "and :amount between o.fromAmount and o.toAmount ")
				.setParameter("overdueDays", overdueDays)
				.setParameter("amount", amount)
				.getResultList();
		
		if (fees != null && !fees.isEmpty()){
			LoanCollectionFee fee = fees.get(0);
			return fee.getValue();
		}
		else
			return BigDecimal.ZERO;
		
	}
	
}
