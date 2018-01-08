package com.powerfin.model;

import java.io.Serializable;
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

@Entity
@Table(name = "portfolio_recovery_management_status")
@Views({ @View(members = "portfolioRecoveryManagementStatusId;" + "name;"),
		@View(name = "Reference", members = "portfolioRecoveryManagementStatusId;" + "name;"),
		@View(name = "OnlyRead", members = "name;") })
@Tab(properties = "portfolioRecoveryManagementStatusId, name")
public class PortfolioRecoveryManagementStatus implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "portfolio_recovery_management_status_id", unique = true, nullable = false, length = 3)
	private String portfolioRecoveryManagementStatusId;

	@Column(nullable = false, length = 50)
	@Required
	@DisplaySize(20)
	private String name;

	@OneToMany(mappedBy = "portfolioRecoveryManagementStatus")
	private List<PortfolioRecoveryManagement> portfolioRecoveryManagements;

	public String getPortfolioRecoveryManagementStatusId() {
		return portfolioRecoveryManagementStatusId;
	}

	public void setPortfolioRecoveryManagementStatusId(String portfolioRecoveryManagementStatusId) {
		this.portfolioRecoveryManagementStatusId = portfolioRecoveryManagementStatusId;
	}

	public List<PortfolioRecoveryManagement> getPortfolioRecoveryManagements() {
		return portfolioRecoveryManagements;
	}

	public void setPortfolioRecoveryManagements(List<PortfolioRecoveryManagement> portfolioRecoveryManagements) {
		this.portfolioRecoveryManagements = portfolioRecoveryManagements;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}