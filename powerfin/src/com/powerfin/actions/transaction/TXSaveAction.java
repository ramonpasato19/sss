package com.powerfin.actions.transaction;

import java.math.*;
import java.util.*;

import org.openxava.actions.*;
import org.openxava.jpa.XPersistence;
import org.openxava.model.*;

import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.model.*;

public class TXSaveAction extends SaveAction{

	public List<TransactionAccount> getTransactionAccounts(Transaction transaction) throws Exception
	{
		Account creditAccount = getCreditAccount();
		Account debitAccount = getDebitAccount();

		List<TransactionAccount> transactionAccounts = new ArrayList<TransactionAccount>();
		transactionAccounts.add(TransactionAccountHelper.createCustomDebitTransactionAccount(debitAccount, getValue(), transaction));
		transactionAccounts.add(TransactionAccountHelper.createCustomCreditTransactionAccount(creditAccount, getValue(), transaction));	
		
		return transactionAccounts;
	}
	
	public void preSaveAction(Transaction transaction) throws Exception {}
	
	public void postSaveAction(Transaction transaction) throws Exception {}
	
	public void extraValidations() throws Exception {
		getCreditAccount();
		getDebitAccount();
		getValue();
	}
	
	@SuppressWarnings("unchecked")
	public String getTransactionStatusId() {
		Map<String, String> mapStatus = (Map<String, String>) getView().getValue("transactionStatus");
		return (String)mapStatus.get("transactionStatusId");
	}

	@SuppressWarnings("unchecked")
	public String getCurrencyId() {
		Map<String, String> mapStatus = (Map<String, String>) getView().getValue("currency");
		return (String)mapStatus.get("currencyId");
	}
	
	@SuppressWarnings("unchecked")
	public String getSecondaryCategory() throws Exception
	{
		Map<String, String> mapCategories = (Map<String, String>) getView().getValue("secondaryCategory");
		return (String)mapCategories.get("categoryId");

	}
	
	public String getDocumentNumber() throws Exception
	{
		return (String) getView().getValue("documentNumber");
	}
	
	public boolean isNewTransaction()
	{
		return getView().isKeyEditable();
	}
	
	public BigDecimal getValue()
	{
		BigDecimal value = (BigDecimal) getView().getRoot().getValue("value");
		if (value == null)
			throw new OperativeException("value_is_required");
		if (value.compareTo(BigDecimal.ZERO)<=0)
			throw new OperativeException("value_must_be_greater_than_zero",value);
		return value;
	}
	
	public Integer getSubaccount()
	{
		Integer value = (Integer) getView().getRoot().getValue("subaccount");
		if (value == null)
			throw new OperativeException("subaccount_is_required");
		if (value<=0)
			throw new OperativeException("subaccount_must_be_greater_than_zero",value);
		return value;
	}
	
	public BigDecimal getExchangeRate()
	{
		BigDecimal value = (BigDecimal) getView().getRoot().getValue("exchangeRate");
		if (value == null || value.compareTo(BigDecimal.ZERO)==0)
			throw new OperativeException("exchangeRate_is_required");
		return value;
	}
	
	public String getRemark()
	{
		String value = (String) getView().getValue("remark");
		if (value == null || value.isEmpty())
			throw new OperativeException("remark_is_required");
		return value;
	}
	
	@SuppressWarnings("rawtypes")
	public Account getCreditAccount() throws Exception
	{
		Map keyValues = null;
		keyValues = getView().getRoot().getSubview("creditAccount").getKeyValuesWithValue();
		try
		{
			Account account = (Account)
					MapFacade.findEntity(getView().getRoot().getSubview("creditAccount").getModelName(), keyValues);
			return account;
		}catch (javax.ejb.ObjectNotFoundException ex)
		{
			throw new OperativeException("credit_account_is_required");
		}
	}
	
	@SuppressWarnings("rawtypes")
	public Account getDebitAccount() throws Exception
	{
		Map keyValues = null;
		keyValues = getView().getRoot().getSubview("debitAccount").getKeyValuesWithValue();
		try
		{
			Account account = (Account)
					MapFacade.findEntity(getView().getRoot().getSubview("debitAccount").getModelName(), keyValues);
			return account;
		}catch (javax.ejb.ObjectNotFoundException ex)
		{
			throw new OperativeException("debit_account_is_required");
		}
	}

	@SuppressWarnings("rawtypes")
	public void execute() throws Exception
	{
		//Execute aditional validations
		extraValidations();
		
		// Save Transaction
		super.execute();
		
		if (getErrors().isEmpty()) { 
			
			// Find the just saved transaction
            Map keyValues = getView().getKeyValues(); 
            Transaction transaction = (Transaction) MapFacade.findEntity(getView().getModelName(), keyValues); 
            
            boolean financialProcessed = false;
            
			try {
	
	            // Ejecute pre actions
	            preSaveAction(transaction);
	            
	            // Process Transaction
	            if (TransactionHelper.isRequest(transaction))
	            	financialProcessed = TransactionHelper.processTransaction(transaction, getTransactionAccounts(transaction));
	            else
	            {
	            	if (TransactionHelper.isFinancialSaved(transaction) && transaction.getTransactionAccounts().isEmpty())
	            		financialProcessed = TransactionHelper.processTransaction(transaction, getTransactionAccounts(transaction));
		        	else
		        		financialProcessed = TransactionHelper.processTransaction(transaction);
	            }
	           
				if (financialProcessed)
					addMessage("financial_created");
				
				// Ejecute post actions
				postSaveAction(transaction);
				
				//Clear all fields
				getView().clear();
			}
			catch (Exception e)
			{
				XPersistence.rollback();
				//transaction = (Transaction) MapFacade.findEntity(getView().getModelName(), keyValues);
				//transaction.setTransactionStatus(TransactionHelper.getTransactionStatusByStatusId(TransactionHelper.TRANSACTION_REQUEST_STATUS_ID));
				//XPersistence.getManager().merge(transaction);
				throw e;
			}
		}
	}
}
