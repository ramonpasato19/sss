package com.powerfin.model;

import java.io.*;
import java.math.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import com.powerfin.model.superclass.*;
import com.powerfin.model.types.Types.*;


/**
 * The persistent class for the natural_person database table.
 * 
 */
@Entity
@Table(name="natural_person")
@Views({
	@View(members="#"
		+ "personId,"
		+ "name;"
		+ "generalInformation{#"
			+ "identificationType, identification;"
			+ "firstName, secondName;"
			+ "paternalSurname, maternalSurname;"
			+ "gender, maritalStatus;"
			+ "email, activity;"
			+ "nationality, levelInstruction;"
			+ "familyLoads, separationProperties;"
			+ "cellPhoneNumber1;creditLimit}"
		+ "homeAddress{#"
			+ "location{"
				+ "homeType;"
				+ "homeDistrict;"
				+ "homeMainStreet, homeNumber; "
				+ "homeSideStreet;homeSector;"
				+ "homePhoneNumber1;}"
			+ "rental{#"
				+ "homeRenterName; homeResidenceTime;"
				+ "homeRenterPhoneNumber; homeRentalValue;}}"
		+ "work{#"
			+ "workIdentification;"
			+ "workPlace;"
			+ "workDistrict;"
			+ "workAddress;"
			+ "workActivity;"
			+ "workPhoneNumber, workPhoneExtension;"
			+ "workPosition, workSeniority"
		+ "}"
		+ "PersonIncome{personIncomes}"
		+ "PersonExpense{personExpenses}"
		+ "PersonDeposit{personDeposits}"
		+ "PersonLoan{personLoans}"
		+ "PersonCreditCard{personCreditCards}"
		+ "PersonImmovable{personImmovables}"
		+ "PersonMovable{personMovables}"
		+ "PersonalReference{personalReferences}"
		+ ""),
	@View(name="Reference", members="#"
		+ "generalInformation{#"
			+ "identificationType, identification;"
			+ "name;"
			+ "maritalStatus;"
			+ "email, activity;"
			+ "nationality;"
			+ "cellPhoneNumber1;creditLimit}"
		+ "homeAddress{#"
			+ "location{"
				+ "homeType;"
				+ "homeDistrict;"
				+ "homeMainStreet, homeNumber; "
				+ "homeSideStreet;homeSector;"
				+ "homePhoneNumber1;}"
			+ "rental{#"
				+ "homeRenterName; homeResidenceTime;"
				+ "homeRenterPhoneNumber; homeRentalValue;}}"
		+ "work{#"
			+ "workIdentification;"
			+ "workPlace;"
			+ "workDistrict;"
			+ "workAddress;"
			+ "workActivity;"
			+ "workPhoneNumber, workPhoneExtension;"
			+ "workPosition, workSeniority}"				
		+ "PersonalReference{#personalReferences}"),
	@View(name="Simple", members="identification; name")}
)
@Tab(properties="personId, person.identification, person.name, person.email")
public class NaturalPerson extends CommonPerson implements Serializable {
	private static final long serialVersionUID = 1L;

	@Temporal(TemporalType.DATE)
	@Column(name="birth_date")
	private Date birthDate;

	@Column(name="cell_phone_number_1", length=50)
	private String cellPhoneNumber1;

	@Column(name="family_loads")
	private Integer familyLoads;

	@Column(name="first_name", nullable=false, length=50)
	@Required
	private String firstName;

	@Column(name="home_rental_value", precision=11, scale=2)
	private BigDecimal homeRentalValue;

	@Column(name="home_renter_name", length=100)
	private String homeRenterName;

	@Column(name="home_renter_phone_number", length=50)
	private String homeRenterPhoneNumber;

	@Column(name="home_residence_time", length=50)
	private String homeResidenceTime;

	@Column(name="income_category", length=50)
	private String incomeCategory;

	@Column(name="maternal_surname", length=50)
	private String maternalSurname;

	@Column(name="old_work_phone_number", length=50)
	private String oldWorkPhoneNumber;

	@Column(name="old_work_place", length=100)
	private String oldWorkPlace;

	@Column(name="old_work_position", length=100)
	private String oldWorkPosition;

	@Column(name="old_work_seniority", length=50)
	private String oldWorkSeniority;

	@Column(name="paternal_surname", nullable=false, length=50)
	@Required
	private String paternalSurname;

	@Column(name="residence_time", length=50)
	private String residenceTime;

	@Column(name="second_name", length=50)
	private String secondName;

	@Column(name="separation_properties")
	private YesNoIntegerType separationProperties;

	@Column(name="work_activity", length=100)
	private String workActivity;

	@Column(name="work_address", length=400)
	private String workAddress;

	@Column(name="work_identification", length=50)
	private String workIdentification;

	@Column(name="work_phone_extension", length=50)
	@DisplaySize(20)
	private String workPhoneExtension;

	@Column(name="work_phone_number", length=50)
	private String workPhoneNumber;

	@Column(name="work_place", length=100)
	private String workPlace;

	@Column(name="work_position", length=100)
	private String workPosition;

	@Column(name="work_seniority", length=50)
	@DisplaySize(20)
	private String workSeniority;

	//bi-directional many-to-one association to City
	@ManyToOne
	@JoinColumn(name="birth_city_id")
	private City city;

	//bi-directional many-to-one association to District
	@ManyToOne
	@JoinColumn(name="work_district_id")
	@NoCreate
	@NoModify
	@ReferenceView("WorkDistrict")
	private District workDistrict;

	//bi-directional many-to-one association to District
	@ManyToOne
	@JoinColumn(name="home_district_id")
	@NoCreate
	@NoModify
	@ReferenceView("HomeDistrict")
	private District homeDistrict;

	//bi-directional many-to-one association to District
	@ManyToOne
	@JoinColumn(name="old_work_district_id")
	private District oldWorkDistrict;

	//bi-directional many-to-one association to Gender
	@ManyToOne
	@JoinColumn(name="gender_id", nullable=false)
	@DescriptionsList(descriptionProperties="name")
	@Required
	@NoCreate
	@NoModify
	private Gender gender;

	//bi-directional many-to-one association to HomeType
	@ManyToOne
	@JoinColumn(name = "home_type_id", nullable = true)
	@DescriptionsList(descriptionProperties = "homeTypeId, name")
	@NoCreate
	@NoModify
	private HomeType homeType;

	//bi-directional many-to-one association to LevelInstruction
	@ManyToOne
	@JoinColumn(name="level_instruction_id")
	@DescriptionsList
	@NoCreate
	@NoModify
	private LevelInstruction levelInstruction;

	//bi-directional many-to-one association to MaritalStatus
	@ManyToOne
	@JoinColumn(name="marital_status_id")
	@DescriptionsList(descriptionProperties="name")
	@Required
	@NoCreate
	@NoModify
	private MaritalStatus maritalStatus;

	//bi-directional many-to-one association to Nationality
	@ManyToOne
	@JoinColumn(name="nationality_id")
	@DescriptionsList
	@NoCreate
	@NoModify
	private Nationality nationality;
	
	public NaturalPerson() {
	}

	public Date getBirthDate() {
		return this.birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getCellPhoneNumber1() {
		return this.cellPhoneNumber1;
	}

	public void setCellPhoneNumber1(String cellPhoneNumber1) {
		this.cellPhoneNumber1 = cellPhoneNumber1;
	}

	public Integer getFamilyLoads() {
		return this.familyLoads;
	}

	public void setFamilyLoads(Integer familyLoads) {
		this.familyLoads = familyLoads;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public BigDecimal getHomeRentalValue() {
		return this.homeRentalValue;
	}

	public void setHomeRentalValue(BigDecimal homeRentalValue) {
		this.homeRentalValue = homeRentalValue;
	}

	public String getHomeRenterName() {
		return this.homeRenterName;
	}

	public void setHomeRenterName(String homeRenterName) {
		this.homeRenterName = homeRenterName;
	}

	public String getHomeRenterPhoneNumber() {
		return this.homeRenterPhoneNumber;
	}

	public void setHomeRenterPhoneNumber(String homeRenterPhoneNumber) {
		this.homeRenterPhoneNumber = homeRenterPhoneNumber;
	}

	public String getHomeResidenceTime() {
		return this.homeResidenceTime;
	}

	public void setHomeResidenceTime(String homeResidenceTime) {
		this.homeResidenceTime = homeResidenceTime;
	}

	public String getIncomeCategory() {
		return this.incomeCategory;
	}

	public void setIncomeCategory(String incomeCategory) {
		this.incomeCategory = incomeCategory;
	}

	public String getMaternalSurname() {
		return this.maternalSurname;
	}

	public void setMaternalSurname(String maternalSurname) {
		this.maternalSurname = maternalSurname;
	}

	public String getOldWorkPhoneNumber() {
		return this.oldWorkPhoneNumber;
	}

	public void setOldWorkPhoneNumber(String oldWorkPhoneNumber) {
		this.oldWorkPhoneNumber = oldWorkPhoneNumber;
	}

	public String getOldWorkPlace() {
		return this.oldWorkPlace;
	}

	public void setOldWorkPlace(String oldWorkPlace) {
		this.oldWorkPlace = oldWorkPlace;
	}

	public String getOldWorkPosition() {
		return this.oldWorkPosition;
	}

	public void setOldWorkPosition(String oldWorkPosition) {
		this.oldWorkPosition = oldWorkPosition;
	}

	public String getOldWorkSeniority() {
		return this.oldWorkSeniority;
	}

	public void setOldWorkSeniority(String oldWorkSeniority) {
		this.oldWorkSeniority = oldWorkSeniority;
	}

	public String getPaternalSurname() {
		return this.paternalSurname;
	}

	public void setPaternalSurname(String paternalSurname) {
		this.paternalSurname = paternalSurname;
	}

	public String getResidenceTime() {
		return this.residenceTime;
	}

	public void setResidenceTime(String residenceTime) {
		this.residenceTime = residenceTime;
	}

	public String getSecondName() {
		return this.secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public YesNoIntegerType getSeparationProperties() {
		return this.separationProperties;
	}

	public void setSeparationProperties(YesNoIntegerType separationProperties) {
		this.separationProperties = separationProperties;
	}

	public String getWorkActivity() {
		return this.workActivity;
	}

	public void setWorkActivity(String workActivity) {
		this.workActivity = workActivity;
	}

	public String getWorkAddress() {
		return this.workAddress;
	}

	public void setWorkAddress(String workAddress) {
		this.workAddress = workAddress;
	}

	public String getWorkIdentification() {
		return this.workIdentification;
	}

	public void setWorkIdentification(String workIdentification) {
		this.workIdentification = workIdentification;
	}

	public String getWorkPhoneExtension() {
		return this.workPhoneExtension;
	}

	public void setWorkPhoneExtension(String workPhoneExtension) {
		this.workPhoneExtension = workPhoneExtension;
	}

	public String getWorkPhoneNumber() {
		return this.workPhoneNumber;
	}

	public void setWorkPhoneNumber(String workPhoneNumber) {
		this.workPhoneNumber = workPhoneNumber;
	}

	public String getWorkPlace() {
		return this.workPlace;
	}

	public void setWorkPlace(String workPlace) {
		this.workPlace = workPlace;
	}

	public String getWorkPosition() {
		return this.workPosition;
	}

	public void setWorkPosition(String workPosition) {
		this.workPosition = workPosition;
	}

	public String getWorkSeniority() {
		return this.workSeniority;
	}

	public void setWorkSeniority(String workSeniority) {
		this.workSeniority = workSeniority;
	}

	public City getCity() {
		return this.city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public District getWorkDistrict() {
		return workDistrict;
	}

	public void setWorkDistrict(District workDistrict) {
		this.workDistrict = workDistrict;
	}

	public District getHomeDistrict() {
		return homeDistrict;
	}

	public void setHomeDistrict(District homeDistrict) {
		this.homeDistrict = homeDistrict;
	}

	public District getOldWorkDistrict() {
		return oldWorkDistrict;
	}

	public void setOldWorkDistrict(District oldWorkDistrict) {
		this.oldWorkDistrict = oldWorkDistrict;
	}

	public Gender getGender() {
		return this.gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public HomeType getHomeType() {
		return this.homeType;
	}

	public void setHomeType(HomeType homeType) {
		this.homeType = homeType;
	}

	public LevelInstruction getLevelInstruction() {
		return this.levelInstruction;
	}

	public void setLevelInstruction(LevelInstruction levelInstruction) {
		this.levelInstruction = levelInstruction;
	}

	public MaritalStatus getMaritalStatus() {
		return this.maritalStatus;
	}

	public void setMaritalStatus(MaritalStatus maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public Nationality getNationality() {
		return this.nationality;
	}

	public void setNationality(Nationality nationality) {
		this.nationality = nationality;
	}

}