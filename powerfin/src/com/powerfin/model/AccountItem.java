package com.powerfin.model;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;
import java.math.BigDecimal;


/**
 * The persistent class for the account_item database table.
 * 
 */
@Entity
@Table(name="account_item")
@NamedQuery(name="AccountItem.findAll", query="SELECT a FROM AccountItem a")
public class AccountItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="account_id", unique=true, nullable=false)
	private Integer accountId;

	@Column(length=50)
	private String code;

	@Column(nullable=false, precision=12, scale=3)
	private BigDecimal cost;

	@Column(length=400)
	private String description;

	@Column(nullable=false)
	private Integer inventoried;

	@Column(nullable=false, length=100)
	private String name;

	@Column(nullable=false, precision=12, scale=3)
	private BigDecimal price;

	@Column(name="registration_date", nullable=false)
	private Timestamp registrationDate;

	@Column(name="user_registering", nullable=false, length=30)
	private String userRegistering;

	//bi-directional one-to-one association to Account
	@OneToOne
	@JoinColumn(name="account_id", nullable=false, insertable=false, updatable=false)
	private Account account;

	//bi-directional many-to-one association to Tax
	@ManyToOne
	@JoinColumn(name="tax_id", nullable=false)
	private Tax tax;

	//bi-directional many-to-one association to UnitMeasure
	@ManyToOne
	@JoinColumn(name="unit_measure", nullable=false)
	private UnitMeasure unitMeasureBean;

	public AccountItem() {
	}

	public Integer getAccountId() {
		return this.accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public BigDecimal getCost() {
		return this.cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getInventoried() {
		return this.inventoried;
	}

	public void setInventoried(Integer inventoried) {
		this.inventoried = inventoried;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getPrice() {
		return this.price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Timestamp getRegistrationDate() {
		return this.registrationDate;
	}

	public void setRegistrationDate(Timestamp registrationDate) {
		this.registrationDate = registrationDate;
	}

	public String getUserRegistering() {
		return this.userRegistering;
	}

	public void setUserRegistering(String userRegistering) {
		this.userRegistering = userRegistering;
	}

	public Account getAccount() {
		return this.account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Tax getTax() {
		return this.tax;
	}

	public void setTax(Tax tax) {
		this.tax = tax;
	}

	public UnitMeasure getUnitMeasureBean() {
		return this.unitMeasureBean;
	}

	public void setUnitMeasureBean(UnitMeasure unitMeasureBean) {
		this.unitMeasureBean = unitMeasureBean;
	}

}