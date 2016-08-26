package com.powerfin.model;

import java.io.*;
import java.math.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import com.powerfin.model.superclass.*;


/**
 * The persistent class for the natural_person database table.
 * 
 */
@Entity
@Table(name="natural_person")
@View(members="#"
		+ "personId;"
		+ "identificationType;"
		+ "identification;"
		+ "firstName, secondName;"
		+ "paternalSurname, maternalSurname;"
		+ "gender, maritalStatus;"
		+ "homePhoneNumber1, cellPhoneNumber1;"
		+ "email;"
		+ "address[#homeMainStreet, homeNumber; "
		+ "homeSideStreet;homeSector]"
		+ "")
@Tab(properties="personId, person.identification, person.name, person.email")
public class NaturalPerson extends AuditEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="person_id", unique=true, nullable=false)
	@ReadOnly
	@Hidden
	private Integer personId;

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

	@Column(name="home_main_street", length=100)
	private String homeMainStreet;

	@Column(name="home_number", length=50)
	@DisplaySize(20)
	private String homeNumber;

	@Column(name="home_phone_number_1", length=50)
	private String homePhoneNumber1;

	@Column(name="home_phone_number_2", length=50)
	private String homePhoneNumber2;

	@Column(name="home_rental_value", precision=11, scale=2)
	private BigDecimal homeRentalValue;

	@Column(name="home_renter_name", length=100)
	private String homeRenterName;

	@Column(name="home_renter_phone_number", length=50)
	private String homeRenterPhoneNumber;

	@Column(name="home_residence_time", length=50)
	private String homeResidenceTime;

	@Column(name="home_sector", length=100)
	private String homeSector;

	@Column(name="home_side_street", length=100)
	private String homeSideStreet;

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
	private Integer separationProperties;

	@Column(name="work_activity", length=100)
	private String workActivity;

	@Column(name="work_address", length=400)
	private String workAddress;

	@Column(name="work_identification", length=50)
	private String workIdentification;

	@Column(name="work_phone_extension", length=50)
	private String workPhoneExtension;

	@Column(name="work_phone_number", length=50)
	private String workPhoneNumber;

	@Column(name="work_place", length=100)
	private String workPlace;

	@Column(name="work_position", length=100)
	private String workPosition;

	@Column(name="work_seniority", length=50)
	private String workSeniority;

	//bi-directional many-to-one association to City
	@ManyToOne
	@JoinColumn(name="birth_city_id")
	private City city;

	//bi-directional many-to-one association to District
	@ManyToOne
	@JoinColumn(name="work_district_id")
	private District workDistrict;

	//bi-directional many-to-one association to District
	@ManyToOne
	@JoinColumn(name="home_district_id")
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
	private Gender gender;

	//bi-directional many-to-one association to HomeType
	@ManyToOne
	@JoinColumn(name="home_type_id")
	private HomeType homeType;

	//bi-directional many-to-one association to LevelInstruction
	@ManyToOne
	@JoinColumn(name="level_instruction_id")
	private LevelInstruction levelInstruction;

	//bi-directional many-to-one association to MaritalStatus
	@ManyToOne
	@JoinColumn(name="marital_status_id")
	@DescriptionsList(descriptionProperties="name")
	@Required
	private MaritalStatus maritalStatus;

	//bi-directional many-to-one association to Nationality
	@ManyToOne
	@JoinColumn(name="nationality_id")
	private Nationality nationality;

	//bi-directional one-to-one association to Person
	@OneToOne
	@JoinColumn(name="person_id", nullable=false, insertable=false, updatable=false)
	private Person person;

	@Transient
	@DisplaySize(20)
	@Required
	private String identification;
	
	@Transient
	@DisplaySize(20)
	private String email;
	
	@Transient
	@ManyToOne
	@NoCreate
	@NoModify
	@DescriptionsList(descriptionProperties = "name")
	@Required
	private IdentificationType identificationType;
	
	public NaturalPerson() {
	}

	public Integer getPersonId() {
		return this.personId;
	}

	public void setPersonId(Integer personId) {
		this.personId = personId;
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

	public String getHomeMainStreet() {
		return this.homeMainStreet;
	}

	public void setHomeMainStreet(String homeMainStreet) {
		this.homeMainStreet = homeMainStreet;
	}

	public String getHomeNumber() {
		return this.homeNumber;
	}

	public void setHomeNumber(String homeNumber) {
		this.homeNumber = homeNumber;
	}

	public String getHomePhoneNumber1() {
		return this.homePhoneNumber1;
	}

	public void setHomePhoneNumber1(String homePhoneNumber1) {
		this.homePhoneNumber1 = homePhoneNumber1;
	}

	public String getHomePhoneNumber2() {
		return this.homePhoneNumber2;
	}

	public void setHomePhoneNumber2(String homePhoneNumber2) {
		this.homePhoneNumber2 = homePhoneNumber2;
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

	public String getHomeSector() {
		return this.homeSector;
	}

	public void setHomeSector(String homeSector) {
		this.homeSector = homeSector;
	}

	public String getHomeSideStreet() {
		return this.homeSideStreet;
	}

	public void setHomeSideStreet(String homeSideStreet) {
		this.homeSideStreet = homeSideStreet;
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

	public Integer getSeparationProperties() {
		return this.separationProperties;
	}

	public void setSeparationProperties(Integer separationProperties) {
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

}