package com.powerfin.actions.negotiation;

import java.io.*;
import java.math.*;
import java.util.*;

import org.openxava.jpa.*;
import org.openxava.util.*;

import com.powerfin.helper.*;
import com.powerfin.model.*;
import com.powerfin.model.dto.*;
import com.powerfin.util.*;

public class NegotiationProcessLoan {
	
	private Person person;
	private Product productPayable;
	private Product productLoan;
	private TransactionModule transactionModule;
	private NegotiationFile negotiationFile = null;
	private Account account = null;
	private Account accountPayable;
	private AccountLoan accountLoan = null;
	private AccountStatus portfolioStatusActive = null;
	private AccountStatus portfolioStatusRequest = null;
	
	String[] dataLine;
	String validationMessages;
	
	NegotiationLoanDTO loanDTO;
	
	public NegotiationProcessLoan(NegotiationFile negotiationFile) {
		this.negotiationFile = negotiationFile;
	}
	
	@SuppressWarnings({ "unchecked" })
	public void execute(String fileString) throws Exception{
        BufferedReader br = null;
        String delimiter = "\t";
        int lineNumber = 1;
        int row=0;
       
    	try {
    		
			br = new BufferedReader(new StringReader(fileString));
	        for(String line; (line = br.readLine()) != null; ) {
	        	if(row>0){//informacion desde la 2da linea
	        		lineNumber++;
	                dataLine = line.split(delimiter);
	                System.out.println("line: "+line );
	                validationMessages = NegotiationHelper.validateFieldsLoanFile(dataLine);
	                
	                if(validationMessages.equals(NegotiationHelper.MESSAGE_OK))
	                {
	                	loanDTO = new NegotiationLoanDTO(dataLine); 

	                	person = null;
	                	productPayable = null;
	                	productLoan = null;
	                	accountPayable = null;
	                	
	                	List<Person> persons = (List<Person>) XPersistence.getManager()
	        				.createQuery("select p from Person p where p.identification = :identification")  
	        				.setParameter("identification", loanDTO.getIdentification())
	        				.getResultList();
	                	
		        		if(!persons.isEmpty()){
		        			person = (Person) persons.get(0);
		        		}
		        		
		        		productPayable = XPersistence.getManager().find(Product.class, loanDTO.getProductPayable());
		        		productLoan = XPersistence.getManager().find(Product.class, loanDTO.getProductLoan());
		        		transactionModule = XPersistence.getManager().find(TransactionModule.class,	AccountLoanHelper.PURCHASE_PORTFOLIO_TRANSACTION_MODULE);
		        		portfolioStatusActive =  XPersistence.getManager().find(AccountStatus.class, AccountLoanHelper.PURCHASE_SALE_STATUS_ACTIVE);
		        		portfolioStatusRequest =  XPersistence.getManager().find(AccountStatus.class, AccountLoanHelper.PURCHASE_SALE_STATUS_REQUEST);
		        		
		        		if (person==null)
		        			validationMessages=XavaResources.getString("person_not_found_for_create_account", loanDTO.getIdentification());
		        		
		        		if (productLoan==null)
		        			validationMessages=XavaResources.getString("product_loan_not_found_for_create_account", loanDTO.getOriginalAccount());
		        		
		        		if (productPayable==null)
		        			validationMessages=XavaResources.getString("product_payable_not_found_for_create_account", loanDTO.getOriginalAccount());
		        		
		        		if (transactionModule==null)
		        			validationMessages=XavaResources.getString("transaction_module_not_found_for_create_account", loanDTO.getOriginalAccount());
		        		
		        		List<Account> accountsLoans = XPersistence.getManager()
			     				.createQuery("select a from Account a where a.accountId = :accountId")  
			     				.setParameter("accountId", loanDTO.getOriginalAccount())
			     				.getResultList();
		            	
		            	if(accountsLoans!=null && !accountsLoans.isEmpty()){
		            		validationMessages=XavaResources.getString("account_already_exists", loanDTO.getOriginalAccount());
		            	}
		            	
	        			List<Account> accountsPayables = XPersistence.getManager().createQuery("SELECT o FROM Account o "
	        					+ "WHERE o.person.personId = :personId "
	        					+ "AND o.product.productId = :productId")
	        					.setParameter("personId", person.getPersonId())
	        					.setParameter("productId", productPayable.getProductId())
	        					.getResultList();
	        			if (accountsPayables!=null && !accountsPayables.isEmpty())
	        				accountPayable = accountsPayables.get(0);
		        			
		        		//if (person==null || productLoan==null || productPayable==null || transactionModule==null)
	        			if(validationMessages.equals(NegotiationHelper.MESSAGE_OK))
	        			{
		        			createAccount();
		        			createAccountPayable();
		        			createAccountLoan();
		        			createAccountPortfolio();
		        			createTransaction();
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

	private void createAccount() throws Exception{
		
		account = new Account();
		
		try{
			account.setPerson(person);
			account.setProduct(productLoan);
			account.setAccountId(loanDTO.getOriginalAccount());
			account.setCurrency(account.getProduct().getCurrency());
			account = AccountHelper.createAccount(account);
			
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}
	
	private void createAccountLoan() throws Exception {
		
		accountLoan = new AccountLoan();
		
		try {
			accountLoan.setFrecuency(XPersistence.getManager().find(Frecuency.class,Integer.parseInt(loanDTO.getFrecuency()))); 
			accountLoan.setOriginalAccount(loanDTO.getOriginalAccount());
			accountLoan.setOriginalAmount(new BigDecimal(loanDTO.getOriginalAmount()));
			accountLoan.setAmount(new BigDecimal(loanDTO.getAmount()));
			accountLoan.setInterestRate(new BigDecimal(loanDTO.getInterestRate()));
			accountLoan.setStartDatePayment(UtilApp.formatDate.parse(loanDTO.getStartDatePayment()));
			accountLoan.setPeriod(Integer.parseInt(loanDTO.getPeriod()));
			accountLoan.setQuotasNumber(Integer.parseInt(loanDTO.getQuotasNumber()));
			accountLoan.setPaymentDay(Integer.parseInt(loanDTO.getPaymentDay()));
			accountLoan.setFixedQuota(null);
			accountLoan.setAccountId(account.getAccountId());
			accountLoan.setIssueDate(CompanyHelper.getCurrentAccountingDate());
			accountLoan.setDisbursementDate(accountLoan.getIssueDate());
			accountLoan.setDisbursementAccount(accountPayable);
			accountLoan.setContractNumber(loanDTO.getOriginalAccount());
			accountLoan.setDaysGrace(productLoan.getDaysGrace());
			accountLoan.setDaysGraceCollectionFee(productLoan.getDaysGraceCollectionFee());
			accountLoan.setApplyDefaultInterestAccrued(productLoan.getApplyDefaultInterestAccrued());
			accountLoan.setApplyAutomaticDebit(productLoan.getApplyAutomaticDebit());
			accountLoan.setPerson(person);
			accountLoan.setProduct(productPayable);
			if(!UtilApp.isNullOrEmpty(loanDTO.getInsuranceAccountId()))
				accountLoan.setInsuranceAccount(XPersistence.getManager().find(Account.class, loanDTO.getInsuranceAccountId()));
			if(!UtilApp.isNullOrEmpty(loanDTO.getMortgageAccountId()))
				accountLoan.setMortgageAccount(XPersistence.getManager().find(Account.class, loanDTO.getMortgageAccountId()));
			if(!UtilApp.isNullOrEmpty(loanDTO.getInsuranceAmount()))
				accountLoan.setInsuranceAmount(new BigDecimal(loanDTO.getInsuranceAmount()));
			if(!UtilApp.isNullOrEmpty(loanDTO.getMortgageAmount()))
				accountLoan.setInsuranceMortgageAmount(new BigDecimal(loanDTO.getMortgageAmount()));
			
			XPersistence.getManager().persist(accountLoan);
			
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}
	
	private void createAccountPayable() throws Exception{
		
		if (accountPayable==null)
		{
			Account newAccount = new Account();
			
			try{			
				newAccount.setPerson(person);
				newAccount.setProduct(productPayable);
				newAccount = AccountHelper.createAccount(newAccount);
				AccountPayable newAccountPayable = new AccountPayable();
				newAccountPayable.setPerson(person);
				newAccountPayable.setProduct(productPayable);
				newAccountPayable.setAccountId(newAccount.getAccountId());
				XPersistence.getManager().persist(newAccountPayable);
				
				accountPayable = newAccount;
			
			}catch(Exception e){
				e.printStackTrace();
				throw e;
			}
		}
	}
	
	private void createAccountPortfolio() throws Exception{
		try{
			
			AccountPortfolio accountPortfolio = new AccountPortfolio();
			accountPortfolio.setAccountId(account.getAccountId());
			accountPortfolio.setPurchaseRate(new BigDecimal(loanDTO.getPurchaseSpreadRate()));
			accountPortfolio.setPurchaseAmount(accountLoan.getAmount());
			accountPortfolio.setPurchaseSpread(new BigDecimal(loanDTO.getPurchaseSpreadAmount()));
			accountPortfolio.setStatusId(negotiationFile.getNegotiation().getNegotiationType().getNegotiationTypeId());
			accountPortfolio.setPurchaseNegotiation(negotiationFile.getNegotiation());
			accountPortfolio.setPurchaseStatus(portfolioStatusActive);
			accountPortfolio.setSaleStatus(portfolioStatusRequest);
			XPersistence.getManager().persist(accountPortfolio);
			
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}
	
	private void createTransaction() throws Exception{
		 
		try{
			Transaction transaction = TransactionHelper.getNewInitTransaction();
  			transaction.setTransactionModule(transactionModule);
  			transaction.setTransactionStatus(transactionModule.getDefaultTransactionStatus());
  			transaction.setValue(accountLoan.getAmount());
  			transaction.setRemark(account.getAccountId());
  			transaction.setDebitAccount(account);
  			transaction.setCurrency(account.getCurrency());
  			
  			XPersistence.getManager().persist(transaction);
  			
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}
	
	@SuppressWarnings("unused")
	private void deleteAccountLoan()
	{
		System.out.println("/////////////////////deleteAccountLoan");
		
		XPersistence.getManager().createQuery("DELETE FROM AccountLoan o "
				+ "WHERE o.account.accountId=:accountId ")
		.setParameter("accountId", loanDTO.getOriginalAccount())
		.executeUpdate();
		
		XPersistence.getManager().createQuery("DELETE FROM AccountPortfolio o "
				+ "WHERE o.account.accountId=:accountId ")
		.setParameter("accountId", loanDTO.getOriginalAccount())
		.executeUpdate();
		
		XPersistence.getManager().createQuery("DELETE FROM Account o "
				+ "WHERE o.accountId=:accountId ")
		.setParameter("accountId", loanDTO.getOriginalAccount())
		.executeUpdate();
	}
}
