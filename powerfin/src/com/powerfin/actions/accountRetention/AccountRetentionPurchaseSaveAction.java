package com.powerfin.actions.accountRetention;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;
import org.openxava.model.*;
import org.openxava.util.*;
import org.openxava.validators.*;

import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.model.*;
import com.powerfin.util.*;

public class AccountRetentionPurchaseSaveAction extends SaveAction{

	private String accountStatusId;
	private String transactionModuleId;
	
	@SuppressWarnings("unchecked")
	public void execute() throws Exception {

		String accountId = getView().getValueString("accountId");
		accountStatusId = getView().getSubview("accountStatus").getValueString("accountStatusId");	
		String productId = getView().getSubview("product").getValueString("productId");
		String accountInvoiceId = getView().getSubview("accountInvoice").getValueString("accountId");
		String externalCode = "";
		
		if (!UtilApp.fieldIsEmpty(getView().getValueString("establishmentCode")))
			externalCode += getView().getValueString("establishmentCode")+"-";
		if (!UtilApp.fieldIsEmpty(getView().getValueString("emissionPointCode")))
			externalCode += getView().getValueString("emissionPointCode")+"-";
		if (!UtilApp.fieldIsEmpty(getView().getValueString("sequentialCode")))
			externalCode += getView().getValueString("sequentialCode");
		validate();
		
		AccountInvoice accountInvoice = XPersistence.getManager().find(AccountInvoice.class, accountInvoiceId);
		if (getView().isKeyEditable()) { //On Create Account
			Account account = AccountHelper.createAccount(productId, accountInvoice.getAccount().getPerson().getPersonId(), accountStatusId, null, externalCode, null);
			getView().setValue("accountId", account.getAccountId());
			addMessage("account_created", account.getClass().getName());
		}else
		{
			Account account = XPersistence.getManager().find(Account.class, accountId);
			account.setCode(externalCode);
			account.setPerson(accountInvoice.getAccount().getPerson());
			
			if (!account.getAccountStatus().getAccountStatusId().equals(AccountInvoiceHelper.STATUS_RETENTION_REQUEST))
				throw new OperativeException("account_has_already_been_processed", externalCode);
			
			account.setAccountStatus(XPersistence.getManager().find(AccountStatus.class, accountStatusId));
			account = AccountHelper.updateAccount(account);
			addMessage("account_modified", account.getClass().getName());
		}

		// Create/Update Account Retention
		super.execute();
		
		if (getErrors().isEmpty()) {
			accountId = getView().getValueString("accountId");
	        AccountRetention accountRetention = XPersistence.getManager().find(AccountRetention.class, accountId);
	        
            TransactionModule tm = XPersistence.getManager().find(TransactionModule.class, getTransactionModuleId());
            List<Transaction> transactions =  (List<Transaction>)XPersistence.getManager().createQuery("SELECT o FROM Transaction o "
    				+ "WHERE o.transactionModule=:transactionModule AND o.creditAccount=:account")
    				.setParameter("transactionModule", tm)
    				.setParameter("account", accountRetention.getAccount())
    				.getResultList();
            
            if (transactions.isEmpty())
            {
            	Transaction transaction = TransactionHelper.getNewInitTransaction();
     			transaction.setTransactionModule(tm);
     			transaction.setTransactionStatus(tm.getDefaultTransactionStatus());
     			transaction.setValue(accountRetention.getTotal());
     			transaction.setRemark(tm.getName());
     			transaction.setCreditAccount(accountRetention.getAccount());
     			transaction.setCurrency(accountRetention.getAccount().getCurrency());
     			
     			XPersistence.getManager().persist(transaction);
            }
            else if (transactions.size()>1)
            {
            	throw new InternalException("multiple_transactions_over_account_in_module", accountRetention.getAccountId(), tm.getTransactionModuleId());
            	
            }
            else
            {
            	Transaction transaction = (Transaction)transactions.get(0);
            	transaction.setTransactionModule(tm);
     			transaction.setTransactionStatus(tm.getDefaultTransactionStatus());
     			transaction.setValue(accountRetention.getTotal());
     			transaction.setRemark(tm.getName());
     			transaction.setCreditAccount(accountRetention.getAccount());
     			transaction.setCurrency(accountRetention.getAccount().getCurrency());
     			
     			XPersistence.getManager().merge(transaction);
     			
     			XPersistence.getManager().createQuery("DELETE FROM TransactionAccount ta "
     					+ "WHERE ta.transaction.transactionId = :transactionId")
     				.setParameter("transactionId", transaction.getTransactionId())
     				.executeUpdate();
            }
		}
		getView().refresh();
	}
	
	private void validate() throws Exception {
		Messages errors = MapFacade.validate(getModelName(), getValuesToSave());
		if (errors.contains()) throw new ValidationException(errors);
		
		if (!getView().isKeyEditable() && accountStatusId==null)
			throw new OperativeException("accountStatus_is_required");
		
		if (getTransactionModuleId() == null || getTransactionModuleId().isEmpty())
			throw new InternalException("property_transactionModuleId_is_required");
	}

	public String getTransactionModuleId() {
		return transactionModuleId;
	}

	public void setTransactionModuleId(String transactionModuleId) {
		this.transactionModuleId = transactionModuleId;
	}
}
