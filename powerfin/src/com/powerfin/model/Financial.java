package com.powerfin.model;

import java.io.Serializable;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;

import com.powerfin.model.superclass.*;

import java.util.Date;
import java.util.List;


/**
 * The persistent class for the financial database table.
 * 
 */
@Entity
@Table(name="financial")
@Views({
	@View(members="accountingDate;"
			+ "remark;"
			+ "voucher;"
			+ "financialStatus;"
			+ "origenUnityId;"
			+ "movements"),
	@View(name = "ConsultTransaction", 
			members = "voucher;" 
			+ "currency; "
			+ "transactionModule;" 
			+ "remark;" 
			+ "requestDate, userRequesting;"
			+ "authorizationDate, userAuthorizing;" 
			+ "accountingDate, documentNumber;"
			+ "financialStatus;"
			+ "transactionId;"
			+ "movements"),
})
@Tabs({
	@Tab(properties="voucher, accountingDate, financialStatus.financialStatusId, transaction.transactionModule, remark"),
	@Tab(name="ConsultTransaction", properties="voucher, transaction.currency.name, transaction.transactionModule.name, accountingDate, financialStatus.financialStatusId, remark"),
})
public class Financial extends AuditEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="financial_id", unique=true, nullable=false, length=32)
	@GeneratedValue(generator="system-uuid") 
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String financialId;

	@Temporal(TemporalType.DATE)
	@Column(name="accounting_date", nullable=false)
	@Required
	@ReadOnly(forViews = "ConsultTransaction")
	private Date accountingDate;

	@Column(length=4000)
	@Required
	@ReadOnly(forViews = "ConsultTransaction")
	private String remark;

	@Column(name="reversed_financial_id", length=32)
	@ReadOnly(forViews = "ConsultTransaction")
	private String reversedFinancialId;

	@Column(length=50)
	@ReadOnly(forViews = "ConsultTransaction")
	private String voucher;

	//bi-directional many-to-one association to FinancialStatus
	@ManyToOne
	@JoinColumn(name="financial_status_id", nullable=false)
	@Required
	@DescriptionsList
	@ReadOnly(forViews = "ConsultTransaction")
	private FinancialStatus financialStatus;

	//bi-directional many-to-one association to Movement
	@OneToMany(mappedBy="financial")
	@ListProperties("branch.branchId, account.accountId, account.code, account.name, subaccount, category.categoryId, debitOrCredit, value, quantity, bookAccount.bookAccountId, remark")
	@ReadOnly(forViews = "ConsultTransaction")
	private List<Movement> movements;

	// bi-directional one-to-one association to Transaction
	@OneToOne
	@JoinColumn(name = "transaction_id")
	@ReadOnly(forViews = "ConsultTransaction")
	private Transaction transaction;
	
	@ManyToOne
	@JoinColumn(name="origen_unity_id")
	@ReadOnly(forViews = "ConsultTransaction")
	private Unity origenUnityId;

	public Financial() {
	}

	public String getFinancialId() {
		return this.financialId;
	}

	public void setFinancialId(String financialId) {
		this.financialId = financialId;
	}

	public Date getAccountingDate() {
		return this.accountingDate;
	}

	public void setAccountingDate(Date accountingDate) {
		this.accountingDate = accountingDate;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getReversedFinancialId() {
		return this.reversedFinancialId;
	}

	public void setReversedFinancialId(String reversedFinancialId) {
		this.reversedFinancialId = reversedFinancialId;
	}

	public String getVoucher() {
		return this.voucher;
	}

	public void setVoucher(String voucher) {
		this.voucher = voucher;
	}

	public FinancialStatus getFinancialStatus() {
		return this.financialStatus;
	}

	public void setFinancialStatus(FinancialStatus financialStatus) {
		this.financialStatus = financialStatus;
	}

	public List<Movement> getMovements() {
		return this.movements;
	}

	public void setMovements(List<Movement> movements) {
		this.movements = movements;
	}

	public Movement addMovement(Movement movement) {
		getMovements().add(movement);
		movement.setFinancial(this);

		return movement;
	}

	public Movement removeMovement(Movement movement) {
		getMovements().remove(movement);
		movement.setFinancial(null);

		return movement;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public Unity getOrigenUnityId() {
		return origenUnityId;
	}

	public void setOrigenUnityId(Unity origenUnityId) {
		this.origenUnityId = origenUnityId;
	}
	
	public String getDocumentNumber()
	{
		if (transaction!=null)
			return transaction.getDocumentNumber();
		return null;
	}
	
	public String getUserAuthorizing()
	{
		if (transaction!=null)
			return transaction.getUserAuthorizing();
		return null;
	}
	
	public String getUserRequesting()
	{
		if (transaction!=null)
			return transaction.getUserRequesting();
		return null;
	}
	
	public Date getAuthorizationDate()
	{
		if (transaction!=null)
			return transaction.getAuthorizationDate();
		return null;
	}
	
	public Date getRequestDate()
	{
		if (transaction!=null)
			return transaction.getRequestDate();
		return null;
	}
	
	public String getTransactionModule()
	{
		if (transaction!=null)
			return transaction.getTransactionModule().getName();
		return null;
	}
	
	public String getCurrency()
	{
		if (transaction!=null)
			return transaction.getCurrency().getCurrencyId();
		return null;
	}
	
	public String getTransactionId()
	{
		if (transaction!=null)
			return transaction.getTransactionId();
		return null;
	}
}