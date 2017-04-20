package com.powerfin.model;

import java.io.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Parameter;
import org.openxava.annotations.*;
import org.openxava.jpa.*;

import com.powerfin.helper.*;
import com.powerfin.model.types.*;
import com.powerfin.model.types.Types.*;


/**
 * The persistent class for the account_status database table.
 * 
 */
@Entity
@Table(name="batch_process_type")
@Views({
	@View(members="batchProcessTypeId; "
			+ "name; activated, startEndDay; "
			+ "actionClass;"
			+ "transactionModule"),
	@View(name="Reference", 
			members="batchProcessTypeId; "
			+ "name; activated, startEndDay"),
	@View(name="AccountingClosingDay", 
	members="batchProcessTypeId; "
	+ "name; activated, startEndDay")
})
public class BatchProcessType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="batch_process_type_id", unique=true, nullable=false, length=50)
	private String batchProcessTypeId;

	@Column(nullable=false, length=100)
	@Required
	@DisplaySize(50)
	@ReadOnly(forViews="AccountingClosingDay")
	private String name;

	@Required
	@Column(nullable=false)
	@ReadOnly(forViews="AccountingClosingDay")
	private Types.YesNoIntegerType activated;
	
	@Type(type="org.openxava.types.EnumStringType",
			   parameters={
				@Parameter(name="strings", value="S,E"), // These are the values stored on the database
				@Parameter(name="enumType", value="com.powerfin.model.types.Types$StartEnd")
			   }
		 )
	@Column(name="start_end_day", nullable=false, length=1)
	@Required
	@ReadOnly(forViews="AccountingClosingDay")
	private StartEnd startEndDay;
	
	@Column(name="action_class", nullable=false, length=200)
	@Required
	@DisplaySize(50)
	@ReadOnly(forViews="AccountingClosingDay")
	private String actionClass;
	
	@ManyToOne
	@JoinColumn(name = "transaction_module_id", nullable = false)
	@NoCreate
	@NoModify
	@Required
	@ReferenceView(value = "simple")
	@ReadOnly(forViews="AccountingClosingDay")
	private TransactionModule transactionModule;
	
	public BatchProcessType() {
	}

	public String getBatchProcessTypeId() {
		return batchProcessTypeId;
	}

	public void setBatchProcessTypeId(String batchProcessTypeId) {
		this.batchProcessTypeId = batchProcessTypeId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TransactionModule getTransactionModule() {
		return transactionModule;
	}

	public void setTransactionModule(TransactionModule transactionModule) {
		this.transactionModule = transactionModule;
	}

	public String getActionClass() {
		return actionClass;
	}

	public void setActionClass(String actionClass) {
		this.actionClass = actionClass;
	}

	public Types.YesNoIntegerType getActivated() {
		return activated;
	}

	public void setActivated(Types.YesNoIntegerType activated) {
		this.activated = activated;
	}

	public StartEnd getStartEndDay() {
		return startEndDay;
	}

	public void setStartEndDay(StartEnd startEndDay) {
		this.startEndDay = startEndDay;
	}
	
	public String getBatchProcessStatus() throws Exception 
	{
		try
		{
			BatchProcess batchProcess = (BatchProcess)XPersistence.getManager().createQuery("SELECT o FROM BatchProcess o "
					+ "WHERE o.batchProcessType.batchProcessTypeId = :batchProcessTypeId "
					+ "AND o.accountingDate = :accountingDate")
					.setParameter("accountingDate", CompanyHelper.getCurrentAccountingDate())
					.setParameter("batchProcessTypeId", getBatchProcessTypeId())
					.getSingleResult();
			return batchProcess.getBatchProcessStatus().getName();
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	public Integer getCountErrorDetails() throws Exception
	{
		try
		{
			BatchProcess batchProcess = (BatchProcess)XPersistence.getManager().createQuery("SELECT o FROM BatchProcess o "
					+ "WHERE o.batchProcessType.batchProcessTypeId = :batchProcessTypeId "
					+ "AND o.accountingDate = :accountingDate")
					.setParameter("accountingDate", CompanyHelper.getCurrentAccountingDate())
					.setParameter("batchProcessTypeId", getBatchProcessTypeId())
					.getSingleResult();
			
			return batchProcess.getCountErrorDetails();
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	public Integer getCountSatisfactoryDetails() throws Exception
	{
		try
		{
			BatchProcess batchProcess = (BatchProcess)XPersistence.getManager().createQuery("SELECT o FROM BatchProcess o "
					+ "WHERE o.batchProcessType.batchProcessTypeId = :batchProcessTypeId "
					+ "AND o.accountingDate = :accountingDate")
					.setParameter("accountingDate", CompanyHelper.getCurrentAccountingDate())
					.setParameter("batchProcessTypeId", getBatchProcessTypeId())
					.getSingleResult();
			
			return batchProcess.getCountSatisfactoryDetails();
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	public Integer getCountRequestDetails() throws Exception
	{
		try
		{
			BatchProcess batchProcess = (BatchProcess)XPersistence.getManager().createQuery("SELECT o FROM BatchProcess o "
					+ "WHERE o.batchProcessType.batchProcessTypeId = :batchProcessTypeId "
					+ "AND o.accountingDate = :accountingDate")
					.setParameter("accountingDate", CompanyHelper.getCurrentAccountingDate())
					.setParameter("batchProcessTypeId", getBatchProcessTypeId())
					.getSingleResult();
			
			return batchProcess.getCountRequestDetails();
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	public Integer getCountTotalDetails() throws Exception
	{
		try
		{
			BatchProcess batchProcess = (BatchProcess)XPersistence.getManager().createQuery("SELECT o FROM BatchProcess o "
					+ "WHERE o.batchProcessType.batchProcessTypeId = :batchProcessTypeId "
					+ "AND o.accountingDate = :accountingDate")
					.setParameter("accountingDate", CompanyHelper.getCurrentAccountingDate())
					.setParameter("batchProcessTypeId", getBatchProcessTypeId())
					.getSingleResult();
			
			return batchProcess.getCountTotalDetails();
		}
		catch (Exception e)
		{
			return null;
		}
	}
}