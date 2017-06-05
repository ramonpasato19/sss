package com.powerfin.actions.accountTerm;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;
import org.openxava.model.*;
import org.openxava.util.*;
import org.openxava.validators.*;

import com.powerfin.core.*;
import com.powerfin.exception.*;
import com.powerfin.helper.*;
import com.powerfin.model.*;

public class AccountTermSaveAction extends SaveAction{

	private String accountStatusId;
	private boolean isCreateAccount;
	
	@SuppressWarnings("unchecked")
	public void execute() throws Exception {
		
		isCreateAccount = getView().isKeyEditable();
		
		String accountId = getView().getValueString("accountId");
		accountStatusId = getView().getSubview("accountStatus").getValueString("accountStatusId");	
		String productId = getView().getSubview("product").getValueString("productId");
		Integer personId = getView().getSubview("person").getValueInt("personId");
		String externalCode = getView().getValueString("externalCode");
		
		validate();
		
		if (getView().isKeyEditable()) { //Create Account
			
			Account account = AccountHelper.createAccount(productId, personId, accountStatusId, null, externalCode, null);
			getView().setValue("accountId", account.getAccountId());
			addMessage("account_created", account.getClass().getName());
		}
		else
		{
			Account account = XPersistence.getManager().find(Account.class, accountId);
			account.setCode(externalCode);
			account.setPerson(XPersistence.getManager().find(Person.class, personId));
			account.setAccountStatus(XPersistence.getManager().find(AccountStatus.class, accountStatusId));
			account = AccountHelper.updateAccount(account);
			addMessage("account_modified", account.getClass().getName());
		}

		// Create/Update Account Term
		super.execute();
		
		if (getErrors().isEmpty()) {
			accountId = getView().getValueString("accountId");
			AccountTerm accountTerm = XPersistence.getManager().find(AccountTerm.class, accountId);
	        
			installment(accountTerm);
			
            TransactionModule tm = XPersistence.getManager().find(TransactionModule.class, 
            		AccountTermHelper.TERM_OPENING_TRANSACTION_MODULE);
            List<Transaction> transactions =  (List<Transaction>)XPersistence.getManager().createQuery("SELECT o FROM Transaction o "
    				+ "WHERE o.transactionModule=:transactionModule AND o.creditAccount=:account")
    				.setParameter("transactionModule", tm)
    				.setParameter("account", accountTerm.getAccount())
    				.getResultList();
            
            if (transactions.isEmpty())
            {
            	Transaction transaction = TransactionHelper.getNewInitTransaction();
     			transaction.setTransactionModule(tm);
     			transaction.setTransactionStatus(tm.getDefaultTransactionStatus());
     			transaction.setValue(accountTerm.getAmount());
     			transaction.setRemark(accountTerm.getAccount().getAccountId());
     			transaction.setCreditAccount(accountTerm.getAccount());
     			transaction.setCurrency(accountTerm.getAccount().getCurrency());
     			
     			XPersistence.getManager().persist(transaction);
            }
            else if (transactions.size()>1)
            {
            	throw new InternalException("multiple_transactions_over_account_in_module", accountTerm.getAccountId(), tm.getTransactionModuleId());
            	
            }
            else
            {
            	Transaction transaction = (Transaction)transactions.get(0);
            	transaction.setTransactionModule(tm);
     			transaction.setTransactionStatus(tm.getDefaultTransactionStatus());
     			transaction.setValue(accountTerm.getAmount());
     			transaction.setRemark(accountTerm.getAccount().getAccountId());
     			transaction.setCreditAccount(accountTerm.getAccount());
     			transaction.setCurrency(accountTerm.getAccount().getCurrency());
     			
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

		if (!isCreateAccount)
			if (accountStatusId==null)
				throw new OperativeException("accountStatus_is_required :"+accountStatusId);
		
	}
	
	private void installment(AccountTerm accountTerm)
	{
		new TermInstallment().execute(accountTerm, null);
	}
}
