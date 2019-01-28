package com.powerfin.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.NoCreate;
import org.openxava.annotations.NoModify;
import org.openxava.annotations.ReferenceView;
import org.openxava.annotations.Required;

@Entity
@Table(name="pos")
public class Pos {

	@Id 
	@Column(name="pos_id", unique=true, nullable=false, length=10)
	private String posId;
	
	//bi-directional many-to-one association to Category
	@ManyToOne
	@JoinColumn(name="branch_id", nullable=false)
	@Required
	@NoCreate
	@NoModify
	@DescriptionsList(descriptionProperties="branchId, name")
	private Branch branch;

	@Column(name="name", nullable=false, length=100)
	private String name;
	
	@Column(name="establishment_code", nullable=true, length=10)
	private String establishmentCode;
	
	@Column(name="emission_point_code", nullable=true, length=10)
	private String emissionPointCode;

	@Column(name="sequential_name", nullable=true, length=100)
	private String sequentialName;
    
	@Column(name="authorization_code", nullable=true, length=100)
	private String authorizationCode;
	
	@Column(name="retention_sequential_name", nullable=true, length=100)
	private String retentionSequentialName;
	
	@ManyToOne
	@JoinColumn(name="cash_account_id", nullable=true)
	@ReferenceView("simple")
	@NoCreate
	@NoModify
	private Account cashAccount;
	
	@ManyToOne
	@JoinColumn(name="credit_card_account_id", nullable=true)
	@ReferenceView("simple")
	@NoCreate
	@NoModify
	private Account creditCardAccount;
	
	@ManyToOne
	@JoinColumn(name="check_account_id", nullable=true)
	@ReferenceView("simple")
	@NoCreate
	@NoModify
	private Account checkAccount;
	
	@ManyToOne
	@JoinColumn(name="price_list_id", nullable=true)
	@DescriptionsList
	@NoCreate
	@NoModify
	private PriceList priceList;
	
	public Pos()
	{
		
	}

	public String getPosId() {
		return posId;
	}

	public void setPosId(String posId) {
		this.posId = posId;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getSequentialName() {
		return sequentialName;
	}

	public void setSequentialName(String sequentialName) {
		this.sequentialName = sequentialName;
	}

	public String getAuthorizationCode() {
		return authorizationCode;
	}

	public void setAuthorizationCode(String authorizationCode) {
		this.authorizationCode = authorizationCode;
	}

	public String getRetentionSequentialName() {
		return retentionSequentialName;
	}

	public void setRetentionSequentialName(String retentionSequentialName) {
		this.retentionSequentialName = retentionSequentialName;
	}

	public Account getCashAccount() {
		return cashAccount;
	}

	public void setCashAccount(Account cashAccount) {
		this.cashAccount = cashAccount;
	}

	public Account getCreditCardAccount() {
		return creditCardAccount;
	}

	public void setCreditCardAccount(Account creditCardAccount) {
		this.creditCardAccount = creditCardAccount;
	}

	public Account getCheckAccount() {
		return checkAccount;
	}

	public void setCheckAccount(Account checkAccount) {
		this.checkAccount = checkAccount;
	}

	public PriceList getPriceList() {
		return priceList;
	}

	public void setPriceList(PriceList priceList) {
		this.priceList = priceList;
	}
	
}
