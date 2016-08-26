package com.powerfin.actions.negotiation;

import java.io.*;
import java.math.*;
import java.util.*;

import org.openxava.jpa.*;
import org.openxava.util.*;

import com.powerfin.helper.*;
import com.powerfin.model.*;
import com.powerfin.model.dto.*;

public class NegotiationDisbursementLoan {
	
	private NegotiationFile negotiationFile = null;
	
	String[] dataLine;
	String validationMessages;
	
	NegotiationLoanDTO loanDTO;
	
	public NegotiationDisbursementLoan(NegotiationFile negotiationFile) {
		this.negotiationFile = negotiationFile;
	}
	
	@SuppressWarnings({ "unchecked" })
	public void execute(String fileString) throws Exception{
        BufferedReader br = null;
        String delimiter = "\t";
        int lineNumber = 1;
        int row=0;
        
        BigDecimal totalCapital = null;
		Account account = null;
		AccountLoan loan = null;
		AccountPortfolio portfolio = null;
		TransactionAccount ta = null;
		Transaction transaction = null;
		List<AccountPaytable> quotas = null;
		List<TransactionAccount> transactionAccounts = null;
		TransactionModule transactionModule = null;
		TransactionStatus transactionStatus = null;
		
    	try {
    		transactionModule = XPersistence.getManager().find(TransactionModule.class,	AccountLoanHelper.PURCHASE_PORTFOLIO_TRANSACTION_MODULE);
    		transactionStatus =XPersistence.getManager().find(TransactionStatus.class, "002");
    		
			br = new BufferedReader(new StringReader(fileString));
	        for(String line; (line = br.readLine()) != null; ) {
	        	//XPersistence.getManager().clear();
	        	if(row>0){//informacion desde la 2da linea
	        		lineNumber++;
	                dataLine = line.split(delimiter);
	                System.out.println("line: "+line );
	                validationMessages = NegotiationHelper.MESSAGE_OK;
	                
	                if(validationMessages.equals(NegotiationHelper.MESSAGE_OK))
	                {
	                	loanDTO = new NegotiationLoanDTO(dataLine); 

	                	totalCapital = BigDecimal.ZERO;
	                	transactionAccounts = new ArrayList<TransactionAccount>();
	                	account = XPersistence.getManager().find(Account.class, loanDTO.getOriginalAccount());
	            		loan = XPersistence.getManager().find(AccountLoan.class, account.getAccountId());
	            		portfolio = XPersistence.getManager().find(AccountPortfolio.class, account.getAccountId());
	            		
	            		
	            		List<Transaction> transactions =  XPersistence.getManager()
        						.createQuery("SELECT o FROM Transaction o "
        								+ "WHERE o.debitAccount.accountId = :accountId "
        								+ "AND o.transactionModule.transactionModuleId = :transactionModuleId "
        								+ "AND o.transactionStatus.transactionStatusId = :transactionStatusId")
        						.setParameter("accountId", account.getAccountId())
        						.setParameter("transactionModuleId", transactionModule.getTransactionModuleId())
        						.setParameter("transactionStatusId", transactionModule.getDefaultTransactionStatus().getTransactionStatusId())
        						.getResultList();
	            		
	            		if (transactions!=null && !transactions.isEmpty())
	            			transaction = transactions.get(0);
	            		
	            		quotas = XPersistence.getManager()
        						.createQuery("SELECT o FROM AccountPaytable o "
        								+ "WHERE o.accountId = :accountId "
        								+ "ORDER BY o.subaccount")
        						.setParameter("accountId", account.getAccountId())
        						.getResultList();
        				
        				
        				if (quotas==null || quotas.isEmpty() || loan.getAmount().compareTo(BigDecimal.ZERO)==0)
        					validationMessages=XavaResources.getString("loan_not_processed_with_out_paytable");
        		    	
        				
        				if (portfolio == null)
        					validationMessages=XavaResources.getString("account_portfolio_not_found", account.getAccountId());
        				
        				
		        		if (account.getAccountStatus().getAccountStatusId().equals(AccountLoanHelper.STATUS_LOAN_ACTIVE))
		        			validationMessages=XavaResources.getString("account_already_disbursement", loanDTO.getOriginalAccount());
		        		
		        		if (transaction == null)
		        			validationMessages=XavaResources.getString("disbursement_transaction_not_found", loanDTO.getOriginalAccount());

	        			if(validationMessages.equals(NegotiationHelper.MESSAGE_OK))
	        			{
	        				try
		                	{
		        				//capital
		        				for (AccountPaytable quota: quotas)
		        				{
		        					ta = TransactionAccountHelper.createCustomDebitTransactionAccount(account, quota.getSubaccount(), quota.getCapital(), transaction, CategoryHelper.getCategoryById(CategoryHelper.CAPITAL_CATEGORY), quota.getDueDate());
		        					ta.setRemark(XavaResources.getString("quota_number", quota.getSubaccount()));
		        					transactionAccounts.add(ta);
		        					
		        					ta = TransactionAccountHelper.createCustomCreditTransactionAccount(loan.getDisbursementAccount(), quota.getCapital(), transaction);
		        					ta.setRemark(XavaResources.getString("quota_number", quota.getSubaccount()));
		        					transactionAccounts.add(ta);
		        					totalCapital = totalCapital.add(quota.getCapital());
		        				}
		        				
		        				//spread_purchase
		        				if (portfolio.getPurchaseSpread()!=null)
		        				{
		        					if (portfolio.getPurchaseSpread().compareTo(BigDecimal.ZERO)>0)
		        					{
		        						ta = TransactionAccountHelper.createCustomDebitTransactionAccount(account, portfolio.getPurchaseSpread(), transaction, CategoryHelper.getCategoryById(CategoryHelper.PURCHASE_SPREAD_PR_CATEGORY));
		        						ta.setRemark(XavaResources.getString("purchase_spread"));
		        						transactionAccounts.add(ta);
		        						
		        						ta = TransactionAccountHelper.createCustomCreditTransactionAccount(portfolio.getPurchaseNegotiation().getDebitCreditAccount(), portfolio.getPurchaseSpread(), transaction);
		        						ta.setRemark(XavaResources.getString("purchase_spread"));
		        						transactionAccounts.add(ta);
		        					}
		        					if (portfolio.getPurchaseSpread().compareTo(BigDecimal.ZERO)<0)
		        					{
		        						//TODO: Falta realizar el proceso cuando el spread es negativo, ganacia en spread de compra. 
		        					}
		        				}
		        				
		        				//transfer capital to broker
		        				ta = TransactionAccountHelper.createCustomDebitTransactionAccount(loan.getDisbursementAccount(), totalCapital, transaction);
		        				ta.setRemark(XavaResources.getString("transfer_loan_amount_to_broker"));
		        				transactionAccounts.add(ta);
		        				
		        				ta = TransactionAccountHelper.createCustomCreditTransactionAccount(portfolio.getPurchaseNegotiation().getDebitCreditAccount(), totalCapital, transaction);
		        				ta.setRemark(XavaResources.getString("transfer_loan_amount_from_customer"));
		        				transactionAccounts.add(ta);
		        				
		        				transaction.setTransactionStatus(transactionStatus);
		        				TransactionHelper.processTransaction(transaction, transactionAccounts);
		        				
		        				account.setAccountStatus(AccountStatusHelper.getAccountStatus(AccountLoanHelper.STATUS_LOAN_ACTIVE));
		        				AccountHelper.updateAccount(account);
		        				//XPersistence.getManager().flush();
		                	}
	        				catch (Exception e)
	        				{
	        					e.printStackTrace();
	        					throw e;
	        				}
	        			}
		        		
	                }
	        		NegotiationHelper.createNegotiationOutput(negotiationFile, lineNumber, validationMessages, null);
	        	}
	            row++;
	        }//fin del for
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			try {
				if (br != null)
					br.close();
	
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
