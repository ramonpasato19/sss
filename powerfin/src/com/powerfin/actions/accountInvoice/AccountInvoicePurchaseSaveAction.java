package com.powerfin.actions.accountInvoice;

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

public class AccountInvoicePurchaseSaveAction extends SaveAction{

	private String accountStatusId;
	private String transactionModuleId;
	Integer branchId = null;
	
	@SuppressWarnings("unchecked")
	public void execute() throws Exception {

		String accountId = getView().getValueString("accountId");
		accountStatusId = getView().getSubview("accountStatus").getValueString("accountStatusId");	
		String productId = getView().getSubview("product").getValueString("productId");
		Integer personId = getView().getSubview("person").getValueInt("personId");
		
		String externalCode = "";
		
		Map<String, Integer> branchMap = (Map<String, Integer>) getView().getRoot().getValue("branch");
		branchId = (Integer)branchMap.get("branchId");
		
		if (!UtilApp.fieldIsEmpty(getView().getValueString("establishmentCode")))
			externalCode += getView().getValueString("establishmentCode")+"-";
		if (!UtilApp.fieldIsEmpty(getView().getValueString("emissionPointCode")))
			externalCode += getView().getValueString("emissionPointCode")+"-";
		if (!UtilApp.fieldIsEmpty(getView().getValueString("sequentialCode")))
			externalCode += getView().getValueString("sequentialCode");
		
		validate();
		
		if (getView().isKeyEditable()) { //Create Account
			
			Account account = AccountHelper.createAccount(productId, personId, accountStatusId, null, externalCode, null, branchId);
			getView().setValue("accountId", account.getAccountId());
			addMessage("account_created", account.getClass().getName());
		}
		else
		{
			Account account = XPersistence.getManager().find(Account.class, accountId);
			account.setCode(externalCode);
			account.setBranch(XPersistence.getManager().find(Branch.class, branchId));
			account.setPerson(XPersistence.getManager().find(Person.class, personId));
			
			if (!account.getAccountStatus().getAccountStatusId().equals(AccountInvoiceHelper.STATUS_INVOICE_REQUEST))
				throw new OperativeException("account_has_already_been_processed", externalCode);
			
			account.setAccountStatus(XPersistence.getManager().find(AccountStatus.class, accountStatusId));
			account = AccountHelper.updateAccount(account);
			addMessage("account_modified", account.getClass().getName());
		}

		// Create/Update Account Invoice
		super.execute();
		
		if (getErrors().isEmpty()) {
			accountId = getView().getValueString("accountId");
			AccountInvoice accountInvoice = XPersistence.getManager().find(AccountInvoice.class, accountId);
	        
            TransactionModule tm = XPersistence.getManager().find(TransactionModule.class, getTransactionModuleId());
            List<Transaction> transactions =  (List<Transaction>)XPersistence.getManager().createQuery("SELECT o FROM Transaction o "
    				+ "WHERE o.transactionModule=:transactionModule AND o.creditAccount=:accountInvoice")
    				.setParameter("transactionModule", tm)
    				.setParameter("accountInvoice", accountInvoice.getAccount())
    				.getResultList();
            
            if (transactions.isEmpty())
            {
            	Transaction transaction = TransactionHelper.getNewInitTransaction();
     			transaction.setTransactionModule(tm);
     			transaction.setTransactionStatus(tm.getDefaultTransactionStatus());
     			transaction.setValue(accountInvoice.getTotal());
     			transaction.setRemark(accountInvoice.getRemark());
     			transaction.setCreditAccount(accountInvoice.getAccount());
     			transaction.setCurrency(accountInvoice.getAccount().getCurrency());
     			transaction.setOrigenUnity(accountInvoice.getUnity());
     			
     			XPersistence.getManager().persist(transaction);
            }
            else if (transactions.size()>1)
            {
            	throw new InternalException("multiple_transactions_over_account_in_module", accountInvoice.getAccountId(), tm.getTransactionModuleId());
            	
            }
            else
            {
            	Transaction transaction = (Transaction)transactions.get(0);
            	transaction.setTransactionModule(tm);
     			transaction.setTransactionStatus(tm.getDefaultTransactionStatus());
     			transaction.setValue(accountInvoice.getTotal());
     			transaction.setRemark(accountInvoice.getRemark());
     			transaction.setCreditAccount(accountInvoice.getAccount());
     			transaction.setCurrency(accountInvoice.getAccount().getCurrency());
     			transaction.setOrigenUnity(accountInvoice.getUnity());
     			
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

		if (!getView().isKeyEditable())
			if (accountStatusId==null)
				throw new OperativeException("accountStatus_is_required",accountStatusId);
		
		if (getTransactionModuleId() == null || getTransactionModuleId().isEmpty())
			throw new InternalException("property_transactionModuleId_is_required");
		
		if (branchId == null)
			throw new OperativeException("branch_is_required");
		
	}

	public String getTransactionModuleId() {
		return transactionModuleId;
	}

	public void setTransactionModuleId(String transactionModuleId) {
		this.transactionModuleId = transactionModuleId;
	}
	
}
