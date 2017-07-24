package com.powerfin.model;

import java.io.*;
import java.math.*;
import java.util.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Parameter;
import org.openxava.annotations.*;

import com.powerfin.actions.inventory.*;
import com.powerfin.helper.*;
import com.powerfin.model.types.*;
import com.powerfin.model.types.Types.*;


/**
 * The persistent class for the transaction_account database table.
 * 
 */
@Entity
@Table(name="transaction_account")
@Views({
	@View(members="account; subaccount;"
			+ "category; "
			+ "debitOrCredit;"
			+ "value;"
			+ "remark"),
	@View(name="ForManualEntry", members="account; "
			+ "branch;"
			+ "debitOrCredit;"
			+ "value;"
			+ "remark;"),
	@View(name="ForGeneral", members="account; "
			+ "category; "
			+ "branch;"
			+ "subaccount; "
			+ "debitOrCredit;"
			+ "value;"
			+ "quantity;"
			+ "remark;"),
	@View(name="ForList", members="account; "
			+ "debitOrCredit;"
			+ "value;"
			+ "remark;"),
	@View(name="ForTransferItem", members="account; "
			+ "quantity;"
			+ "averageCost;"
			+ "value;"
			+ "debitOrCredit;")
	
})
@Tab(properties="account.code, account.name, subaccount, category.categoryId, debitOrCredit, value")
public class TransactionAccount implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="transaction_account_id", unique=true, nullable=false, length=32)
	@GeneratedValue(generator="system-uuid") 
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String transactionAccountId;

	//bi-directional many-to-one association to Account
	@ManyToOne
	@JoinColumn(name="account_id", nullable=false)
	@ReferenceView("normal")
	@NoCreate
	@NoModify
	@SearchActions({
		@SearchAction(forViews="ForManualEntry", value="SearchAccount.SearchAccountToManualEntry"),
		@SearchAction(forViews="ForGeneral", value="SearchAccount.SearchAccount"),
		@SearchAction(forViews="ForTransferItem", value="SearchAccount.SearchAccountItem")
	})
	@Required
	@OnChange(forViews="ForTransferItem", value=OnChangeAccountTransferItemAction.class)
	private Account account;

	//bi-directional many-to-one association to Category
	@ManyToOne
	@JoinColumn(name="category_id", nullable=false)
	@ReferenceView("Reference")
	@NoCreate
	@NoModify
	private Category category;

	@Type(type="org.openxava.types.EnumStringType",
		   parameters={
			@Parameter(name="strings", value="D,C"), // These are the values stored on the database
			@Parameter(name="enumType", value="com.powerfin.model.types.Types$DebitOrCredit")
		   }
	 )
	@Column(name="debit_or_credit", nullable=false, length=1)
	@Required
	@ReadOnly(forViews="ForTransferItem")
	private DebitOrCredit debitOrCredit;
	
	@Column(nullable=false)
	private Integer subaccount;

	@Column(name="line")
	private Integer line;
	
	@Column(nullable=false, precision=19, scale=2)
	@Required
	@DisplaySize(20)
	@ReadOnly(forViews="ForTransferItem")
	private BigDecimal value;

	@Column(length = 4000)
	private String remark;
	
	@Column(name="update_balance", nullable=false)
	private Types.YesNoIntegerType updateBalance;
	
	@Column(name="official_value", nullable=false)
	private Types.YesNoIntegerType officialValue;
	
	@Temporal(TemporalType.DATE)
	@Column(name="due_date", unique=true, nullable=true)
	private Date dueDate;
	
	//bi-directional many-to-one association to Transaction
	@ManyToOne
	@JoinColumn(name="transaction_id", nullable=false)
	private Transaction transaction;

	@Column(name="quantity", precision=13, scale=4)
	@DisplaySize(20)
	@OnChange(forViews="ForTransferItem", value=OnChangeQuantityTransferItemAction.class)
	private BigDecimal quantity;

	@ManyToOne
	@JoinColumn(name="unity_id")
	private Unity unity;

	@ManyToOne
	@JoinColumn(name="branch_id")
	@NoCreate
	@NoModify
	@DescriptionsList(descriptionProperties = "name")
	@ReadOnly(forViews="TransferItem")
	private Branch branch;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="registration_date", nullable=false)
	private Date registrationDate;
	
	@Transient
	@ReadOnly
	private BigDecimal averageCost;
	
	public TransactionAccount() {
	}

	public String getTransactionAccountId() {
		return this.transactionAccountId;
	}

	public void setTransactionAccountId(String transactionAccountId) {
		this.transactionAccountId = transactionAccountId;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public DebitOrCredit getDebitOrCredit() {
		return debitOrCredit;
	}

	public void setDebitOrCredit(DebitOrCredit debitOrCredit) {
		this.debitOrCredit = debitOrCredit;
	}

	public Integer getSubaccount() {
		return this.subaccount;
	}

	public void setSubaccount(Integer subaccount) {
		this.subaccount = subaccount;
	}

	public BigDecimal getValue() {
		return this.value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public Transaction getTransaction() {
		return this.transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}
	
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Types.YesNoIntegerType getUpdateBalance() {
		return updateBalance;
	}

	public void setUpdateBalance(Types.YesNoIntegerType updateBalance) {
		this.updateBalance = updateBalance;
	}

	public Types.YesNoIntegerType getOfficialValue() {
		return officialValue;
	}

	public void setOfficialValue(Types.YesNoIntegerType officialValue) {
		this.officialValue = officialValue;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Integer getLine() {
		return line;
	}

	public void setLine(Integer line) {
		this.line = line;
	}

	@PrePersist
	public void onPersist() throws Exception
	{
		if (branch == null)
			branch = CompanyHelper.getDefaultBranch();
		if (category == null)
			category = CategoryHelper.getBalanceCategory();
		if (subaccount == null)
			subaccount = 0;
		if (quantity == null)
			quantity = BigDecimal.ZERO;
		if (updateBalance==null)
			updateBalance = YesNoIntegerType.YES;
		if (officialValue==null)
			officialValue = YesNoIntegerType.NO;
		setRegistrationDate(new Date());
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public Unity getUnity() {
		return unity;
	}

	public void setUnity(Unity unity) {
		this.unity = unity;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public BigDecimal getAverageCost() {
		return averageCost;
	}

	public void setAverageCost(BigDecimal averageCost) {
		this.averageCost = averageCost;
	}

}