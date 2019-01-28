package com.powerfin.model.superclass;

import java.math.BigDecimal;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import com.powerfin.model.*;

@MappedSuperclass
public class CommonPerson extends AuditEntity {

	@Id
	@Column(name="person_id", unique=true, nullable=false)
	@ReadOnly
	@Hidden
	private Integer personId;
	
	@OneToOne
	@JoinColumn(name="person_id", nullable=false, insertable=false, updatable=false)
	private Person person;
	
	@Column(name="home_main_street", length=100)
	private String homeMainStreet;

	@Column(name="home_number", length=50)
	@DisplaySize(20)
	private String homeNumber;

	@Column(name="home_phone_number_1", length=50)
	private String homePhoneNumber1;

	@Column(name="home_phone_number_2", length=50)
	private String homePhoneNumber2;
	
	@Column(name="home_sector", length=100)
	private String homeSector;

	@Column(name="home_side_street", length=100)
	private String homeSideStreet;
	
	@Transient
	@DisplaySize(20)
	@Required
	private String identification;
	
	@Transient
	@Column(name="credit_limit", nullable=true, precision=11, scale=2)
	private BigDecimal creditLimit;
	
	@Transient
	@DisplaySize(50)
	@Stereotype("EMAIL")
	private String email;
	
	@Transient
	@Column(length = 100)
	@DisplaySize(50)
	private String activity;
	
	@Transient
	@Column(length = 100)
	@DisplaySize(100)
	@ReadOnly
	private String name;
	
	@Transient
	@ManyToOne
	@NoCreate
	@NoModify
	@DescriptionsList(descriptionProperties = "name")
	@Required
	private IdentificationType identificationType;
	
	@Transient
	@AsEmbedded
	@OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
	@ListProperties("creditCardType.name, financialInstitution, cardNumber, balance, feeAmount")
	private List<PersonCreditCard> personCreditCards;

	@Transient
	@AsEmbedded
	@OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
	@ListProperties("depositType.name, financialInstitution, accountNumber, openingDate, amountLetter")
	private List<PersonDeposit> personDeposits;

	@Transient
	@AsEmbedded
	@OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
	@ListProperties("expenseType.expenseClass, expenseType.name, amount, description")
	private List<PersonExpense> personExpenses;

	@Transient
	@AsEmbedded
	@OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
	@ListProperties("immovableType.name, address, amount, mortgaged")
	private List<PersonImmovable> personImmovables;

	@Transient
	@AsEmbedded
	@OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
	@ListProperties("incomeType.incomeClass, incomeType.name, amount, description")
	private List<PersonIncome> personIncomes;

	@Transient
	@AsEmbedded
	@OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
	@ListProperties("loanType.name, financialInstitution, feeAmount, balance, scoreQualification")
	private List<PersonLoan> personLoans;

	@Transient
	@AsEmbedded
	@OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
	@ListProperties("movableType.name, mark, model, year, amount, pledge")
	private List<PersonMovable> personMovables;

	@Transient
	@AsEmbedded
	@OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
	@ListProperties("nearFamily, relationship, address, homePhone, workPhone, cellPhone")
	private List<PersonalReference> personalReferences;

	// bi-directional many-to-one association to TradeReference
	@OneToMany(mappedBy = "person")
	private List<TradeReference> tradeReferences;
	public Integer getPersonId() {
		return personId;
	}

	public void setPersonId(Integer personId) {
		this.personId = personId;
	}

	public Person getPerson() {
		return this.person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
	
	public String getIdentification() {
		if(person!=null)
			return person.getIdentification();
		return identification;
	}

	public void setIdentification(String identification) {
		this.identification = identification;
	}

	public IdentificationType getIdentificationType() {
		if(person!=null)
			return person.getIdentificationType();
		return identificationType;
	}

	public void setIdentificationType(IdentificationType identificationType) {
		this.identificationType = identificationType;
	}

	public String getEmail() {
		if(person!=null)
			return person.getEmail();
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		if(person!=null)
			return person.getName();
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getActivity() {
		if(person!=null)
			return person.getActivity();
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public List<PersonDeposit> getPersonDeposits() {
		if(person!=null)
			return person.getPersonDeposits();
		return personDeposits;
	}

	public void setPersonDeposits(List<PersonDeposit> personDeposits) {
		this.personDeposits = personDeposits;
	}

	public List<PersonCreditCard> getPersonCreditCards() {
		if(person!=null)
			return person.getPersonCreditCards();
		return personCreditCards;
	}

	public void setPersonCreditCards(List<PersonCreditCard> personCreditCards) {
		this.personCreditCards = personCreditCards;
	}

	public List<PersonExpense> getPersonExpenses() {
		if(person!=null)
			return person.getPersonExpenses();
		return personExpenses;
	}

	public void setPersonExpenses(List<PersonExpense> personExpenses) {
		this.personExpenses = personExpenses;
	}

	public List<PersonImmovable> getPersonImmovables() {
		if(person!=null)
			return person.getPersonImmovables();
		return personImmovables;
	}

	public void setPersonImmovables(List<PersonImmovable> personImmovables) {
		this.personImmovables = personImmovables;
	}

	public List<PersonIncome> getPersonIncomes() {
		if(person!=null)
			return person.getPersonIncomes();
		return personIncomes;
	}

	public void setPersonIncomes(List<PersonIncome> personIncomes) {
		this.personIncomes = personIncomes;
	}

	public List<PersonLoan> getPersonLoans() {
		if(person!=null)
			return person.getPersonLoans();
		return personLoans;
	}

	public void setPersonLoans(List<PersonLoan> personLoans) {
		this.personLoans = personLoans;
	}

	public List<PersonMovable> getPersonMovables() {
		if(person!=null)
			return person.getPersonMovables();
		return personMovables;
	}

	public void setPersonMovables(List<PersonMovable> personMovables) {
		this.personMovables = personMovables;
	}

	public List<PersonalReference> getPersonalReferences() {
		if(person!=null)
			return person.getPersonalReferences();
		return personalReferences;
	}

	public void setPersonalReferences(List<PersonalReference> personalReferences) {
		this.personalReferences = personalReferences;
	}

	public List<TradeReference> getTradeReferences() {
		if(person!=null)
			return person.getTradeReferences();
		return tradeReferences;
	}

	public void setTradeReferences(List<TradeReference> tradeReferences) {
		this.tradeReferences = tradeReferences;
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

	public String getHomeSector() {
		return homeSector;
	}

	public void setHomeSector(String homeSector) {
		this.homeSector = homeSector;
	}

	public String getHomeSideStreet() {
		return homeSideStreet;
	}

	public void setHomeSideStreet(String homeSideStreet) {
		this.homeSideStreet = homeSideStreet;
	}

	public BigDecimal getCreditLimit() {
		if(person!=null)
			return person.getCreditLimit();
		return creditLimit;
	}

	public void setCreditLimit(BigDecimal creditLimit) {
		this.creditLimit = creditLimit;
	}
	
}
