package com.powerfin.model.dto;

public class NegotiationPaytableDTO {

	private String originalAccount;
	private String quotaNumber;
	private String dueDate;
	private String provisionDays;
	private String capitalReduced;
	private String capital;
	private String interest;
	private String insurance;
	private String insuranceMortgage;
	
	public NegotiationPaytableDTO(String[] dataLine)
	{
		originalAccount = dataLine[0];
		quotaNumber = dataLine[1];
		dueDate = dataLine[2];
		provisionDays = dataLine[3];
		capitalReduced = dataLine[4];
		capital = dataLine[5];
		interest = dataLine[6];
		insurance = dataLine[7];
		insuranceMortgage = dataLine[8];
	}
	public String getOriginalAccount() {
		return originalAccount;
	}
	public void setOriginalAccount(String originalAccount) {
		this.originalAccount = originalAccount;
	}
	public String getQuotaNumber() {
		return quotaNumber;
	}
	public void setQuotaNumber(String quotaNumber) {
		this.quotaNumber = quotaNumber;
	}
	public String getDueDate() {
		return dueDate;
	}
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}
	public String getProvisionDays() {
		return provisionDays;
	}
	public void setProvisionDays(String provisionDays) {
		this.provisionDays = provisionDays;
	}
	public String getCapitalReduced() {
		return capitalReduced;
	}
	public void setCapitalReduced(String capitalReduced) {
		this.capitalReduced = capitalReduced;
	}
	public String getCapital() {
		return capital;
	}
	public void setCapital(String capital) {
		this.capital = capital;
	}
	public String getInterest() {
		return interest;
	}
	public void setInterest(String interest) {
		this.interest = interest;
	}
	public String getInsurance() {
		return insurance;
	}
	public void setInsurance(String insurance) {
		this.insurance = insurance;
	}
	public String getInsuranceMortgage() {
		return insuranceMortgage;
	}
	public void setInsuranceMortgage(String insuranceMortgage) {
		this.insuranceMortgage = insuranceMortgage;
	}
	
}
