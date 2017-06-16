package com.powerfin.model;

import java.io.*;
import java.math.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import com.powerfin.helper.*;
import com.powerfin.model.superclass.*;
import com.powerfin.model.types.*;


/**
 * The persistent class for the account_invoice database table.
 * 
 */
@Entity
@Table(name="account_invoice")
@Views({
	@View(members="accountId, companyAccountingDate; accountStatus;"
				+ "issueDate, dueDate;"
				+ "person{person;}"
				+ "product{product;}"
				+ "voucher{establishmentCode; emissionPointCode; sequentialCode; authorizationCode;}"
				+ "remark{remark;}"
				+ "taxDeclaration{invoiceTaxSupport;invoiceVoucherType;invoiceProviderType; partRelated;}"
				+ "paymentType{invoicePaymentType;countryPayment;doubleTaxationConventionPayment;underTheStatutePayment;}"
				+ "paymentMethods{accountInvoicePayments;}"
				+ "detail{details;}"
				),
	@View(name="InvoicePurchase", 
		members="accountId, companyAccountingDate; accountStatus;"
				+ "issueDate, dueDate;"
				+ "person{person;}"
				+ "product{product;}"
				+ "voucher{establishmentCode; emissionPointCode; sequentialCode; authorizationCode;}"
				+ "remark{remark;}"
				+ "taxDeclaration{invoiceTaxSupport;invoiceVoucherType;invoiceProviderType; partRelated;}"
				+ "paymentType{invoicePaymentType;countryPayment;doubleTaxationConventionPayment;underTheStatutePayment;}"
				+ "paymentMethods{accountInvoicePayments;}"
				+ "detail{details;}"
				+ "balances{balance;}"
				),
	@View(name="ConsultPurchaseInvoice", 
		members="accountId, companyAccountingDate; accountStatus;"
				+ "issueDate, dueDate;"
				+ "taxDeclaration{invoiceTaxSupport;invoiceVoucherType;invoiceProviderType; partRelated;}"
				+ "person{person;}"
				+ "product{product;}"
				+ "voucher{establishmentCode; emissionPointCode; sequentialCode; authorizationCode;}"
				+ "paymentType{invoicePaymentType;countryPayment;doubleTaxationConventionPayment;underTheStatutePayment;}"
				+ "paymentMethods{accountInvoicePayments;}"
				+ "detail{details;}"
				+ "remark{remark;}"
				),
	@View(name="InvoiceSale", 
		members="accountId, companyAccountingDate; accountStatus;"
				+ "issueDate, dueDate;"
				+ "person{person;}"
				+ "product{product;}"
				+ "voucher{invoiceVoucherType;establishmentCode; emissionPointCode; sequentialCode; authorizationCode;}"
				+ "remark{remark;}"
				+ "detail{details;}"
				+ "balances{balance;}"
				),		
	@View(name="RequestTXInvoicePurchase", 
		members="accountId, companyAccountingDate; accountStatus;"
				+ "issueDate, dueDate; "
				+ "unity;"
				+ "person{person;}"
				+ "product{product;}"
				+ "voucher{establishmentCode; emissionPointCode; sequentialCode; authorizationCode;}"
				+ "remark{remark;}"
				+ "taxDeclaration{invoiceTaxSupport;invoiceVoucherType;invoiceProviderType; partRelated;}"
				+ "paymentType{invoicePaymentType;countryPayment;doubleTaxationConventionPayment;underTheStatutePayment;}"
				+ "paymentMethods{accountInvoicePayments;}"
				+ "detail{details;}"
				),		
	@View(name="AuthorizeTXInvoicePurchase", 
		members="info{accountId, companyAccountingDate; accountStatus;"
				+ "issueDate, dueDate;}"
				+ "person{person;}"
				+ "product{product;}"
				+ "voucher{establishmentCode; emissionPointCode; sequentialCode; authorizationCode;}"
				+ "remark{remark;}"
				+ "taxDeclaration{invoiceTaxSupport;invoiceVoucherType;invoiceProviderType; partRelated;}"
				+ "paymentType{invoicePaymentType;countryPayment;doubleTaxationConventionPayment;underTheStatutePayment;}"
				+ "paymentMethods{accountInvoicePayments;}"
				+ "detail{details;}"
				),
	@View(name="RequestTXInvoiceSale", 
		members="accountId, companyAccountingDate; accountStatus;"
				+ "issueDate, dueDate; "
				+ "unity; "
				+ "person{person;}"
				+ "product{product;}"
				+ "voucher{invoiceVoucherType;establishmentCode; emissionPointCode; sequentialCode; authorizationCode;}"
				+ "remark{remark;}"
				+ "detail{details;}"
				),
	@View(name="AuthorizeTXInvoiceSale", 
		members="info{accountId, companyAccountingDate; accountStatus;"
				+ "issueDate, dueDate;}"
				+ "voucher{invoiceVoucherType;establishmentCode; emissionPointCode; sequentialCode; authorizationCode;}"
				+ "remark{remark;}"
				+ "detail{details;}"
				),
	@View(name="RequestTXCreditNotePurchase", 
		members="accountId, companyAccountingDate; accountStatus;"
				+ "issueDate;"
				+ "unity;"
				+ "accountModified;"
				+ "person{person;}"
				+ "product{product;}"
				+ "voucher{invoiceVoucherType;establishmentCode; emissionPointCode; sequentialCode; authorizationCode;}"
				+ "remark{remark;}"
				+ "detail{details;}"
				),		
	@View(name="AuthorizeTXCreditNotePurchase", 
		members="info{accountId, companyAccountingDate; accountStatus;"
				+ "issueDate, dueDate;}"
				+ "accountModified;"
				+ "person{person;}"
				+ "product{product;}"
				+ "voucher{invoiceVoucherType;establishmentCode; emissionPointCode; sequentialCode; authorizationCode;}"
				+ "remark{remark;}"
				+ "detail{details;}"
				),
	@View(name="RequestTXCreditNoteSale", 
		members="accountId, companyAccountingDate; accountStatus;"
				+ "issueDate;"
				+ "unity;"
				+ "accountModified;"
				+ "person{person;}"
				+ "product{product;}"
				+ "voucher{invoiceVoucherType;establishmentCode; emissionPointCode; sequentialCode; authorizationCode;}"
				+ "remark{remark;}"
				+ "detail{details;}"
				),		
	@View(name="AuthorizeTXCreditNoteSale", 
		members="info{accountId, companyAccountingDate; accountStatus;"
				+ "issueDate, dueDate;}"
				+ "accountModified;"
				+ "person{person;}"
				+ "product{product;}"
				+ "voucher{invoiceVoucherType;establishmentCode; emissionPointCode; sequentialCode; authorizationCode;}"
				+ "remark{remark;}"
				+ "detail{details;}"
				),
	@View(name="PrintTXInvoiceSale", 
		members=""
				+ "accountId; issueDate, dueDate;"
				+ "voucher{invoiceVoucherType;establishmentCode; emissionPointCode; sequentialCode; authorizationCode;}"
				+ "remark{remark;}"
				+ "detail{details;}"
			),
	@View(name="ConsultInvoiceActive", 
		members="accountId, companyAccountingDate; accountStatus;"
				+ "issueDate, dueDate;"
				+ "taxDeclaration{invoiceTaxSupport;invoiceVoucherType;invoiceProviderType; partRelated;}"
				+ "person{person;}"
				+ "product{product;}"
				+ "voucher{establishmentCode; emissionPointCode; sequentialCode; authorizationCode;}"
				+ "paymentType{invoicePaymentType;countryPayment;doubleTaxationConventionPayment;underTheStatutePayment;}"
				+ "paymentMethods{accountInvoicePayments;}"
				+ "detail{details;}"
				+ "remark{remark;}"
				),
	@View(name="RequestTXOrderItems", 
		members="accountId, companyAccountingDate; accountStatus;"
				+ "issueDate;"
				+ "unity;"
				+ "person{person;}"
				+ "product{product;}"
				+ "remark{sequentialCode; remark;}"
				+ "detail{details;}"
				),		
	@View(name="AuthorizeTXOrderItems", 
		members="accountId, companyAccountingDate; accountStatus;"
				+ "issueDate,"
				+ "person{person;}"
				+ "product{product;}"
				+ "remark{sequentialCode; remark;}"
				+ "detail{details;}"
				),		
	@View(name="TXOrderToInvoice", 
		members="accountId, companyAccountingDate;"
				+ "issueDate,"
				+ "person{person;}"
				+ "product{product;}"
				+ "detail{details;}"
				),
	@View(name="reference", members="account;"
			+ "dueDate, issueDate;"),
	@View(name="forRetention", 
			members="accountId;accountStatus;"
			+ "establishmentCode, emissionPointCode, sequentialCode;"
			+ "subtotal, taxes, total"),
	@View(name="forCreditNote", 
			members="accountId;accountStatus;"
			+ "establishmentCode, emissionPointCode, sequentialCode;"
			+ "subtotal, taxes, total"),
	@View(name="selectInvoice", 
			members="accountId;accountStatus;"
			+ "establishmentCode, emissionPointCode, sequentialCode")
})
@Tabs({
	@Tab(properties="account.accountId, account.currency.currencyId, account.person.name, account.code, account.accountStatus.name, account.product.name, issueDate"),
	
	@Tab(name="TXInvoicePurchase", properties="account.accountId, account.currency.currencyId, account.person.name, account.code, issueDate",
		baseCondition = "${account.accountStatus.accountStatusId} = '001' "
			+ "and ${account.product.productType.productTypeId} ='"+AccountInvoiceHelper.INVOICE_PURCHASE_PRODUCT_TYPE_ID+"'"),
	@Tab(name="InvoicePurchase", properties="account.accountId, account.currency.currencyId, account.person.name, account.code, account.accountStatus.name, account.product.name, issueDate, balance",
		baseCondition = "${account.product.productType.productTypeId} ='"+AccountInvoiceHelper.INVOICE_PURCHASE_PRODUCT_TYPE_ID+"'"),
	
	@Tab(name="TXInvoiceSale", properties="account.accountId, account.currency.currencyId, account.person.name, account.code, account.externalCode, issueDate",
		baseCondition = "${account.accountStatus.accountStatusId} = '001' "
			+ "and ${account.product.productType.productTypeId} ='"+AccountInvoiceHelper.INVOICE_SALE_PRODUCT_TYPE_ID+"'"),
	@Tab(name="InvoiceSale", properties="account.accountId, account.currency.currencyId, account.person.name, account.code, account.accountStatus.name, account.product.name, issueDate",
		baseCondition = "${account.product.productType.productTypeId} ='"+AccountInvoiceHelper.INVOICE_SALE_PRODUCT_TYPE_ID+"'"),
	@Tab(name="PrinterInvoiceSale", properties="account.accountId, account.currency.currencyId, account.person.name, account.code, issueDate",
	baseCondition = "${account.accountStatus.accountStatusId} IN ('002','005') "
		+ "and ${account.product.productType.productTypeId} ='"+AccountInvoiceHelper.INVOICE_SALE_PRODUCT_TYPE_ID+"'"),
	
	@Tab(name="TXCreditNotePurchase", properties="account.accountId, account.currency.currencyId, account.person.name, account.code, issueDate",
	baseCondition = "${account.accountStatus.accountStatusId} = '001' "
			+ "and ${account.product.productType.productTypeId} ='"+AccountInvoiceHelper.CREDIT_NOTE_PURCHASE_PRODUCT_TYPE_ID+"'"),
	@Tab(name="CreditNotePurchase", properties="account.accountId, account.currency.currencyId, account.person.name, account.code, account.accountStatus.name, account.product.name, issueDate, balance",
	baseCondition = "${account.product.productType.productTypeId} ='"+AccountInvoiceHelper.CREDIT_NOTE_PURCHASE_PRODUCT_TYPE_ID+"'"),
	
	@Tab(name="TXCreditNoteSale", properties="account.accountId, account.currency.currencyId, account.person.name, account.code, issueDate",
		baseCondition = "${account.accountStatus.accountStatusId} = '001' "
			+ "and ${account.product.productType.productTypeId} ='"+AccountInvoiceHelper.CREDIT_NOTE_SALE_PRODUCT_TYPE_ID+"'"),
	@Tab(name="CreditNoteSale", properties="account.accountId, account.currency.currencyId, account.person.name, account.code, account.accountStatus.name, account.product.name, issueDate",
		baseCondition = "${account.product.productType.productTypeId} ='"+AccountInvoiceHelper.CREDIT_NOTE_SALE_PRODUCT_TYPE_ID+"'"),
	
	@Tab(name="TXOrderItems", properties="account.accountId, account.currency.currencyId, account.person.name, account.code, issueDate",
		baseCondition = "${account.accountStatus.accountStatusId} = '001' "
			+ "and ${account.product.productType.productTypeId} ='"+AccountInvoiceHelper.ORDER_PURCHASE_PRODUCT_TYPE_ID+"'"),
	@Tab(name="TXConvertOrderToInvoice", properties="account.accountId, account.currency.currencyId, account.person.name, account.code, issueDate",
		baseCondition = "${account.accountStatus.accountStatusId} = '002' "
			+ "and ${account.product.productType.productTypeId} ='"+AccountInvoiceHelper.ORDER_PURCHASE_PRODUCT_TYPE_ID+"'")
})
public class AccountInvoice extends AuditEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="account_id", unique=true, nullable=false)
	@ReadOnly(notForViews="forRetention, forCreditNote")
	@Hidden
	private String accountId;
	
	@Temporal(TemporalType.DATE)
	@Column(name="due_date")
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale")
	private Date dueDate;

	@Temporal(TemporalType.DATE)
	@Column(name="issue_date", nullable=false)
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale")
	@Required
	private Date issueDate;

	@Column(length=400)
	@Required
	@Stereotype("TEXT_AREA")
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale")
	private String remark;

	@Column(name="establishment_code", nullable=true, length=5)
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale")
	private String establishmentCode;
	
	@Column(name="emission_point_code", nullable=true, length=5)
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale")
	private String emissionPointCode;

	@Column(name="sequential_code", nullable=true, length=50)
	@Required
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale")
	private String sequentialCode;
    
	@Column(name="authorization_code", nullable=true, length=50)
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale")
	private String authorizationCode;
	
	@Column(name="part_related", nullable=true)
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale")
	private Types.YesNoIntegerType partRelated;
	
	@Column(name="double_taxation_convention_payment", nullable=true)
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale")
	private Types.YesNoIntegerType doubleTaxationConventionPayment;
	
	@Column(name="under_the_statute_payment", nullable=true)
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale")
	private Types.YesNoIntegerType underTheStatutePayment;
		
	@ManyToOne
	@JoinColumn(name = "account_modified_id", nullable = true)
	@NoCreate
	@NoModify
	@ReferenceView("forCreditNote")
	@ReadOnly(forViews="AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale")
	@SearchActions({
		@SearchAction(forViews="RequestTXCreditNotePurchase", value="SearchAccount.SearchInvoicePurchaseToApplyCreditNote"),
		@SearchAction(forViews="RequestTXCreditNoteSale", value="SearchAccount.SearchInvoiceSaleToApplyCreditNote"),
	})
	private AccountInvoice accountModified;
	
	@OneToMany(mappedBy="accountInvoice", cascade = CascadeType.ALL)
	@AsEmbedded
	@ListProperties("accountDetail.accountId, accountDetail.name, taxPercentage, unitPrice, quantity,"
			+ "amount[accountInvoice.subtotal, "
			+ "accountInvoice.calculateTaxes, accountInvoice.calculateTotal]"
			)
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale, AuthorizeTXOrderItems, TXOrderToInvoice")
	@CollectionViews({
		@CollectionView(forViews="RequestTXInvoicePurchase, AuthorizeTXInvoicePurchase, RequestTXOrderItems", value = "InvoicePurchase"),
		@CollectionView(forViews="RequestTXInvoiceSale, AuthorizeTXInvoiceSale", value = "InvoiceSale"),
		@CollectionView(forViews="RequestTXCreditNotePurchase, AuthorizeTXCreditNotePurchase", value = "CreditNotePurchase"),
		@CollectionView(forViews="RequestTXCreditNoteSale, AuthorizeTXCreditNoteSale", value = "CreditNoteSale"),
	})
	@NewActions({
		@NewAction(forViews="RequestTXInvoicePurchase", value="AccountInvoiceActions.createNewInvoiceDetailToInvoice"),
		@NewAction(forViews="RequestTXInvoiceSale", value="AccountInvoiceActions.createNewInvoiceDetailToInvoice"),
		@NewAction(forViews="RequestTXCreditNotePurchase", value="AccountInvoiceActions.createNewInvoiceDetailToInvoice"),
		@NewAction(forViews="RequestTXCreditNoteSale", value="AccountInvoiceActions.createNewInvoiceDetailToInvoice")
	})
	private List<AccountInvoiceDetail> details;
	
	@OneToMany(mappedBy="accountInvoice", cascade = CascadeType.ALL)
	@AsEmbedded
	@ListProperties("invoicePaymentMethod.name")
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale")
	private List<AccountInvoicePayment> accountInvoicePayments;

	@OneToMany(mappedBy="accountInvoice", cascade = CascadeType.ALL)
	private List<AccountInvoiceTax> accountInvoiceTaxes;
	
	//bi-directional one-to-one association to Account
	@OneToOne
	@JoinColumn(name="account_id", nullable=false, insertable=false, updatable=false)
	private Account account;
	
	@ManyToOne
	@JoinColumn(name = "invoice_tax_support_id", nullable = true)
	@NoCreate
	@NoModify
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale")
	private InvoiceTaxSupport invoiceTaxSupport;
	
	@ManyToOne
	@JoinColumn(name = "invoice_voucher_type_id", nullable = true)
	@NoCreate
	@NoModify
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale")
	private InvoiceVoucherType invoiceVoucherType;
	
	@ManyToOne
	@JoinColumn(name = "invoice_provider_type_id", nullable = true)
	@NoCreate
	@NoModify
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale")
	@DescriptionsList(descriptionProperties="name")
	private InvoiceProviderType invoiceProviderType;
	
	@ManyToOne
	@JoinColumn(name = "invoice_payment_type_id", nullable = true)
	@NoCreate
	@NoModify
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale")
	@DescriptionsList(descriptionProperties="name")
	private InvoicePaymentType invoicePaymentType;
	
	@ManyToOne
	@JoinColumn(name = "country_payment_id", nullable = true)
	@NoCreate
	@NoModify
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale")
	private Country countryPayment;
	
	@ManyToOne
	@JoinColumn(name="unity_id")
	@Required
	@NoCreate
	@NoModify
	@DescriptionsList(descriptionProperties = "name")
	private Unity unity;
	
	//////////////////////////////////////////////////////////
	@Transient
	@ManyToOne
	@Required
	@NoCreate
	@NoModify
	@ReferenceView("Reference")
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale")
	private Person person;
	
	@Transient
	@ManyToOne
	@DescriptionsList
	@NoCreate
	@NoModify
	@ReadOnly(forViews="RequestTXInvoicePurchase, RequestTXInvoiceSale, RequestTXCreditNotePurchase, RequestTXCreditNoteSale, RequestTXOrderItems")
	private AccountStatus accountStatus;
	
	@Transient
	@ManyToOne
	@Required
	@NoCreate
	@NoModify
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale")
	@ReferenceView("Reference")
	@SearchActions({
		@SearchAction(forViews="RequestTXInvoicePurchase", value="SearchProduct.SearchInvoicePurchase"),
		@SearchAction(forViews="RequestTXInvoiceSale", value="SearchProduct.SearchInvoiceSale"),
		@SearchAction(forViews="RequestTXCreditNotePurchase", value="SearchProduct.SearchCreditNotePurchase"),
		@SearchAction(forViews="RequestTXCreditNoteSale", value="SearchProduct.SearchCreditNoteSale"),
		@SearchAction(forViews="RequestTXOrderItems", value="SearchProduct.SearchOrderPurchase"),
	})
	private Product product;
	
	@Transient
	@Temporal(TemporalType.DATE)
	@DefaultValueCalculator(com.powerfin.calculators.CurrentAccountingDateCalculator.class)
	@ReadOnly
	private Date companyAccountingDate;
	
	public AccountInvoice() {
	}

	public String getAccountId() {
		return this.accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public Date getDueDate() {
		return this.dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Date getIssueDate() {
		return this.issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Account getAccount() {
		return this.account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
	
	
	public List<AccountInvoiceDetail> getDetails() {
		return details;
	}

	public void setDetails(
			List<AccountInvoiceDetail> details) {
		this.details = details;
	}

	public Person getPerson() {
		if (account!=null)
			person = account.getPerson();
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
		onUpdateEntity();
	}
	
	public AccountStatus getAccountStatus() {
		if (account!=null)
			return account.getAccountStatus();
		else 
			return accountStatus;
	}

	public void setAccountStatus(AccountStatus accountStatus) {
		this.accountStatus = accountStatus;
		onUpdateEntity();
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

	public InvoiceTaxSupport getInvoiceTaxSupport() {
		return invoiceTaxSupport;
	}

	public void setInvoiceTaxSupport(InvoiceTaxSupport invoiceTaxSupport) {
		this.invoiceTaxSupport = invoiceTaxSupport;
	}

	public List<AccountInvoicePayment> getAccountInvoicePayments() {
		return accountInvoicePayments;
	}

	public void setAccountInvoicePayments(
			List<AccountInvoicePayment> accountInvoicePayments) {
		this.accountInvoicePayments = accountInvoicePayments;
	}

	public InvoiceVoucherType getInvoiceVoucherType() {
		return invoiceVoucherType;
	}

	public void setInvoiceVoucherType(InvoiceVoucherType invoiceVoucherType) {
		this.invoiceVoucherType = invoiceVoucherType;
	}

	public InvoiceProviderType getInvoiceProviderType() {
		return invoiceProviderType;
	}

	public void setInvoiceProviderType(InvoiceProviderType invoiceProviderType) {
		this.invoiceProviderType = invoiceProviderType;
	}

	public String getEstablishmentCode() {
		return establishmentCode;
	}

	public void setEstablishmentCode(String establishmentCode) {
		this.establishmentCode = establishmentCode;
	}

	public String getEmissionPointCode() {
		return emissionPointCode;
	}

	public void setEmissionPointCode(String emissionPointCode) {
		this.emissionPointCode = emissionPointCode;
	}

	public String getSequentialCode() {
		return sequentialCode;
	}

	public void setSequentialCode(String sequentialCode) {
		this.sequentialCode = sequentialCode;
	}

	public String getAuthorizationCode() {
		return authorizationCode;
	}

	public void setAuthorizationCode(String authorizationCode) {
		this.authorizationCode = authorizationCode;
	}

	public Types.YesNoIntegerType getPartRelated() {
		return partRelated;
	}

	public void setPartRelated(Types.YesNoIntegerType partRelated) {
		this.partRelated = partRelated;
	}

	public Types.YesNoIntegerType getDoubleTaxationConventionPayment() {
		return doubleTaxationConventionPayment;
	}

	public void setDoubleTaxationConventionPayment(
			Types.YesNoIntegerType doubleTaxationConventionPayment) {
		this.doubleTaxationConventionPayment = doubleTaxationConventionPayment;
	}

	public Types.YesNoIntegerType getUnderTheStatutePayment() {
		return underTheStatutePayment;
	}

	public void setUnderTheStatutePayment(
			Types.YesNoIntegerType underTheStatutePayment) {
		this.underTheStatutePayment = underTheStatutePayment;
	}

	public InvoicePaymentType getInvoicePaymentType() {
		return invoicePaymentType;
	}

	public void setInvoicePaymentType(InvoicePaymentType invoicePaymentType) {
		this.invoicePaymentType = invoicePaymentType;
	}

	public Country getCountryPayment() {
		return countryPayment;
	}

	public void setCountryPayment(Country countryPayment) {
		this.countryPayment = countryPayment;
	}

	public Date getCompanyAccountingDate() {
		return CompanyHelper.getCurrentAccountingDate();
	}

	public void setCompanyAccountingDate(Date companyAccountingDate) {
		this.companyAccountingDate = companyAccountingDate;
	}

	public AccountInvoice getAccountModified() {
		return accountModified;
	}

	public void setAccountModified(AccountInvoice accountModified) {
		this.accountModified = accountModified;
	}

	public Unity getUnity() {
		return unity;
	}

	public void setUnity(Unity unity) {
		this.unity = unity;
	}

	public BigDecimal getSubtotal() {
		BigDecimal value = BigDecimal.ZERO;
		for (AccountInvoiceDetail detail: details) {
			value = value.add(detail.getAmount());
		}
		return value;
	}
	
	public BigDecimal getCalculateTaxes()
	{
		BigDecimal value = BigDecimal.ZERO;

		List<AccountInvoiceTax> taxes = AccountInvoiceHelper.getCalculatedAccountInvoiceTaxes(this);

		for (AccountInvoiceTax tax : taxes)
			value = value.add(tax.getTaxAmount());
		
		return value;
	}
	
	public BigDecimal getTaxes()
	{
		BigDecimal value = BigDecimal.ZERO;

		for (AccountInvoiceTax tax: accountInvoiceTaxes) {
			if (tax.getTaxAmount()!=null)
				value = value.add(tax.getTaxAmount());
		}
		
		return value;
	}
	
	public BigDecimal getTotal() {
		return getSubtotal().add(getTaxes());
	}
	
	public BigDecimal getCalculateTotal() {
		return getSubtotal().add(getCalculateTaxes());
	}
	
	public BigDecimal getBalance() throws Exception {
		return BalanceHelper.getBalance(getAccount().getAccountId());
	}
}