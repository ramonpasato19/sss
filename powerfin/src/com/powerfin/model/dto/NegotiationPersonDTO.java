package com.powerfin.model.dto;

public class NegotiationPersonDTO {

	private String personType;
	private String nationality;
	private String identificationType;
	private String identification;
	private String paternalSurname;
	private String maternalSurname;
	private String firtsName;
	private String secondName;
	private String gender;
	private String maritalStatus;
	private String birthDate;
	private String homeDistrict;
	private String mainStreet;
	private String sideStreet;
	private String homeNumber;
	private String homeSector;
	private String email;
	private String activity;
	
	public NegotiationPersonDTO(String[] dataLine)
	{
		personType = dataLine[0];
		nationality = dataLine[1];
		identificationType = dataLine[2];
		identification = dataLine[3];
		paternalSurname = dataLine[4];
		maternalSurname = dataLine[5];
		firtsName = dataLine[6];
		secondName = dataLine[7];
		gender = dataLine[8];
		maritalStatus = dataLine[9];
		birthDate = dataLine[10];
		homeDistrict = dataLine[11];
		mainStreet = dataLine[12];
		sideStreet = dataLine[13];
		homeNumber = dataLine[14];
		homeSector = dataLine[15];
		email = dataLine[16];
		activity = dataLine[17];
		
	}
	public String getPersonType() {
		return personType;
	}
	public void setPersonType(String personType) {
		this.personType = personType;
	}
	public String getNationality() {
		return nationality;
	}
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	public String getIdentificationType() {
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
	public String getPaternalSurname() {
		return paternalSurname;
	}
	public void setPaternalSurname(String paternalSurname) {
		this.paternalSurname = paternalSurname;
	}
	public String getMaternalSurname() {
		return maternalSurname;
	}
	public void setMaternalSurname(String maternalSurname) {
		this.maternalSurname = maternalSurname;
	}
	public String getFirtsName() {
		return firtsName;
	}
	public void setFirtsName(String firtsName) {
		this.firtsName = firtsName;
	}
	public String getSecondName() {
		return secondName;
	}
	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getMaritalStatus() {
		return maritalStatus;
	}
	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}
	public String getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}
	
	public String getHomeDistrict() {
		return homeDistrict;
	}
	public void setHomeDistrict(String homeDistrict) {
		this.homeDistrict = homeDistrict;
	}
	public String getMainStreet() {
		return mainStreet;
	}
	public void setMainStreet(String mainStreet) {
		this.mainStreet = mainStreet;
	}
	public String getSideStreet() {
		return sideStreet;
	}
	public void setSideStreet(String sideStreet) {
		this.sideStreet = sideStreet;
	}
	public String getHomeNumber() {
		return homeNumber;
	}
	public void setHomeNumber(String homeNumber) {
		this.homeNumber = homeNumber;
	}
	public String getHomeSector() {
		return homeSector;
	}
	public void setHomeSector(String homeSector) {
		this.homeSector = homeSector;
	}
	public String getActivity() {
		return activity;
	}
	public void setActivity(String activity) {
		this.activity = activity;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	
}
