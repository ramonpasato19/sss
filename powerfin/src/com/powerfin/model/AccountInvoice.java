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
					+ "issueDate, dueDate;"
					+ "person{person;}"
					+ "product{product;}"
					+ "voucher{establishmentCode; emissionPointCode; sequentialCode; authorizationCode;}"
					//+ "voucher{invoiceVoucherType;establishmentCode; sequentialCode; }"
					+ "remark{remark;}"
					+ "taxDeclaration{invoiceTaxSupport;invoiceVoucherType;invoiceProviderType; partRelated;}"
					+ "paymentType{invoicePaymentType;countryPayment;doubleTaxationConventionPayment;underTheStatutePayment;}"
					+ "paymentMethods{accountInvoicePayments;}"
					+ "detail{details;}"
					),		
	@View(name="AuthorizeTXInvoicePurchase", 
			members="accountId, companyAccountingDate; accountStatus;"
					+ "issueDate, dueDate;"
					+ "person{person;}"
					+ "product{product;}"
					+ "voucher{establishmentCode; emissionPointCode; sequentialCode; authorizationCode;}"
					//+ "voucher{invoiceVoucherType;establishmentCode; sequentialCode; }"
					+ "remark{remark;}"
					+ "taxDeclaration{invoiceTaxSupport;invoiceVoucherType;invoiceProviderType; partRelated;}"
					//+ "taxDeclaration{invoiceVoucherType}"
					+ "paymentType{invoicePaymentType;countryPayment;doubleTaxationConventionPayment;underTheStatutePayment;}"
					+ "paymentMethods{accountInvoicePayments;}"
					+ "detail{details;}"
					),
					
	@View(name="RequestTXInvoiceSale", 
			members="accountId, companyAccountingDate; accountStatus;"
					+ "issueDate, dueDate;"
					+ "person{person;}"
					+ "product{product;}"
					+ "voucher{invoiceVoucherType;establishmentCode; emissionPointCode; sequentialCode; authorizationCode;}"
					+ "remark{remark;}"
					+ "detail{details;}"
					),		
	@View(name="AuthorizeTXInvoiceSale", 
			//members="accountId, companyAccountingDate; accountStatus;"
			members=""
					+ "issueDate, dueDate;"
					//+ "person{person;}"
					//+ "product{product;}"
					+ "voucher{invoiceVoucherType;establishmentCode; emissionPointCode; sequentialCode; authorizationCode;}"
					+ "remark{remark;}"
					+ "detail{details;}"
					),
	@View(name="reference", members="account;"
			+ "dueDate, issueDate;"),
	@View(name="forRetention", 
			members="accountId;accountStatus;"
			+ "establishmentCode, emissionPointCode, sequentialCode;"
			+ "subtotal, vat, total")
})
@Tabs({
	@Tab(properties="account.accountId, account.currency.currencyId, account.person.name, account.code, account.accountStatus.name, account.product.name, issueDate, subtotal, vat, total"),
	@Tab(name="TXInvoicePurchase", properties="account.accountId, account.currency.currencyId, account.person.name, account.code, issueDate, subtotal, vat, total",
		baseCondition = "${account.accountStatus.accountStatusId} = '001' "
			+ "and ${account.product.productType.productTypeId} ='"+AccountInvoiceHelper.INVOICE_PURCHASE_PRODUCT_TYPE_ID+"'"),
	@Tab(name="InvoicePurchase", properties="account.accountId, account.currency.currencyId, account.person.name, account.code, account.accountStatus.name, account.product.name, issueDate, balance",
		baseCondition = "${account.product.productType.productTypeId} ='"+AccountInvoiceHelper.INVOICE_PURCHASE_PRODUCT_TYPE_ID+"'"),
	@Tab(name="TXInvoiceSale", properties="account.accountId, account.currency.currencyId, account.person.name, account.code, issueDate, subtotal, vat, total",
		baseCondition = "${account.accountStatus.accountStatusId} = '001' "
			+ "and ${account.product.productType.productTypeId} ='"+AccountInvoiceHelper.INVOICE_SALE_PRODUCT_TYPE_ID+"'"),
	@Tab(name="InvoiceSale", properties="account.accountId, account.currency.currencyId, account.person.name, account.code, account.accountStatus.name, account.product.name, issueDate",
		baseCondition = "${account.product.productType.productTypeId} ='"+AccountInvoiceHelper.INVOICE_SALE_PRODUCT_TYPE_ID+"'"),
})
public class AccountInvoice extends AuditEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="account_id", unique=true, nullable=false)
	@ReadOnly(notForViews="forRetention")
	@Hidden
	private String accountId;
	
	@Temporal(TemporalType.DATE)
	@Column(name="due_date")
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale")
	private Date dueDate;

	@Temporal(TemporalType.DATE)
	@Column(name="issue_date", nullable=false)
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale")
	@Required
	private Date issueDate;

	@Column(length=400)
	@Required
	@Stereotype("TEXT_AREA")
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale")
	private String remark;

	@Column(name="establishment_code", nullable=true, length=5)
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale")
	private String establishmentCode;
	
	@Column(name="emission_point_code", nullable=true, length=5)
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale")
	private String emissionPointCode;

	@Column(name="sequential_code", nullable=true, length=50)
	@Required
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale")
	private String sequentialCode;
    
	@Column(name="authorization_code", nullable=true, length=37)
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale")
	private String authorizationCode;
	
	@Column(name="part_related", nullable=true)
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale")
	private Types.YesNoIntegerType partRelated;
	
	@Column(name="double_taxation_convention_payment", nullable=true)
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale")
	private Types.YesNoIntegerType doubleTaxationConventionPayment;
	
	@Column(name="under_the_statute_payment", nullable=true)
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale")
	private Types.YesNoIntegerType underTheStatutePayment;
		
	@OneToMany(mappedBy="accountInvoice", cascade = CascadeType.ALL)
	@AsEmbedded
	@ListProperties("accountDetail.accountId, accountDetail.name, unitPrice, quantity,"
			+ "amount[accountInvoice.subtotal, "
			//+ "amount[accountInvoice.subtotal, accountInvoice.subtotalZeroVat, "
			//+ "accountInvoice.subtotalNotVat, accountInvoice.subtotalExcVat, accountInvoice.subtotalXXXVat, "
			+ " accountInvoice.vat, accountInvoice.total]"
			)
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale")
	@CollectionViews({
		@CollectionView(forViews="RequestTXInvoicePurchase, AuthorizeTXInvoicePurchase", value = "InvoicePurchase"),
		@CollectionView(forViews="RequestTXInvoiceSale, AuthorizeTXInvoiceSale", value = "InvoiceSale")
	})
	@NewActions({
		@NewAction(forViews="RequestTXInvoicePurchase", value="AccountInvoiceActions.createNewInvoiceDetailToInvoice"),
		@NewAction(forViews="RequestTXInvoiceSale", value="AccountInvoiceActions.createNewInvoiceDetailToInvoice")
	})
	private List<AccountInvoiceDetail> details;
	
	@OneToMany(mappedBy="accountInvoice", cascade = CascadeType.ALL)
	@AsEmbedded
	@ListProperties("invoicePaymentMethod.name")
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale")
	private List<AccountInvoicePayment> accountInvoicePayments;

	//bi-directional one-to-one association to Account
	@OneToOne
	@JoinColumn(name="account_id", nullable=false, insertable=false, updatable=false)
	private Account account;
	
	@ManyToOne
	@JoinColumn(name = "invoice_tax_support_id", nullable = true)
	@NoCreate
	@NoModify
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale")
	private InvoiceTaxSupport invoiceTaxSupport;
	
	@ManyToOne
	@JoinColumn(name = "invoice_voucher_type_id", nullable = true)
	@NoCreate
	@NoModify
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale")
	private InvoiceVoucherType invoiceVoucherType;
	
	@ManyToOne
	@JoinColumn(name = "invoice_provider_type_id", nullable = true)
	@NoCreate
	@NoModify
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale")
	@DescriptionsList(descriptionProperties="name")
	private InvoiceProviderType invoiceProviderType;
	
	@ManyToOne
	@JoinColumn(name = "invoice_payment_type_id", nullable = true)
	@NoCreate
	@NoModify
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale")
	@DescriptionsList(descriptionProperties="name")
	private InvoicePaymentType invoicePaymentType;
	
	@ManyToOne
	@JoinColumn(name = "country_payment_id", nullable = true)
	@NoCreate
	@NoModify
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale")
	private Country countryPayment;
	
	//////////////////////////////////////////////////////////
	@Transient
	@ManyToOne
	@Required
	@NoCreate
	@NoModify
	@ReferenceView("Reference")
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale")
	private Person person;
	
	@Transient
	@ManyToOne
	@DescriptionsList
	@NoCreate
	@NoModify
	@ReadOnly(forViews="RequestTXInvoicePurchase, RequestTXInvoiceSale")
	private AccountStatus accountStatus;
	
	@Transient
	@ManyToOne
	@Required
	@NoCreate
	@NoModify
	@ReadOnly(forViews="AuthorizeTXInvoicePurchase, AuthorizeTXInvoiceSale")
	@ReferenceView("Reference")
	@SearchActions({
		@SearchAction(forViews="RequestTXInvoicePurchase", value="SearchProduct.SearchInvoicePurchase"),
		@SearchAction(forViews="RequestTXInvoiceSale", value="SearchProduct.SearchInvoiceSale")
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

	public BigDecimal getSubtotal() {
		BigDecimal value = BigDecimal.ZERO;
		for (AccountInvoiceDetail detail: details) {
			value = value.add(detail.getAmount());
			/*
			for (AccountInvoiceDetailTax detailTax: detail.getTaxes())
			{
				value = value.add(detailTax.getTaxBase());
			}
			*/
		}
		return value;
	}
	
	public BigDecimal getSubtotalNotVat() {
		BigDecimal value = BigDecimal.ZERO;
		for (AccountInvoiceDetail detail: details) {
			if (detail.getTax().getTaxId().equals("IVANOT"))
				value = value.add(detail.getAmount());
			/*
			for (AccountInvoiceDetailTax detailTax: detail.getTaxes())
			{
				if (detailTax.getTax().getTaxId().equals("IVANOT"))
					value = value.add(detailTax.getTaxBase());
			}*/
		}
		return value;
	}
	
	public BigDecimal getSubtotalExcVat() {
		BigDecimal value = BigDecimal.ZERO;
		for (AccountInvoiceDetail detail: details) {
			if (detail.getTax().getTaxId().equals("IVAEXC"))
				value = value.add(detail.getAmount());
			/*
			for (AccountInvoiceDetailTax detailTax: detail.getTaxes())
			{
				if (detailTax.getTax().getTaxId().equals("IVAEXC"))
					value = value.add(detailTax.getTaxBase());
			}*/
		}
		return value;
	}
	
	public BigDecimal getSubtotalZeroVat() {
		BigDecimal value = BigDecimal.ZERO;
		for (AccountInvoiceDetail detail: details) {
			if (detail.getTax().getTaxId().equals("IVA0"))
				value = value.add(detail.getAmount());
			/*
			for (AccountInvoiceDetailTax detailTax: detail.getTaxes())
			{
				if (detailTax.getTax().getTaxId().equals("IVA0"))
					value = value.add(detailTax.getTaxBase());
			}*/
		}
		return value;
	}

	public BigDecimal getSubtotalXXXVat() {
		BigDecimal value = BigDecimal.ZERO;
		for (AccountInvoiceDetail detail: details) {
			if (detail.getTaxAmount()!=null)
				value = value.add(detail.getAmount());
			/*
			for (AccountInvoiceDetailTax detailTax: detail.getTaxes())
			{
				if (detailTax.getTax().getTaxId().equals("IVA12"))
					value = value.add(detailTax.getTaxBase());
			}*/
		}
		return value;
	}	
	
	public BigDecimal getVat()
	{
		BigDecimal value = BigDecimal.ZERO;
		for (AccountInvoiceDetail detail: details) {
			if (detail.getTaxAmount()!=null)
				value = value.add(detail.getTaxAmount());
			/*
			for (AccountInvoiceDetailTax detailTax: detail.getTaxes())
			{
				if (detailTax.getTax().getTaxId().equals("IVA12"))
					value = value.add(detailTax.getAmount());
			}*/
		}
		return value;
	}
	public BigDecimal getTotal() {
		BigDecimal value = BigDecimal.ZERO;
		for (AccountInvoiceDetail detail: details) {
			value = value.add(detail.getFinalAmount());
		}
		return value;
	}
	
	public BigDecimal getBalance() {
		return BalanceHelper.getBalance(getAccount().getAccountId());
	}
}