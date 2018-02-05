package com.powerfin.model;

import java.io.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;

import com.powerfin.helper.*;
import com.powerfin.model.superclass.*;

/**
 * The persistent class for the person database table.
 * 
 */
@Entity
@Table(name = "person")
@Views({ 
	@View(members = "personId;" + "identificationType;" + "identification;" + "name;" + "personType;"),
	@View(name = "NewPerson", members = "personId;" + "identificationType;" + "identification;" + "name;" + "personType;"),
	@View(name = "PersonList", members = "personId;" 
			+ "identificationType;" 
			+ "identification;" 
			+ "name;"
			+ "personType;"
			+ "projectedAccountingDate;" 
			+ "accountPayables{accountPayables};"
			+ "purchaseInvoices{purchaseInvoices};"
			+ "saleInvoices{saleInvoices};"
			+ "loans{accountLoans};"
			+ "terms{accountTerms};"),
	@View(name = "Reference", members = "personId;" + "identification; " + "name;" + "personType;" + "email"),
	@View(name = "LoanReference", members = "personId;" + "identification; " + "name;" + "personType;" + "email;" + "accountPayables"),
	@View(name = "TermReference", members = "personId;" + "identification; " + "name;" + "personType;" + "email;" + "accountPayables"),
	@View(name = "ShortReference", members = "personId;" + "identification; " + "name;" ),
	@View(name = "simple", members = "identification, name;") 
})
@Tabs({ 
	@Tab(properties = "personId, identificationType.identificationTypeId, identification, name, personType.personTypeId, email"),
	@Tab(name = "PersonList", properties = "personId, identificationType.identificationTypeId, identification, name, personType.personTypeId, email"),
	@Tab(name = "NewPerson", properties = "personId, identificationType.identificationTypeId, identification, name, personType.personTypeId, email")
})
public class Person extends AuditEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "person_id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_id")
	@SequenceGenerator(name = "sequence_id", sequenceName = "person_sequence", allocationSize = 1)
	@Hidden
	@ReadOnly(notForViews = "Reference, ShortReference, LoanReference, TermReference")
	private Integer personId;

	@Column(length = 100)
	@ReadOnly(forViews="PersonList")
	private String activity;

	@Column(length = 50)
	@ReadOnly(forViews="PersonList")
	private String email;

	@Column(name = "external_code", length = 50)
	@ReadOnly(forViews="PersonList")
	private String externalCode;

	@Column(length = 50)
	@DisplaySize(20)
	@Required
	@ReadOnly(forViews="PersonList")
	private String identification;

	@Column(length = 150)
	@DisplaySize(50)
	@Required
	@ReadOnly(forViews="PersonList")
	private String name;

	@Column(name = "other_income_source", length = 100)
	@ReadOnly(forViews="PersonList")
	private String otherIncomeSource;

	// bi-directional many-to-one association to Account
	@OneToMany(mappedBy = "person")
	private List<Account> accounts;

	// bi-directional many-to-one association to Company
	@OneToMany(mappedBy = "person")
	private List<Company> companies;

	// bi-directional one-to-one association to LegalPerson
	@OneToOne(mappedBy = "person")
	private LegalPerson legalPerson;

	// bi-directional one-to-one association to NaturalPerson
	@OneToOne(mappedBy = "person")
	private NaturalPerson naturalPerson;

	// bi-directional many-to-one association to IdentificationType
	@ManyToOne
	@JoinColumn(name = "identification_type_id", nullable = false)
	@NoCreate
	@NoModify
	@DescriptionsList(descriptionProperties = "name")
	@Required
	@ReadOnly(forViews="PersonList")
	private IdentificationType identificationType;

	// bi-directional many-to-one association to City
	@ManyToOne
	@JoinColumn(name = "person_type_id", nullable = false)
	@NoCreate
	@NoModify
	@DescriptionsList(descriptionProperties = "name")
	@Required
	@ReadOnly(forViews="PersonList")
	private PersonType personType;

	// bi-directional many-to-one association to PersonCreditCard
	@OneToMany(mappedBy = "person")
	private List<PersonCreditCard> personCreditCards;

	// bi-directional many-to-one association to PersonDeposit
	@OneToMany(mappedBy = "person")
	private List<PersonDeposit> personDeposits;

	// bi-directional many-to-one association to PersonExpense
	@OneToMany(mappedBy = "person")
	private List<PersonExpense> personExpenses;

	// bi-directional many-to-one association to PersonImmovable
	@OneToMany(mappedBy = "person")
	private List<PersonImmovable> personImmovables;

	// bi-directional many-to-one association to PersonIncome
	@OneToMany(mappedBy = "person")
	private List<PersonIncome> personIncomes;

	// bi-directional many-to-one association to PersonLoan
	@OneToMany(mappedBy = "person")
	private List<PersonLoan> personLoans;

	// bi-directional many-to-one association to PersonMovable
	@OneToMany(mappedBy = "person")
	private List<PersonMovable> personMovables;

	// bi-directional many-to-one association to PersonalReference
	@OneToMany(mappedBy = "person")
	private List<PersonalReference> personalReferences;

	// bi-directional many-to-one association to TradeReference
	@OneToMany(mappedBy = "person")
	private List<TradeReference> tradeReferences;

	@Transient
	@Column
	@ReadOnly(notForViews = "PersonList")
	private Date projectedAccountingDate;
	
	@Transient
	@ReadOnly
	@ListProperties("accountId, currency, product.name, code, balance, advanceBalance, advanceSalePortfolioBalance")
	@CollectionView("simpleBalance")
	private List<Account> accountPayables;

	@Transient
	@ReadOnly
	@ListProperties("accountId, currency, product.name, code, balance")
	private List<Account> purchaseInvoices;

	@Transient
	@ReadOnly
	@ListProperties("accountId, currency, product.name, code, balance")
	private List<Account> saleInvoices;
	
	@Transient
	@ReadOnly
	@ListProperties("accountId, currency, product.name")
	private List<Account> accountLoans;
	
	@Transient
	@ReadOnly
	@ListProperties("accountId, currency, product.name")
	private List<Account> accountTerms;
	
	public Person() {
	}

	public Integer getPersonId() {
		return this.personId;
	}

	public void setPersonId(Integer personId) {
		this.personId = personId;
	}

	public String getActivity() {
		return this.activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getExternalCode() {
		return this.externalCode;
	}

	public void setExternalCode(String externalCode) {
		this.externalCode = externalCode;
	}

	public String getIdentification() {
		return this.identification;
	}

	public void setIdentification(String identification) {
		this.identification = identification;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOtherIncomeSource() {
		return this.otherIncomeSource;
	}

	public void setOtherIncomeSource(String otherIncomeSource) {
		this.otherIncomeSource = otherIncomeSource;
	}

	public PersonType getPersonType() {
		return personType;
	}

	public void setPersonType(PersonType personType) {
		this.personType = personType;
	}

	public List<Account> getAccounts() {
		return this.accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}

	public Account addAccount(Account account) {
		getAccounts().add(account);
		account.setPerson(this);

		return account;
	}

	public Account removeAccount(Account account) {
		getAccounts().remove(account);
		account.setPerson(null);

		return account;
	}

	public List<Company> getCompanies() {
		return this.companies;
	}

	public void setCompanies(List<Company> companies) {
		this.companies = companies;
	}

	public Company addCompany(Company company) {
		getCompanies().add(company);
		company.setPerson(this);

		return company;
	}

	public Company removeCompany(Company company) {
		getCompanies().remove(company);
		company.setPerson(null);

		return company;
	}

	public LegalPerson getLegalPerson() {
		return this.legalPerson;
	}

	public void setLegalPerson(LegalPerson legalPerson) {
		this.legalPerson = legalPerson;
	}

	public NaturalPerson getNaturalPerson() {
		return this.naturalPerson;
	}

	public void setNaturalPerson(NaturalPerson naturalPerson) {
		this.naturalPerson = naturalPerson;
	}

	public IdentificationType getIdentificationType() {
		return this.identificationType;
	}

	public void setIdentificationType(IdentificationType identificationType) {
		this.identificationType = identificationType;
	}

	public List<PersonCreditCard> getPersonCreditCards() {
		return this.personCreditCards;
	}

	public void setPersonCreditCards(List<PersonCreditCard> personCreditCards) {
		this.personCreditCards = personCreditCards;
	}

	public PersonCreditCard addPersonCreditCard(PersonCreditCard personCreditCard) {
		getPersonCreditCards().add(personCreditCard);
		personCreditCard.setPerson(this);

		return personCreditCard;
	}

	public PersonCreditCard removePersonCreditCard(PersonCreditCard personCreditCard) {
		getPersonCreditCards().remove(personCreditCard);
		personCreditCard.setPerson(null);

		return personCreditCard;
	}

	public List<PersonDeposit> getPersonDeposits() {
		return this.personDeposits;
	}

	public void setPersonDeposits(List<PersonDeposit> personDeposits) {
		this.personDeposits = personDeposits;
	}

	public PersonDeposit addPersonDeposit(PersonDeposit personDeposit) {
		getPersonDeposits().add(personDeposit);
		personDeposit.setPerson(this);

		return personDeposit;
	}

	public PersonDeposit removePersonDeposit(PersonDeposit personDeposit) {
		getPersonDeposits().remove(personDeposit);
		personDeposit.setPerson(null);

		return personDeposit;
	}

	public List<PersonExpense> getPersonExpenses() {
		return this.personExpenses;
	}

	public void setPersonExpenses(List<PersonExpense> personExpenses) {
		this.personExpenses = personExpenses;
	}

	public PersonExpense addPersonExpens(PersonExpense personExpens) {
		getPersonExpenses().add(personExpens);
		personExpens.setPerson(this);

		return personExpens;
	}

	public PersonExpense removePersonExpens(PersonExpense personExpens) {
		getPersonExpenses().remove(personExpens);
		personExpens.setPerson(null);

		return personExpens;
	}

	public List<PersonImmovable> getPersonImmovables() {
		return this.personImmovables;
	}

	public void setPersonImmovables(List<PersonImmovable> personImmovables) {
		this.personImmovables = personImmovables;
	}

	public PersonImmovable addPersonImmovable(PersonImmovable personImmovable) {
		getPersonImmovables().add(personImmovable);
		personImmovable.setPerson(this);

		return personImmovable;
	}

	public PersonImmovable removePersonImmovable(PersonImmovable personImmovable) {
		getPersonImmovables().remove(personImmovable);
		personImmovable.setPerson(null);

		return personImmovable;
	}

	public List<PersonIncome> getPersonIncomes() {
		return this.personIncomes;
	}

	public void setPersonIncomes(List<PersonIncome> personIncomes) {
		this.personIncomes = personIncomes;
	}

	public PersonIncome addPersonIncome(PersonIncome personIncome) {
		getPersonIncomes().add(personIncome);
		personIncome.setPerson(this);

		return personIncome;
	}

	public PersonIncome removePersonIncome(PersonIncome personIncome) {
		getPersonIncomes().remove(personIncome);
		personIncome.setPerson(null);

		return personIncome;
	}

	public List<PersonLoan> getPersonLoans() {
		return this.personLoans;
	}

	public void setPersonLoans(List<PersonLoan> personLoans) {
		this.personLoans = personLoans;
	}

	public PersonLoan addPersonLoan(PersonLoan personLoan) {
		getPersonLoans().add(personLoan);
		personLoan.setPerson(this);

		return personLoan;
	}

	public PersonLoan removePersonLoan(PersonLoan personLoan) {
		getPersonLoans().remove(personLoan);
		personLoan.setPerson(null);

		return personLoan;
	}

	public List<PersonMovable> getPersonMovables() {
		return this.personMovables;
	}

	public void setPersonMovables(List<PersonMovable> personMovables) {
		this.personMovables = personMovables;
	}

	public PersonMovable addPersonMovable(PersonMovable personMovable) {
		getPersonMovables().add(personMovable);
		personMovable.setPerson(this);

		return personMovable;
	}

	public PersonMovable removePersonMovable(PersonMovable personMovable) {
		getPersonMovables().remove(personMovable);
		personMovable.setPerson(null);

		return personMovable;
	}

	public List<PersonalReference> getPersonalReferences() {
		return this.personalReferences;
	}

	public void setPersonalReferences(List<PersonalReference> personalReferences) {
		this.personalReferences = personalReferences;
	}

	public PersonalReference addPersonalReference(PersonalReference personalReference) {
		getPersonalReferences().add(personalReference);
		personalReference.setPerson(this);

		return personalReference;
	}

	public PersonalReference removePersonalReference(PersonalReference personalReference) {
		getPersonalReferences().remove(personalReference);
		personalReference.setPerson(null);

		return personalReference;
	}

	public List<TradeReference> getTradeReferences() {
		return this.tradeReferences;
	}

	public void setTradeReferences(List<TradeReference> tradeReferences) {
		this.tradeReferences = tradeReferences;
	}

	public TradeReference addTradeReference(TradeReference tradeReference) {
		getTradeReferences().add(tradeReference);
		tradeReference.setPerson(this);

		return tradeReference;
	}

	public TradeReference removeTradeReference(TradeReference tradeReference) {
		getTradeReferences().remove(tradeReference);
		tradeReference.setPerson(null);

		return tradeReference;
	}

	@SuppressWarnings("unchecked")
	public List<Account> getAccountPayables() {
		return (List<Account>) XPersistence.getManager()
				.createQuery("SELECT o FROM Account o " 
						+ "WHERE o.person.personId=:personId "
						+ "AND o.product.productType.productClass.productClassId = :productClassId "
						+ "AND o.accountStatus.accountStatusId=:accountStatusId ")
				.setParameter("personId", personId)
				.setParameter("productClassId", ProductClassHelper.VIEW)
				.setParameter("accountStatusId", AccountPayableHelper.STATUS_PAYABLE_ACTIVE)
				.getResultList();
	}

	public void setAccountPayables(List<Account> accountPayables) {
		this.accountPayables = accountPayables;
	}
	
	@SuppressWarnings("unchecked")
	public List<Account> getAccountTerms() {
		return (List<Account>) XPersistence.getManager()
				.createQuery("SELECT o FROM Account o " 
						+ "WHERE o.product.productType.productClass.productClassId = :productClassId "
						+ "AND o.accountStatus.accountStatusId in ('002') "
						+ "AND o.person.personId=:personId ")
				.setParameter("personId", personId)
				.setParameter("productClassId", ProductClassHelper.TERM)
				.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Account> getAccountLoans() {
		return (List<Account>) XPersistence.getManager()
				.createQuery("SELECT o FROM Account o " 
						+ "WHERE o.product.productType.productClass.productClassId = :productClassId "
						+ "AND o.accountStatus.accountStatusId in ('002') "
						+ "AND o.person.personId=:personId ")
				.setParameter("personId", personId)
				.setParameter("productClassId", ProductClassHelper.LOAN)
				.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Account> getPurchaseInvoices() {
		return (List<Account>) XPersistence.getManager()
				.createQuery("SELECT o FROM Account o " 
						+ "WHERE o.product.productType.productTypeId=:productTypeId "
						+ "AND o.accountStatus.accountStatusId in ('002','005') "
						+ "AND o.person.personId=:personId ")
				.setParameter("personId", personId)
				.setParameter("productTypeId", AccountInvoiceHelper.INVOICE_PURCHASE_PRODUCT_TYPE_ID)
				.getResultList();
	}

	public void setPurchaseInvoices(List<Account> purchaseInvoices) {
		this.purchaseInvoices = purchaseInvoices;
	}

	@SuppressWarnings("unchecked")
	public List<Account> getSaleInvoices() {
		return (List<Account>) XPersistence.getManager()
				.createQuery("SELECT o FROM Account o " 
						+ "WHERE o.product.productType.productTypeId=:productTypeId "
						+ "AND o.accountStatus.accountStatusId in ('002','005') "
						+ "AND o.person.personId=:personId ")
				.setParameter("personId", personId)
				.setParameter("productTypeId", AccountInvoiceHelper.INVOICE_SALE_PRODUCT_TYPE_ID)
				.getResultList();
	}

	public void setSaleInvoices(List<Account> saleInvoices) {
		this.saleInvoices = saleInvoices;
	}

	public Date getProjectedAccountingDate() {
		return projectedAccountingDate;
	}

	public void setProjectedAccountingDate(Date projectedAccountingDate) {
		this.projectedAccountingDate = projectedAccountingDate;
	}

	

}