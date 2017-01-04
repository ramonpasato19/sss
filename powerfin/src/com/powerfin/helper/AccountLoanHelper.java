package com.powerfin.helper;

import java.math.*;
import java.util.*;

import org.openxava.jpa.*;
import org.openxava.util.*;

import com.powerfin.exception.*;
import com.powerfin.model.*;
import com.powerfin.model.types.Types.*;
import com.powerfin.util.*;

public class AccountLoanHelper {
	
	public final static String STATUS_LOAN_ACTIVE = "002";
	public final static String STATUS_LOAN_CANCEL = "003";
	public final static String STATUS_PROCESS_FINANCIAL = "002";
	public final static String LOAN_DISBURSEMENT_TRANSACTION_MODULE = "LOANDISBURSEMENT";
	public final static String SALE_PORTFOLIO_TRANSACTION_MODULE = "SALEPORTFOLIO";
	public final static String PURCHASE_PORTFOLIO_TRANSACTION_MODULE = "PURCHASEPORTFOLIO";
	
	@SuppressWarnings("unchecked")
	public static void getAllOverdueBalancesByBroker(Integer brokerPersonId, Date projectedAccountingDate) {
		Date accountingDate = CompanyHelper.getCurrentAccountingDate();
		if (projectedAccountingDate!=null)
			accountingDate =projectedAccountingDate; 
		String query = "SELECT a FROM Account a, AccountPortfolio p, Negotiation n "
				+ "WHERE a.accountId = p.accountId "
				+ "AND a.accountStatus.accountStatusId  = '002' "
				+ "AND p.purchaseNegotiation.negotiationId = n.negotiationId "
				+ "AND n.brokerPerson.personId = :brokerPersonId ";
		
		List<Account> accounts = XPersistence.getManager()
				.createQuery(query)
				.setParameter("brokerPersonId", brokerPersonId)
				.getResultList();
		if (accounts!=null && !accounts.isEmpty())
		{
			for(Account a:accounts)
				getOverdueBalances(a, accountingDate, false);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void getAllOverdueBalancesSalePortfolioByBroker(Integer brokerPersonId) {
		Date accountingDate = CompanyHelper.getCurrentAccountingDate();
		String query = "SELECT a FROM Account a, AccountPortfolio p, Negotiation n "
				+ "WHERE a.accountId = p.accountId "
				+ "AND a.accountStatus.accountStatusId  = '002' "
				+ "AND p.saleNegotiation.negotiationId = n.negotiationId "
				+ "AND n.brokerPerson.personId = :brokerPersonId ";
		
		List<Account> accounts = XPersistence.getManager()
				.createQuery(query)
				.setParameter("accountingDate", accountingDate)
				.setParameter("toDate", UtilApp.DEFAULT_EXPIRY_DATE)
				.setParameter("brokerPersonId", brokerPersonId)
				.getResultList();
		if (accounts!=null && !accounts.isEmpty())
		{
			for(Account a:accounts)
				getOverdueBalancesSalePortfolio(a, accountingDate);
		}
	}
	
	public static List<AccountOverdueBalance> getOverdueBalances(Account account) {
		return getOverdueBalances(account, null, false);
	}
	
	@SuppressWarnings("unchecked")
	public static List<AccountOverdueBalance> getOverdueBalances(Account account, Date projectedAccountingDate, boolean forPrepayment) {
		String schema = CompanyHelper.getSchema().toLowerCase();
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
				+ "and ap.payment_date is null "
				+ ") x group by subaccount, due_date, days_overdue, "
				+ " real_days_overdue, last_payment_date,last_payment_date_collection,last_payment_date_default_int ";
		
		
		if (projectedAccountingDate.after(accountingDate))
		{
				//overdue quota on projected date
			query +="union all "
				+"select ap.subaccount, ap.due_date, ap.capital, ap.interest, "
				+ "ap.insurance, ap.insurance_mortgage, "
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
				+ "ap.insurance, ap.insurance_mortgage, "
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
	
	public static List<AccountOverdueBalance> getOverdueBalancesSalePortfolio(Account account) {
		return getOverdueBalancesSalePortfolio(account, CompanyHelper.getCurrentAccountingDate());
	}
	
	@SuppressWarnings("unchecked")
	public static List<AccountOverdueBalance> getOverdueBalancesSalePortfolio(Account account, Date projectedAccountingDate) {
		String schema = CompanyHelper.getSchema().toLowerCase();
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
				+ "select b.subaccount, ap.due_date, "
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
				+ "and ap.payment_date is null "
				+ ") x group by subaccount, due_date, days_overdue, last_payment_date "
				
				+ "union all "
				+ "select ap.subaccount, ap.due_date, ap.capital, ap.interest, "
				+ "COALESCE(:projectedAccountingDate - ap.due_date, 0) days_overdue, "
				+ "null last_payment_date "
				+ "from "+schema+".account_sold_paytable ap "
				+ "where ap.account_id = :accountId "
				+ "and ap.due_date > :accountingDate "
				+ "and ap.due_date <= :projectedAccountingDate "
				
				+ "union all "
				+ "select ap.subaccount, ap.due_date, 0 capital, "
				+ "(case when ap.due_date<=:projectedAccountingDate then ap.interest "
				+ "else "
				+ "round((ap.interest/ap.provision_days)*(:projectedAccountingDate-(ap.due_date-ap.provision_days)),2) "
				+ "end) interest, "
				+ "0 days_overdue, "
				+ "null last_payment_date "
				+ "from "+schema+".account_sold_paytable ap "
				+ "where ap.account_id = :accountId "
				+ "and ap.due_date > :projectedAccountingDate "
				+ "and (ap.due_date-ap.provision_days) <= :projectedAccountingDate "
				
				+ ") z "
				+ "order by z.subaccount";
		System.out.println(query);
		System.out.println(account.getAccountId());
		System.out.println(projectedAccountingDate);
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
				BigDecimal total = overdueBalance.getCapital()
						.add(overdueBalance.getInterest());
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
	
	public static List<TransactionAccount> getTransactionAccountsForAccountLoanPayment(Transaction transaction, 
			AccountLoan accountLoan, Account debitAccount, 
			BigDecimal transactionValue) throws Exception
	{
		List<AccountOverdueBalance> overdueBalances = AccountLoanHelper.getOverdueBalances(
				accountLoan.getAccount(),
				transaction.getValueDate()==null?transaction.getAccountingDate():transaction.getValueDate(),
				false);
		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		TransactionAccount ta = null;
		BigDecimal valueToApply = BigDecimal.ZERO;
		Account disbursementAccount = accountLoan.getDisbursementAccount(); 
		
		if (overdueBalances==null || overdueBalances.isEmpty())
    		throw new OperativeException("payment_not_processed_with_out_overdue_balances");
	
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
		
		for (AccountOverdueBalance quota: overdueBalances)
		{
			if (transactionValue.compareTo(BigDecimal.ZERO)==0)
				break;
			
			//ReceivableFeePayment
			if (quota.getReceivableFee().compareTo(BigDecimal.ZERO)>0)
			{
				valueToApply = quota.getReceivableFee();
				
				if (transactionValue.compareTo(valueToApply)<0)
					valueToApply = transactionValue;
				
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(accountLoan.getAccount(), quota.getSubaccount(), valueToApply, transaction, CategoryHelper.getCategoryById(CategoryHelper.RECEIVABLE_FEE_RE_CATEGORY), quota.getDueDate());
				ta.setRemark(XavaResources.getString("receivable_fee_payment", accountLoan.getAccountId()));
				transactionAccounts.add(ta);
				
				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(disbursementAccount, valueToApply, transaction);
				ta.setRemark(XavaResources.getString("receivable_fee_payment", accountLoan.getAccountId()));
				transactionAccounts.add(ta);
				
				transactionValue = transactionValue.subtract(valueToApply);
			}
			
			if (transactionValue.compareTo(BigDecimal.ZERO)==0)
				break;
			
			//LegalFeePayment
			if (quota.getLegalFee().compareTo(BigDecimal.ZERO)>0)
			{
				valueToApply = quota.getLegalFee();
				
				if (transactionValue.compareTo(valueToApply)<0)
					valueToApply = transactionValue;
				
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(accountLoan.getAccount(), quota.getSubaccount(), valueToApply, transaction, CategoryHelper.getCategoryById(CategoryHelper.LEGAL_FEE_RE_CATEGORY), quota.getDueDate());
				ta.setRemark(XavaResources.getString("legal_fee_payment", accountLoan.getAccountId()));
				transactionAccounts.add(ta);
				
				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(disbursementAccount, valueToApply, transaction);
				ta.setRemark(XavaResources.getString("legal_fee_payment", accountLoan.getAccountId()));
				transactionAccounts.add(ta);
				
				transactionValue = transactionValue.subtract(valueToApply);
			}
			
			if (transactionValue.compareTo(BigDecimal.ZERO)==0)
				break;
			
			//CollectionFee
			//Si no cubre el valor completo de los cargos de cobranza termina la prelacion
			if (quota.getCollectionFee().compareTo(BigDecimal.ZERO)>0)
			{
				valueToApply = quota.getCollectionFee();
				
				if (transactionValue.compareTo(valueToApply)<0)
					break;
					//valueToApply = transactionValue;
				
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(accountLoan.getAccount(), quota.getSubaccount(), valueToApply, transaction, CategoryHelper.getCategoryById(CategoryHelper.COLLECTION_FEE_IN_CATEGORY), quota.getDueDate());
				ta.setRemark(XavaResources.getString("collection_fee_payment_quota_number", accountLoan.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(disbursementAccount, valueToApply, transaction);
				ta.setRemark(XavaResources.getString("collection_fee_payment_quota_number", accountLoan.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				transactionValue = transactionValue.subtract(valueToApply);
			}
			
			if (transactionValue.compareTo(BigDecimal.ZERO)==0)
				break;
			
			//DefaultInterest
			//Si no cubre el valor completo de la mora termina la prelacion
			if (quota.getDefaultInterest().compareTo(BigDecimal.ZERO)>0)
			{
				valueToApply = quota.getDefaultInterest();
				
				if (transactionValue.compareTo(valueToApply)<0)
					break;
					//valueToApply = transactionValue;
				
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(accountLoan.getAccount(), quota.getSubaccount(), valueToApply, transaction, CategoryHelper.getCategoryById(CategoryHelper.DEFAULT_INTEREST_IN_CATEGORY), quota.getDueDate());
				ta.setRemark(XavaResources.getString("default_interest_payment_quota_number", accountLoan.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(disbursementAccount, valueToApply, transaction);
				ta.setRemark(XavaResources.getString("default_interest_payment_quota_number", accountLoan.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				transactionValue = transactionValue.subtract(valueToApply);
			}
			
			if (transactionValue.compareTo(BigDecimal.ZERO)==0)
				break;
			
			//Insurance
			if (quota.getInsurance().compareTo(BigDecimal.ZERO)>0)
			{
				valueToApply = quota.getInsurance();
				
				if (transactionValue.compareTo(valueToApply)<0)
					valueToApply = transactionValue;
				
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(accountLoan.getAccount(), quota.getSubaccount(), valueToApply, transaction, CategoryHelper.getCategoryById(CategoryHelper.INSURANCE_RECEIVABLE_CATEGORY), quota.getDueDate());
				ta.setRemark(XavaResources.getString("insurance_payment_quota_number", accountLoan.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(disbursementAccount, valueToApply, transaction);
				ta.setRemark(XavaResources.getString("insurance_payment_quota_number", accountLoan.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				transactionValue = transactionValue.subtract(valueToApply);
			}
			
			if (transactionValue.compareTo(BigDecimal.ZERO)==0)
				break;
			
			//InsuranceMortgage
			if (quota.getInsuranceMortgage().compareTo(BigDecimal.ZERO)>0)
			{
				valueToApply = quota.getInsuranceMortgage();
				
				if (transactionValue.compareTo(valueToApply)<0)
					valueToApply = transactionValue;
				
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(accountLoan.getAccount(), quota.getSubaccount(), valueToApply, transaction, CategoryHelper.getCategoryById(CategoryHelper.MORTGAGE_RECEIVABLE_CATEGORY), quota.getDueDate());
				ta.setRemark(XavaResources.getString("insurance_mortgage_payment_quota_number", accountLoan.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(disbursementAccount, valueToApply, transaction);
				ta.setRemark(XavaResources.getString("insurance_mortgage_payment_quota_number", accountLoan.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				transactionValue = transactionValue.subtract(valueToApply);
			}

			if (transactionValue.compareTo(BigDecimal.ZERO)==0)
				break;
			
			//Interest
			if (quota.getInterest().compareTo(BigDecimal.ZERO)>0)
			{
				valueToApply = quota.getInterest();
				
				if (transactionValue.compareTo(valueToApply)<0)
					valueToApply = transactionValue;
				
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(accountLoan.getAccount(), quota.getSubaccount(), valueToApply, transaction, CategoryHelper.getCategoryById(CategoryHelper.INTEREST_PR_CATEGORY), quota.getDueDate());
				ta.setRemark(XavaResources.getString("interest_payment_quota_number", accountLoan.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(disbursementAccount, valueToApply, transaction);
				ta.setRemark(XavaResources.getString("interest_payment_quota_number", accountLoan.getAccountId(), quota.getSubaccount()));
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
				
				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(accountLoan.getAccount(), quota.getSubaccount(), valueToApply, transaction, CategoryHelper.getCategoryById(CategoryHelper.CAPITAL_CATEGORY), quota.getDueDate());
				ta.setRemark(XavaResources.getString("capital_payment_quota_number", accountLoan.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(disbursementAccount, valueToApply, transaction);
				ta.setRemark(XavaResources.getString("capital_payment_quota_number", accountLoan.getAccountId(), quota.getSubaccount()));
				transactionAccounts.add(ta);
				
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

	
}
