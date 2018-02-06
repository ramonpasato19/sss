package com.powerfin.helper;

import java.math.*;
import java.util.*;

import javax.persistence.TemporalType;

import org.openxava.jpa.*;
import org.openxava.util.*;

import com.powerfin.exception.*;
import com.powerfin.model.*;
import com.powerfin.model.types.*;
import com.powerfin.model.types.Types.*;
import com.powerfin.util.*;

public class AccountLoanHelper {
	
	public final static Integer EXPIRATION_FRECUENCY_ID = 1;
	public final static String PURCHASE_SALE_STATUS_ACTIVE = "002";
	public final static String PURCHASE_SALE_STATUS_REQUEST = "001";
	public final static String STATUS_LOAN_REQUEST = "001";
	public final static String STATUS_LOAN_ACTIVE = "002";
	public final static String STATUS_LOAN_CANCEL = "003";
	public final static String STATUS_PROCESS_FINANCIAL = "002";
	public final static String LOAN_DISBURSEMENT_TRANSACTION_MODULE = "LOANDISBURSEMENT";
	public final static String SALE_PORTFOLIO_TRANSACTION_MODULE = "SALEPORTFOLIO";
	public final static String PURCHASE_PORTFOLIO_TRANSACTION_MODULE = "PURCHASEPORTFOLIO";
	
	public final static String PURCHASE_PORTFOLIO_STATUS_ID = "001";
	public final static String SALE_PORTFOLIO_STATUS_ID = "002";
	public final static String REPURCHASE_PORTFOLIO_STATUS_ID = "003";
	
	public static void generateAllOverdueBalancesByProduct(String productId, Date projectedAccountingDate) {
		
		Date accountingDate = CompanyHelper.getCurrentAccountingDate();
		if (projectedAccountingDate!=null)
			accountingDate = projectedAccountingDate;
		String schema = XPersistence.getDefaultSchema().toLowerCase();
		
		String queryAccount = "SELECT a.account_id "
				+ "FROM "+schema+".account a, "+schema+".product p " 
				+ "WHERE a.product_id = p.account_id "
				+ "AND p.product_id = '"+productId+"' "
				+ "AND a.account_status_id  = '002'";
		
		generateOverdueBalances(queryAccount, accountingDate, false);
		
	}
	
	public static void generateAllOverdueBalancesByBroker(Integer brokerPersonId, Date projectedAccountingDate) {
		
		Date accountingDate = CompanyHelper.getCurrentAccountingDate();
		if (projectedAccountingDate!=null)
			accountingDate = projectedAccountingDate;
		String schema = XPersistence.getDefaultSchema().toLowerCase();
		
		String queryAccount = "SELECT a.account_id "
				+ "FROM "+schema+".account a, "+schema+".account_portfolio p, "+schema+".negotiation n "
				+ "WHERE a.account_id = p.account_id "
				+ "AND a.account_status_id  = '002' "
				+ "AND p.purchase_negotiation_id = n.negotiation_id "
				+ "AND n.broker_person_id = "+brokerPersonId;
		
		generateOverdueBalances(queryAccount, accountingDate, false);
	}
	
	public static void generateAllOverdueBalancesByPerson(Integer personId, Date projectedAccountingDate) {
		
		Date accountingDate = CompanyHelper.getCurrentAccountingDate();
		if (projectedAccountingDate!=null)
			accountingDate = projectedAccountingDate;
		String schema = XPersistence.getDefaultSchema().toLowerCase();
		
		String queryAccount = "SELECT a.account_id "
				+ "FROM "+schema+".account a, "+schema+".product p, "+schema+".product_type pt " 
				+ "WHERE a.product_id = p.account_id "
				+ "AND p.product_type_id = pt.product_type_id "
				+ "AND pt.product_class_id = '"+ProductClassHelper.LOAN+"' "
				+ "AND a.account_status_id  = '002' "
				+ "AND a.person_id = "+personId;
		
		generateOverdueBalances(queryAccount, accountingDate, false);
	}
	
	public static void generateAllOverdueBalancesSalePortfolioByBroker(Integer brokerPersonId, Date projectedAccountingDate) {
		Date accountingDate = CompanyHelper.getCurrentAccountingDate();
		if (projectedAccountingDate!=null)
			accountingDate = projectedAccountingDate;
		String schema = XPersistence.getDefaultSchema().toLowerCase();
		
		String queryAccount = "SELECT a.account_id "
				+ "FROM "+schema+".account a, "+schema+".account_portfolio p, "+schema+".negotiation n "
				+ "WHERE a.account_id = p.account_id "
				+ "AND p.sale_status_id  = '002' "
				+ "AND p.sale_negotiation_id = n.negotiation_id "
				+ "AND n.broker_person_id = "+brokerPersonId;
		
		generateOverdueBalancesSalePortfolio(queryAccount, accountingDate, false);
	}
	
	@Deprecated
	public static List<AccountOverdueBalance> getOverdueBalancesOld(Account account) {
		return getOverdueBalancesOld(account, null, false);
	}
	
	@SuppressWarnings("unchecked")
	public static List<AccountOverdueBalance> getOverdueBalances(Account account) {
		generateOverdueBalances(account);
		return (List<AccountOverdueBalance>)XPersistence.getManager()
				.createQuery("SELECT o FROM AccountOverdueBalance o "
						+ "WHERE o.account.accountId = :accountId")
				.setParameter("accountId", account.getAccountId())
				.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public static List<AccountOverdueBalance> getOverdueBalances(Account account, Date projectedAccountingDate, boolean forPrepayment) {
		generateOverdueBalances(account, projectedAccountingDate, forPrepayment);
		return (List<AccountOverdueBalance>)XPersistence.getManager()
				.createQuery("SELECT o FROM AccountOverdueBalance o "
						+ "WHERE o.account.accountId = :accountId")
				.setParameter("accountId", account.getAccountId())
				.getResultList();
	}
	
	public static void generateOverdueBalances(Account account) {
		generateOverdueBalances(account, CompanyHelper.getCurrentAccountingDate());
	}
	
	public static void generateOverdueBalances(Account account, Date projectedAccountingDate) {
		generateOverdueBalances(account, projectedAccountingDate, false);
	}
	
	public static void generateOverdueBalances(Account account, Date projectedAccountingDate, boolean forPrepayment) {
		String queryAccount = "'"+account.getAccountId()+"'";
		generateOverdueBalances(queryAccount, projectedAccountingDate, forPrepayment);
	}
	
	public static void generateOverdueBalances(String queryAccount, Date projectedAccountingDate, boolean forPrepayment) {
		
		String schema = XPersistence.getDefaultSchema().toLowerCase();
		Date accountingDate = CompanyHelper.getCurrentAccountingDate();
		
		if (projectedAccountingDate == null)
			projectedAccountingDate = accountingDate;
		
		if (projectedAccountingDate.before(accountingDate))
			accountingDate = projectedAccountingDate;
		
		XPersistence.getManager()
		.createNativeQuery("DELETE FROM "+schema+".account_overdue_balance o "
				+ "WHERE o.account_id in ("+ queryAccount+")")
		.executeUpdate();
		
		XPersistence.commit();
		
		String query = "insert into "+schema+".account_overdue_balance ("
				+ "account_overdue_balance_id, account_id, subaccount, due_date, capital, interest, "
				+ "insurance, insurance_mortgage, receivable_fee, legal_fee, "
				+ "overdue_days, real_overdue_days, last_payment_date, last_payment_date_collection, "
				+ "last_payment_date_default_int, accounting_date, default_interest, collection_fee, total) ";
		
		query += 
				"select z.*, "
				+ "capital+interest+default_interest+insurance+insurance_mortgage+receivable_fee+legal_fee as total "
				+ "from ( "
				+ "select x.*, "
				+ schema+".get_default_interest(x.real_overdue_days, x.overdue_days, capital+insurance+insurance_mortgage, x.account_id, x.last_payment_date_default_int) default_interest, "
				+ schema+".get_collection_fee(x.overdue_days, capital+interest+insurance+insurance_mortgage, x.account_id, x.last_payment_date_collection) collection_fee "
				+ "from ( "
				+ "select 'odue-'||b.account_id||'-'||b.subaccount id, "
				+ "b.account_id, b.subaccount, ap.due_date,  "
				+ "COALESCE(max(CASE WHEN b.category_id = 'CAPITAL' THEN balance END),0) capital, "
				+ "COALESCE(max(CASE WHEN b.category_id = 'INTERESTPR' THEN balance END),0) interest, " 
				+ "COALESCE(max(CASE WHEN b.category_id = 'INSURANRE' THEN balance END),0) insurance,  "
				+ "COALESCE(max(CASE WHEN b.category_id = 'MORTGAGERE' THEN balance END),0) insurance_mortgage, " 
				+ "COALESCE(max(CASE WHEN b.category_id = 'RECEIFEERE' THEN balance END),0) receivable_fee,  "
				+ "COALESCE(max(CASE WHEN b.category_id = 'LEGALFEERE' THEN balance END),0) legal_fee,  "
				+ "COALESCE(:projectedAccountingDate - ap.due_date, 0) overdue_days,  "
				+ "COALESCE(:projectedAccountingDate - ap.last_payment_date_default_int, 0) real_overdue_days, " 
				+ "ap.last_payment_date,  "
				+ "ap.last_payment_date_collection, "
				+ "ap.last_payment_date_default_int, "
				+ "cast(:projectedAccountingDate as date) accounting_date "
				+ "from "+schema+".balance b, "+schema+".account_paytable ap  "
				+ "where b.to_date = :toDate "
				+ "and b.category_id in ('CAPITAL','INTERESTPR','INSURANRE','MORTGAGERE','LEGALFEERE','RECEIFEERE') " 
				+ "and b.account_id = ap.account_id  "
				+ "and b.subaccount = ap.subaccount  "
				+ "and ap.due_date <= :accountingDate "
				+ "and b.account_id in ("+queryAccount+") "
				+ "group by b.account_id, b.subaccount, ap.due_date, ap.last_payment_date_default_int, ap.last_payment_date, ap.last_payment_date_collection "
				+ ") as x  ";
		
		
		if (projectedAccountingDate.after(accountingDate))
		{
				//overdue quota on projected date
			query +="union all "
				+ "select 'proj-'||account_id||'-'||subaccount id, "
				+ "ap. account_id, "
				+ "ap.subaccount, "
				+ "ap.due_date, "
				+ "COALESCE(ap.capital,0) capital, "
				+ "COALESCE(ap.interest,0) interest, "
				+ "COALESCE(ap.insurance,0) insurance, "
				+ "COALESCE(ap.insurance_mortgage,0) insurance_mortgage, "
				+ "0 receivable_fee, "
				+ "0 legal_fee, "
				+ "COALESCE(:projectedAccountingDate - ap.due_date, 0) days_overdue, "
				+ "COALESCE(:projectedAccountingDate - ap.due_date, 0) real_days_overdue, "
				+ "null last_payment_date, "
				+ "null last_payment_date_collection, "
				+ "null last_payment_date_default_int, "
				+ "cast(:projectedAccountingDate as date) accounting_date, "
				+ schema+".get_default_interest(COALESCE(:projectedAccountingDate - ap.due_date, 0), COALESCE(:projectedAccountingDate - ap.due_date, 0), "
				+ "COALESCE(ap.capital,0) + COALESCE(ap.insurance,0) + COALESCE(ap.insurance_mortgage,0), " 
				+ "ap.account_id, ap.last_payment_date_default_int) default_interest, "
				+ schema+".get_collection_fee(COALESCE(:projectedAccountingDate - ap.due_date, 0), "
				+ "COALESCE(ap.capital,0) + COALESCE(ap.interest,0) + COALESCE(ap.insurance,0) + COALESCE(ap.insurance_mortgage,0), " 
				+ "ap.account_id, ap.last_payment_date_collection) collection_fee "
				+ "from "+schema+".account_paytable ap "
				+ "where ap.due_date > :accountingDate "
				+ "and ap.due_date <= :projectedAccountingDate "
				+ "and ap.account_id in ("+queryAccount+") ";
			
		}
		if (forPrepayment)
		{
			query +=  "union all "
				+ "select 'prep-'||ap.account_id||'-'||ap.subaccount id, "
				+ "ap.account_id, "
				+ "ap.subaccount, "
				+ "ap.due_date, "
				+ "capital, "
				+ "(case when (ap.due_date-ap.provision_days) <= :projectedAccountingDate then "
				+ "round((ap.interest/ap.provision_days)*(:projectedAccountingDate-(ap.due_date-ap.provision_days)),2) "
				+ "else "
				+ "0 "
				+ "end) interest, "
				+ "COALESCE(ap.insurance,0), "
				+ "COALESCE(ap.insurance_mortgage,0), "				
				+ "0 receivable_fee, "
				+ "0 legal_fee, "
				+ "0 days_overdue, "
				+ "0 real_days_overdue, "
				+ "null last_payment_date, "
				+ "null last_payment_date_collection, "
				+ "null last_payment_date_default_int, "
				+ "cast(:projectedAccountingDate as date) accounting_date, "
				+ "0 as default_interest, "
				+ "0 as collection_fee "
				+ "from "+schema+".account_paytable ap "
				+ "where ap.due_date > :projectedAccountingDate "
				+ "and ap.account_id in ("+queryAccount+") ";
		}

		query+= ") z "
				+ "order by z.account_id, z.subaccount";

		System.out.println(query);
		XPersistence.getManager().createNativeQuery(query)
		.setParameter("toDate", UtilApp.DEFAULT_EXPIRY_DATE, TemporalType.DATE)
		.setParameter("accountingDate", accountingDate, TemporalType.DATE)
		.setParameter("projectedAccountingDate", projectedAccountingDate, TemporalType.DATE)
		.executeUpdate();

		XPersistence.commit();

	}
	
	public static void generateOverdueBalancesSalePortfolio(Account account) {
		generateOverdueBalancesSalePortfolio(account, CompanyHelper.getCurrentAccountingDate());
	}
	
	public static void generateOverdueBalancesSalePortfolio(Account account, Date projectedAccountingDate) {
		generateOverdueBalancesSalePortfolio(account, projectedAccountingDate, false);
	}
	
	public static void generateOverdueBalancesSalePortfolio(Account account, Date projectedAccountingDate, boolean forPrepayment) {
		String queryAccount = "'"+account.getAccountId()+"'";
		generateOverdueBalancesSalePortfolio(queryAccount, projectedAccountingDate, forPrepayment);
	}
	
	@SuppressWarnings("unchecked")
	public static List<AccountOverdueBalance> getOverdueBalancesSalePortfolio(Account account) {
		generateOverdueBalancesSalePortfolio(account);
		return (List<AccountOverdueBalance>)XPersistence.getManager()
				.createQuery("SELECT o FROM AccountOverdueBalance o "
						+ "WHERE o.account.accountId = :accountId")
				.setParameter("accountId", account.getAccountId())
				.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public static List<AccountOverdueBalance> getOverdueBalancesSalePortfolio(Account account, Date projectedAccountingDate, boolean forPrepayment) {
		generateOverdueBalancesSalePortfolio(account, projectedAccountingDate, forPrepayment);
		return (List<AccountOverdueBalance>)XPersistence.getManager()
				.createQuery("SELECT o FROM AccountOverdueBalance o "
						+ "WHERE o.account.accountId = :accountId")
				.setParameter("accountId", account.getAccountId())
				.getResultList();
	}
	
	@Deprecated
	public static List<AccountOverdueBalance> getOverdueBalancesSalePortfolioOld(Account account, Date projectedAccountingDate) {
		return getOverdueBalancesSalePortfolioOld(account, projectedAccountingDate, false);
	}
	
	public static void generateOverdueBalancesSalePortfolio(String queryAccount, Date projectedAccountingDate, boolean forPrepayment) {
		String schema = XPersistence.getDefaultSchema().toLowerCase();
		Date accountingDate = CompanyHelper.getCurrentAccountingDate();
		if (projectedAccountingDate == null)
			projectedAccountingDate = accountingDate;
		
		if (projectedAccountingDate.before(accountingDate))
			accountingDate = projectedAccountingDate;
		
		XPersistence.getManager().createNativeQuery("DELETE FROM "+schema+".account_overdue_balance o "
				+ "WHERE o.account_id in ("+ queryAccount+")"
				)
		.executeUpdate();
		
		XPersistence.commit();
		
		String query = "insert into "+schema+".account_overdue_balance ("
				+ "account_overdue_balance_id, account_id, subaccount, due_date, capital, interest, total, "
				+ "overdue_days, last_payment_date, accounting_date)";
		
		query += "select * from ("
				+ "select 's-odue-'||account_id||'-'||subaccount id, "
				+ "account_id, "
				+ "subaccount, "
				+ "due_date, "
				+ "sum(capital) capital, "
				+ "sum(interest) interest, "
				+ "sum(capital+interest) total, "
				+ "days_overdue, "
				+ "last_payment_date, "
				+ "cast(:projectedAccountingDate as date) "
				+ "from ( "
				+ "select b.account_id, b.subaccount, ap.due_date, "
				+ "COALESCE((case when b.category_id = 'SCAPITAL' then COALESCE(b.balance,0) else 0 end),0) capital, "
				+ "COALESCE((case when b.category_id = 'INTERESTIN' then COALESCE(b.balance,0) else 0 end),0) interest, "
				+ "COALESCE(:projectedAccountingDate - ap.due_date, 0) days_overdue, "
				+ "ap.last_payment_date "
				+ "from "+schema+".balance b, "+schema+".account_sold_paytable ap "
				+ "where b.to_date = :toDate "
				+ "and b.category_id in ('SCAPITAL','INTERESTIN') "
				+ "and b.account_id = ap.account_id "
				+ "and b.subaccount = ap.subaccount "
				+ "and ap.due_date <= :accountingDate "
				+ "and b.account_id in ("+queryAccount+")"
				+ ") x group by account_id, subaccount, due_date, days_overdue, last_payment_date ";
				
		if (projectedAccountingDate.after(accountingDate))
		{
				//overdue quota on projected date
			query +=" union all "
				+ "select 's-proj-'||ap.account_id||'-'||ap.subaccount id, "
				+ "ap.account_id, "
				+ "ap.subaccount, "
				+ "ap.due_date, "
				+ "COALESCE(ap.capital,0) capital, "
				+ "COALESCE(ap.interest,0) interest, "
				+ "COALESCE(ap.capital,0) + COALESCE(ap.interest,0) as total, "
				+ "COALESCE(:projectedAccountingDate - ap.due_date, 0) days_overdue, "
				+ "null last_payment_date, "
				+ ":projectedAccountingDate "
				+ "from "+schema+".account_sold_paytable ap "
				+ "where ap.due_date > :accountingDate "
				+ "and ap.due_date <= :projectedAccountingDate "
				+ "and ap.account_id in ("+queryAccount+") ";
		}
			
		if (forPrepayment)
		{
			query += " union all "
				+ "select 's-prep-'||ap.account_id||'-'||ap.subaccount id, "
				+ "ap.account_id, "
				+ "ap.subaccount, "
				+ "ap.due_date, "
				+ "COALESCE(ap.capital,0), "
				+ "(case when (ap.due_date-ap.provision_days) <= :projectedAccountingDate then "
				+ "round((ap.interest/ap.provision_days)*(:projectedAccountingDate-(ap.due_date-ap.provision_days)),2) "
				+ "else "
				+ "0 "
				+ "end) interest, "
				+ "COALESCE(ap.capital,0) + "
				+ "(case when (ap.due_date-ap.provision_days) <= :projectedAccountingDate then "
				+ "round((ap.interest/ap.provision_days)*(:projectedAccountingDate-(ap.due_date-ap.provision_days)),2) "
				+ "else "
				+ "0 "
				+ "end) total, "
				+ "0 days_overdue, "
				+ "null last_payment_date, "
				+ ":projectedAccountingDate "
				+ "from "+schema+".account_sold_paytable ap "
				+ "where ap.due_date > :projectedAccountingDate "
				+ "and ap.account_id in ("+queryAccount+") ";
		}

			query+= ") z "
					+ "order by z.account_id, z.subaccount";
		System.out.println(query);
		XPersistence.getManager().createNativeQuery(query)
		.setParameter("toDate", UtilApp.DEFAULT_EXPIRY_DATE, TemporalType.DATE)
		.setParameter("accountingDate", accountingDate, TemporalType.DATE)
		.setParameter("projectedAccountingDate", projectedAccountingDate, TemporalType.DATE)
		.executeUpdate();

		XPersistence.commit();

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
	public static BigDecimal getBalanceByAccountLoan(String accountId)
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
	public static BigDecimal getOverdueBalanceByAccountLoan(String accountId)
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
		
		if (accountLoan.getDaysGraceCollectionFee()!=null)
			daysGrace = accountLoan.getDaysGraceCollectionFee();
		
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
		
		return getLoanCollectionFee(accountLoan.getAccount().getProduct(), overdueDays, amount);
		
	}
	
	private static BigDecimal getDefaultInterestByQuota(AccountOverdueBalance overdueBalance)
	{
		BigDecimal overdueValue = BigDecimal.ZERO;
		BigDecimal defaultInterest = BigDecimal.ZERO;
		BigDecimal rate = null;
		BigDecimal dayRate = null;
		BigDecimal defaultInterestRateOrValue = null;
		int daysGrace = 0;
		int overdueDays = 0;
		
		AccountLoan accountLoan = XPersistence.getManager().find(AccountLoan.class, overdueBalance.getAccountId());
		
		if (accountLoan.getDaysGrace()!=null)
			daysGrace = accountLoan.getDaysGrace();
		
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
		
		defaultInterestRateOrValue = getDefaultInterestRateOrValue(accountLoan.getAccount().getProduct(), overdueDays);
		
		if (defaultInterestRateOrValue==null)
			throw new OperativeException("default_interest_rate_not_found",accountLoan.getAccount().getAccountId(), overdueBalance.getOverdueDays());
		
		if (defaultInterestRateOrValue!=null && defaultInterestRateOrValue.compareTo(BigDecimal.ZERO)==0)
			return defaultInterest;
		
		if (overdueValue.compareTo(BigDecimal.ZERO)<=0)
			return defaultInterest;
			
		if (overdueDays<=0)
			return defaultInterest;
		
		if (overdueDays<=daysGrace)
			return defaultInterest;

		if (accountLoan.getApplyDefaultInterestAccrued()==Types.YesNoIntegerType.NO)
			overdueDays = overdueDays-daysGrace;
		
		if(accountLoan.getAccount().getProduct().getDefaultInterestType().equals(Types.RateValue.RATE))
		{
			defaultInterestRateOrValue = defaultInterestRateOrValue.add(BigDecimal.ONE);
			rate = accountLoan.getInterestRate().multiply(defaultInterestRateOrValue);
			dayRate = rate.divide(new BigDecimal(100), 10, RoundingMode.HALF_UP);
			dayRate = dayRate.divide(new BigDecimal("360"), 10, RoundingMode.HALF_UP);	
			defaultInterest = overdueValue.multiply(dayRate);
			defaultInterest = defaultInterest.multiply(new BigDecimal(overdueDays)).setScale(2, RoundingMode.HALF_UP);
			
		}
		else if(accountLoan.getAccount().getProduct().getDefaultInterestType().equals(Types.RateValue.VALUE))
		{
			defaultInterest = defaultInterestRateOrValue.multiply(new BigDecimal(overdueDays)).setScale(2, RoundingMode.HALF_UP);
		}
			
		return defaultInterest;
	}
	
	@SuppressWarnings("unchecked")
	private static BigDecimal getDefaultInterestRateOrValue(Product product, int overdueDays)
	{
		List<DefaultInterestRate> rates = XPersistence.getManager()
				.createQuery("SELECT o FROM DefaultInterestRate o "
				+ "WHERE :overdueDays BETWEEN o.fromDays AND o.toDays "
				+ "AND o.product.productId = :productId")
				.setParameter("overdueDays", overdueDays)
				.setParameter("productId", product.getProductId())
				.getResultList();
		
		if (rates != null && !rates.isEmpty()){
			DefaultInterestRate rate = rates.get(0);
			if (product.getDefaultInterestType().equals(Types.RateValue.RATE))
				return rate.getRate();
			else if (product.getDefaultInterestType().equals(Types.RateValue.VALUE))
				return rate.getDailyValue();
			else
				return null;
		}
		else
			return null;
		
	}
	
	@SuppressWarnings("unchecked")
	private static BigDecimal getLoanCollectionFee(Product product, int overdueDays, BigDecimal amount)
	{		
		List<LoanCollectionFee> fees = XPersistence.getManager()
				.createQuery("SELECT o FROM LoanCollectionFee o "
				+ "WHERE :overdueDays BETWEEN o.fromDays AND o.toDays "
				+ "AND :amount BETWEEN o.fromAmount AND o.toAmount "
				+ "AND o.product.productId = :productId")
				.setParameter("overdueDays", overdueDays)
				.setParameter("amount", amount)
				.setParameter("productId", product.getProductId())
				.getResultList();
		
		if (fees != null && !fees.isEmpty()){
			LoanCollectionFee fee = fees.get(0);
			return fee.getValue();
		}
		else
			return BigDecimal.ZERO;
		
	}
	
	@SuppressWarnings("unchecked")
	public static List<TransactionAccount> getTransactionAccountsForAccountLoanPayment(Transaction transaction, 
			AccountLoan accountLoan, Account debitAccount, 
			BigDecimal transactionValue) throws Exception
	{
		String queryAccount = "'"+accountLoan.getAccountId()+"'";
		
		AccountLoanHelper.generateOverdueBalances(
				queryAccount,
				transaction.getValueDate()==null?transaction.getAccountingDate():transaction.getValueDate(),
				false);
		
		List<AccountOverdueBalance> overdueBalances =  AccountLoanHelper.getOverdueBalances(accountLoan.getAccount());
				
		List<PrelationOrder> prelationOrders = XPersistence.getManager()
				.createQuery("SELECT o FROM PrelationOrder o "
						+ "WHERE o.product.productId = :productId "
						+ "ORDER BY o.prelationOrder")
				.setParameter("productId", accountLoan.getAccount().getProduct().getProductId())
				.getResultList();
		
		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		TransactionAccount ta = null;
		BigDecimal valueToApply = BigDecimal.ZERO;
		Account disbursementAccount = accountLoan.getDisbursementAccount();
		
		if (overdueBalances==null || overdueBalances.isEmpty())
    		throw new OperativeException("payment_not_processed_with_out_overdue_balances");

		if (prelationOrders==null || prelationOrders.isEmpty())
    		throw new OperativeException("prelation_orders_not_found", accountLoan.getAccount().getProduct().getProductId());
		
		if (accountLoan.getDisbursementAccount()==null)
			throw new OperativeException("disbursement_account_not_found");
		
		if (!accountLoan.getDisbursementAccount().getAccountId().equals(debitAccount.getAccountId()))
		{
			ta = TransactionAccountHelper.createCustomDebitTransactionAccount(debitAccount, transactionValue, transaction);
			ta.setRemark(XavaResources.getString("transfer_to_customer_for_payment_loan", accountLoan.getAccountId()));
			transactionAccounts.add(ta);
			
			ta = TransactionAccountHelper.createCustomCreditTransactionAccount(disbursementAccount, transactionValue, transaction);
			ta.setRemark(XavaResources.getString("transfer_from_broker_for_payment_loan", accountLoan.getAccountId()));
			transactionAccounts.add(ta);
		}
		else
			disbursementAccount = debitAccount;
		
		outerloop:
		for (AccountOverdueBalance quota: overdueBalances)
		{
			for (PrelationOrder prelation : prelationOrders)
			{
				if (transactionValue.compareTo(BigDecimal.ZERO)==0)
					break;

				valueToApply = getValueToApplyByPrelationOrder(prelation, quota);

				if (valueToApply.compareTo(BigDecimal.ZERO)==0)
					continue;

				if (prelation.getAllowPartialPayment().equals(YesNoIntegerType.NO))
					if (transactionValue.compareTo(valueToApply)<0)					
						break outerloop;

				if (transactionValue.compareTo(valueToApply)<0)
					valueToApply = transactionValue;
				
				transactionAccounts.addAll(getTransactionAccountsByPrelationOrder(prelation, accountLoan, quota, valueToApply, transaction));

				transactionValue = transactionValue.subtract(valueToApply);
			}		
		}
		return transactionAccounts;
	}
	
	@SuppressWarnings("unchecked")
	public static void postAccountLoanPaymentSaveAction(Transaction transaction) throws Exception
	{
		Date paymentDate = transaction.getValueDate()==null?transaction.getAccountingDate():transaction.getValueDate();
		
		if (TransactionHelper.isFinancialSaved(transaction))
		{
			System.out.println("POST SAVE ACTION**********************");
			List<AccountPaytable> accountPaytables = XPersistence.getManager()
					.createQuery("SELECT ap FROM AccountPaytable ap "
							+ "WHERE ap.accountId = :accountId "
							+ "AND ap.subaccount IN (SELECT DISTINCT(o.subaccount) "
							+ "FROM TransactionAccount o "
							+ "WHERE o.transaction.transactionId = :transactionId "
							+ "AND o.account.accountId = :accountId "
							+ "AND o.debitOrCredit = :debitOrCredit) "
							+ "ORDER BY ap.subaccount")
					.setParameter("accountId", transaction.getCreditAccount().getAccountId())
					.setParameter("transactionId", transaction.getTransactionId())
					.setParameter("debitOrCredit", DebitOrCredit.CREDIT)
					.getResultList();
			
			for (AccountPaytable accountPaytable: accountPaytables)
			{
				BigDecimal capitalBalance = AccountLoanHelper.getBalanceByQuotaAndCategory(accountPaytable.getAccountId(), accountPaytable.getSubaccount(), CategoryHelper.CAPITAL_CATEGORY);			
				
				if (capitalBalance.compareTo(BigDecimal.ZERO)<=0)
				{
					accountPaytable.setPaymentDate(paymentDate);
					System.out.println("Update AccountPaytable "+accountPaytable.getAccountId()+"|"+accountPaytable.getSubaccount()+" to cancel");
				}
				accountPaytable.setLastPaymentDate(paymentDate);
				XPersistence.getManager().merge(accountPaytable);
				System.out.println("Update AccountPaytable "+accountPaytable.getAccountId()+"|"+accountPaytable.getSubaccount()+" last payment = "+transaction.getAccountingDate());
			}
			
			accountPaytables = XPersistence.getManager()
					.createQuery("SELECT ap FROM AccountPaytable ap "
							+ "WHERE ap.accountId = :accountId "
							+ "AND ap.subaccount IN (SELECT DISTINCT(o.subaccount) "
							+ "FROM TransactionAccount o "
							+ "WHERE o.transaction.transactionId = :transactionId "
							+ "AND o.account.accountId = :accountId "
							+ "AND o.category.categoryId = :categoryId "
							+ "AND o.debitOrCredit = :debitOrCredit) "
							+ "ORDER BY ap.subaccount")
					.setParameter("accountId", transaction.getCreditAccount().getAccountId())
					.setParameter("transactionId", transaction.getTransactionId())
					.setParameter("categoryId", CategoryHelper.COLLECTION_FEE_IN_CATEGORY)
					.setParameter("debitOrCredit", DebitOrCredit.CREDIT)
					.getResultList();
			
			for (AccountPaytable accountPaytable: accountPaytables)
			{
				accountPaytable.setLastPaymentDateCollection(paymentDate);
				XPersistence.getManager().merge(accountPaytable);
				System.out.println("Update AccountPaytable "+accountPaytable.getAccountId()+"|"+accountPaytable.getSubaccount()+" last payment collection = "+transaction.getAccountingDate());
			}
			
			accountPaytables = XPersistence.getManager()
					.createQuery("SELECT ap FROM AccountPaytable ap "
							+ "WHERE ap.accountId = :accountId "
							+ "AND ap.subaccount IN (SELECT DISTINCT(o.subaccount) "
							+ "FROM TransactionAccount o "
							+ "WHERE o.transaction.transactionId = :transactionId "
							+ "AND o.account.accountId = :accountId "
							+ "AND o.category.categoryId = :categoryId "
							+ "AND o.debitOrCredit = :debitOrCredit) "
							+ "ORDER BY ap.subaccount")
					.setParameter("accountId", transaction.getCreditAccount().getAccountId())
					.setParameter("transactionId", transaction.getTransactionId())
					.setParameter("categoryId", CategoryHelper.DEFAULT_INTEREST_IN_CATEGORY)
					.setParameter("debitOrCredit", DebitOrCredit.CREDIT)
					.getResultList();
			
			for (AccountPaytable accountPaytable: accountPaytables)
			{
				accountPaytable.setLastPaymentDateDefaultInterest(paymentDate);
				XPersistence.getManager().merge(accountPaytable);
				System.out.println("Update AccountPaytable "+accountPaytable.getAccountId()+"|"+accountPaytable.getSubaccount()+" last payment default interest = "+transaction.getAccountingDate());
			}
			/*
			if (AccountLoanHelper.getBalanceByaccountLoan(transaction.getCreditAccount().getAccountId()).compareTo(BigDecimal.ZERO)<=0)
			{
				Account persistAccount = XPersistence.getManager().find(Account.class, transaction.getCreditAccount().getAccountId());
				persistAccount.setAccountStatus(AccountStatusHelper.getAccountStatus(AccountLoanHelper.STATUS_LOAN_CANCEL));
				persistAccount.setCancellationDate(currentDate);
				AccountHelper.updateAccount(persistAccount);
			}
			*/
			
			XPersistence.getManager().createQuery("DELETE FROM AccountOverdueBalance o "
					+ "WHERE o.accountId=:accountId ")
			.setParameter("accountId", transaction.getCreditAccount().getAccountId())
			.executeUpdate();			
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void postSalePortfolioPaymentSaveAction(Transaction transaction) throws Exception
	{
		if (TransactionHelper.isFinancialSaved(transaction))
		{
			System.out.println("POST SAVE ACTION**********************");
			List<AccountSoldPaytable> accountPaytables = XPersistence.getManager()
					.createQuery("SELECT ap FROM AccountSoldPaytable ap "
							+ "WHERE ap.accountId = :accountId "
							+ "AND ap.subaccount IN (SELECT DISTINCT(o.subaccount) "
							+ "FROM TransactionAccount o "
							+ "WHERE o.transaction.transactionId = :transactionId "
							+ "AND o.account.accountId = :accountId "
							+ "AND o.debitOrCredit = :debitOrCredit) "
							+ "ORDER BY ap.subaccount")
					.setParameter("accountId", transaction.getDebitAccount().getAccountId())
					.setParameter("transactionId", transaction.getTransactionId())
					.setParameter("debitOrCredit", DebitOrCredit.DEBIT)
					.getResultList();
			
			for (AccountSoldPaytable accountPaytable: accountPaytables)
			{
				BigDecimal capitalBalance = AccountLoanHelper.getBalanceByQuotaAndCategory(accountPaytable.getAccountId(), accountPaytable.getSubaccount(), CategoryHelper.SALE_CAPITAL_CATEGORY);			
				
				if (capitalBalance.compareTo(BigDecimal.ZERO)<=0)
				{
					accountPaytable.setPaymentDate(transaction.getAccountingDate());
					System.out.println("Update AccountSoldPaytable "+accountPaytable.getAccountId()+"|"+accountPaytable.getSubaccount()+" to cancel");
				}
				accountPaytable.setLastPaymentDate(transaction.getAccountingDate());
				XPersistence.getManager().merge(accountPaytable);
				System.out.println("Update AccountSoldPaytable "+accountPaytable.getAccountId()+"|"+accountPaytable.getSubaccount()+" last payment = "+transaction.getAccountingDate());
			}
			
			/*
			if (AccountLoanHelper.getBalanceByaccountLoan(transaction.getCreditAccount().getAccountId()).compareTo(BigDecimal.ZERO)<=0)
			{
				Account persistAccount = XPersistence.getManager().find(Account.class, transaction.getCreditAccount().getAccountId());
				persistAccount.setAccountStatus(AccountStatusHelper.getAccountStatus(AccountLoanHelper.STATUS_LOAN_CANCEL));
				persistAccount.setCancellationDate(currentDate);
				AccountHelper.updateAccount(persistAccount);
			}
			*/
			
			XPersistence.getManager().createQuery("DELETE FROM AccountOverdueBalance o "
					+ "WHERE o.accountId=:accountId ")
			.setParameter("accountId", transaction.getDebitAccount().getAccountId())
			.executeUpdate();
		}
		
	}

	public static List<TransactionAccount> getTransactionAccountsForBatchPymentSalePortfolio(
			Transaction transaction, 
			AccountLoan accountLoan, 
			Account creditAccount, 
			Integer subAccount,
			BigDecimal capital,
			BigDecimal interest,
			BigDecimal defaultInterest) throws Exception
	{
		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		TransactionAccount ta = null;	
	
		AccountSoldPaytable quota = (AccountSoldPaytable) XPersistence.getManager()
				.createQuery("SELECT o FROM AccountSoldPaytable o "
				+ "WHERE o.account.accountId = :accountId "
				+ "AND o.subaccount = :subaccount")
				.setParameter("accountId", accountLoan.getAccount().getAccountId())
				.setParameter("subaccount", subAccount)
				.getSingleResult();
		
		//DefaultInterest
		if (defaultInterest!=null && defaultInterest.compareTo(BigDecimal.ZERO)>0)
		{
			ta = TransactionAccountHelper.createCustomDebitTransactionAccount(accountLoan.getAccount(), quota.getSubaccount(), defaultInterest, transaction, CategoryHelper.getCategoryById(CategoryHelper.DEFAULT_INTEREST_EX_CATEGORY), quota.getDueDate());
			ta.setRemark(XavaResources.getString("sale_default_interest_payment_quota_number", accountLoan.getAccountId(), quota.getSubaccount()));
			transactionAccounts.add(ta);
			
			ta = TransactionAccountHelper.createCustomCreditTransactionAccount(creditAccount, defaultInterest, transaction);
			ta.setRemark(XavaResources.getString("sale_default_interest_payment_quota_number", accountLoan.getAccountId(), quota.getSubaccount()));
			transactionAccounts.add(ta);
		}
		
		//Interest
		if (interest!=null && interest.compareTo(BigDecimal.ZERO)>0)
		{			
			ta = TransactionAccountHelper.createCustomDebitTransactionAccount(accountLoan.getAccount(), quota.getSubaccount(), interest, transaction, CategoryHelper.getCategoryById(CategoryHelper.INTEREST_IN_CATEGORY), quota.getDueDate());
			ta.setRemark(XavaResources.getString("sale_interest_payment_quota_number", accountLoan.getAccountId(), quota));
			transactionAccounts.add(ta);
			
			ta = TransactionAccountHelper.createCustomCreditTransactionAccount(creditAccount, interest, transaction);
			ta.setRemark(XavaResources.getString("sale_interest_payment_quota_number", accountLoan.getAccountId(), quota.getSubaccount()));
			transactionAccounts.add(ta);			
		}

		//CapitalPayment
		if (capital!=null && capital.compareTo(BigDecimal.ZERO)>0)
		{
			ta = TransactionAccountHelper.createCustomDebitTransactionAccount(accountLoan.getAccount(), quota.getSubaccount(), capital, transaction, CategoryHelper.getCategoryById(CategoryHelper.SALE_CAPITAL_CATEGORY), quota.getDueDate());
			ta.setRemark(XavaResources.getString("sale_capital_payment_quota_number", accountLoan.getAccountId(), quota));
			transactionAccounts.add(ta);
			
			ta = TransactionAccountHelper.createCustomCreditTransactionAccount(creditAccount, capital, transaction);
			ta.setRemark(XavaResources.getString("sale_capital_payment_quota_number", accountLoan.getAccountId(), quota.getSubaccount()));
			transactionAccounts.add(ta);			
		}
		return transactionAccounts;
	}
	
	public static List<TransactionAccount> getTransactionAccountsForNormalPymentSalePortfolio(
			Transaction transaction, 
			AccountLoan accountLoan, 
			Account creditAccount, 
			BigDecimal capital,
			BigDecimal interest,
			BigDecimal defaultInterest) throws Exception
	{
		List<AccountOverdueBalance> overdueBalances = AccountLoanHelper.getOverdueBalancesSalePortfolio(accountLoan.getAccount());
		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		TransactionAccount ta = null;
		BigDecimal valueToApply = BigDecimal.ZERO;
		int firstQuota = 1;
		
		if (overdueBalances==null || overdueBalances.isEmpty())
    		throw new OperativeException("payment_not_processed_with_out_overdue_balances");
	
		if (accountLoan.getDisbursementAccount()==null)
			throw new OperativeException("disbursement_account_not_found");
		
		BigDecimal transactionValue = capital.add(interest);
		
		for (AccountOverdueBalance quota: overdueBalances)
		{
			if (transactionValue.compareTo(BigDecimal.ZERO)==0)
				break;
			
			//Interest
			if (quota.getInterest().compareTo(BigDecimal.ZERO)>0)
			{
				valueToApply = quota.getInterest();
				
				if (transactionValue.compareTo(valueToApply)<0)
					valueToApply = transactionValue;
				
				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(accountLoan.getAccount(), quota.getSubaccount(), valueToApply, transaction, CategoryHelper.getCategoryById(CategoryHelper.INTEREST_IN_CATEGORY), quota.getDueDate());
				ta.setRemark(XavaResources.getString("sale_interest_payment_quota_number", accountLoan.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(creditAccount, valueToApply, transaction);
				ta.setRemark(XavaResources.getString("sale_interest_payment_quota_number", accountLoan.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				transactionValue = transactionValue.subtract(valueToApply);
				
			}
			
			if (transactionValue.compareTo(BigDecimal.ZERO)==0)
				break;
			
			//CapitalPayment
			if (quota.getCapital().compareTo(BigDecimal.ZERO)>0)
			{
				valueToApply = quota.getCapital();
				
				if (transactionValue.compareTo(valueToApply)<0)
					valueToApply = transactionValue;
				
				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(accountLoan.getAccount(), quota.getSubaccount(), valueToApply, transaction, CategoryHelper.getCategoryById(CategoryHelper.SALE_CAPITAL_CATEGORY), quota.getDueDate());
				ta.setRemark(XavaResources.getString("sale_capital_payment_quota_number", accountLoan.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(creditAccount, valueToApply, transaction);
				ta.setRemark(XavaResources.getString("sale_capital_payment_quota_number", accountLoan.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				transactionValue = transactionValue.subtract(valueToApply);
				
			}
			
			if (firstQuota==1)
			{
				if (defaultInterest!=null && defaultInterest.compareTo(BigDecimal.ZERO)>0)
				{
					ta = TransactionAccountHelper.createCustomDebitTransactionAccount(accountLoan.getAccount(), quota.getSubaccount(), defaultInterest, transaction, CategoryHelper.getCategoryById(CategoryHelper.DEFAULT_INTEREST_EX_CATEGORY));
					ta.setRemark(XavaResources.getString("sale_default_interest_payment", accountLoan.getAccountId()));
					transactionAccounts.add(ta);
					
					ta = TransactionAccountHelper.createCustomCreditTransactionAccount(creditAccount, defaultInterest, transaction);
					ta.setRemark(XavaResources.getString("sale_default_interest_payment", accountLoan.getAccountId()));
					transactionAccounts.add(ta);
				}
			}
			
			firstQuota++;
			
		}
		return transactionAccounts;
	}

	private static BigDecimal getValueToApplyByPrelationOrder(PrelationOrder prelation, 
			AccountOverdueBalance quota) throws Exception
	{
		//ReceivableFeePayment
		if (prelation.getCategory().getCategoryId().equals("RECEIFEERE"))
			return quota.getReceivableFee();
		//LegalFeePayment
		else if (prelation.getCategory().getCategoryId().equals("LEGALFEERE"))
			return quota.getLegalFee();
		//CollectionFee
		else if (prelation.getCategory().getCategoryId().equals("COLLEFEEIN"))
			return quota.getCollectionFee();	
		//DefaultInterest
		else if (prelation.getCategory().getCategoryId().equals("DEFINTERIN"))
			return quota.getDefaultInterest();
		//Insurance
		else if (prelation.getCategory().getCategoryId().equals("INSURANRE"))
			return quota.getInsurance();
		//InsuranceMortgage
		else if (prelation.getCategory().getCategoryId().equals("MORTGAGERE"))
			return quota.getInsuranceMortgage();
		//Interest
		else if (prelation.getCategory().getCategoryId().equals("INTERESTPR"))
			return quota.getInterest();
		//CapitalPayment
		else if (prelation.getCategory().getCategoryId().equals("CAPITAL"))
			return quota.getCapital();
		return BigDecimal.ZERO;
	}
	
	private static List<TransactionAccount> getTransactionAccountsByPrelationOrder(PrelationOrder prelation, 
			AccountLoan accountLoan, 
			AccountOverdueBalance quota, 
			BigDecimal valueToApply, 
			Transaction transaction) throws Exception
	{
		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		TransactionAccount ta = null;
		Account disbursementAccount = accountLoan.getDisbursementAccount();
		//ReceivableFeePayment
		if (prelation.getCategory().getCategoryId().equals("RECEIFEERE"))
		{
			ta = TransactionAccountHelper.createCustomCreditTransactionAccount(accountLoan.getAccount(), quota.getSubaccount(), valueToApply, transaction, CategoryHelper.getCategoryById(CategoryHelper.RECEIVABLE_FEE_RE_CATEGORY), quota.getDueDate());
			ta.setRemark(XavaResources.getString("receivable_fee_payment", accountLoan.getAccountId()));
			transactionAccounts.add(ta);
			
			ta = TransactionAccountHelper.createCustomDebitTransactionAccount(disbursementAccount, valueToApply, transaction);
			ta.setRemark(XavaResources.getString("receivable_fee_payment", accountLoan.getAccountId()));
			transactionAccounts.add(ta);
		}
		//LegalFeePayment
		else if (prelation.getCategory().getCategoryId().equals("LEGALFEERE"))
		{
			ta = TransactionAccountHelper.createCustomCreditTransactionAccount(accountLoan.getAccount(), quota.getSubaccount(), valueToApply, transaction, CategoryHelper.getCategoryById(CategoryHelper.LEGAL_FEE_RE_CATEGORY), quota.getDueDate());
			ta.setRemark(XavaResources.getString("legal_fee_payment", accountLoan.getAccountId()));
			transactionAccounts.add(ta);
			
			ta = TransactionAccountHelper.createCustomDebitTransactionAccount(disbursementAccount, valueToApply, transaction);
			ta.setRemark(XavaResources.getString("legal_fee_payment", accountLoan.getAccountId()));
			transactionAccounts.add(ta);
		}
		//CollectionFee
		else if (prelation.getCategory().getCategoryId().equals("COLLEFEEIN"))
		{
			ta = TransactionAccountHelper.createCustomCreditTransactionAccount(accountLoan.getAccount(), quota.getSubaccount(), valueToApply, transaction, CategoryHelper.getCategoryById(CategoryHelper.COLLECTION_FEE_IN_CATEGORY), quota.getDueDate());
			ta.setRemark(XavaResources.getString("collection_fee_payment_quota_number", accountLoan.getAccountId(), quota.getSubaccount()));
			transactionAccounts.add(ta);
			
			ta = TransactionAccountHelper.createCustomDebitTransactionAccount(disbursementAccount, valueToApply, transaction);
			ta.setRemark(XavaResources.getString("collection_fee_payment_quota_number", accountLoan.getAccountId(), quota.getSubaccount()));
			transactionAccounts.add(ta);
		}	
		//DefaultInterest
		else if (prelation.getCategory().getCategoryId().equals("DEFINTERIN"))
		{
			ta = TransactionAccountHelper.createCustomCreditTransactionAccount(accountLoan.getAccount(), quota.getSubaccount(), valueToApply, transaction, CategoryHelper.getCategoryById(CategoryHelper.DEFAULT_INTEREST_IN_CATEGORY), quota.getDueDate());
			ta.setRemark(XavaResources.getString("default_interest_payment_quota_number", accountLoan.getAccountId(), quota.getSubaccount()));
			transactionAccounts.add(ta);
			
			ta = TransactionAccountHelper.createCustomDebitTransactionAccount(disbursementAccount, valueToApply, transaction);
			ta.setRemark(XavaResources.getString("default_interest_payment_quota_number", accountLoan.getAccountId(), quota.getSubaccount()));
			transactionAccounts.add(ta);
		}
		//Insurance
		else if (prelation.getCategory().getCategoryId().equals("INSURANRE"))
		{
			ta = TransactionAccountHelper.createCustomCreditTransactionAccount(accountLoan.getAccount(), quota.getSubaccount(), valueToApply, transaction, CategoryHelper.getCategoryById(CategoryHelper.INSURANCE_RECEIVABLE_CATEGORY), quota.getDueDate());
			ta.setRemark(XavaResources.getString("insurance_payment_quota_number", accountLoan.getAccountId(), quota.getSubaccount()));
			transactionAccounts.add(ta);
			
			ta = TransactionAccountHelper.createCustomDebitTransactionAccount(disbursementAccount, valueToApply, transaction);
			ta.setRemark(XavaResources.getString("insurance_payment_quota_number", accountLoan.getAccountId(), quota.getSubaccount()));
			transactionAccounts.add(ta);
		}
		//InsuranceMortgage
		else if (prelation.getCategory().getCategoryId().equals("MORTGAGERE"))
		{
			ta = TransactionAccountHelper.createCustomCreditTransactionAccount(accountLoan.getAccount(), quota.getSubaccount(), valueToApply, transaction, CategoryHelper.getCategoryById(CategoryHelper.MORTGAGE_RECEIVABLE_CATEGORY), quota.getDueDate());
			ta.setRemark(XavaResources.getString("insurance_mortgage_payment_quota_number", accountLoan.getAccountId(), quota.getSubaccount()));
			transactionAccounts.add(ta);
			
			ta = TransactionAccountHelper.createCustomDebitTransactionAccount(disbursementAccount, valueToApply, transaction);
			ta.setRemark(XavaResources.getString("insurance_mortgage_payment_quota_number", accountLoan.getAccountId(), quota.getSubaccount()));
			transactionAccounts.add(ta);
		}
		//Interest
		else if (prelation.getCategory().getCategoryId().equals("INTERESTPR"))
		{
			ta = TransactionAccountHelper.createCustomCreditTransactionAccount(accountLoan.getAccount(), quota.getSubaccount(), valueToApply, transaction, CategoryHelper.getCategoryById(CategoryHelper.INTEREST_PR_CATEGORY), quota.getDueDate());
			ta.setRemark(XavaResources.getString("interest_payment_quota_number", accountLoan.getAccountId(), quota.getSubaccount()));
			transactionAccounts.add(ta);
			
			ta = TransactionAccountHelper.createCustomDebitTransactionAccount(disbursementAccount, valueToApply, transaction);
			ta.setRemark(XavaResources.getString("interest_payment_quota_number", accountLoan.getAccountId(), quota.getSubaccount()));
			transactionAccounts.add(ta);
		}
		//CapitalPayment
		else if (prelation.getCategory().getCategoryId().equals("CAPITAL"))
		{
			ta = TransactionAccountHelper.createCustomCreditTransactionAccount(accountLoan.getAccount(), quota.getSubaccount(), valueToApply, transaction, CategoryHelper.getCategoryById(CategoryHelper.CAPITAL_CATEGORY), quota.getDueDate());
			ta.setRemark(XavaResources.getString("capital_payment_quota_number", accountLoan.getAccountId(), quota.getSubaccount()));
			transactionAccounts.add(ta);
			
			ta = TransactionAccountHelper.createCustomDebitTransactionAccount(disbursementAccount, valueToApply, transaction);
			ta.setRemark(XavaResources.getString("capital_payment_quota_number", accountLoan.getAccountId(), quota.getSubaccount()));
			transactionAccounts.add(ta);			
		}
		return transactionAccounts;
	}
	
	public static BigDecimal getIncomeUtilityDistribution(AccountPortfolio accountPortfolio)
	{
		String distributionUtility = accountPortfolio.getSalePortfolioUtilityDistribution();

		if (distributionUtility==null)
			return BigDecimal.ZERO;
		
		try
		{
			String[] distribution = distributionUtility.split("/");
			BigDecimal liabilityDistribution = new BigDecimal(distribution[0]);
			BigDecimal incomeDistribution = new BigDecimal(distribution[1]);
			BigDecimal utility = accountPortfolio.getSaleSpread().subtract(accountPortfolio.getPurchaseSpread());
			
			if (liabilityDistribution.add(incomeDistribution).compareTo(new BigDecimal(100))!=0)
				throw new InternalException("sale_portfolio_utility_distribution_is_incorrect", distributionUtility);
			
			return utility.multiply(incomeDistribution.divide(new BigDecimal(100), 10, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP);
		}
		catch(Exception e)
		{
			throw new InternalException("sale_portfolio_utility_distribution_is_incorrect", distributionUtility);
		}
		
	}
	
	@Deprecated
	@SuppressWarnings("unchecked")
	public static List<AccountOverdueBalance> getOverdueBalancesSalePortfolioOld(Account account, Date projectedAccountingDate, boolean forPrepayment) {
		String schema = XPersistence.getDefaultSchema().toLowerCase();
		List<AccountOverdueBalance> overdueBalances = new ArrayList<AccountOverdueBalance>();
		Date accountingDate = CompanyHelper.getCurrentAccountingDate();
		if (projectedAccountingDate == null)
			projectedAccountingDate = accountingDate;
		
		String query = "select * from ("
				+ "select subaccount, "
				+ "due_date, "
				+ "sum(capital) capital, "
				+ "sum(interest) interest, "
				+ "days_overdue, "
				+ "last_payment_date "
				+ "from ( "
				+ "select b.account_id, b.subaccount, ap.due_date, "
				+ "COALESCE((case when b.category_id = 'SCAPITAL' then COALESCE(b.balance,0) else 0 end),0) capital, "
				+ "COALESCE((case when b.category_id = 'INTERESTIN' then COALESCE(b.balance,0) else 0 end),0) interest, "
				+ "COALESCE(:projectedAccountingDate - ap.due_date, 0) days_overdue, "
				+ "ap.last_payment_date "
				+ "from "+schema+".balance b, "+schema+".account_sold_paytable ap "
				+ "where b.account_id = :accountId "
				+ "and b.to_date = :toDate "
				+ "and b.category_id in ('SCAPITAL','INTERESTIN') "
				+ "and b.account_id = ap.account_id "
				+ "and b.subaccount = ap.subaccount "
				+ "and ap.due_date <= :accountingDate "
				+ ") x group by account_id, subaccount, due_date, days_overdue, last_payment_date ";
				
		if (projectedAccountingDate.after(accountingDate))
		{
				//overdue quota on projected date
			query +="union all "
				+ "select ap.subaccount, "
				+ "ap.due_date, "
				+ "COALESCE(ap.capital,0) capital, "
				+ "COALESCE(ap.interest,0) interest, "
				+ "COALESCE(:projectedAccountingDate - ap.due_date, 0) days_overdue, "
				+ "null last_payment_date "
				+ "from "+schema+".account_sold_paytable ap "
				+ "where ap.account_id = :accountId "
				+ "and ap.due_date > :accountingDate "
				+ "and ap.due_date <= :projectedAccountingDate ";
		}
			
		if (forPrepayment)
		{
			query +=  "union all "
				+ "select ap.subaccount, "
				+ "ap.due_date, "
				+ "COALESCE(ap.capital,0), "
				+ "(case when (COALESCE(ap.due_date,0)-COALESCE(ap.provision_days,0)) <= :projectedAccountingDate then "
				+ "round((ap.interest/ap.provision_days)*(:projectedAccountingDate-(ap.due_date-ap.provision_days)),2) "
				+ "else "
				+ "0 "
				+ "end) interest, "
				+ "0 days_overdue, "
				+ "null last_payment_date "
				+ "from "+schema+".account_sold_paytable ap "
				+ "where ap.account_id = :accountId "
				+ "and ap.due_date > :projectedAccountingDate ";
		}
	
			query+= ") z "
					+ "order by z.subaccount";

		List<Object[]> balances = XPersistence.getManager()
				.createNativeQuery(query)
				.setParameter("accountId", account.getAccountId())
				.setParameter("toDate", UtilApp.DEFAULT_EXPIRY_DATE)
				.setParameter("accountingDate", accountingDate)
				.setParameter("projectedAccountingDate", projectedAccountingDate)
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
				overdueBalance.setOverdueDays((Integer) balance[4]);
				overdueBalance.setLastPaymentDate((Date) balance[5]);
				
				overdueBalance.setInsurance(BigDecimal.ZERO);
				overdueBalance.setInsuranceMortgage(BigDecimal.ZERO);
				overdueBalance.setReceivableFee(BigDecimal.ZERO);
				overdueBalance.setLegalFee(BigDecimal.ZERO);
				overdueBalance.setRealOverdueDays(0);
				overdueBalance.setLastPaymentDateCollection(null);
				overdueBalance.setLastPaymentDateDefaultInterest(null);
				overdueBalance.setDefaultInterest(BigDecimal.ZERO);
				overdueBalance.setCollectionFee(BigDecimal.ZERO);
				
				BigDecimal total = overdueBalance.getCapital()
						.add(overdueBalance.getInterest());
				overdueBalance.setTotal(total);
				if (total.compareTo(BigDecimal.ZERO)>0)
					overdueBalances.add(overdueBalance);
			}
		}		
		
		
		for (AccountOverdueBalance overdueBalance:overdueBalances)
			XPersistence.getManager().persist(overdueBalance);

		XPersistence.commit();
		
		return overdueBalances;
	}
	
	@Deprecated
	@SuppressWarnings("unchecked")
	public static List<AccountOverdueBalance> getOverdueBalancesOld(Account account, Date projectedAccountingDate, boolean forPrepayment) {
		String schema = XPersistence.getDefaultSchema().toLowerCase();
		List<AccountOverdueBalance> overdueBalances = new ArrayList<AccountOverdueBalance>();
		Date accountingDate = CompanyHelper.getCurrentAccountingDate();
		if (projectedAccountingDate == null)
			projectedAccountingDate = accountingDate;
		
		String query = "select * from ( "
				//overdue quotas in balance
				+ "select subaccount, "
				+ "due_date, "
				+ "sum(capital) capital, "
				+ "sum(interest) interest, "
				+ "sum(insurance) insurance, "
				+ "sum(insurance_mortgage) insurance_mortgage, "
				+ "sum(receivable_fee) receivable_fee, "
				+ "sum(legal_fee) legal_fee, "
				+ "days_overdue, "
				+ "real_days_overdue, "
				+ "last_payment_date, "
				+ "last_payment_date_collection, "
				+ "last_payment_date_default_int "
				+ "from ( "
				+ "select b.subaccount, ap.due_date, "
				+ "COALESCE((case when b.category_id = 'CAPITAL' then COALESCE(b.balance,0) else 0 end),0) capital, "
				+ "COALESCE((case when b.category_id = 'INTERESTPR' then COALESCE(b.balance,0) else 0 end),0) interest, "
				+ "COALESCE((case when b.category_id = 'INSURANRE' then COALESCE(b.balance,0) else 0 end),0) insurance, "
				+ "COALESCE((case when b.category_id = 'MORTGAGERE' then COALESCE(b.balance,0) else 0 end),0) insurance_mortgage, "
				+ "COALESCE((case when b.category_id = 'RECEIFEERE' then COALESCE(b.balance,0) else 0 end),0) receivable_fee, "
				+ "COALESCE((case when b.category_id = 'LEGALFEERE' then COALESCE(b.balance,0) else 0 end),0) legal_fee, "
				+ "COALESCE(:projectedAccountingDate - ap.due_date, 0) days_overdue, "
				+ "COALESCE(:projectedAccountingDate - ap.last_payment_date_default_int, 0) real_days_overdue, "
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
				+ ") x group by subaccount, due_date, days_overdue, "
				+ " real_days_overdue, last_payment_date,last_payment_date_collection,last_payment_date_default_int ";
		
		
		if (projectedAccountingDate.after(accountingDate))
		{
				//overdue quota on projected date
			query +="union all "
				+"select ap.subaccount, ap.due_date, COALESCE(ap.capital,0), COALESCE(ap.interest,0), "
				+ "COALESCE(ap.insurance,0), COALESCE(ap.insurance_mortgage,0), "
				+ "0 receivable_fee, 0 legal_fee, "
				+ "COALESCE(:projectedAccountingDate - ap.due_date, 0) days_overdue, "
				+ "COALESCE(:projectedAccountingDate - ap.due_date, 0) real_days_overdue, "
				+ "null last_payment_date, "
				+ "null last_payment_date_collection, "
				+ "null last_payment_date_default_int "
				+ "from "+schema+".account_paytable ap "
				+ "where ap.account_id = :accountId "
				+ "and ap.due_date > :accountingDate "
				+ "and ap.due_date <= :projectedAccountingDate ";
			
		}
		if (forPrepayment)
		{
			query +=  "union all "
				+ "select ap.subaccount, ap.due_date, capital, "
				+ "(case when (ap.due_date-ap.provision_days) <= :projectedAccountingDate then "
				+ "round((ap.interest/ap.provision_days)*(:projectedAccountingDate-(ap.due_date-ap.provision_days)),2) "
				+ "else "
				+ "0 "
				+ "end) interest, "
				+ "COALESCE(ap.insurance,0), COALESCE(ap.insurance_mortgage,0), "
				+ "0 receivable_fee, 0 legal_fee, "
				+ "0 days_overdue, "
				+ "0 real_days_overdue, "
				+ "null last_payment_date, "
				+ "null last_payment_date_collection, "
				+ "null last_payment_date_default_int "
				+ "from "+schema+".account_paytable ap "
				+ "where ap.account_id = :accountId "
				+ "and ap.due_date > :projectedAccountingDate ";
				//+ "and (ap.due_date-ap.provision_days) <= :projectedAccountingDate "
		}

		query+= ") z "
				+ "order by subaccount ";
		
		List<Object[]> balances = XPersistence.getManager()
				.createNativeQuery(query)
				.setParameter("accountId", account.getAccountId())
				.setParameter("toDate", UtilApp.DEFAULT_EXPIRY_DATE)
				.setParameter("accountingDate", accountingDate)
				.setParameter("projectedAccountingDate", projectedAccountingDate)
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
			XPersistence.getManager().persist(overdueBalance);
		XPersistence.commit();
		return overdueBalances;
	}
}
