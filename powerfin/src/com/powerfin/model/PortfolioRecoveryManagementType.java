package com.powerfin.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.openxava.annotations.DisplaySize;
import org.openxava.annotations.Required;
import org.openxava.annotations.Tab;
import org.openxava.annotations.View;
import org.openxava.annotations.Views;

/**
 * Clase que utiliza para indicar los tipos de gestión que existen de acuerdo al
 * numero de día vencidos asi como el monto de deuda vencida
 * 
 * @author david
 */

@Entity
@Table(name = "portfolio_recovery_management_type")
@Views({ @View(members = "portfolioRecoveryManagementTypeId;" + "name;"),
		@View(name = "Reference", members = "portfolioRecoveryManagementTypeId;" + "name;"),
		@View(name = "OnlyRead", members = "name;") })
@Tab(properties = "portfolioRecoveryManagementTypeId, name")
public class PortfolioRecoveryManagementType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "portfolio_recovery_management_type_id", unique = true, nullable = false, length = 3)
	private String portfolioRecoveryManagementTypeId;

	@Column(nullable = false, length = 50)
	@Required
	@DisplaySize(20)
	private String name;

	// bi-directional many-to-one association to Application
	@OneToMany(mappedBy = "portfolioRecoveryManagementType")
	private List<PortfolioRecoveryManagement> portfolioRecoveryManagements;

	@Column(name = "minimum_expired_days")
	private Integer minimumExpiredDays;

	@Column(name = "maximum_expired_days")
	private Integer maximumExpiredDays;
	
	@Column(name="minimum_amount", nullable=true, precision=11, scale=2)
	private BigDecimal minimumAmount;

	public String getPortfolioRecoveryManagementTypeId() {
		return portfolioRecoveryManagementTypeId;
	}

	public void setPortfolioRecoveryManagementTypeId(String portfolioRecoveryManagementTypeId) {
		this.portfolioRecoveryManagementTypeId = portfolioRecoveryManagementTypeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PortfolioRecoveryManagement> getPortfolioRecoveryManagements() {
		return portfolioRecoveryManagements;
	}

	public void setPortfolioRecoveryManagements(List<PortfolioRecoveryManagement> portfolioRecoveryManagements) {
		this.portfolioRecoveryManagements = portfolioRecoveryManagements;
	}

	public Integer getMinimumExpiredDays() {
		return minimumExpiredDays;
	}

	public void setMinimumExpiredDays(Integer minimumExpiredDays) {
		this.minimumExpiredDays = minimumExpiredDays;
	}

	public Integer getMaximumExpiredDays() {
		return maximumExpiredDays;
	}

	public void setMaximumExpiredDays(Integer maximumExpiredDays) {
		this.maximumExpiredDays = maximumExpiredDays;
	}

	public BigDecimal getMinimumAmount() {
		return minimumAmount;
	}

	public void setMinimumAmount(BigDecimal minimumAmount) {
		this.minimumAmount = minimumAmount;
	}

}