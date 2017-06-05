package com.powerfin.model;

import java.io.*;
import java.math.*;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.openxava.annotations.*;

import com.powerfin.helper.*;
import com.powerfin.model.superclass.*;
import com.powerfin.model.types.*;


/**
 * The persistent class for the account_loan database table.
 * 
 */
@Entity
@Table(name="account_term")
@Views({
	@View( 
		members="accountId, companyAccountingDate; accountStatus;"
				+ "person{person;}"
				+ "product{product;}"
				+ "termData{"
				+ "issueDate;"
				+ "amount;"
				+ "interestRate;"
				+ "paytableType;"
				+ "frecuency, quotasNumber;"
				+ "startDatePayment, paymentDay;"
				+ "automaticRenewal, numberRenewals;"
				+ "period;"
				+ "}"
				+ "disbursementAccount{disbursementAccount}"
				+ "previousAccount{previousAccount}"
				),
	@View(name="RequestTXAccountTerm",
		members="accountId, companyAccountingDate; accountStatus;"
				+ "person{person;}"
				+ "product{product;}"
				+ "termData{"
				+ "issueDate;disbursementDate;"
				+ "amount;"
				+ "interestRate;"
				+ "paytableType;"
				+ "frecuency, quotasNumber;"
				+ "startDatePayment, paymentDay;"
				+ "automaticRenewal, numberRenewals;"
				+ "period;"
				+ "}"
				+ "disbursementAccount{disbursementAccount}"
				+ "previousAccount{previousAccount}"
				+ "quotas{accountPaytables}"
				),
	@View(name="AuthorizeTXAccountTerm",
		members="data{issueDate;disbursementDate;"
				+ "amount;"
				+ "interestRate;"
				+ "paytableType;"
				+ "frecuency, quotasNumber;"
				+ "startDatePayment, paymentDay;"
				+ "automaticRenewal, numberRenewals;"
				+ "period;"
				+ "disbursementAccount}"
				+ "previousAccount{previousAccount}"
				+ "quotas{accountPaytables}"
				),
	@View(name="ConsultAccountTerm",
		members="#accountId, accountStatus;"
				+ "person{person;}"
				+ "product{product;}"
				+ "termData{#"
				+ "issueDate;"
				+ "amount;"
				+ "interestRate;"
				+ "paytableType;"
				+ "frecuency, quotasNumber;"
				+ "startDatePayment, paymentDay;"
				+ "automaticRenewal, numberRenewals;"
				+ "period;"
				+ "}"
				+ "disbursementAccount{disbursementAccount}"
				+ "previousAccount{previousAccount}"
				+ "paytable{accountPaytables}"
				),
})
@Tabs({
	@Tab(properties=""),
	@Tab(name="TXAccountTerm", properties="account.accountId, account.person.name, account.code, account.accountStatus.name, account.product.name"),
	@Tab(name="RequestTXAccountTerm", properties="account.accountId, account.person.name, account.code, account.accountStatus.name, account.product.name", baseCondition = "${account.accountStatus.accountStatusId} = '001' "),
	@Tab(name="ConsultAccountTerm", properties="account.accountId, account.person.name, account.accountStatus.name, account.product.name")
})
public class AccountTerm extends AuditEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="account_id", unique=true, nullable=false)
	@ReadOnly
	@Hidden
	private String accountId;

	@Column(precision=11, scale=2)
	@Required
	@ReadOnly(forViews="ConsultAccountTerm")
	private BigDecimal amount;

	@Column(name="contract_number", length=30)
	@ReadOnly(forViews="ConsultAccountTerm")
	private String contractNumber;

	@Column(name="daily_rate", precision=11, scale=7)
	@ReadOnly(forViews="ConsultAccountTerm")
	private BigDecimal dailyRate;

	@Column(name="automatic_renewal")
	@ReadOnly(forViews="ConsultAccountTerm")
	@Required
	private Types.YesNoIntegerType automaticRenewal;

	@Column(name="number_renewals")
	@ReadOnly(forViews="ConsultAccountTerm")
	private Integer numberRenewals;

	@Temporal(TemporalType.DATE)
	@Column(name="disbursement_date")
	@Required
	@ReadOnly(forViews="ConsultAccountTerm")
	private Date disbursementDate;

	@Column(name="interest_rate", precision=5, scale=2)
	@Required
	@ReadOnly(forViews="ConsultAccountTerm")
	@DecimalMin(value="0.00")
	private BigDecimal interestRate;

	@Temporal(TemporalType.DATE)
	@Column(name="issue_date")
	@Required
	@ReadOnly(forViews="ConsultAccountTerm")
	private Date issueDate;

	@Column(name="payment_day")
	@Required
	@ReadOnly(forViews="ConsultAccountTerm")
	@Min(1)
	@Max(31)
	private Integer paymentDay;

	@ReadOnly
	private Integer period;

	@Column(name="quotas_number")
	@Required
	@ReadOnly(forViews="ConsultAccountTerm")
	@Min(1)
	private Integer quotasNumber;

	@Temporal(TemporalType.DATE)
	@Column(name="start_date_payment")
	@Required
	@ReadOnly(forViews="ConsultAccountTerm")
	private Date startDatePayment;

	@OneToOne
	@JoinColumn(name="account_id", nullable=false, insertable=false, updatable=false)
	private Account account;
	
	@ManyToOne
	@JoinColumn(name="previous_account_id")
	@NoCreate
	@NoModify
	@ReferenceView("simple")
	@SearchAction(forViews="RequestTXAccountTerm", value="SearchGeneralAccount.SearchAccountTerm")
	@ReadOnly(forViews="ConsultAccountTerm")
	private Account previousAccount;

	@ManyToOne
	@JoinColumn(name="disbursement_account_id")
	@NoCreate
	@NoModify
	@Required
	@ReferenceView("simple")
	@SearchAction(forViews="RequestTXAccountTerm", value="SearchGeneralAccount.SearchPayableAccount")
	@ReadOnly(forViews="ConsultAccountTerm")
	private Account disbursementAccount;

	@ManyToOne
	@JoinColumn(name="frecuency_id")
	@NoCreate
	@NoModify
	@Required
	@DescriptionsList(descriptionProperties="name")
	@ReadOnly(forViews="ConsultAccountTerm")
	private Frecuency frecuency;
	
	@ManyToOne
	@JoinColumn(name="paytable_type_id")
	@NoCreate
	@NoModify
	@Required
	@DescriptionsList(descriptionProperties="name")
	@ReadOnly(forViews="ConsultAccountTerm")
	private PaytableType paytableType;
	
	//////////////////////////////////////////////////////////
	@Transient
	@ManyToOne
	@Required
	@NoCreate
	@NoModify
	@ReferenceView("TermReference")
	@ReadOnly(forViews="ConsultAccountTerm")
	private Person person;
	
	@Transient
	@ManyToOne
	@DescriptionsList
	@NoCreate
	@NoModify
	@ReadOnly(forViews="ConsultAccountTerm, RequestTXAccountTerm")
	private AccountStatus accountStatus;
	
	@Transient
	@ManyToOne
	@Required
	@NoCreate
	@NoModify
	@ReadOnly(forViews="ConsultAccountTerm")
	@ReferenceView("Reference")
	@SearchActions({
	@SearchAction(value="SearchProduct.SearchTermProducts")
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
	@ListProperties("subaccount, dueDate, provisionDays, capitalReduced, capital, interest, totalDividend, paymentDate")
	@ReadOnly(forViews="ConsultAccountTerm")
	@ListActions({
		@ListAction("Print.generatePdf"),
		@ListAction("Print.generateExcel")
	})
	private List<AccountPaytable> accountPaytables;
	
	public AccountTerm() {
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

	public Date getDisbursementDate() {
		return this.disbursementDate;
	}

	public void setDisbursementDate(Date disbursementDate) {
		this.disbursementDate = disbursementDate;
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

	public List<AccountPaytable> getAccountPaytables() {
		return accountPaytables;
	}

	public void setAccountPaytables(List<AccountPaytable> accountPaytables) {
		this.accountPaytables = accountPaytables;
	}

	public Types.YesNoIntegerType getAutomaticRenewal() {
		return automaticRenewal;
	}

	public void setAutomaticRenewal(Types.YesNoIntegerType automaticRenewal) {
		this.automaticRenewal = automaticRenewal;
	}

	public Integer getNumberRenewals() {
		return numberRenewals;
	}

	public void setNumberRenewals(Integer numberRenewals) {
		this.numberRenewals = numberRenewals;
	}

	public Account getPreviousAccount() {
		return previousAccount;
	}

	public void setPreviousAccount(Account previousAccount) {
		this.previousAccount = previousAccount;
	}

	public PaytableType getPaytableType() {
		return paytableType;
	}

	public void setPaytableType(PaytableType paytableType) {
		this.paytableType = paytableType;
	}

	@PrePersist
	public void onCreate()
	{
		updateData();		
	}
	
	@PreUpdate
	public void onUpdate()
	{
		updateData();
	}
	
	private void updateData()
	{
		
		if (getPaymentDay()>31)
			setPaymentDay(31);
		
		if (getFrecuency().getFrecuencyId() == AccountTermHelper.EXPIRATION_FRECUENCY_ID)
		{
			Calendar cal = Calendar.getInstance();
			cal.setTime(getStartDatePayment());
			setPaymentDay(cal.get(Calendar.DAY_OF_MONTH));
			setQuotasNumber(1);
		}
	}
}