package com.powerfin.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.NoCreate;
import org.openxava.annotations.NoFrame;
import org.openxava.annotations.NoModify;
import org.openxava.annotations.ReadOnly;
import org.openxava.annotations.Stereotype;
import org.openxava.annotations.Tab;
import org.openxava.annotations.Tabs;
import org.openxava.annotations.View;
import org.openxava.annotations.Views;
import org.openxava.util.Users;

/**
 * The persistent class for the account database table.
 * 
 */

@Entity
@Table(name = "portfolio_recovery_management_detail")
@Views({ @View(members = "creditInformation[# numberDetail, overdueQuotas,overdueBalances,overdueFrom;"
		+ "capital, defaultInterest,interest,vehicleInsurance, mortgageInsurance;" + "receivableFee,collectionFee];"
		+ "management;" + "portfolioRecoveryManagementDetailResult,resultDate;"
		+ "userCreate,userUpdate,dateCreate,dateUpdate;") })
@Tabs({ @Tab(properties = "portfolioRecoveryManagementDetailId") })
public class PortfolioRecoveryManagementDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "portfolio_recovery_management_detail_id", unique = true, nullable = false, length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String portfolioRecoveryManagementDetailId;

	@Column(name = "number_detail")
	@ReadOnly
	private Integer numberDetail;

	@Stereotype("MEMO")
	private String management;

	@Column(name = "overdue_quotas")
	@ReadOnly
	private Integer overdueQuotas;

	@Column(name = "overdue_balances", length=10)
	@ReadOnly
	private BigDecimal overdueBalances;

	@Column(name = "overdue_from")
	@Temporal(TemporalType.DATE)
	@ReadOnly
	private Date overdueFrom;

	@Column(name = "capital", length=10)
	@ReadOnly
	private BigDecimal capital;

	@Column(name = "default_interest", length=10)
	@ReadOnly
	private BigDecimal defaultInterest;

	@Column(name = "interest", length=10)
	@ReadOnly
	private BigDecimal interest;

	@Column(name = "vehicle_insurance", length=10)
	@ReadOnly
	private BigDecimal vehicleInsurance;

	@Column(name = "mortgage_insurance", length=10)
	@ReadOnly
	private BigDecimal mortgageInsurance;

	@Column(name = "receivable_fee", length=10)
	@ReadOnly
	private BigDecimal receivableFee;

	@Column(name = "collection_fee", length=10)
	@ReadOnly
	private BigDecimal collectionFee;

	@ManyToOne
	@JoinColumn(name = "portfolio_recovery_management_detail_result_id", nullable = true)
	@NoFrame
	@NoCreate
	@NoModify
	@DescriptionsList(descriptionProperties = "description")
	private PortfolioRecoveryManagementDetailResult portfolioRecoveryManagementDetailResult;

	@Column(name = "result_date")
	@Temporal(TemporalType.DATE)
	private Date resultDate;

	@ReadOnly
	@Column(name = "user_create", length=10)
	private String userCreate;

	@ReadOnly
	@Column(name = "user_update", length=10)
	private String userUpdate;

	@ReadOnly
	@Column(name = "user_supervisor", length=10)
	private String userSupervisor;

	@ReadOnly
	@Column(name = "date_create")
	@Temporal(TemporalType.TIMESTAMP)
	@Stereotype("FECHAHORA")
	private Date dateCreate;

	@ReadOnly
	@Column(name = "date_update")
	@Temporal(TemporalType.TIMESTAMP)
	@Stereotype("FECHAHORA")
	private Date dateUpdate;

	@ManyToOne
	@JoinColumn(name = "portfolio_recovery_management_id", nullable = false)
	private PortfolioRecoveryManagement portfolioRecoveryManagement;

	@PrePersist
	public void onCreate() {
		this.setUserCreate(Users.getCurrent());
		this.setDateCreate(new Date());

	}

	@PreUpdate
	public void onUpdate() {
		this.setUserUpdate(Users.getCurrent());
		this.setDateUpdate(new Date());
	}

	public String getPortfolioRecoveryManagementDetailId() {
		return portfolioRecoveryManagementDetailId;
	}

	public void setPortfolioRecoveryManagementDetailId(String portfolioRecoveryManagementDetailId) {
		this.portfolioRecoveryManagementDetailId = portfolioRecoveryManagementDetailId;
	}

	public Integer getNumberDetail() {
		return numberDetail;
	}

	public void setNumberDetail(Integer numberDetail) {
		this.numberDetail = numberDetail;
	}

	public String getManagement() {
		return management;
	}

	public void setManagement(String management) {
		this.management = management;
	}

	public Integer getOverdueQuotas() {
		return overdueQuotas;
	}

	public void setOverdueQuotas(Integer overdueQuotas) {
		this.overdueQuotas = overdueQuotas;
	}

	public BigDecimal getOverdueBalances() {
		return overdueBalances;
	}

	public void setOverdueBalances(BigDecimal overdueBalances) {
		this.overdueBalances = overdueBalances;
	}

	public Date getOverdueFrom() {
		return overdueFrom;
	}

	public void setOverdueFrom(Date overdueFrom) {
		this.overdueFrom = overdueFrom;
	}

	public BigDecimal getCapital() {
		return capital;
	}

	public void setCapital(BigDecimal capital) {
		this.capital = capital;
	}

	public BigDecimal getDefaultInterest() {
		return defaultInterest;
	}

	public void setDefaultInterest(BigDecimal defaultInterest) {
		this.defaultInterest = defaultInterest;
	}

	public BigDecimal getInterest() {
		return interest;
	}

	public void setInterest(BigDecimal interest) {
		this.interest = interest;
	}

	public BigDecimal getVehicleInsurance() {
		return vehicleInsurance;
	}

	public void setVehicleInsurance(BigDecimal vehicleInsurance) {
		this.vehicleInsurance = vehicleInsurance;
	}

	public BigDecimal getMortgageInsurance() {
		return mortgageInsurance;
	}

	public void setMortgageInsurance(BigDecimal mortgageInsurance) {
		this.mortgageInsurance = mortgageInsurance;
	}

	public BigDecimal getReceivableFee() {
		return receivableFee;
	}

	public void setReceivableFee(BigDecimal receivableFee) {
		this.receivableFee = receivableFee;
	}

	public BigDecimal getCollectionFee() {
		return collectionFee;
	}

	public void setCollectionFee(BigDecimal collectionFee) {
		this.collectionFee = collectionFee;
	}

	public PortfolioRecoveryManagementDetailResult getPortfolioRecoveryManagementDetailResult() {
		return portfolioRecoveryManagementDetailResult;
	}

	public void setPortfolioRecoveryManagementDetailResult(
			PortfolioRecoveryManagementDetailResult portfolioRecoveryManagementDetailResult) {
		this.portfolioRecoveryManagementDetailResult = portfolioRecoveryManagementDetailResult;
	}

	public Date getResultDate() {
		return resultDate;
	}

	public void setResultDate(Date resultDate) {
		this.resultDate = resultDate;
	}

	public String getUserCreate() {
		return userCreate;
	}

	public void setUserCreate(String userCreate) {
		this.userCreate = userCreate;
	}

	public String getUserUpdate() {
		return userUpdate;
	}

	public void setUserUpdate(String userUpdate) {
		this.userUpdate = userUpdate;
	}

	public String getUserSupervisor() {
		return userSupervisor;
	}

	public void setUserSupervisor(String userSupervisor) {
		this.userSupervisor = userSupervisor;
	}

	public Date getDateCreate() {
		return dateCreate;
	}

	public void setDateCreate(Date dateCreate) {
		this.dateCreate = dateCreate;
	}

	public Date getDateUpdate() {
		return dateUpdate;
	}

	public void setDateUpdate(Date dateUpdate) {
		this.dateUpdate = dateUpdate;
	}

	public PortfolioRecoveryManagement getPortfolioRecoveryManagement() {
		return portfolioRecoveryManagement;
	}

	public void setPortfolioRecoveryManagement(PortfolioRecoveryManagement portfolioRecoveryManagement) {
		this.portfolioRecoveryManagement = portfolioRecoveryManagement;
	}

}
