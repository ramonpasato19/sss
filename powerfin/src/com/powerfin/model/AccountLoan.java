package com.powerfin.model;

import java.io.*;
import java.math.*;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;

import com.powerfin.helper.*;
import com.powerfin.model.superclass.*;
import com.powerfin.model.types.*;


/**
 * The persistent class for the account_loan database table.
 * 
 */
@Entity
@Table(name="account_loan")
@Views({
	@View( 
		members="accountId, companyAccountingDate; accountStatus, operatingConditionName, legalConditionName;"
				+ "person{person;}"
				+ "product{product;}"
				+ "loanData{"
				+ "issueDate;"
				+ "amount;"
				+ "interestRate;"
				+ "frecuency, quotasNumber;"
				+ "startDatePayment, paymentDay;"
				+ "period;"
				+ "daysGrace, daysGraceCollectionFee;"
				+ "applyAutomaticDebit;"
				+ "applyDefaultInterestAccrued;"
				+ "cancellationDate"
				+ "}"
				+ "disbursementAccount{disbursementAccount}"
				+ "vehicleInsurer{vehicleInsurer, insuranceAccount;}"
				+ "mortgageInsurer{mortgageInsurer, mortgageAccount}"
				),
	@View(name="RequestTXAccountLoan",
		members="accountId, companyAccountingDate; accountStatus;"
				+ "person{person;}"
				+ "codebtor{codebtor;}"
				+ "product{product;}"
				+ "loanData{"
				+ "issueDate;disbursementDate;"
				+ "amount;"
				+ "interestRate;"
				+ "frecuency, quotasNumber;"
				+ "startDatePayment, paymentDay;"
				+ "period,fixedQuota;"
				+ "}"
				+ "disbursementAccount{disbursementAccount}"
				+ "vehicleInsurer{vehicleInsurer, insuranceAccount;}"
				+ "mortgageInsurer{mortgageInsurer, mortgageAccount}"
				+ "quotas{accountPaytables}"
				),
	@View(name="PrintDocumentsAccountLoan",
		members="accountId, companyAccountingDate; accountStatus;"
				+ "person{person;}"
				+ "codebtor{codebtor;}"
				+ "product{product;}"
				+ "loanData{"
				+ "issueDate;disbursementDate;"
				+ "amount;"
				+ "interestRate;"
				+ "frecuency, quotasNumber;"
				+ "startDatePayment, paymentDay;"
				+ "period,fixedQuota;"
				+ "}"
				+ "disbursementAccount{disbursementAccount}"
				+ "vehicleInsurer{vehicleInsurer, insuranceAccount;}"
				+ "mortgageInsurer{mortgageInsurer, mortgageAccount}"
				+ "quotas{accountPaytables}"
				),
	@View(name="AuthorizeTXAccountLoan",
		members="data{issueDate;disbursementDate;"
				+ "amount;"
				+ "interestRate;"
				+ "frecuency, quotasNumber;"
				+ "startDatePayment, paymentDay;"
				+ "period,fixedQuota;"
				+ "disbursementAccount}"
				+ "vehicleInsurer{vehicleInsurer, insuranceAccount;}"
				+ "mortgageInsurer{mortgageInsurer, mortgageAccount}"
				+ "quotas{accountPaytables}"
				),
	@View(name="ConsultAccountLoan",
		members="#accountId, accountStatus, operatingConditionName, legalConditionName;"
				+ "person{person;}"
				+ "codebtor{codebtor;}"
				+ "product{product;}"
				+ "loanData{#"
				+ "issueDate;"
				+ "amount;"
				+ "interestRate;"
				+ "frecuency, quotasNumber;"
				+ "startDatePayment, paymentDay;"
				+ "period;"
				+ "daysGrace, daysGraceCollectionFee; "
				+ "insuranceMortgageAmount, insuranceAmount;"
				+ "applyAutomaticDebit, applyDefaultInterestAccrued;"
				+ "cancellationDate"
				+ "}"
				+ "disbursementAccount{disbursementAccount}"
				+ "vehicleInsurer{vehicleInsurer, insuranceAccount;}"
				+ "mortgageInsurer{mortgageInsurer, mortgageAccount}"
				+ "portfolio{"
				+ "accountPortfolioStatusName;"
				+ "purchase[#"
				+ "purchaseBrokerName, purchasePortfolioSequence, purchasePortfolioDate;"
				+ "purchaseAmount, purchaseSpread, purchaseRate;"
				+ "purchaseStatusName"
				+ "];"
				+ "sale[#"
				+ "saleBrokerName, salePortfolioSequence, salePortfolioDate;"
				+ "saleAmount, saleSpread, saleRate;"
				+ "saleStatusName"
				+ "]"
				+ "}"
				+ "paytable{accountPaytables}"
				+ "overdueBalances{projectedAccountingDate;accountOverdueBalances}"
				+ "remark{remark}"
				),
	@View(name="ConsultPurchasePortfolio",
		members="#accountId, accountStatus, operatingConditionName, legalConditionName;"
				+ "person{person;}"
				+ "product{product;}"
				+ "loanData{#"
				+ "issueDate;"
				+ "amount;"
				+ "interestRate;"
				+ "frecuency, quotasNumber;"
				+ "startDatePayment, paymentDay;"
				+ "period;"
				+ "insuranceMortgageAmount, insuranceAmount;"
				+ "applyAutomaticDebit, applyDefaultInterestAccrued;"
				+ "daysGrace, daysGraceCollectionFee; "
				+ "cancellationDate"
				+ "}"
				+ "disbursementAccount{disbursementAccount}"
				+ "vehicleInsurer{vehicleInsurer, insuranceAccount;}"
				+ "mortgageInsurer{mortgageInsurer, mortgageAccount}"
				+ "portfolio{"
				+ "accountPortfolioStatusName;"
				+ "purchase[#"
				+ "purchaseBrokerName, purchasePortfolioSequence, purchasePortfolioDate;"
				+ "purchaseAmount, purchaseSpread, purchaseRate;"
				+ "purchaseStatusName"
				+ "];"
				+ "sale[#"
				+ "saleBrokerName, salePortfolioSequence, salePortfolioDate;"
				+ "saleAmount, saleSpread, saleRate;"
				+ "saleStatusName"
				+ "]"
				+ "}"
				+ "paytable{accountPaytables}"
				+ "overdueBalances{projectedAccountingDate;accountOverdueBalances}"
				+ "remark{remark}"
				),
	@View(name="ConsultOriginationPortfolio",
		members="#accountId, accountStatus, operatingConditionName, legalConditionName;"
				+ "person{person;}"
				+ "product{product;}"
				+ "loanData{#"
				+ "issueDate;"
				+ "amount;"
				+ "interestRate;"
				+ "frecuency, quotasNumber;"
				+ "startDatePayment, paymentDay;"
				+ "period;"
				+ "daysGrace, daysGraceCollectionFee; "
				+ "insuranceMortgageAmount, insuranceAmount;"
				+ "applyAutomaticDebit, applyDefaultInterestAccrued;"
				+ "cancellationDate"
				+ "}"
				+ "disbursementAccount{disbursementAccount}"
				+ "vehicleInsurer{vehicleInsurer, insuranceAccount;}"
				+ "mortgageInsurer{mortgageInsurer, mortgageAccount}"
				+ "portfolio{"
				+ "accountPortfolioStatusName;"
				+ "purchase[#"
				+ "purchaseBrokerName, purchasePortfolioSequence, purchasePortfolioDate;"
				+ "purchaseAmount, purchaseSpread, purchaseRate;"
				+ "purchaseStatusName"
				+ "];"
				+ "sale[#"
				+ "saleBrokerName, salePortfolioSequence, salePortfolioDate;"
				+ "saleAmount, saleSpread, saleRate;"
				+ "saleStatusName"
				+ "]"
				+ "}"
				+ "paytable{accountPaytables}"
				+ "overdueBalances{projectedAccountingDate;accountOverdueBalances}"
				+ "remark{remark}"
				),
	@View(name="ConsultSalePortfolio",
		members="#accountId, accountStatus, operatingConditionName, legalConditionName;"
				+ "person{person;}"
				+ "product{product;}"
				+ "loanData{#"
				+ "issueDate;"
				+ "amount;"
				+ "interestRate;"
				+ "frecuency, quotasNumber;"
				+ "startDatePayment, paymentDay;"
				+ "period;"
				+ "daysGrace, daysGraceCollectionFee; "
				+ "insuranceMortgageAmount, insuranceAmount"
				+ "}"
				+ "disbursementAccount{disbursementAccount}"
				+ "vehicleInsurer{vehicleInsurer, insuranceAccount;}"
				+ "mortgageInsurer{mortgageInsurer, mortgageAccount}"
				+ "portfolio{"
				+ "accountPortfolioStatusName;"
				+ "purchase[#"
				+ "purchaseBrokerName, purchasePortfolioSequence, purchasePortfolioDate;"
				+ "purchaseAmount, purchaseSpread, purchaseRate;"
				+ "purchaseStatusName"
				+ "];"
				+ "sale[#"
				+ "saleBrokerName, salePortfolioSequence, salePortfolioDate;"
				+ "saleAmount, saleSpread, saleRate;"
				+ "saleStatusName"
				+ "]"
				+ "}"
				+ "paytable{accountSoldPaytables}"
				+ "overdueBalances{projectedAccountingDate;accountOverdueBalances}"
				+ "remark{remark}"
				),
	
	@View(name="ReferencePortfolioRecoveryManagement", 
			members="accountId ;"			
					+"generalInformation{#"
					+ "identificationType,"+"identification;"
					+ "name,"+ "maritalStatus;"
					+ "email,"+ "activity;"
					+ "nationality;"
					+ "homePhoneNumber1,"
					+ "homePhoneNumber2;"	
					+ "cellPhoneNumber1"
					+ "};"
					+ "homeAddress{#"					
					+ "homeDistrict[country,region;state,city;district];"
					+ "homeMainStreet, homeNumber; "
					+ "homeSideStreet;homeSector;"			
					+ "};"
					+ "PersonalReference{"
					+ "nameReference1,addressReference1, homePhoneReference1, cellPhoneReference1,workPhoneReference1,relationshipReference1;"
					+ "nameReference2,addressReference2, homePhoneReference2, cellPhoneReference2,workPhoneReference2,relationshipReference2;"
					+ "nameReference3,addressReference3, homePhoneReference3, cellPhoneReference3,workPhoneReference3,relationshipReference3;"
					+ "};")
	
})
@Tabs({
	@Tab(properties="account.accountId, account.person.name, account.accountStatus.name, account.product.name"),
	@Tab(name="TXAccountLoan", properties="account.accountId, account.person.name, account.code, account.accountStatus.name, account.product.name", baseCondition = "${account.accountStatus.accountStatusId} = '001'"),
	@Tab(name="PrintDocumentsAccountLoan", properties="account.accountId, account.person.name, account.code, account.accountStatus.name, account.product.name", baseCondition = "${account.accountStatus.accountStatusId} = '001'"),
	@Tab(name="ConsultPurchasePortfolio", properties="account.accountId, account.person.name, account.accountStatus.name, account.product.name"),
	@Tab(name="ConsultOriginationPortfolio", properties="account.accountId, account.person.name, account.accountStatus.name, account.product.name"),
	@Tab(name="ConsultSalePortfolio", properties="account.accountId, account.person.name, account.accountStatus.name, account.product.name"),
	@Tab(name="ConsultAccountLoan", properties="account.accountId, account.person.name, account.accountStatus.name, account.product.name")
})
public class AccountLoan extends AuditEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="account_id", unique=true, nullable=false, length=20)
	@ReadOnly(notForViews="ReferencePortfolioRecoveryManagement" )
	@Hidden
	private String accountId;

	@Column(precision=11, scale=2)
	@Required
	@ReadOnly(forViews="ConsultPurchasePortfolio, ConsultSalePortfolio, ConsultAccountLoan, ConsultOriginationPortfolio, PrintDocumentsAccountLoan")
	private BigDecimal amount;

	@Column(name="contract_number", length=30)
	@ReadOnly(forViews="ConsultPurchasePortfolio, ConsultSalePortfolio, ConsultAccountLoan, ConsultOriginationPortfolio, PrintDocumentsAccountLoan")
	private String contractNumber;

	@Column(name="daily_rate", precision=11, scale=7)
	@ReadOnly(forViews="ConsultPurchasePortfolio, ConsultSalePortfolio, ConsultAccountLoan, ConsultOriginationPortfolio, PrintDocumentsAccountLoan")
	private BigDecimal dailyRate;

	@Column(name="days_grace")
	@ReadOnly(forViews="ConsultPurchasePortfolio, ConsultSalePortfolio, ConsultAccountLoan, ConsultOriginationPortfolio, PrintDocumentsAccountLoan")
	private Integer daysGrace;

	@Column(name="days_grace_collection_fee")
	@ReadOnly(forViews="ConsultPurchasePortfolio, ConsultSalePortfolio, ConsultAccountLoan, ConsultOriginationPortfolio, PrintDocumentsAccountLoan")
	private Integer daysGraceCollectionFee;

	@Temporal(TemporalType.DATE)
	@Column(name="disbursement_date")
	@ReadOnly(forViews="ConsultPurchasePortfolio, ConsultSalePortfolio, ConsultAccountLoan, ConsultOriginationPortfolio, PrintDocumentsAccountLoan")
	private Date disbursementDate;

	@Column(name="fixed_quota", precision=11, scale=2)
	@ReadOnly(forViews="ConsultPurchasePortfolio, ConsultSalePortfolio, ConsultAccountLoan, ConsultOriginationPortfolio, AuthorizeTXAccountLoan, RequestTXAccountLoan, PrintDocumentsAccountLoan")
	private BigDecimal fixedQuota;

	@Column(name="interest_rate", precision=5, scale=2)
	@Required
	@ReadOnly(forViews="ConsultPurchasePortfolio, ConsultSalePortfolio, ConsultAccountLoan, ConsultOriginationPortfolio, PrintDocumentsAccountLoan")
	@DecimalMin(value="0.00")
	private BigDecimal interestRate;

	@Temporal(TemporalType.DATE)
	@Column(name="issue_date")
	@Required
	@ReadOnly(forViews="ConsultPurchasePortfolio, ConsultSalePortfolio, ConsultAccountLoan, ConsultOriginationPortfolio, PrintDocumentsAccountLoan")
	private Date issueDate;

	@Column(name="original_account", length=50)
	@ReadOnly(forViews="ConsultPurchasePortfolio, ConsultSalePortfolio, ConsultAccountLoan, ConsultOriginationPortfolio, PrintDocumentsAccountLoan")
	private String originalAccount;

	@Column(name="original_amount", precision=11, scale=2)
	@ReadOnly(forViews="ConsultPurchasePortfolio, ConsultSalePortfolio, ConsultAccountLoan, ConsultOriginationPortfolio, PrintDocumentsAccountLoan")
	private BigDecimal originalAmount;

	@Column(name="payment_day")
	@Required
	@ReadOnly(forViews="ConsultPurchasePortfolio, ConsultSalePortfolio, ConsultAccountLoan, ConsultOriginationPortfolio, PrintDocumentsAccountLoan")
	@Min(1)
	@Max(31)
	private Integer paymentDay;

	@ReadOnly
	private Integer period;

	@Column(name="quotas_number")
	@Required
	@ReadOnly(forViews="ConsultPurchasePortfolio, ConsultSalePortfolio, ConsultAccountLoan, ConsultOriginationPortfolio, PrintDocumentsAccountLoan")
	@Min(1)
	private Integer quotasNumber;

	@Column(name="insurance_quotas_number")
	//@Required
	@ReadOnly(forViews="ConsultPurchasePortfolio, ConsultSalePortfolio, ConsultAccountLoan, ConsultOriginationPortfolio, PrintDocumentsAccountLoan")
	private Integer insuranceQuotasNumber;
	
	@Column(name="apply_default_interest_accrued")
	@ReadOnly(forViews="ConsultPurchasePortfolio, ConsultSalePortfolio, ConsultAccountLoan, ConsultOriginationPortfolio, PrintDocumentsAccountLoan")
	private Types.YesNoIntegerType applyDefaultInterestAccrued;
	
	@Column(name="apply_automatic_debit")
	@ReadOnly(forViews="ConsultPurchasePortfolio, ConsultSalePortfolio, ConsultAccountLoan, ConsultOriginationPortfolio, PrintDocumentsAccountLoan")
	private Types.YesNoIntegerType applyAutomaticDebit;
	
	@Temporal(TemporalType.DATE)
	@Column(name="start_date_payment")
	@Required
	@ReadOnly(forViews="ConsultPurchasePortfolio, ConsultSalePortfolio, ConsultAccountLoan, ConsultOriginationPortfolio, PrintDocumentsAccountLoan")
	private Date startDatePayment;

	//bi-directional one-to-one association to Account
	@OneToOne
	@JoinColumn(name="account_id", nullable=false, insertable=false, updatable=false)
	private Account account;

	@Column(name="insurance_amount", precision=11, scale=2)
	@ReadOnly(forViews="ConsultPurchasePortfolio, ConsultSalePortfolio, ConsultAccountLoan, ConsultOriginationPortfolio, PrintDocumentsAccountLoan")
	private BigDecimal insuranceAmount;

	@Column(name="insurance_mortgage_amount", precision=11, scale=2)
	@ReadOnly(forViews="ConsultPurchasePortfolio, ConsultSalePortfolio, ConsultAccountLoan, ConsultOriginationPortfolio, PrintDocumentsAccountLoan")
	private BigDecimal insuranceMortgageAmount;
	
	//bi-directional many-to-one association to Account
	@ManyToOne
	@JoinColumn(name="disbursement_account_id")
	@NoCreate
	@NoModify
	@Required
	@ReferenceView("simple")
	@SearchAction(forViews="RequestTXAccountLoan", value="SearchGeneralAccount.SearchPayableAccount")
	@ReadOnly(forViews="ConsultPurchasePortfolio, ConsultSalePortfolio, ConsultAccountLoan, ConsultOriginationPortfolio, PrintDocumentsAccountLoan")
	private Account disbursementAccount;

	//bi-directional many-to-one association to Account
	@ManyToOne
	@JoinColumn(name="insurance_account_id")
	@NoCreate
	@NoModify
	//@Required
	@ReferenceView("simple")
	@ReadOnly(forViews="ConsultPurchasePortfolio, ConsultSalePortfolio, ConsultAccountLoan, ConsultOriginationPortfolio, PrintDocumentsAccountLoan")
	private Account insuranceAccount;

	//bi-directional many-to-one association to Account
	@ManyToOne
	@JoinColumn(name="mortgage_account_id")
	@NoCreate
	@NoModify
	//@Required
	@ReferenceView("simple")
	@ReadOnly(forViews="ConsultPurchasePortfolio, ConsultSalePortfolio, ConsultAccountLoan, ConsultOriginationPortfolio, PrintDocumentsAccountLoan")
	private Account mortgageAccount;
	
	//bi-directional many-to-one association to Frecuency
	@ManyToOne
	@JoinColumn(name="frecuency_id")
	@NoCreate
	@NoModify
	@Required
	@DescriptionsList(descriptionProperties="name")
	@ReadOnly(forViews="ConsultPurchasePortfolio, ConsultSalePortfolio, ConsultAccountLoan, ConsultOriginationPortfolio, PrintDocumentsAccountLoan")
	private Frecuency frecuency;

	//bi-directional many-to-one association to Seller
	@ManyToOne
	@JoinColumn(name="mortgage_insurer_id", nullable=true)
	@NoCreate
	@NoModify
	//@Required
	@NoFrame
	@ReferenceView("Reference")
	@ReadOnly(forViews="ConsultPurchasePortfolio, ConsultSalePortfolio, ConsultAccountLoan, ConsultOriginationPortfolio, PrintDocumentsAccountLoan")
	private Insurer mortgageInsurer;
	
	//bi-directional many-to-one association to Seller
	@ManyToOne
	@JoinColumn(name="vehicle_insurer_id", nullable=true)
	@NoCreate
	@NoModify
	//@Required
	@NoFrame
	@ReferenceView("Reference")
	@ReadOnly(forViews="ConsultPurchasePortfolio, ConsultSalePortfolio, ConsultAccountLoan, ConsultOriginationPortfolio, PrintDocumentsAccountLoan")
	private Insurer vehicleInsurer;
	
	@ManyToOne
	@JoinColumn(name="codebtor_person_id")
	@NoCreate
	@NoModify
	@ReferenceView("LoanReference")
	private Person codebtor;
	
	//////////////////////////////////////////////////////////
	
	@Transient
	@Column(length = 4000)
	@Stereotype("SIMPLE_HTML_TEXT")
	@ReadOnly
	private String remark;
	
	@Transient
	@ManyToOne
	@Required
	@NoCreate
	@NoModify
	@ReferenceView("LoanReference")
	@ReadOnly(forViews="ConsultPurchasePortfolio, ConsultSalePortfolio, ConsultAccountLoan, ConsultOriginationPortfolio, PrintDocumentsAccountLoan")
	private Person person;
	
	
	@Transient
	@ManyToOne
	//@Required
	@ReferenceView(value="Reference")
	private NaturalPerson naturalPerson;
	
	@Transient
	@ManyToOne
	//@Required
	@ReferenceView(value="Reference")
	private LegalPerson legalPerson;
	
	
	@Transient
	@ManyToOne
	@DescriptionsList
	@NoCreate
	@NoModify
	@ReadOnly(forViews="ConsultPurchasePortfolio, ConsultSalePortfolio, ConsultAccountLoan, ConsultOriginationPortfolio, PrintDocumentsAccountLoan")
	private AccountStatus accountStatus;
	
	@Transient
	@ManyToOne
	@Required
	@NoCreate
	@NoModify
	@ReadOnly(forViews="ConsultPurchasePortfolio, ConsultSalePortfolio, ConsultAccountLoan, ConsultOriginationPortfolio, PrintDocumentsAccountLoan")
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
	@ListProperties("subaccount, dueDate, provisionDays, capitalReduced, capital, interest, totalDividend, insurance, insuranceMortgage, totalQuota, paymentDate, lastPaymentDate, lastPaymentDateCollection, lastPaymentDateDefaultInterest")
	@ReadOnly(forViews="ConsultPurchasePortfolio, ConsultAccountLoan, PrintDocumentsAccountLoan")
	@ListActions({
		@ListAction("Print.generatePdf"),
		@ListAction("Print.generateExcel")
	})
	private List<AccountPaytable> accountPaytables;
	
	//bi-directional many-to-one association to AccountPaytable
	@Transient
	@AsEmbedded
	@OneToMany(mappedBy = "account")
	@OrderBy("subaccount")
	@ListProperties("subaccount, dueDate, provisionDays, capitalReduced, capital, interest, totalDividend, lastPaymentDate, paymentDate")
	@ReadOnly(forViews="ConsultSalePortfolio")
	@ListActions({
		@ListAction("Print.generatePdf"),
		@ListAction("Print.generateExcel")
	})
	private List<AccountSoldPaytable> accountSoldPaytables;
	
	@Transient
	@AsEmbedded
	@OneToMany(mappedBy="account")
	@OrderBy("subaccount")
	@ListProperties("subaccount, dueDate, paymentDate, overdueDays, capital, interest, insuranceMortgage, insurance, defaultInterest, collectionFee, legalFee, receivableFee, total")
	@ReadOnly(forViews="ConsultPurchasePortfolio, ConsultSalePortfolio, ConsultAccountLoan, ConsultOriginationPortfolio")
	@ListActions({
		@ListAction("Print.generatePdf"),
		@ListAction("Print.generateExcel")
	})
	private List<AccountOverdueBalance> accountOverdueBalances;
	
	@Transient
	@Temporal(TemporalType.DATE)
	@Actions({
		@Action(forViews="ConsultAccountLoan", value = "AccountLoan.GetOverdueBalanceForConsult", alwaysEnabled=true ),
		@Action(forViews="ConsultPurchasePortfolio", value = "AccountLoan.GetOverdueBalancePPForConsult", alwaysEnabled=true ),
		@Action(forViews="ConsultOriginationPortfolio", value = "AccountLoan.GetOverdueBalanceOPForConsult", alwaysEnabled=true ),
		@Action(forViews="ConsultSalePortfolio", value = "AccountLoan.GetOverdueBalanceSPForConsult", alwaysEnabled=true )
	})
	private Date projectedAccountingDate;
	
	
	
	
	///////////////////////////////////////////////////////////////////////
	// Campos usados para mostrar la información de Deudor
	//////////////////////////////////////////////////////////////////////
	@Transient
	@Column(length=10)
	@ReadOnly
	private String identificationType;
	
	@Transient
	@ReadOnly
	@Column(length=20)
	private String identification;
	
	@Transient
	@ReadOnly
	private String name;
	
	@Transient
	@ReadOnly
	private String email;
	
	@Transient
	@ReadOnly
	private String activity;
	
	@Transient
	@ReadOnly
	@Column(length=30)
	private String maritalStatus;
	
	@Transient
	@ReadOnly
	@Column(length=30)
	private String nationality;
	
	@Transient
	@ReadOnly
	@Column(length=20)
	private String homePhoneNumber1;
	
	@Transient
	@ReadOnly
	@Column(length=20)
	private String homePhoneNumber2;
	
	@Transient
	@ReadOnly
	@Column(length=20)
	private String cellPhoneNumber1;
	
	@Transient
	@ReadOnly
	private String homeMainStreet;
	
	@Transient
	@ReadOnly
	@Column(length=20)
	private String homeNumber;
	
	@Transient
	@ReadOnly
	private String homeSideStreet;
	
	@Transient
	@ReadOnly
	private String homeSector;
	
	/////////////////////////////////////////////////////////
	// Dirección
	/////////////////////////////////////////////////////////
	@Transient
	@ReadOnly
	@Column(length=30)
	private String country;
	
	@Transient
	@ReadOnly
	@Column(length=30)
	private String region;
	
	@Transient
	@ReadOnly
	@Column(length=30)
	private String state;
	
	@Transient
	@ReadOnly
	@Column(length=30)
	private String city;
	
	@Transient
	@ReadOnly
	@Column(length=30)	
	private String district;
	
	/////////////////////////////////////////////////////////
	// Referencias Personales
	/////////////////////////////////////////////////////////
	@Transient
	@ReadOnly
	@Column(length=35)
	private String nameReference1;
	
	@Transient
	@ReadOnly
	private String addressReference1;
	
	@Transient
	@ReadOnly
	@Column(length=15)
	private String homePhoneReference1;
	
	@Transient
	@ReadOnly
	@Column(length=15)
	private String cellPhoneReference1;
	
	@Transient
	@ReadOnly
	@Column(length=15)
	private String workPhoneReference1;
	
	@Transient
	@ReadOnly
	@Column(length=20)
	private String relationshipReference1;
	
	
	@Transient
	@ReadOnly	
	@Column(length=35)
	private String nameReference2;
	
	@Transient
	@ReadOnly
	private String addressReference2;
	
	@Transient
	@ReadOnly
	@Column(length=15)
	private String homePhoneReference2;
	
	@Transient
	@ReadOnly
	@Column(length=15)
	private String cellPhoneReference2;
	
	@Transient
	@ReadOnly
	@Column(length=15)
	private String workPhoneReference2;
	
	@Transient
	@ReadOnly
	@Column(length=20)
	private String relationshipReference2;
	
	@Transient
	@ReadOnly
	@Column(length=35)
	private String nameReference3;
	
	@Transient
	@ReadOnly
	private String addressReference3;
	
	@Transient
	@ReadOnly
	@Column(length=15)
	private String homePhoneReference3;
	
	@Transient
	@ReadOnly
	@Column(length=15)
	private String cellPhoneReference3;
	
	@Transient
	@ReadOnly
	@Column(length=15)
	private String workPhoneReference3;
	
	@Transient
	@ReadOnly
	@Column(length=20)
	private String relationshipReference3;
	
	@Transient
	@ReadOnly
	@Column(length=20)
	@DisplaySize(20)
	private String operatingConditionName;
	
	@Transient
	@ReadOnly
	@Column(length=20)
	@DisplaySize(20)
	private String legaConditionName;
	
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

	public Integer getDaysGrace() {
		return daysGrace;
	}

	public void setDaysGrace(Integer daysGrace) {
		this.daysGrace = daysGrace;
	}

	public Integer getDaysGraceCollectionFee() {
		return daysGraceCollectionFee;
	}

	public void setDaysGraceCollectionFee(Integer daysGraceCollectionFee) {
		this.daysGraceCollectionFee = daysGraceCollectionFee;
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
	
	
	public NaturalPerson getNaturalPerson() {
		if (account!=null) {
			try {
				if (account.getPerson().getPersonType().getPersonTypeId().equals("NAT")) {
					naturalPerson = XPersistence.getManager().find(NaturalPerson.class, account.getPerson().getPersonId());				
				}				
			} catch (Exception e) {
			}
		}
		return naturalPerson;
	}
	public void setNaturalPerson(NaturalPerson naturalPerson) {
		this.naturalPerson = naturalPerson;
	}
	
	public LegalPerson getLegalPerson() {
		if (account!=null) {
			try {
				if (account.getPerson().getPersonType().getPersonTypeId().equals("LEG")) {
					legalPerson = XPersistence.getManager().find(LegalPerson.class, account.getPerson().getPersonId());				
				}				
			} catch (Exception e) {
			}
		}
		return legalPerson;
	}
	public void setLegalPerson(NaturalPerson naturalPerson) {
		this.naturalPerson = naturalPerson;
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

	public Types.YesNoIntegerType getApplyDefaultInterestAccrued() {
		return applyDefaultInterestAccrued;
	}

	public void setApplyDefaultInterestAccrued(Types.YesNoIntegerType applyDefaultInterestAccrued) {
		this.applyDefaultInterestAccrued = applyDefaultInterestAccrued;
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

	public Types.YesNoIntegerType getApplyAutomaticDebit() {
		return applyAutomaticDebit;
	}

	public void setApplyAutomaticDebit(Types.YesNoIntegerType applyAutomaticDebit) {
		this.applyAutomaticDebit = applyAutomaticDebit;
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
			if (n!=null && n.getBrokerPerson()!=null)
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
			if (n!=null && n.getBrokerPerson()!=null)
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
	
	
	public List<AccountSoldPaytable> getAccountSoldPaytables() {
		return accountSoldPaytables;
	}

	public void setAccountSoldPaytables(List<AccountSoldPaytable> accountSoldPaytables) {
		this.accountSoldPaytables = accountSoldPaytables;
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
		setDaysGrace(getProduct().getDaysGrace());
		
		setDaysGraceCollectionFee(getProduct().getDaysGraceCollectionFee());
		
		setApplyDefaultInterestAccrued(getProduct().getApplyDefaultInterestAccrued());
		
		setApplyAutomaticDebit(getProduct().getApplyAutomaticDebit());
		
		if (originalAmount==null)
			originalAmount=amount;
		
		if (getPaymentDay()>31)
			setPaymentDay(31);
		
		if (getFrecuency().getFrecuencyId() == AccountLoanHelper.EXPIRATION_FRECUENCY_ID)
		{
			Calendar cal = Calendar.getInstance();
			cal.setTime(getStartDatePayment());
			setPaymentDay(cal.get(Calendar.DAY_OF_MONTH));
			setQuotasNumber(1);
		}
	}
	
	
	public void loadPersonalData() {
		Account account = XPersistence.getManager().find(Account.class, (String)getAccount().getAccountId());
		Person person =  account.getPerson();
		
		if (person.getPersonType().getPersonTypeId().equals("NAT")) {
			NaturalPerson naturalPerson = XPersistence.getManager().find(NaturalPerson.class, account.getPerson().getPersonId());
			fillDataNaturalPerson(naturalPerson);
		}
		else{
			LegalPerson legalPerson = XPersistence.getManager().find(LegalPerson.class, account.getPerson().getPersonId());
			fillDataLegalPerson(legalPerson);
		}
	}
	
	
	public void fillDataLegalPerson(LegalPerson legalPerson) {
		
		this.setIdentificationType(legalPerson.getIdentificationType().getIdentificationTypeId());
		this.setIdentification(legalPerson.getIdentification());
		this.setName(legalPerson.getName());
		this.setEmail(legalPerson.getEmail());
		this.setActivity(legalPerson.getActivity());
		this.setHomePhoneNumber1(legalPerson.getHomePhoneNumber1());
		this.setHomePhoneNumber2(legalPerson.getHomePhoneNumber2());
		this.setHomeMainStreet(legalPerson.getHomeMainStreet());
		this.setHomeNumber(legalPerson.getHomeNumber());
		this.setHomeSideStreet(legalPerson.getHomeSideStreet());
		this.setHomeSector(legalPerson.getHomeSector());
		
		try {
			District district = XPersistence.getManager().find(District.class, legalPerson.getHomeDistrict().getDistrictId());
			
			fillDistrict(district);						
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	private void fillDataNaturalPerson(NaturalPerson naturalPerson) {
		
		this.setIdentificationType(naturalPerson.getIdentificationType().getIdentificationTypeId());
		this.setIdentification(naturalPerson.getIdentification());
		this.setName(naturalPerson.getName());
		this.setEmail(naturalPerson.getEmail());
		this.setActivity(naturalPerson.getActivity());
		this.setHomePhoneNumber1(naturalPerson.getHomePhoneNumber1());
		this.setHomePhoneNumber2(naturalPerson.getHomePhoneNumber2());
		this.setHomeMainStreet(naturalPerson.getHomeMainStreet());
		this.setHomeNumber(naturalPerson.getHomeNumber());
		this.setHomeSideStreet(naturalPerson.getHomeSideStreet());
		this.setHomeSector(naturalPerson.getHomeSector());
		
		
		
		
		
		try {
			List<PersonalReference> personalReferences=naturalPerson.getPersonalReferences();
			if (personalReferences!=null) {
				if (personalReferences.size()>=1) {					
					fillPersonalReference(personalReferences.get(0), 1);					 
				}
				if (personalReferences.size()>=2) {
					fillPersonalReference(personalReferences.get(1), 2);
				}
				if (personalReferences.size()>=3) {
					fillPersonalReference(personalReferences.get(2), 3);
				}
			}
			
			if (naturalPerson.getHomeDistrict()!=null)
			{
				District district = XPersistence.getManager().find(District.class, naturalPerson.getHomeDistrict().getDistrictId());
				fillDistrict(district);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void fillPersonalReference(PersonalReference personalReference, int index) {
		Person person = personalReference.getPerson();
		if (index==1) {
			if (person!=null) {
				this.setNameReference1(person.getName());				
			}
			this.setAddressReference1(personalReference.getAddress());
			this.setHomePhoneReference1(personalReference.getHomePhone());
			this.setCellPhoneReference1(personalReference.getCellPhone());
			this.setWorkPhoneReference1(personalReference.getWorkPhone());
			this.setRelationshipReference1(personalReference.getRelationship());
		}else if (index == 2) {
			if (person!=null) {
				this.setNameReference2(person.getName());				
			}
			this.setAddressReference2(personalReference.getAddress());
			this.setHomePhoneReference2(personalReference.getHomePhone());
			this.setCellPhoneReference2(personalReference.getCellPhone());
			this.setWorkPhoneReference2(personalReference.getWorkPhone());
			this.setRelationshipReference2(personalReference.getRelationship());
		}else if (index == 3) {
			if (person!=null) {
				this.setNameReference3(person.getName());				
			}
			this.setAddressReference3(personalReference.getAddress());
			this.setHomePhoneReference3(personalReference.getHomePhone());
			this.setCellPhoneReference3(personalReference.getCellPhone());
			this.setWorkPhoneReference3(personalReference.getWorkPhone());
			this.setRelationshipReference3(personalReference.getRelationship());
		}
	}
	
	
	
	private void fillDistrict(District district) throws Exception{
	
		this.setCountry(district.getCountry().getName());
		this.setRegion(district.getRegion().getName());
		this.setState(district.getState().getName());
		this.setCity(district.getCity().getName());
		this.setDistrict(district.getName());
	}
	
	

	public String getIdentificationType() {
		if (identificationType==null && account!=null) {
			loadPersonalData();
		}
		return identificationType;

	}

	public void setIdentificationType(String identificationType) {
		this.identificationType = identificationType;
	}

	public String getIdentification() {
		return identification;
	}

	public void setIdentification(String identification) {
		this.identification = identification;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getHomePhoneNumber1() {
		return homePhoneNumber1;
	}

	public void setHomePhoneNumber1(String homePhoneNumber1) {
		this.homePhoneNumber1 = homePhoneNumber1;
	}

	public String getHomePhoneNumber2() {
		return homePhoneNumber2;
	}

	public void setHomePhoneNumber2(String homePhoneNumber2) {
		this.homePhoneNumber2 = homePhoneNumber2;
	}

	public String getCellPhoneNumber1() {
		return cellPhoneNumber1;
	}

	public void setCellPhoneNumber1(String cellPhoneNumber1) {
		this.cellPhoneNumber1 = cellPhoneNumber1;
	}

	public String getHomeMainStreet() {
		return homeMainStreet;
	}

	public void setHomeMainStreet(String homeMainStreet) {
		this.homeMainStreet = homeMainStreet;
	}

	public String getHomeNumber() {
		return homeNumber;
	}

	public void setHomeNumber(String homeNumber) {
		this.homeNumber = homeNumber;
	}

	public String getHomeSideStreet() {
		return homeSideStreet;
	}

	public void setHomeSideStreet(String homeSideStreet) {
		this.homeSideStreet = homeSideStreet;
	}

	public String getHomeSector() {
		return homeSector;
	}

	public void setHomeSector(String homeSector) {
		this.homeSector = homeSector;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getNameReference1() {
		return nameReference1;
	}

	public void setNameReference1(String nameReference1) {
		this.nameReference1 = nameReference1;
	}

	public String getAddressReference1() {
		return addressReference1;
	}

	public void setAddressReference1(String addressReference1) {
		this.addressReference1 = addressReference1;
	}

	public String getHomePhoneReference1() {
		return homePhoneReference1;
	}

	public void setHomePhoneReference1(String homePhoneReference1) {
		this.homePhoneReference1 = homePhoneReference1;
	}

	public String getCellPhoneReference1() {
		return cellPhoneReference1;
	}

	public void setCellPhoneReference1(String cellPhoneReference1) {
		this.cellPhoneReference1 = cellPhoneReference1;
	}

	public String getWorkPhoneReference1() {
		return workPhoneReference1;
	}

	public void setWorkPhoneReference1(String workPhoneReference1) {
		this.workPhoneReference1 = workPhoneReference1;
	}

	public String getRelationshipReference1() {
		return relationshipReference1;
	}

	public void setRelationshipReference1(String relationshipReference1) {
		this.relationshipReference1 = relationshipReference1;
	}

	public String getNameReference2() {
		return nameReference2;
	}

	public void setNameReference2(String nameReference2) {
		this.nameReference2 = nameReference2;
	}

	public String getAddressReference2() {
		return addressReference2;
	}

	public void setAddressReference2(String addressReference2) {
		this.addressReference2 = addressReference2;
	}

	public String getHomePhoneReference2() {
		return homePhoneReference2;
	}

	public void setHomePhoneReference2(String homePhoneReference2) {
		this.homePhoneReference2 = homePhoneReference2;
	}

	public String getCellPhoneReference2() {
		return cellPhoneReference2;
	}

	public void setCellPhoneReference2(String cellPhoneReference2) {
		this.cellPhoneReference2 = cellPhoneReference2;
	}

	public String getWorkPhoneReference2() {
		return workPhoneReference2;
	}

	public void setWorkPhoneReference2(String workPhoneReference2) {
		this.workPhoneReference2 = workPhoneReference2;
	}

	public String getRelationshipReference2() {
		return relationshipReference2;
	}

	public void setRelationshipReference2(String relationshipReference2) {
		this.relationshipReference2 = relationshipReference2;
	}

	public String getNameReference3() {
		return nameReference3;
	}

	public void setNameReference3(String nameReference3) {
		this.nameReference3 = nameReference3;
	}

	public String getAddressReference3() {
		return addressReference3;
	}

	public void setAddressReference3(String addressReference3) {
		this.addressReference3 = addressReference3;
	}

	public String getHomePhoneReference3() {
		return homePhoneReference3;
	}

	public void setHomePhoneReference3(String homePhoneReference3) {
		this.homePhoneReference3 = homePhoneReference3;
	}

	public String getCellPhoneReference3() {
		return cellPhoneReference3;
	}

	public void setCellPhoneReference3(String cellPhoneReference3) {
		this.cellPhoneReference3 = cellPhoneReference3;
	}

	public String getWorkPhoneReference3() {
		return workPhoneReference3;
	}

	public void setWorkPhoneReference3(String workPhoneReference3) {
		
		this.workPhoneReference3 = workPhoneReference3;
	}

	public String getRelationshipReference3() {
		return relationshipReference3;
	}

	public void setRelationshipReference3(String relationshipReference3) {
		this.relationshipReference3 = relationshipReference3;
	}

	public void setLegalPerson(LegalPerson legalPerson) {
		this.legalPerson = legalPerson;
	}
	
	public Date getCancellationDate()
	{
		return account.getCancellationDate();
	}
	
	public String getLegalConditionName()
	{
		if (account.getLegalCondition() != null)
			return account.getLegalCondition().getName();
		return null;
	}
	
	public String getOperatingConditionName()
	{
		if (account.getOperatingCondition() != null)
			return account.getOperatingCondition().getName();
		return null;
	}
	
	public String getAccountPortfolioStatusName()
	{
		AccountPortfolio ap = (AccountPortfolio)XPersistence.getManager().find(AccountPortfolio.class, account.getAccountId());
		if (ap!=null)
			return ap.getAccountPortfolioStatus().getName();
		return null;
	}
	public String getPurchaseStatusName()
	{
		AccountPortfolio ap = (AccountPortfolio)XPersistence.getManager().find(AccountPortfolio.class, account.getAccountId());
		if (ap!=null)
			return ap.getPurchaseStatus().getName();
		return null;
	}
	public String getSaleStatusName()
	{
		AccountPortfolio ap = (AccountPortfolio)XPersistence.getManager().find(AccountPortfolio.class, account.getAccountId());
		if (ap!=null)
			return ap.getSaleStatus().getName();
		return null;
	}
	public BigDecimal getPurchaseAmount()
	{
		AccountPortfolio ap = (AccountPortfolio)XPersistence.getManager().find(AccountPortfolio.class, account.getAccountId());
		if (ap!=null)
			return ap.getPurchaseAmount();
		return null;
	}
	public BigDecimal getSaleAmount()
	{
		AccountPortfolio ap = (AccountPortfolio)XPersistence.getManager().find(AccountPortfolio.class, account.getAccountId());
		if (ap!=null)
			return ap.getSaleAmount();
		return null;
	}
	public BigDecimal getPurchaseSpread()
	{
		AccountPortfolio ap = (AccountPortfolio)XPersistence.getManager().find(AccountPortfolio.class, account.getAccountId());
		if (ap!=null)
			return ap.getPurchaseSpread();
		return null;
	}
	public BigDecimal getSaleSpread()
	{
		AccountPortfolio ap = (AccountPortfolio)XPersistence.getManager().find(AccountPortfolio.class, account.getAccountId());
		if (ap!=null)
			return ap.getSaleSpread();
		return null;
	}
	public BigDecimal getPurchaseRate()
	{
		AccountPortfolio ap = (AccountPortfolio)XPersistence.getManager().find(AccountPortfolio.class, account.getAccountId());
		if (ap!=null)
			return ap.getPurchaseRate();
		return null;
	}
	public BigDecimal getSaleRate()
	{
		AccountPortfolio ap = (AccountPortfolio)XPersistence.getManager().find(AccountPortfolio.class, account.getAccountId());
		if (ap!=null)
			return ap.getSaleRate();
		return null;
	}

	public Person getCodebtor() {
		return codebtor;
	}

	public void setCodebtor(Person codebtor) {
		this.codebtor = codebtor;
	}

	public String getRemark() {
		if (getAccount()!=null)
			return getAccount().getRemark();
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}