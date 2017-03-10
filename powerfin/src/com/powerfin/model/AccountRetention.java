package com.powerfin.model;

import java.math.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import com.powerfin.helper.*;
import com.powerfin.model.superclass.*;

/**
 * The persistent class for the account_invoice_retention database table.
 * 
 */
@Entity
@Table(name = "account_retention")
@Views({
@View(members = "accountId;accountStatus;issueDate;"
		+ "accountInvoice;"
		+ "retention_info{establishmentCode; emissionPointCode; sequentialCode; authorizationCode};"
		+ "product{product};"		
		+ "detail{details}"),
@View(name="RequestTXRetentionPurchase", members = "accountId, companyAccountingDate;accountStatus;issueDate;"
		+ "accountInvoice;"
		+ "voucher{establishmentCode; emissionPointCode; sequentialCode; authorizationCode};"
		+ "product{product};"
		+ "detail{details}"),
@View(name="AuthorizeTXRetentionPurchase", members = "issueDate;"
		+ "accountInvoice{accountInvoice};"
		+ "voucher{establishmentCode; emissionPointCode; sequentialCode; authorizationCode};"
		+ "product{product};"
		+ "detail{details}"),
@View(name="RequestTXRetentionSale", members = "accountId, companyAccountingDate;accountStatus;issueDate;"
		+ "accountInvoice;"
		+ "voucher{establishmentCode; emissionPointCode; sequentialCode; authorizationCode};"
		+ "product{product};"
		+ "detail{details}"),
@View(name="AuthorizeTXRetentionSale", members = "issueDate;"
		+ "accountInvoice{accountInvoice};"
		+ "voucher{establishmentCode; emissionPointCode; sequentialCode; authorizationCode};"
		+ "product{product};"
		+ "detail{details}")
})
@Tabs({
	@Tab(properties="account.accountId, account.person.name, issueDate, total, account.accountStatus.name"),
	@Tab(name="TXRetentionPurchase", properties="account.accountId, account.person.name, issueDate, total",
			baseCondition = "${account.accountStatus.accountStatusId} = '001' "
					+ "and ${account.product.productType.productTypeId} ='"+AccountInvoiceHelper.RETENTION_PURCHASE_PRODUCT_TYPE_ID+"'"),
	@Tab(name="TXRetentionSale", properties="account.accountId, account.person.name, issueDate, total",
			baseCondition = "${account.accountStatus.accountStatusId} = '001' "
			+ "and ${account.product.productType.productTypeId} ='"+AccountInvoiceHelper.RETENTION_SALE_PRODUCT_TYPE_ID+"'")
})
public class AccountRetention extends AuditEntity {

	@Id
	@Column(name="account_id", unique=true, nullable=false)
	@Hidden
	@ReadOnly
	private String accountId;

	//bi-directional one-to-one association to Account
	@OneToOne
	@JoinColumn(name="account_id", nullable=false, insertable=false, updatable=false)
	private Account account;
	
	@Temporal(TemporalType.DATE)
	@Column(name="issue_date", nullable=false)
	@ReadOnly(forViews="AuthorizeTXRetentionPurchase, AuthorizeTXRetentionSale")
	@Required
	private Date issueDate;
	
	@ManyToOne
	@JoinColumn(name = "account_invoice_id", nullable = true)
	@Required
	@NoCreate
	@NoModify
	@ReferenceView("forRetention")
	@ReadOnly(forViews="AuthorizeTXRetentionPurchase, AuthorizeTXRetentionSale")
	@SearchActions({
		@SearchAction(forViews="RequestTXRetentionPurchase", value="SearchAccount.SearchActiveInvoicePurchase"),
		@SearchAction(forViews="RequestTXRetentionSale", value="SearchAccount.SearchActiveInvoiceSale"),
	})
	private AccountInvoice accountInvoice;

	@Column(name="establishment_code", nullable=false, length=3)
	@Required
	@ReadOnly(forViews="AuthorizeTXRetentionPurchase, AuthorizeTXRetentionSale")
	private String establishmentCode;
	
	@Column(name="emission_point_code", nullable=false, length=3)
	@Required
	@ReadOnly(forViews="AuthorizeTXRetentionPurchase, AuthorizeTXRetentionSale")
	private String emissionPointCode;

	@Column(name="sequential_code", nullable=false, length=9)
	@Required
	@ReadOnly(forViews="AuthorizeTXRetentionPurchase, AuthorizeTXRetentionSale")
	private String sequentialCode;
    
	@Column(name="authorization_code", nullable=false, length=37)
	@Required
	@ReadOnly(forViews="AuthorizeTXRetentionPurchase, AuthorizeTXRetentionSale")
	private String authorizationCode;
    
	@OneToMany(mappedBy="accountRetention", cascade = CascadeType.ALL)
	@AsEmbedded
	@ListProperties("retentionConcept.retentionConceptId, amount, taxPercentage, finalAmount")
	@ReadOnly(forViews="AuthorizeTXRetentionPurchase, AuthorizeTXRetentionSale")
	@NewAction("AccountRetentionActions.createNewRetentionDetailToRetention")
	private List<AccountRetentionDetail> details;
	
	@Transient
	@ManyToOne
	@DescriptionsList
	@NoCreate
	@NoModify
	@ReadOnly(forViews="RequestTXRetentionPurchase, RequestTXRetentionSale")
	private AccountStatus accountStatus;
	
	@Transient
	@ManyToOne
	@Required
	@NoCreate
	@NoModify
	@ReadOnly(forViews="AuthorizeTXRetentionPurchase, AuthorizeTXRetentionSale")
	@ReferenceView("Reference")
	@SearchActions({
		@SearchAction(forViews="RequestTXRetentionPurchase", value="SearchProduct.SearchRetentionPurchase"),
		@SearchAction(forViews="RequestTXRetentionSale", value="SearchProduct.SearchRetentionSale")
	})
	private Product product;
	
	@Transient
	@Temporal(TemporalType.DATE)
	@DefaultValueCalculator(com.powerfin.calculators.CurrentAccountingDateCalculator.class)
	@ReadOnly
	private Date companyAccountingDate;
	
	public AccountRetention() {
		
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public AccountInvoice getAccountInvoice() {
		return accountInvoice;
	}

	public void setAccountInvoice(AccountInvoice accountInvoice) {
		this.accountInvoice = accountInvoice;
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

	public List<AccountRetentionDetail> getDetails() {
		return details;
	}

	public void setDetails(List<AccountRetentionDetail> details) {
		this.details = details;
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
	
	public Date getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	public BigDecimal getTotal() {
		BigDecimal value = BigDecimal.ZERO;
		for (AccountRetentionDetail detail: details) {
			value = value.add(detail.getFinalAmount());
		}
		return value;
	}

	public Date getCompanyAccountingDate() {
		return companyAccountingDate;
	}

	public void setCompanyAccountingDate(Date companyAccountingDate) {
		this.companyAccountingDate = companyAccountingDate;
	}
	
	public BigDecimal getBalance() {
		return BalanceHelper.getBalance(getAccount().getAccountId());
	}
}