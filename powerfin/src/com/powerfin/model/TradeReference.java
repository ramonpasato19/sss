package com.powerfin.model;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the trade_reference database table.
 * 
 */
@Entity
@Table(name="trade_reference")
@NamedQuery(name="TradeReference.findAll", query="SELECT t FROM TradeReference t")
public class TradeReference implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="trade_reference_id", unique=true, nullable=false, length=32)
	private String tradeReferenceId;

	@Column(name="commercial_intitucion", length=100)
	private String commercialIntitucion;

	@Column(length=50)
	private String phone;

	@Column(name="purchase_amount", precision=11, scale=2)
	private BigDecimal purchaseAmount;

	@Temporal(TemporalType.DATE)
	@Column(name="purchase_date")
	private Date purchaseDate;

	@Column(name="registration_date", nullable=false)
	private Timestamp registrationDate;

	@Column(name="user_registering", nullable=false, length=30)
	private String userRegistering;

	//bi-directional many-to-one association to Person
	@ManyToOne
	@JoinColumn(name="person_id", nullable=false)
	private Person person;

	public TradeReference() {
	}

	public String getTradeReferenceId() {
		return this.tradeReferenceId;
	}

	public void setTradeReferenceId(String tradeReferenceId) {
		this.tradeReferenceId = tradeReferenceId;
	}

	public String getCommercialIntitucion() {
		return this.commercialIntitucion;
	}

	public void setCommercialIntitucion(String commercialIntitucion) {
		this.commercialIntitucion = commercialIntitucion;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public BigDecimal getPurchaseAmount() {
		return this.purchaseAmount;
	}

	public void setPurchaseAmount(BigDecimal purchaseAmount) {
		this.purchaseAmount = purchaseAmount;
	}

	public Date getPurchaseDate() {
		return this.purchaseDate;
	}

	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
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

	public Person getPerson() {
		return this.person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

}