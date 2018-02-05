package com.powerfin.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.openxava.annotations.DefaultValueCalculator;
import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.ListProperties;
import org.openxava.annotations.NewAction;
import org.openxava.annotations.NoCreate;
import org.openxava.annotations.NoFrame;
import org.openxava.annotations.NoModify;
import org.openxava.annotations.ReadOnly;
import org.openxava.annotations.ReferenceView;
import org.openxava.annotations.RemoveAction;
import org.openxava.annotations.Tab;
import org.openxava.annotations.Tabs;
import org.openxava.annotations.View;
import org.openxava.annotations.Views;
import org.openxava.calculators.CurrentDateCalculator;

@Entity
@Table(name = "portfolio_recovery_management")
@Views({ @View(members = "#portfolioRecoveryManagementId;" + "beginDate,"
		+ "finalDate;portfolioRecoveryManagementStatus," + "portfolioRecoveryManagementType;" + "accountLoan;"
		+ "portfolioRecoveryManagementDetails") })
@Tabs({ @Tab(properties = "portfolioRecoveryManagementId,accountLoan.accountId, accountLoan.account.name,accountLoan.account.person.identification") })
public class PortfolioRecoveryManagement implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "portfolio_recovery_management_id", unique = true, nullable = false)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "sequence_id")
    @SequenceGenerator(name="sequence_id", sequenceName = "portfolio_recovery_management_sequence", allocationSize = 1)
	@ReadOnly
	private Integer portfolioRecoveryManagementId;

	@ManyToOne
	@NoCreate
	@NoModify
	@ReferenceView("ReferencePortfolioRecoveryManagement")
	@JoinColumn(name = "account_loan_id")
	// @OnChange(OnChangeSelectedAccountLoan.class)
	private AccountLoan accountLoan;

	@Temporal(TemporalType.DATE)
	@Column(name = "begin_date")
	@DefaultValueCalculator(CurrentDateCalculator.class)
	private Date beginDate;

	@Temporal(TemporalType.DATE)
	@Column(name = "final_date")
	@ReadOnly
	private Date finalDate;

	@ManyToOne
	@JoinColumn(name = "portfolio_recovery_management_status_id", nullable = true)
	@NoFrame
	@NoCreate
	@NoModify
	@DescriptionsList(descriptionProperties = "name")
	private PortfolioRecoveryManagementStatus portfolioRecoveryManagementStatus;

	@ManyToOne
	@JoinColumn(name = "portfolio_recovery_management_type_id", nullable = true)
	@NoFrame
	@NoCreate
	@NoModify
	@DescriptionsList(descriptionProperties = "name")
	private PortfolioRecoveryManagementType portfolioRecoveryManagementType;

	@OneToMany(mappedBy = "portfolioRecoveryManagement", cascade = CascadeType.ALL)
	@ListProperties("numberDetail, management, portfolioRecoveryManagementDetailResult.description, resultDate, userCreate, userUpdate, dateCreate, dateUpdate")
	@OrderBy("dateCreate")
	@RemoveAction("")		
	@NewAction("PortfolioRecoveryManagmentDetailController.new")	
	private List<PortfolioRecoveryManagementDetail> portfolioRecoveryManagementDetails;

	public PortfolioRecoveryManagement() {
	}

	public Integer getPortfolioRecoveryManagementId() {
		return portfolioRecoveryManagementId;
	}

	public void setPortfolioRecoveryManagementId(Integer portfolioRecoveryManagementId) {
		this.portfolioRecoveryManagementId = portfolioRecoveryManagementId;
	}

	public AccountLoan getAccountLoan() {
		return accountLoan;
	}

	public void setAccountLoan(AccountLoan accountLoan) {
		this.accountLoan = accountLoan;
	}

	public PortfolioRecoveryManagementStatus getPortfolioRecoveryManagementStatus() {
		return portfolioRecoveryManagementStatus;
	}

	public void setPortfolioRecoveryManagementStatus(
			PortfolioRecoveryManagementStatus portfolioRecoveryManagementStatus) {
		this.portfolioRecoveryManagementStatus = portfolioRecoveryManagementStatus;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getFinalDate() {
		return finalDate;
	}

	public void setFinalDate(Date finalDate) {
		this.finalDate = finalDate;
	}

	public PortfolioRecoveryManagementType getPortfolioRecoveryManagementType() {
		return portfolioRecoveryManagementType;
	}

	public void setPortfolioRecoveryManagementType(PortfolioRecoveryManagementType portfolioRecoveryManagementType) {
		this.portfolioRecoveryManagementType = portfolioRecoveryManagementType;
	}

	public List<PortfolioRecoveryManagementDetail> getPortfolioRecoveryManagementDetails() {
		return portfolioRecoveryManagementDetails;
	}

	public void setPortfolioRecoveryManagementDetails(
			List<PortfolioRecoveryManagementDetail> portfolioRecoveryManagementDetails) {
		this.portfolioRecoveryManagementDetails = portfolioRecoveryManagementDetails;
	}

}
