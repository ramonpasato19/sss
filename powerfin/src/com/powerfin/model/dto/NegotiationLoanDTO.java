package com.powerfin.model.dto;

public class NegotiationLoanDTO {

	private String identification;
	private String originalAccount;
	private String originalAmount;
	private String amount;
	private String period;
	private String quotasNumber;
	private String paymentDay;
	private String purchaseSpreadAmount;
	private String purchaseSpreadRate;
	private String startDatePayment;
	private String interestRate;
	private String productLoan;
	private String productPayable;
	private String frecuency;
	private String daysGrace;
	private String insuranceAccountId;
	private String mortgageAccountId;
	private String insuranceAmount;
	private String mortgageAmount;
	
	public NegotiationLoanDTO(String[] dataLine)
	{
		identification = dataLine[0];
		originalAccount = dataLine[1];
		originalAmount = dataLine[2];
		amount = dataLine[3];
		period = dataLine[4];
		quotasNumber = dataLine[5];
		paymentDay = dataLine[6];
		purchaseSpreadAmount = dataLine[7];
		purchaseSpreadRate = dataLine[8];
		startDatePayment = dataLine[9];
		interestRate = dataLine[10];
		productLoan = dataLine[11];
		productPayable = dataLine[12];
		frecuency = dataLine[13];
		daysGrace = dataLine[14];
		insuranceAccountId = dataLine[15];
		mortgageAccountId = dataLine[16];
		insuranceAmount = dataLine[17];
		mortgageAmount = dataLine[18];
	}
	public String getIdentification() {
		return identification;
	}
	public void setIdentification(String identification) {
		this.identification = identification;
	}
	public String getOriginalAccount() {
		return originalAccount;
	}
	public void setOriginalAccount(String originalAccount) {
		this.originalAccount = originalAccount;
	}
	public String getOriginalAmount() {
		return originalAmount;
	}
	public void setOriginalAmount(String originalAmount) {
		this.originalAmount = originalAmount;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getQuotasNumber() {
		return quotasNumber;
	}
	public void setQuotasNumber(String quotasNumber) {
		this.quotasNumber = quotasNumber;
	}
	public String getPaymentDay() {
		return paymentDay;
	}
	public void setPaymentDay(String paymentDay) {
		this.paymentDay = paymentDay;
	}
	public String getPurchaseSpreadAmount() {
		return purchaseSpreadAmount;
	}
	public void setPurchaseSpreadAmount(String purchaseSpreadAmount) {
		this.purchaseSpreadAmount = purchaseSpreadAmount;
	}
	public String getPurchaseSpreadRate() {
		return purchaseSpreadRate;
	}
	public void setPurchaseSpreadRate(String purchaseSpreadRate) {
		this.purchaseSpreadRate = purchaseSpreadRate;
	}
	public String getStartDatePayment() {
		return startDatePayment;
	}
	public void setStartDatePayment(String startDatePayment) {
		this.startDatePayment = startDatePayment;
	}
	public String getInterestRate() {
		return interestRate;
	}
	public void setInterestRate(String interestRate) {
		this.interestRate = interestRate;
	}
	public String getProductLoan() {
		return productLoan;
	}
	public void setProductLoan(String productLoan) {
		this.productLoan = productLoan;
	}
	public String getProductPayable() {
		return productPayable;
	}
	public void setProductPayable(String productPayable) {
		this.productPayable = productPayable;
	}
	public String getFrecuency() {
		return frecuency;
	}
	public void setFrecuency(String frecuency) {
		this.frecuency = frecuency;
	}
	public String getDaysGrace() {
		return daysGrace;
	}
	public void setDaysGrace(String daysGrace) {
		this.daysGrace = daysGrace;
	}
	public String getInsuranceAccountId() {
		return insuranceAccountId;
	}
	public void setInsuranceAccountId(String insuranceAccountId) {
		this.insuranceAccountId = insuranceAccountId;
	}
	public String getMortgageAccountId() {
		return mortgageAccountId;
	}
	public void setMortgageAccountId(String mortgageAccountId) {
		this.mortgageAccountId = mortgageAccountId;
	}
	public String getInsuranceAmount() {
		return insuranceAmount;
	}
	public void setInsuranceAmount(String insuranceAmount) {
		this.insuranceAmount = insuranceAmount;
	}
	public String getMortgageAmount() {
		return mortgageAmount;
	}
	public void setMortgageAmount(String mortgageAmount) {
		this.mortgageAmount = mortgageAmount;
	}
	
}
