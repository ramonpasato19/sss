package com.powerfin.model.dto;

public class NegotiationSalePortfolioDTO {

	private String originalAccount;
	private String amount;
	private String saleSpreadAmount;
	private String saleSpreadRate;
	
	public NegotiationSalePortfolioDTO(String[] dataLine)
	{
		originalAccount = dataLine[0];
		amount = dataLine[1];
		saleSpreadAmount = dataLine[2];
		saleSpreadRate = dataLine[3];
		
	}

	public String getOriginalAccount() {
		return originalAccount;
	}

	public void setOriginalAccount(String originalAccount) {
		this.originalAccount = originalAccount;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getSaleSpreadAmount() {
		return saleSpreadAmount;
	}

	public void setSaleSpreadAmount(String saleSpreadAmount) {
		this.saleSpreadAmount = saleSpreadAmount;
	}

	public String getSaleSpreadRate() {
		return saleSpreadRate;
	}

	public void setSaleSpreadRate(String saleSpreadRate) {
		this.saleSpreadRate = saleSpreadRate;
	}
	
}
