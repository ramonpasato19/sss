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
@Table(name = "portfolio_recovery_management_detail_result")
@Views({ @View(members = "portfolioRecoveryManagementDetailResultId;" + "description;"),
		@View(name = "Reference", members = "portfolioRecoveryManagementDetailResultId;" + "description;"),
		@View(name = "OnlyRead", members = "description;") })
@Tab(properties = "portfolioRecoveryManagementDetailResultId, description")
public class PortfolioRecoveryManagementDetailResult implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "portfolio_recovery_management_detail_result_id", unique = true, nullable = false, length = 3)
	private String portfolioRecoveryManagementDetailResultId;

	@Column(nullable = false, length = 50)
	@Required
	@DisplaySize(20)
	private String description;

	// bi-directional many-to-one association to Application
	@OneToMany(mappedBy = "portfolioRecoveryManagementDetailResult")
	private List<PortfolioRecoveryManagementDetail> portfolioRecoveryManagementDetail;

	public String getPortfolioRecoveryManagementDetailResultId() {
		return portfolioRecoveryManagementDetailResultId;
	}

	public void setPortfolioRecoveryManagementDetailResultId(String portfolioRecoveryManagementDetailResultId) {
		this.portfolioRecoveryManagementDetailResultId = portfolioRecoveryManagementDetailResultId;
	}

	public List<PortfolioRecoveryManagementDetail> getPortfolioRecoveryManagementDetail() {
		return portfolioRecoveryManagementDetail;
	}

	public void setPortfolioRecoveryManagementDetail(
			List<PortfolioRecoveryManagementDetail> portfolioRecoveryManagementDetail) {
		this.portfolioRecoveryManagementDetail = portfolioRecoveryManagementDetail;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}