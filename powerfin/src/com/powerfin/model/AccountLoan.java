package com.powerfin.model;

import java.io.*;
import java.math.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;

import com.powerfin.model.superclass.*;


/**
 * The persistent class for the account_loan database table.
 * 
 */
@Entity
@Table(name="account_loan")
@Views({
	@View( 
			members="accountId, companyAccountingDate; accountStatus;"
					+ "person{person;}"
					+ "product{product;}"
					+ "loanData{"
					+ "issueDate;"
					+ "amount;"
					+ "interestRate;"
					+ "frecuency, quotasNumber;"
					+ "startDatePayment, paymentDay;"
					+ "period;"
					+ "}"
					+ "disbursementAccount{disbursementAccount}"
					+ "thirdPerson{mortgageInsurer;vehicleInsurer}"
					),
	@View(name="RequestTXAccountLoan",
			members="accountId, companyAccountingDate; accountStatus;"
					+ "person{person;}"
					+ "product{product;}"
					+ "loanData{"
					+ "issueDate;disbursementDate;"
					+ "amount;"
					+ "interestRate;"
					+ "frecuency, quotasNumber;"
					+ "startDatePayment, paymentDay;"
					+ "period;"
					+ "}"
					+ "disbursementAccount{disbursementAccount}"
					+ "thirdPerson{mortgageInsurer;vehicleInsurer}"
					+ "quotas{accountPaytables}"
					),
	@View(name="AuthorizeTXAccountLoan",
			members="data{issueDate;disbursementDate;"
					+ "amount;"
					+ "interestRate;"
					+ "frecuency, quotasNumber;"
					+ "startDatePayment, paymentDay;"
					+ "period;"
					+ "disbursementAccount}"
					+ "thirdPerson{mortgageInsurer;vehicleInsurer}"
					+ "quotas{accountPaytables}"
					),
	@View(name="ConsultPurchasePortfolio",
			members="#accountId, accountStatus;"
					+ "person{person;}"
					+ "product{product;}"
					+ "loanData{#"
					+ "issueDate;"
					+ "amount;"
					+ "interestRate;"
					+ "frecuency, quotasNumber;"
					+ "startDatePayment, paymentDay;"
					+ "period;"
					+ "insuranceMortgageAmount, insuranceAmount"
					+ "}"
					+ "disbursementAccount{disbursementAccount}"
					+ "thirdPerson{#"
						+ "mortgageInsurerName;"
						+ "insurerName;"
						+ "purchaseBrokerName, purchasePortfolioSequence, purchasePortfolioDate;"
						+ "saleBrokerName, salePortfolioSequence, salePortfolioDate;"
					+ "}"
					+ "paytable{accountPaytables}"
					+ "overdueBalances{projectedAccountingDate;accountOverdueBalances}"
					),
	
})
@Tabs({
	@Tab(properties=""),
	@Tab(name="TXAccountLoan", properties="account.accountId, account.person.name, account.code, account.accountStatus.name, account.product.name"),
	@Tab(name="ConsultPurchasePortfolio", properties="account.accountId, account.person.name, account.accountStatus.name, account.product.name")
})
public class AccountLoan extends AuditEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="account_id", unique=true, nullable=false)
	@ReadOnly
	@Hidden
	private String accountId;

	@Column(precision=11, scale=2)
	@Required
	@ReadOnly(forViews="ConsultPurchasePortfolio")
	private BigDecimal amount;

	@Column(name="contract_number", length=30)
	@ReadOnly(forViews="ConsultPurchasePortfolio")
	private String contractNumber;

	@Column(name="daily_rate", precision=11, scale=7)
	@ReadOnly(forViews="ConsultPurchasePortfolio")
	private BigDecimal dailyRate;

	@Column(name="day_grace")
	@ReadOnly(forViews="ConsultPurchasePortfolio")
	private Integer dayGrace;

	@Column(name="day_grace_collection_fee")
	@ReadOnly(forViews="ConsultPurchasePortfolio")
	private Integer dayGraceCollectionFee;
	
	@Column(name="default_interest_rate", precision=5, scale=2)
	@ReadOnly(forViews="ConsultPurchasePortfolio")
	private BigDecimal defaultInterestRate;

	@Temporal(TemporalType.DATE)
	@Column(name="disbursement_date")
	@ReadOnly(forViews="ConsultPurchasePortfolio")
	private Date disbursementDate;

	@Column(name="fixed_quota", precision=11, scale=2)
	@ReadOnly(forViews="ConsultPurchasePortfolio")
	private BigDecimal fixedQuota;

	@Column(name="interest_rate", precision=5, scale=2)
	@Required
	@ReadOnly(forViews="ConsultPurchasePortfolio")
	private BigDecimal interestRate;

	@Temporal(TemporalType.DATE)
	@Column(name="issue_date")
	@Required
	@ReadOnly(forViews="ConsultPurchasePortfolio")
	private Date issueDate;

	@Column(name="original_account", length=50)
	@ReadOnly(forViews="ConsultPurchasePortfolio")
	private String originalAccount;

	@Column(name="original_amount", precision=11, scale=2)
	@ReadOnly(forViews="ConsultPurchasePortfolio")
	private BigDecimal originalAmount;

	@Column(name="payment_day")
	@Required
	@ReadOnly(forViews="ConsultPurchasePortfolio")
	private Integer paymentDay;

	@ReadOnly
	private Integer period;

	@Column(name="quotas_number")
	@Required
	@ReadOnly(forViews="ConsultPurchasePortfolio")
	private Integer quotasNumber;

	@Column(name="insurance_quotas_number")
	//@Required
	@ReadOnly(forViews="ConsultPurchasePortfolio")
	private Integer insuranceQuotasNumber;
	
	@Temporal(TemporalType.DATE)
	@Column(name="start_date_payment")
	@Required
	@ReadOnly(forViews="ConsultPurchasePortfolio")
	private Date startDatePayment;

	//bi-directional one-to-one association to Account
	@OneToOne
	@JoinColumn(name="account_id", nullable=false, insertable=false, updatable=false)
	private Account account;

	@Column(name="insurance_amount", precision=11, scale=2)
	@ReadOnly(forViews="ConsultPurchasePortfolio")
	private BigDecimal insuranceAmount;

	@Column(name="insurance_mortgage_amount", precision=11, scale=2)
	@ReadOnly(forViews="ConsultPurchasePortfolio")
	private BigDecimal insuranceMortgageAmount;
	
	//bi-directional many-to-one association to Account
	@ManyToOne
	@JoinColumn(name="disbursement_account_id")
	@NoCreate
	@NoModify
	@Required
	@ReferenceView("simple")
	@SearchAction(forViews="RequestTXAccountLoan", value="SearchGeneralAccount.SearchPayableAccount")
	@ReadOnly(forViews="ConsultPurchasePortfolio")
	private Account disbursementAccount;

	//bi-directional many-to-one association to Account
	@ManyToOne
	@JoinColumn(name="insurance_account_id")
	@NoCreate
	@NoModify
	//@Required
	@ReferenceView("simple")
	@ReadOnly(forViews="ConsultPurchasePortfolio")
	private Account insuranceAccount;

	//bi-directional many-to-one association to Account
	@ManyToOne
	@JoinColumn(name="mortgage_account_id")
	@NoCreate
	@NoModify
	//@Required
	@ReferenceView("simple")
	@ReadOnly(forViews="ConsultPurchasePortfolio")
	private Account mortgageAccount;
	
	//bi-directional many-to-one association to Frecuency
	@ManyToOne
	@JoinColumn(name="frecuency_id")
	@NoCreate
	@NoModify
	@Required
	@DescriptionsList(descriptionProperties="name")
	@ReadOnly(forViews="ConsultPurchasePortfolio")
	private Frecuency frecuency;

	//bi-directional many-to-one association to Seller
	@ManyToOne
	@JoinColumn(name="mortgage_insurer_id", nullable=true)
	@NoCreate
	@NoModify
	//@Required
	@NoFrame
	@ReferenceView("Reference")
	@ReadOnly(forViews="ConsultPurchasePortfolio")
	private Insurer mortgageInsurer;
	
	//bi-directional many-to-one association to Seller
	@ManyToOne
	@JoinColumn(name="vehicle_insurer_id", nullable=true)
	@NoCreate
	@NoModify
	//@Required
	@NoFrame
	@ReferenceView("Reference")
	@ReadOnly(forViews="ConsultPurchasePortfolio")
	private Insurer vehicleInsurer;
	
	//////////////////////////////////////////////////////////
	@Transient
	@ManyToOne
	@Required
	@NoCreate
	@NoModify
	@ReferenceView("LoanReference")
	@ReadOnly(forViews="ConsultPurchasePortfolio")
	private Person person;
	
	@Transient
	@ManyToOne
	@DescriptionsList
	@NoCreate
	@NoModify
	@ReadOnly(forViews="ConsultPurchasePortfolio")
	private AccountStatus accountStatus;
	
	@Transient
	@ManyToOne
	@Required
	@NoCreate
	@NoModify
	@ReadOnly(forViews="ConsultPurchasePortfolio")
	@ReferenceView("Reference")
	@SearchActions({
	@SearchAction(value="SearchProduct.SearchLoanProducts")
	})
	private Product product;
	
	@Transient
	@Temporal(TemporalType.DATE)
	@DefaultValueCalculator(com.powerfin.calculators.CurrentAccountingDateCalculator.class)
	@ReadOnly
	private Date companyAccountingDate;

	//bi-directional many-to-one association to AccountPaytable
	@Transient
	@AsEmbedded
	@OneToMany(mappedBy = "account")
	@OrderBy("subaccount")
	@ListProperties("subaccount, dueDate, provisionDays, capitalReduced, capital, interest, totalDividend, insurance, insuranceMortgage, totalQuota, paymentDate")
	@ReadOnly(forViews="ConsultPurchasePortfolio")
	private List<AccountPaytable> accountPaytables;
	
	@Transient
	@AsEmbedded
	@OneToMany(mappedBy="account")
	@OrderBy("subaccount")
	@ListProperties("subaccount, dueDate, overdueDays, capital, interest, insuranceMortgage, insurance, defaultInterest, collectionFee, legalFee, receivableFee, total")
	@ReadOnly(forViews="ConsultPurchasePortfolio")
	private List<AccountOverdueBalance> accountOverdueBalances;
	
	@Transient
	@Temporal(TemporalType.DATE)
	@Action(forViews="ConsultPurchasePortfolio", value = "AccountLoan.GetOverdueBalanceForConsult", alwaysEnabled=true )
	private Date projectedAccountingDate;
	
	public AccountLoan() {
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getContractNumber() {
		return this.contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	public BigDecimal getDailyRate() {
		return this.dailyRate;
	}

	public void setDailyRate(BigDecimal dailyRate) {
		this.dailyRate = dailyRate;
	}

	public Integer getDayGrace() {
		return this.dayGrace;
	}

	public void setDayGrace(Integer dayGrace) {
		this.dayGrace = dayGrace;
	}

	public Integer getDayGraceCollectionFee() {
		return dayGraceCollectionFee;
	}

	public void setDayGraceCollectionFee(Integer dayGraceCollectionFee) {
		this.dayGraceCollectionFee = dayGraceCollectionFee;
	}

	public BigDecimal getDefaultInterestRate() {
		return this.defaultInterestRate;
	}

	public void setDefaultInterestRate(BigDecimal defaultInterestRate) {
		this.defaultInterestRate = defaultInterestRate;
	}

	public Date getDisbursementDate() {
		return this.disbursementDate;
	}

	public void setDisbursementDate(Date disbursementDate) {
		this.disbursementDate = disbursementDate;
	}

	public BigDecimal getFixedQuota() {
		return this.fixedQuota;
	}

	public void setFixedQuota(BigDecimal fixedQuota) {
		this.fixedQuota = fixedQuota;
	}

	public BigDecimal getInterestRate() {
		return this.interestRate;
	}

	public void setInterestRate(BigDecimal interestRate) {
		this.interestRate = interestRate;
	}

	public Date getIssueDate() {
		return this.issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	public String getOriginalAccount() {
		return this.originalAccount;
	}

	public void setOriginalAccount(String originalAccount) {
		this.originalAccount = originalAccount;
	}

	public BigDecimal getOriginalAmount() {
		return this.originalAmount;
	}

	public void setOriginalAmount(BigDecimal originalAmount) {
		this.originalAmount = originalAmount;
	}

	public Integer getPaymentDay() {
		return this.paymentDay;
	}

	public void setPaymentDay(Integer paymentDay) {
		this.paymentDay = paymentDay;
	}

	public Integer getPeriod() {
		return this.period;
	}

	public void setPeriod(Integer period) {
		this.period = period;
	}

	public Integer getQuotasNumber() {
		return this.quotasNumber;
	}

	public void setQuotasNumber(Integer quotasNumber) {
		this.quotasNumber = quotasNumber;
	}

	public Date getStartDatePayment() {
		return this.startDatePayment;
	}

	public void setStartDatePayment(Date startDatePayment) {
		this.startDatePayment = startDatePayment;
	}

	public Account getAccount() {
		return this.account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Frecuency getFrecuency() {
		return this.frecuency;
	}

	public void setFrecuency(Frecuency frecuency) {
		this.frecuency = frecuency;
	}

	public Person getPerson() {
		if (account!=null)
			return account.getPerson();
		else 
			return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public AccountStatus getAccountStatus() {
		if (account!=null)
			return account.getAccountStatus();
		else 
			return accountStatus;
	}

	public void setAccountStatus(AccountStatus accountStatus) {
		this.accountStatus = accountStatus;
	}

	public Product getProduct() {
		if (account!=null)
			return account.getProduct();
		else 
			return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Date getCompanyAccountingDate() {
		return companyAccountingDate;
	}

	public void setCompanyAccountingDate(Date companyAccountingDate) {
		this.companyAccountingDate = companyAccountingDate;
	}

	public Account getDisbursementAccount() {
		return disbursementAccount;
	}

	public void setDisbursementAccount(Account disbursementAccount) {
		this.disbursementAccount = disbursementAccount;
	}

	public Account getInsuranceAccount() {
		return insuranceAccount;
	}

	public void setInsuranceAccount(Account insuranceAccount) {
		this.insuranceAccount = insuranceAccount;
	}

	public Account getMortgageAccount() {
		return mortgageAccount;
	}

	public void setMortgageAccount(Account mortgageAccount) {
		this.mortgageAccount = mortgageAccount;
	}
	
	public BigDecimal getInsuranceAmount() {
		return insuranceAmount;
	}

	public void setInsuranceAmount(BigDecimal insuranceAmount) {
		this.insuranceAmount = insuranceAmount;
	}

	public BigDecimal getInsuranceMortgageAmount() {
		return insuranceMortgageAmount;
	}

	public void setInsuranceMortgageAmount(BigDecimal insuranceMortgageAmount) {
		this.insuranceMortgageAmount = insuranceMortgageAmount;
	}

	public Integer getInsuranceQuotasNumber() {
		return insuranceQuotasNumber;
	}

	public void setInsuranceQuotasNumber(Integer insuranceQuotasNumber) {
		this.insuranceQuotasNumber = insuranceQuotasNumber;
	}

	public Insurer getMortgageInsurer() {
		return mortgageInsurer;
	}

	public void setMortgageInsurer(Insurer mortgageInsurer) {
		this.mortgageInsurer = mortgageInsurer;
	}

	public Insurer getVehicleInsurer() {
		return vehicleInsurer;
	}

	public void setVehicleInsurer(Insurer vehicleInsurer) {
		this.vehicleInsurer = vehicleInsurer;
	}

	public List<AccountPaytable> getAccountPaytables() {
		//if(account!=null)
			//return account.getAccountPaytables();
		return accountPaytables;
	}

	public List<AccountOverdueBalance> getAccountOverdueBalances() {
		return accountOverdueBalances;
	}

	public void setAccountOverdueBalances(List<AccountOverdueBalance> accountOverdueBalances) {
		this.accountOverdueBalances = accountOverdueBalances;
	}

	public void setAccountPaytables(List<AccountPaytable> accountPaytables) {
		this.accountPaytables = accountPaytables;
	}

	public Date getProjectedAccountingDate() {
		return projectedAccountingDate;
	}

	public void setProjectedAccountingDate(Date projectedAccountingDate) {
		this.projectedAccountingDate = projectedAccountingDate;
	}

	public String getMortgageInsurerName()
	{
		if (this.insuranceAccount!=null)
		{
			Person p = XPersistence.getManager().find(Person.class, insuranceAccount.getPerson().getPersonId());
			return p.getName();
		}
		return null;
	}
	
	public String getInsurerName()
	{
		if (this.mortgageAccount!=null)
		{
			Person p = XPersistence.getManager().find(Person.class, mortgageAccount.getPerson().getPersonId());
			return p.getName();
		}
		return null;
	}
	
	public String getPurchaseBrokerName()
	{
		String brokerName = null;
		AccountPortfolio ap = XPersistence.getManager().find(AccountPortfolio.class, this.accountId);
		if (ap!=null && ap.getPurchaseNegotiation()!=null)
		{
			Negotiation n = XPersistence.getManager().find(Negotiation.class, ap.getPurchaseNegotiation().getNegotiationId());
			if (n!=null)
				brokerName = n.getBrokerPerson().getName();
		}
		return brokerName;
	}
	public String getSaleBrokerName()
	{
		String brokerName = null;
		AccountPortfolio ap = XPersistence.getManager().find(AccountPortfolio.class, this.accountId);
		if (ap!=null && ap.getSaleNegotiation()!=null)
		{
			Negotiation n = XPersistence.getManager().find(Negotiation.class, ap.getSaleNegotiation().getNegotiationId());
			if (n!=null)
				brokerName = n.getBrokerPerson().getName();
		}
		return brokerName;
	}
	public Date getSalePortfolioDate()
	{
		Date date = null;
		AccountPortfolio ap = XPersistence.getManager().find(AccountPortfolio.class, this.accountId);
		if (ap!=null && ap.getSaleNegotiation()!=null)
		{
			Negotiation n = XPersistence.getManager().find(Negotiation.class, ap.getSaleNegotiation().getNegotiationId());
			if (n!=null && n.getAccountingDate()!=null)
				date = n.getAccountingDate();
		}
		return date;
	}
	public Date getPurchasePortfolioDate()
	{
		Date date = null;
		AccountPortfolio ap = XPersistence.getManager().find(AccountPortfolio.class, this.accountId);
		if (ap!=null && ap.getPurchaseNegotiation()!=null)
		{
			Negotiation n = XPersistence.getManager().find(Negotiation.class, ap.getPurchaseNegotiation().getNegotiationId());
			if (n!=null && n.getAccountingDate()!=null)
				date = n.getAccountingDate();
		}
		return date;
	}
	public Integer getPurchasePortfolioSequence()
	{
		Integer sequence = null;
		AccountPortfolio ap = XPersistence.getManager().find(AccountPortfolio.class, this.accountId);
		if (ap!=null && ap.getPurchaseNegotiation()!=null)
		{
			Negotiation n = XPersistence.getManager().find(Negotiation.class, ap.getPurchaseNegotiation().getNegotiationId());
			if (n!=null && n.getBrokerSequence()!=null)
				sequence = n.getBrokerSequence();
		}
		return sequence;
	}
	public Integer getSalePortfolioSequence()
	{
		Integer sequence = null;
		AccountPortfolio ap = XPersistence.getManager().find(AccountPortfolio.class, this.accountId);
		if (ap!=null && ap.getSaleNegotiation()!=null)
		{
			Negotiation n = XPersistence.getManager().find(Negotiation.class, ap.getSaleNegotiation().getNegotiationId());
			if (n!=null && n.getBrokerSequence()!=null)
				sequence = n.getBrokerSequence();
		}
		return sequence;
	}
	@PrePersist
	public void onCreate()
	{
		if (originalAmount==null)
			originalAmount=amount;
	}
}