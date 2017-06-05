package com.powerfin.actions.company;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;

import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.model.*;
import com.powerfin.model.types.Types.*;

public class AccountingClosingDayAction extends SaveAction {
	
	@SuppressWarnings("unchecked")
	public void execute() throws Exception {
		Date accountingDate = (Date)getView().getRoot().getValue("accountingDate");
		Date nextAccountingDate = (Date)getView().getRoot().getValue("nextAccountingDate");
		
		generateBalanceAccounting(accountingDate);
		
		cleanAccountOverdueBalance();
		
		List<BatchProcessType> batchProcessTypes= XPersistence.getManager()
				.createQuery("SELECT o FROM BatchProcessType o "
				+ "WHERE o.activated = :activated ")
				.setParameter("activated", YesNoIntegerType.YES)
				.getResultList();
		
		for (BatchProcessType batchProcessType : batchProcessTypes)
		{
			List<BatchProcess> batchProcesses= XPersistence.getManager()
					.createQuery("SELECT o FROM BatchProcess o "
					+ "WHERE o.accountingDate = :accountingDate "
					+ "AND o.batchProcessType.batchProcessTypeId = :batchProcessTypeId ")
					.setParameter("accountingDate", accountingDate)
					.setParameter("batchProcessTypeId", batchProcessType.getBatchProcessTypeId())
					.getResultList();
			
			if (batchProcesses==null || batchProcesses.isEmpty())
				throw new OperativeException("there_are_unfinished_batch_processes"); 
			
			BatchProcess batchProcess = batchProcesses.get(0);
			
			if (!batchProcess.getBatchProcessStatus().getBatchProcessStatusId().equals(BatchProcessHelper.BATCH_FINISH_STATUS))
				throw new OperativeException("there_are_unfinished_batch_processes");
		}
		
		getView().setValue("accountingDate", nextAccountingDate);
		
		super.execute();
		
		if (getErrors().isEmpty()) 
		{
			for (BatchProcessType batchProcessType : batchProcessTypes)
			{
				BatchProcess newBatchProcess = new BatchProcess();
				newBatchProcess.setAccountingDate(nextAccountingDate);
				newBatchProcess.setBatchProcessStatus(BatchProcessHelper.getBatchProcessStatus(BatchProcessHelper.BATCH_REQUEST_STATUS));
				newBatchProcess.setBatchProcessType(batchProcessType);
				XPersistence.getManager().persist(newBatchProcess);
			}
			
			XPersistence.commit();
			getView().refresh();
			addMessage("accountingDate_updated_correctly");
		}
	}
	
	private void generateBalanceAccounting(Date accountingDate)
	{
		BalanceAccountingHelper.generateBalanceSheet(accountingDate);
	}
	
	private void cleanAccountOverdueBalance()
	{
		XPersistence.getManager()
		.createQuery("DELETE FROM AccountOverdueBalance o")
		.executeUpdate();
	}
}
