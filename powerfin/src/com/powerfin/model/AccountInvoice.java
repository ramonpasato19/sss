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
	@View(members="#accountId, companyAccountingDate; accountStatus; "
				+ "branch, unity; "
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
		members="#accountId, companyAccountingDate; accountStatus; "
				+ "branch, unity;"
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
				+ "documents{electronicDocument;}"
				),
	@View(name="PurchaseInvoiceReport", 
		members="#accountId, companyAccountingDate; accountStatus; "
				+ "branch, unity;"
				+ "issueDate, dueDate;"
				+ "taxDeclaration{invoiceTaxSupport;invoiceVoucherType;invoiceProviderType; partRelated;}"
				+ "person{person;}"
				+ "product{product;}"
				+ "voucher{establishmentCode; emissionPointCode; sequentialCode; authorizationCode;}"
				+ "paymentType{invoicePaymentType;countryPayment;doubleTaxationConventionPayment;underTheStatutePayment;}"
				+ "paymentMethods{accountInvoicePayments;}"
				+ "detail{details;}"
				+ "remark{remark;}"
				+ "documents{physicalDocument; electronicDocument;}"
				),
	@View(name="InvoiceSale", 
		members="#accountId, companyAccountingDate; accountStatus; "
				+ "branch, unity;"
				+ "issueDate, dueDate;"
				+ "person{person;}"
				+ "product{product;}"
				+ "voucher{invoiceVoucherType;establishmentCode; emissionPointCode; sequentialCode; authorizationCode;}"
				+ "remark{remark;}"
				+ "detail{details;}"
				+ "balances{balance;}"
				+ "documents{electronicDocument;}"
				),		
	@View(name="RequestTXInvoicePurchase", 
		members="#accountId, companyAccountingDate; accountStatus;"
				+ "branch, unity;"
				+ "issueDate, dueDate; "
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
		members="info{accountId, companyAccountingDate; accountStatus; branch; "
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
		members="#accountId, companyAccountingDate; accountStatus; "
				+ "branch, unity;"
				+ "issueDate, dueDate; "
				+ "person{person;}"
				+ "product{product;}"
				+ "voucher{invoiceVoucherType;establishmentCode; emissionPointCode; sequentialCode; authorizationCode;}"
				+ "remark{remark;}"
				+ "detail{details;}"
				+ "Invoices{invoices}"
				),
	@View(name="IssueElectronicInvoiceSale", 
		members="info{accountId, companyAccountingDate; accountStatus; branch; "
				+ "issueDate, dueDate;}"
				+ "person{person;}"
				+ "product{product;}"
				+ "voucher{invoiceVoucherType;establishmentCode; emissionPointCode; sequentialCode; authorizationCode;}"
				+ "remark{remark;}"
				+ "detail{details;}"
				+ "documents{physicalDocument; electronicDocument;}"
				),
	@View(name="AuthorizeTXInvoiceSale", 
		members="info{accountId, companyAccountingDate; accountStatus; branch; "
				+ "issueDate, dueDate;}"
				+ "voucher{invoiceVoucherType;establishmentCode; emissionPointCode; sequentialCode; authorizationCode;}"
				+ "remark{remark;}"
				+ "detail{details;}"
				),
	@View(name="RequestTXCreditNotePurchase", 
		members="#accountId, companyAccountingDate; accountStatus; "
				+ "branch, unity;"
				+ "issueDate;"
				+ "accountModified;"
				+ "person{person;}"
				+ "product{product;}"
				+ "voucher{invoiceVoucherType;establishmentCode; emissionPointCode; sequentialCode; authorizationCode;}"
				+ "remark{remark;}"
				+ "detail{details;}"
				),		
	@View(name="AuthorizeTXCreditNotePurchase", 
		members="info{accountId, companyAccountingDate; accountStatus; branch; "
				+ "issueDate, dueDate;}"
				+ "accountModified;"
				+ "person{person;}"
				+ "product{product;}"
				+ "voucher{invoiceVoucherType;establishmentCode; emissionPointCode; sequentialCode; authorizationCode;}"
				+ "remark{remark;}"
				+ "detail{details;}"
				),
	@View(name="RequestTXCreditNoteSale", 
		members="#accountId, companyAccountingDate; accountStatus; "
				+ "branch, unity;"
				+ "issueDate;"
				+ "accountModified;"
				+ "person{person;}"
				+ "product{product;}"
				+ "voucher{invoiceVoucherType;establishmentCode; emissionPointCode; sequentialCode; authorizationCode;}"
				+ "remark{remark;}"
				+ "detail{details;}"
				),		
	@View(name="AuthorizeTXCreditNoteSale", 
		members="info{accountId, companyAccountingDate; accountStatus; branch; "
				+ "issueDate, dueDate;}"
				+ "accountModified;"
				+ "person{person;}"
				+ "product{product;}"
				+ "voucher{invoiceVoucherType;establishmentCode; emissionPointCode; sequentialCode; authorizationCode;}"
				+ "remark{remark;}"
				+ "detail{details;}"
				),
	@View(name="PrintTXInvoiceSale", 
		members="#accountId; "
				+ "branch, unity;"
				+ "issueDate, dueDate;"
				+ "voucher{invoiceVoucherType;establishmentCode; emissionPointCode; sequentialCode; authorizationCode;}"
				+ "remark{remark;}"
				+ "detail{details;}"
			),
	@View(name="InvoiceActiveReport", 
		members="#accountId, companyAccountingDate; accountStatus;"
				+ "branch, unity;"
				+ "issueDate, dueDate;"
				+ "taxDeclaration{invoiceTaxSupport;invoiceVoucherType;invoiceProviderType; partRelated;}"
				+ "person{person;}"
				+ "product{product;}"
				+ "voucher{establishmentCode; emissionPointCode; sequentialCode; authorizationCode;}"
				+ "paymentType{invoicePaymentType;countryPayment;doubleTaxationConventionPayment;underTheStatutePayment;}"
				+ "paymentMethods{accountInvoicePayments;}"
				+ "detail{details;}"
				+ "remark{remark;}"
				+ "documents{physicalDocument; electronicDocument;}"
				),
	@View(name="RequestTXOrderItems", 
		members="#accountId, companyAccountingDate; accountStatus;"
				+ "branch, unity;"
				+ "issueDate;"
				+ "person{person;}"
				+ "product{product;}"
				+ "remark{sequentialCode; remark;}"
				+ "detail{details;}"
				),		
	@View(name="AuthorizeTXOrderItems", 
		members="#accountId, companyAccountingDate; accountStatus;"
				+ "branch, unity;"
				+ "issueDate,"
				+ "person{person;}"
				+ "product{product;}"
				+ "remark{sequentialCode; remark;}"
				+ "detail{details;}"
				),		
	@View(name="TXOrderToInvoice", 
		members="#accountId, companyAccountingDate;"
				+ "branch, unity;"
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
			+ "subtotal, taxes, total; quantityAccountsItems"),
	@View(name="forCreditNote", 
			members="accountId;accountStatus;"
			+ "establishmentCode, emissionPointCode, sequentialCode;"
			+ "subtotal, taxes, total"),
	@View(name="selectInvoice", 
			members="accountId;accountStatus;"
			+ "establishmentCode, emissionPointCode, sequentialCode")
})
@Tabs({
	@Tab(properties="account.accountId, account.branch.name, account.currency.currencyId, account.person.name, account.code, account.accountStatus.name, account.product.name, issueDate"),
	
	@Tab(name="TXInvoicePurchase", properties="account.accountId, account.branch.name, account.currency.currencyId, account.person.name, account.code, issueDate",
		baseCondition = "${account.accountStatus.accountStatusId} = '001' "
			+ "and ${account.product.productType.productTypeId} ='"+AccountInvoiceHelper.INVOICE_PURCHASE_PRODUCT_TYPE_ID+"'"),
	@Tab(name="InvoicePurchase", properties="account.accountId, account.branch.name, account.currency.currencyId, account.person.name, account.code, account.accountStatus.name, account.product.name, issueDate, balance",
		baseCondition = "${account.product.productType.productTypeId} ='"+AccountInvoiceHelper.INVOICE_PURCHASE_PRODUCT_TYPE_ID+"'"),
	
	@Tab(name="TXInvoiceSale", properties="account.accountId, account.branch.name, account.currency.currencyId, account.person.name, account.code, account.externalCode, issueDate",
		baseCondition = "${account.accountStatus.accountStatusId} = '001' "
			+ "and ${account.product.productType.productTypeId} ='"+AccountInvoiceHelper.INVOICE_SALE_PRODUCT_TYPE_ID+"'"),
	@Tab(name="IssueElectronicInvoiceSale", properties="account.accountId, account.branch.name, account.currency.currencyId, account.person.name, account.code, account.externalCode, issueDate",
	baseCondition = "${account.accountStatus.accountStatusId} = '001' AND ${account.operatingCondition.operatingConditionId} != 'ELE' "
		+ "and ${account.product.productType.productTypeId} ='"+AccountInvoiceHelper.INVOICE_SALE_PRODUCT_TYPE_ID+"'"),
	@Tab(name="InvoiceSale", properties="account.accountId, account.branch.name, account.currency.currencyId, account.person.name, account.code, account.accountStatus.name, account.product.name, issueDate",
		baseCondition = "${account.product.productType.productTypeId} ='"+AccountInvoiceHelper.INVOICE_SALE_PRODUCT_TYPE_ID+"'"),
	@Tab(name="PrinterInvoiceSale", properties="account.accountId, account.branch.name, account.currency.currencyId, account.person.name, account.code, issueDate",
	baseCondition = "${account.accountStatus.accountStatusId} IN ('002','005') "
		+ "and ${account.product.productType.productTypeId} ='"+AccountInvoiceHelper.INVOICE_SALE_PRODUCT_TYPE_ID+"'"),
	
	@Tab(name="TXCreditNotePurchase", properties="account.accountId, account.branch.name, account.currency.currencyId, account.person.name, account.code, issueDate",
	baseCondition = "${account.accountStatus.accountStatusId} = '001' "
			+ "and ${account.product.productType.productTypeId} ='"+AccountInvoiceHelper.CREDIT_NOTE_PURCHASE_PRODUCT_TYPE_ID+"'"),
	@Tab(name="CreditNotePurchase", properties="account.accountId, account.branch.name, account.currency.currencyId, account.person.name, account.code, account.accountStatus.name, account.product.name, issueDate, balance",
	baseCondition = "${account.product.productType.productTypeId} ='"+AccountInvoiceHelper.CREDIT_NOTE_PURCHASE_PRODUCT_TYPE_ID+"'"),
	
	@Tab(name="TXCreditNoteSale", properties="account.accountId, account.branch.name, account.currency.currencyId, account.person.name, account.code, issueDate",
		baseCondition = "${account.accountStatus.accountStatusId} = '001' "
			+ "and ${account.product.productType.productTypeId} ='"+AccountInvoiceHelper.CREDIT_NOTE_SALE_PRODUCT_TYPE_ID+"'"),
	@Tab(name="CreditNoteSale", properties="account.accountId, account.branch.name, account.currency.currencyId, account.person.name, account.code, account.accountStatus.name, account.product.name, issueDate",
		baseCondition = "${account.product.productType.productTypeId} ='"+AccountInvoiceHelper.CREDIT_NOTE_SALE_PRODUCT_TYPE_ID+"'"),
	
	@Tab(name="TXOrderItems", properties="account.accountId, account.branch.name, account.currency.currencyId, account.person.name, account.code, issueDate",
		baseCondition = "${account.accountStatus.accountStatusId} = '001' "
			+ "and ${account.product.productType.productTypeId} ='"+AccountInvoiceHelper.ORDER_PURCHASE_PRODUCT_TYPE_ID+"'"),
	@Tab(name="TXConvertOrderToInvoice", properties="account.accountId, account.branch.name, account.currency.currencyId, account.person.name, account.code, issueDate",
		baseCondition = "${account.accountStatus.accountStatusId} = '002' "
			+ "and ${account.product.productType.productTypeId} ='"+AccountInvoiceHelper.ORDER_PURCHASE_PRODUCT_TYPE_ID+"'"),
	@Tab(name="TXConvertInvoicePurchaseToSale", properties="account.accountId, account.branch.name, account.currency.currencyId, account.person.name, account.code, issueDate",
		baseCondition = "${account.accountStatus.accountStatusId} in ('002','005') "
			+ "and ${account.product.productType.productTypeId} ='"+AccountInvoiceHelper.INVOICE_PURCHASE_PRODUCT_TYPE_ID+"'")
})
public class AccountInvoice extends AuditEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="account_id", unique=true, nullable=false)
	@ReadOnly(notForViews="forRetention, forCreditNote")
	@Hidden
	@DisplaySize(25)
	private String accountId;
	
	@Temporal(TemporalType.DATE)
	@Column(name="due_date")
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale, IssueElectronicInvoiceSale")
	private Date dueDate;

	@Temporal(TemporalType.DATE)
	@Column(name="issue_date", nullable=false)
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale, IssueElectronicInvoiceSale")
	@Required
	private Date issueDate;

	@Column(length=400)
	@Required
	@Stereotype("TEXT_AREA")
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale, IssueElectronicInvoiceSale")
	private String remark;

	@Column(name="establishment_code", nullable=true, length=5)
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale, IssueElectronicInvoiceSale")
	private String establishmentCode;
	
	@Column(name="emission_point_code", nullable=true, length=5)
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale, IssueElectronicInvoiceSale")
	private String emissionPointCode;

	@Column(name="sequential_code", nullable=true, length=50)
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale, IssueElectronicInvoiceSale")
	private String sequentialCode;
    
	@Column(name="authorization_code", nullable=true, length=50)
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale, IssueElectronicInvoiceSale")
	private String authorizationCode;
	
	@Column(name="part_related", nullable=true)
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale, IssueElectronicInvoiceSale")
	private Types.YesNoIntegerType partRelated;
	
	@Column(name="double_taxation_convention_payment", nullable=true)
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale, IssueElectronicInvoiceSale")
	private Types.YesNoIntegerType doubleTaxationConventionPayment;
	
	@Column(name="under_the_statute_payment", nullable=true)
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale, IssueElectronicInvoiceSale")
	private Types.YesNoIntegerType underTheStatutePayment;
		
	@ManyToOne
	@JoinColumn(name = "account_modified_id", nullable = true)
	@NoCreate
	@NoModify
	@ReferenceView("forCreditNote")
	@ReadOnly(forViews="AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale, IssueElectronicInvoiceSale")
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
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale, AuthorizeTXOrderItems, TXOrderToInvoice, IssueElectronicInvoiceSale")
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
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale, IssueElectronicInvoiceSale")
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
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale, IssueElectronicInvoiceSale")
	private InvoiceTaxSupport invoiceTaxSupport;
	
	@ManyToOne
	@JoinColumn(name = "invoice_voucher_type_id", nullable = true)
	@NoCreate
	@NoModify
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale, IssueElectronicInvoiceSale")
	private InvoiceVoucherType invoiceVoucherType;
	
	@ManyToOne
	@JoinColumn(name = "invoice_provider_type_id", nullable = true)
	@NoCreate
	@NoModify
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale, IssueElectronicInvoiceSale")
	@DescriptionsList(descriptionProperties="name")
	private InvoiceProviderType invoiceProviderType;
	
	@ManyToOne
	@JoinColumn(name = "invoice_payment_type_id", nullable = true)
	@NoCreate
	@NoModify
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale, IssueElectronicInvoiceSale")
	@DescriptionsList(descriptionProperties="name")
	private InvoicePaymentType invoicePaymentType;
	
	@ManyToOne
	@JoinColumn(name = "country_payment_id", nullable = true)
	@NoCreate
	@NoModify
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale, IssueElectronicInvoiceSale")
	private Country countryPayment;
	
	@ManyToOne
	@JoinColumn(name="unity_id")
	@NoCreate
	@NoModify
	@DescriptionsList(descriptionProperties = "name")
	private Unity unity;
	
	@ManyToOne
	@JoinColumn(name="pos_id")
	@NoCreate
	@NoModify
	@DescriptionsList(descriptionProperties = "name")
	private Pos pos;
	
	//////////////////////////////////////////////////////////
	
	@Transient
	@Stereotype("FILE")
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale, IssueElectronicInvoiceSale")
	private String electronicDocument;
	
	@Transient
	@Stereotype("FILE")
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale, IssueElectronicInvoiceSale")
	private String physicalDocument;
	
	@Transient
	@ManyToOne
	@Required
	@NoCreate
	@NoModify
	@ReferenceView("Reference")
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale, IssueElectronicInvoiceSale")
	private Person person;
	
	@Transient
	@ManyToOne
	@DescriptionsList
	@NoCreate
	@NoModify
	@ReadOnly(forViews="RequestTXInvoicePurchase, RequestTXInvoiceSale, RequestTXCreditNotePurchase, RequestTXCreditNoteSale, RequestTXOrderItems, IssueElectronicInvoiceSale")
	private AccountStatus accountStatus;
	
	@Transient
	@ManyToOne
	@DescriptionsList(descriptionProperties="branchId, name")
	@NoCreate
	@NoModify
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale, IssueElectronicInvoiceSale")
	private Branch branch;
	
	@Transient
	@ManyToOne
	@Required
	@NoCreate
	@NoModify
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale, IssueElectronicInvoiceSale")
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
	
	@Transient
	@ManyToOne
	@NoCreate
	@NoModify
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale, AuthorizeTXCreditNotePurchase, AuthorizeTXCreditNoteSale, IssueElectronicInvoiceSale")
	@ReferenceView("forRetention")
	@Actions({
		@Action(forViews="RequestTXInvoiceSale", value = "ConvertInvoicePurchaseToSale.import", alwaysEnabled=true ),
	})
	@SearchActions({
		@SearchAction(forViews="RequestTXInvoiceSale", value="SearchAccount.SearchInvoicePurchaseActiveCancel"),
	})
	private AccountInvoice invoices;
	
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

	public Branch getBranch() {
		if (account!=null)
			return account.getBranch();
		else 
			return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
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

	public List<AccountInvoiceTax> getAccountInvoiceTaxes() {
		return accountInvoiceTaxes;
	}

	public void setAccountInvoiceTaxes(List<AccountInvoiceTax> accountInvoiceTaxes) {
		this.accountInvoiceTaxes = accountInvoiceTaxes;
	}
	
	public Unity getUnity() {
		return unity;
	}

	public void setUnity(Unity unity) {
		this.unity = unity;
	}

	public Pos getPos() {
		return pos;
	}

	public void setPos(Pos pos) {
		this.pos = pos;
	}

	public BigDecimal getSubtotal() throws Exception {
		return AccountInvoiceHelper.getSubtotal(this);
	}
	
	public BigDecimal getCalculateTaxes() throws Exception
	{
		return AccountInvoiceHelper.getCalculateTaxes(this);
	}
	
	public BigDecimal getDiscount() throws Exception
	{
		return AccountInvoiceHelper.getDiscount(this);
	}
	
	public BigDecimal getTaxes() throws Exception
	{
		return AccountInvoiceHelper.getTaxes(this);
	}
	
	public BigDecimal getTotal() throws Exception {
		return getSubtotal().add(getTaxes());
	}
	
	public BigDecimal getCalculateTotal() throws Exception {
		return getSubtotal().add(getCalculateTaxes());
	}
	
	public BigDecimal getBalance() throws Exception {
		return BalanceHelper.getBalance(getAccount().getAccountId());
	}
	
	public AccountInvoice getInvoices() {
		return invoices;
	}
	
	public void setInvoices(AccountInvoice invoices) {
		this.invoices = invoices;
	}

	public int getQuantityAccountsItems() {
		return details.size();				
	}

	public String getElectronicDocument() {
		if (account!=null)
			return account.getElectronicDocument();
		else
			return null;
	}

	public void setElectronicDocument(String electronicDocument) {
		this.electronicDocument = electronicDocument;
	}

	public String getPhysicalDocument() {
		if (account!=null)
			return account.getPhysicalDocument();
		else
			return null;
	}

	public void setPhysicalDocument(String physicalDocument) {
		this.physicalDocument = physicalDocument;
	}
	
	
}