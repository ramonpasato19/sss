package com.powerfin.actions.accountLoan;

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

public class PreparePrepaymentSaveAction extends SaveAction{

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
			
			if (!account.getAccountStatus().getAccountStatusId().equals(AccountLoanHelper.STATUS_LOAN_REQUEST))
				throw new OperativeException("account_has_already_been_processed", accountId);
			
			account.setAccountStatus(XPersistence.getManager().find(AccountStatus.class, accountStatusId));
			account = AccountHelper.updateAccount(account);
			addMessage("account_modified", account.getClass().getName());
		}

		// Create/Update Account Loan
		super.execute();
		
		if (getErrors().isEmpty()) {
			accountId = getView().getValueString("accountId");
			AccountLoan accountLoan = XPersistence.getManager().find(AccountLoan.class, accountId);
	        
			installment(accountLoan);
			
            TransactionModule tm = XPersistence.getManager().find(TransactionModule.class, 
            		AccountLoanHelper.LOAN_DISBURSEMENT_TRANSACTION_MODULE);
            List<Transaction> transactions =  (List<Transaction>)XPersistence.getManager().createQuery("SELECT o FROM Transaction o "
    				+ "WHERE o.transactionModule=:transactionModule AND o.debitAccount=:account")
    				.setParameter("transactionModule", tm)
    				.setParameter("account", accountLoan.getAccount())
    				.getResultList();
            
            if (transactions.isEmpty())
            {
            	Transaction transaction = TransactionHelper.getNewInitTransaction();
     			transaction.setTransactionModule(tm);
     			transaction.setTransactionStatus(tm.getDefaultTransactionStatus());
     			transaction.setValue(accountLoan.getAmount());
     			transaction.setRemark(accountLoan.getAccount().getAccountId());
     			transaction.setDebitAccount(accountLoan.getAccount());
     			transaction.setCurrency(accountLoan.getAccount().getCurrency());
     			
     			XPersistence.getManager().persist(transaction);
            }
            else if (transactions.size()>1)
            {
            	throw new InternalException("multiple_transactions_over_account_in_module", accountLoan.getAccountId(), tm.getTransactionModuleId());
            	
            }
            else
            {
            	Transaction transaction = (Transaction)transactions.get(0);
            	transaction.setTransactionModule(tm);
     			transaction.setTransactionStatus(tm.getDefaultTransactionStatus());
     			transaction.setValue(accountLoan.getAmount());
     			transaction.setRemark(accountLoan.getAccount().getAccountId());
     			transaction.setDebitAccount(accountLoan.getAccount());
     			transaction.setCurrency(accountLoan.getAccount().getCurrency());
     			
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
	
	private void installment(AccountLoan accountLoan)
	{
		new Installment().execute(accountLoan, null);
		
	}
}
