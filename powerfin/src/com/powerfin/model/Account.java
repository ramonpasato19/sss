package com.powerfin.model;

import java.io.*;
import java.math.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;

import com.powerfin.calculators.*;
import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.model.superclass.*;
import com.powerfin.model.types.*;
import com.powerfin.util.*;


/**
 * The persistent class for the account database table.
 * 
 */
@Entity
@Table(name="account")
@Views({
	@View(members="accountId;"
					+ "code, alternateCode; "
					+ "name;"
					+ "transactionalName;"
					+ "openingDate, accountStatus; "
					+ "product;"
					+ "person;"
					+ "categoryAccounts"),
	@View(name="reference", 
			members="accountId, code; "
					+ "name;"
					+ "product;"
					+ "person;"),
	@View(name="simple", members="accountId; transactionalNameForView;"),
	@View(name="simpleBalance", members="accountId; transactionalNameForView;balance"),
	@View(name="normal", members="accountId, code; name;"),
	@View(name="Shareholder", 
			members="person;"
					+ "product;"
					+ "accountId;"),
	@View(name="Payable", 
			members="person;"
					+ "product;"
					+ "accountId"),
	@View(name="InvoicePurchase", 
					members="person;"
					+ "product;"
					+ "accountId;"),
	@View(name="Accountant", 
			members="product;"
					+ "name;"
					+ "accountId;"),
	@View(name="Bank", 
			members="product;"
					+ "accountId;"
					+ "name;"
					+ "code;"),
	@View(name="PurchasePortfolioPayment",
			members="#accountId;"
					+ "name;"
					+ "interestRate,"
					+ "totalOverdueBalance;"
					+ "overdueBalances{accountOverdueBalances}"),
	@View(name="SalePortfolioPayment",
			members="#accountId;"
					+ "name;"
					+ "interestRate,"
					+ "totalOverdueBalance;"
					+ "overdueBalances{accountOverdueBalances}")
})
@Tabs({
	@Tab(properties="accountId, code, name, product.productId, product.name, currency.currencyId")
})
public class Account extends AuditEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="account_id", unique=true, nullable=false, length=20)
	@ReadOnly(notForViews="simple, normal, reference, simpleBalance, Bank, PurchasePortfolioPayment, SalePortfolioPayment")
	@DefaultValueCalculator(DefaultAccountIdCalculator.class)
	private String accountId;

	@Column(name="alternate_code", length=50)
	@DisplaySize(30)
	private String alternateCode;

	@Column(length=50)
	@DisplaySize(30)
	private String code;

	@Column(name="external_code", length=50)
	@DisplaySize(30)
	private String externalCode;
	
	@Column(nullable=false, length=100)
	@DisplaySize(50)
	private String name;

	@Column(name="transactional_name", nullable=true, length=100)
	@DisplaySize(50)
	private String transactionalName;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="opening_date", nullable=false)
	@Stereotype("DATETIME")
	@ReadOnly
	private Date openingDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="cancellation_date", nullable=true)
	@ReadOnly
	private Date cancellationDate;
	
	@ManyToOne
	@JoinColumn(name="account_status_id", nullable=false)
	@DescriptionsList
	@NoCreate
	@NoModify
	private AccountStatus accountStatus;

	@ManyToOne
	@JoinColumn(name="currency_id", nullable=false)
	private Currency currency;

	@ManyToOne
	@JoinColumn(name="person_id")
	@NoCreate
	@NoModify
	@ReferenceView("simple")
	private Person person;

	@ManyToOne
	@JoinColumn(name="product_id", nullable=false)
	@Required
	@NoCreate
	@NoModify
	@ReferenceView("Reference")
	@SearchActions({
		@SearchAction(forViews="Shareholder", value="SearchProduct.SearchShareholderProduct"),
		@SearchAction(forViews="Payable", value="SearchProduct.SearchPayableProduct"),
		@SearchAction(forViews="Bank", value="SearchProduct.SearchBankProduct")
	})
	private Product product;

	//bi-directional many-to-one association to CategoryAccount
	@OneToMany(mappedBy="account")
	@ReadOnly
	@ListProperties(value="category.categoryId, category.name, bookAccount, bookAccountName")
	private List<CategoryAccount> categoryAccounts;
	
	//bi-directional many-to-one association to CategoryAccount
	@OneToMany(mappedBy="account")
	@OrderBy("subaccount")
	@ListProperties("subaccount, dueDate, provisionDays, capitalReduced, capital, interest, totalDividend, insurance, insuranceMortgage, totalQuota")
	private List<AccountPaytable> accountPaytables;
	
	@OneToMany(mappedBy="account")
	@OrderBy("subaccount")
	@ListProperties("subaccount, dueDate, overdueDays, capital, interest, defaultInterest, insurance, insuranceMortgage, collectionFee, legalFee, receivableFee, total")
	private List<AccountOverdueBalance> accountOverdueBalances;

	@Transient
	private BigDecimal totalOverdueBalance;
	
	public List<AccountOverdueBalance> getAccountOverdueBalances() {
		return accountOverdueBalances;
	}

	public void setAccountOverdueBalances(List<AccountOverdueBalance> accountOverdueBalances) {
		this.accountOverdueBalances = accountOverdueBalances;
	}

	@PreCreate
	public void onCreate() throws Exception 
	{
		
		if (getProduct()==null)
			throw new OperativeException("the_product_is_required");
		
		if (getProduct().getOwnProduct()==Types.YesNoIntegerType.YES)
			this.setPerson(CompanyHelper.getDefaultPerson());
		
		if (UtilApp.fieldIsEmpty(getName()))
			this.setName(getPerson().getName());
		
		if (UtilApp.fieldIsEmpty(getCode()))
			this.setCode(null);
		
		if (UtilApp.fieldIsEmpty(getAlternateCode()))
			this.setAlternateCode(null);
		
		if (getTransactionalName()==null)
			setTransactionalName(name);
		
		this.setCurrency(getProduct().getCurrency());
		
		this.setOpeningDate(new Date());
		
		if (getAccountStatus()==null)
			this.setAccountStatus(AccountHelper.getDefaultAccountStatusByProduct(getProduct()));
		
		if (getPerson()==null)
			throw new OperativeException("the_person_is_required");
		
		if (getAccountStatus()==null)
			throw new OperativeException("the_account_status_is_required");
		
		AccountHelper.validateUniqueAccount(person.getPersonId(), product.getProductId());
		
		if (getAccountId()==null || getAccountId().equals(AccountHelper.AUTO_ACCOUNT_ID))
		{
			this.setAccountId(ProductHelper.getNewAccountId(getProduct()));
		}
		
		
	}
	
	@PreUpdate
	public void onUpdate()  throws Exception 
	{
		if (getProduct().getOwnProduct()==Types.YesNoIntegerType.YES)
			this.setPerson(CompanyHelper.getDefaultPerson());
		
		if (UtilApp.fieldIsEmpty(getName()))
			this.setName(getPerson().getName());
		
		if (UtilApp.fieldIsEmpty(getCode()))
			this.setCode(null);
		
		if (UtilApp.fieldIsEmpty(getAlternateCode()))
			this.setAlternateCode(null);
		
		this.setCurrency(getProduct().getCurrency());
	}
	
	public Account() {
	}

	public String getAccountId() {
		return this.accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getAlternateCode() {
		return this.alternateCode;
	}

	public void setAlternateCode(String alternateCode) {
		this.alternateCode = alternateCode;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTransactionalName() {
		return transactionalName;
	}

	public void setTransactionalName(String transactionalName) {
		this.transactionalName = transactionalName;
	}

	public Date getOpeningDate() {
		return this.openingDate;
	}

	public void setOpeningDate(Date openingDate) {
		this.openingDate = openingDate;
	}

	public AccountStatus getAccountStatus() {
		return this.accountStatus;
	}

	public void setAccountStatus(AccountStatus accountStatus) {
		this.accountStatus = accountStatus;
	}

	public Currency getCurrency() {
		return this.currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public Person getPerson() {
		return this.person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Product getProduct() {
		return this.product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public List<CategoryAccount> getCategoryAccounts() {
		return categoryAccounts;
	}

	public void setCategoryAccounts(List<CategoryAccount> categoryAccounts) {
		this.categoryAccounts = categoryAccounts;
	}
	
	public List<AccountPaytable> getAccountPaytables() {
		return accountPaytables;
	}

	public void setAccountPaytables(List<AccountPaytable> accountPaytables) {
		this.accountPaytables = accountPaytables;
	}

	public String getExternalCode() {
		return externalCode;
	}

	public void setExternalCode(String externalCode) {
		this.externalCode = externalCode;
	}

	public Date getCancellationDate() {
		return cancellationDate;
	}

	public void setCancellationDate(Date cancellationDate) {
		this.cancellationDate = cancellationDate;
	}

	@Transient
	public String getTransactionalNameForView() {
		if (transactionalName!=null && !transactionalName.isEmpty())
			return transactionalName;
		else
			return name;
	}
	
	@Transient
	public BigDecimal getInterestRate()
	{
		AccountLoan accountLoan = XPersistence.getManager().find(AccountLoan.class, accountId);
		return accountLoan.getInterestRate();
	}
	@Transient
	public BigDecimal getBalance() {
		return BalanceHelper.getBalance(accountId);
	}
	
	@Transient
	public BigDecimal getAdvanceBalance() {
		return BalanceHelper.getBalance(accountId, 0 , CategoryHelper.ADVANCE_CATEGORY);
	}

	@Transient
	public BigDecimal getAdvanceSalePortfolioBalance() {
		return BalanceHelper.getBalance(accountId, 0 , CategoryHelper.ADVANCE_SALE_PORTFOLIO_CATEGORY);
	}
	
	public BigDecimal getTotalOverdueBalance() {
		return AccountLoanHelper.getOverdueBalanceByAccountLoan(accountId);
	}

	public void setTotalOverdueBalance(BigDecimal totalOverdueBalance) {
		this.totalOverdueBalance = totalOverdueBalance;
	}
	
	
	
}