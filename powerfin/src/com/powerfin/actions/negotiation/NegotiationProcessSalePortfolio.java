package com.powerfin.actions.negotiation;

import java.io.*;
import java.math.*;
import java.util.*;

import org.openxava.jpa.*;
import org.openxava.util.*;

import com.powerfin.exception.OperativeException;
import com.powerfin.helper.*;
import com.powerfin.model.*;
import com.powerfin.model.dto.*;
import com.powerfin.util.*;

public class NegotiationProcessSalePortfolio {
	
	private TransactionModule transactionModule;
	private NegotiationFile negotiationFile = null;
	private AccountPortfolio accountPortfolio = null;
	private AccountLoan accountLoan = null;
	List<AccountPaytable> payTables;
	private AccountStatus portfolioStatus = null; 
	private AccountPortfolioStatus accountPortfolioStatus = null;
	private Date currentAccountingDate = null;
	String[] dataLine;
	String validationMessages;
	
	NegotiationSalePortfolioDTO loanDTO;
	
	public NegotiationProcessSalePortfolio(NegotiationFile negotiationFile) {
		this.negotiationFile = negotiationFile;
	}
	
	@SuppressWarnings({ "unchecked" })
	public void execute(String fileString) throws Exception{
        BufferedReader br = null;
        String delimiter = "\t";
        int lineNumber = 1;
        int row=0;
        BigDecimal capitalBalance;
        BigDecimal spreadPurchaseBalance = BigDecimal.ZERO;
        Category purchaseSpreadCategory = null;
        
        currentAccountingDate = CompanyHelper.getCurrentAccountingDate();
        
        accountPortfolioStatus = XPersistence.getManager().find(AccountPortfolioStatus.class, AccountLoanHelper.SALE_PORTFOLIO_STATUS_ID);
    	try {
    		purchaseSpreadCategory = CategoryHelper.getCategoryById(CategoryHelper.PURCHASE_SPREAD_PR_CATEGORY);
			br = new BufferedReader(new StringReader(fileString));
	        for(String line; (line = br.readLine()) != null; ) {
	        	if(row>0){//informacion desde la 2da linea
	        		lineNumber++;
	                dataLine = line.split(delimiter);
	                System.out.println("line: "+line );
	                validationMessages = NegotiationHelper.validateFieldsSalePortfolio(dataLine);
	                
	                if(validationMessages.equals(NegotiationHelper.MESSAGE_OK))
	                {
	                	loanDTO = new NegotiationSalePortfolioDTO(dataLine); 
	                		        		
		        		transactionModule = XPersistence.getManager().find(TransactionModule.class,	AccountLoanHelper.SALE_PORTFOLIO_TRANSACTION_MODULE);
		        		portfolioStatus =  XPersistence.getManager().find(AccountStatus.class,	AccountLoanHelper.PURCHASE_SALE_STATUS_ACTIVE);
		        		
		        		if (transactionModule==null)
		        			validationMessages=XavaResources.getString("transaction_module_not_found", AccountLoanHelper.SALE_PORTFOLIO_TRANSACTION_MODULE);
		            	
		        		payTables = XPersistence.getManager()
		         				.createQuery("SELECT a FROM AccountPaytable a WHERE a.accountId = :accountId "
		         						+ "AND a.subaccount >= :fromSubaccount "
		         						+ "ORDER BY a.subaccount")  
		         				.setParameter("accountId", loanDTO.getOriginalAccount())
		         				.setParameter("fromSubaccount", Integer.parseInt(loanDTO.getSaleFromSubaccount()))
		         				.getResultList();
		        		
		        		List<Balance> balances = XPersistence.getManager()
	            				.createQuery("SELECT o FROM Balance o "
	            						+ "WHERE o.account.accountId = :accountId "
	            						+ "AND o.category.categoryId = :categoryId "
	            						+ "AND o.toDate = :toDate "
	            						+ "AND o.subaccount >= :fromSubaccount "
	            						+ "ORDER BY o.subaccount DESC")
	            				.setParameter("accountId", loanDTO.getOriginalAccount())
	            				.setParameter("categoryId", CategoryHelper.CAPITAL_CATEGORY)
	            				.setParameter("toDate", UtilApp.DEFAULT_EXPIRY_DATE)
	            				.setParameter("fromSubaccount", Integer.parseInt(loanDTO.getSaleFromSubaccount()))
	            				.getResultList();
		        		
		        		List<Balance> spreadBalances = XPersistence.getManager()
	            				.createQuery("SELECT o FROM Balance o "
	            						+ "WHERE o.account.accountId = :accountId "
	            						+ "AND o.category.categoryId = :categoryId "
	            						+ "AND o.toDate = :toDate ")
	            				.setParameter("accountId", loanDTO.getOriginalAccount())
	            				.setParameter("categoryId", purchaseSpreadCategory.getCategoryId())
	            				.setParameter("toDate", UtilApp.DEFAULT_EXPIRY_DATE)
	            				.getResultList();
		        		
		        		capitalBalance = BigDecimal.ZERO;
		        		spreadPurchaseBalance = BigDecimal.ZERO;
		        		
		        		if (balances==null || balances.isEmpty())
	                		throw new OperativeException("sale_not_processed_with_out_balances");
	            		else
	            			for (Balance balance : balances)
	            				capitalBalance = capitalBalance.add(balance.getBalance());
		        		
		        		if (capitalBalance.compareTo(BigDecimal.ZERO)<=0)
	                		throw new OperativeException("sale_not_processed_with_balance_zero");
	            		
	            		if (capitalBalance.compareTo(new BigDecimal(loanDTO.getAmount()))<0)
	                		throw new OperativeException("sale_not_processed_balance_is_less_than_sale_amount",capitalBalance,new BigDecimal(loanDTO.getAmount()));
	            		
	            		if (capitalBalance.compareTo(new BigDecimal(loanDTO.getAmount()))>0)
	                		throw new OperativeException("sale_not_processed_sale_amout_less_than_balance",capitalBalance,new BigDecimal(loanDTO.getAmount()));
	            		
	            		if (spreadBalances!=null && !spreadBalances.isEmpty())
	            			for (Balance spreadBalance : spreadBalances)
	            				spreadPurchaseBalance = spreadPurchaseBalance.add(spreadBalance.getBalance());
	            		
		        		accountLoan = XPersistence.getManager().find(AccountLoan.class, loanDTO.getOriginalAccount());
		        		accountPortfolio = XPersistence.getManager().find(AccountPortfolio.class, loanDTO.getOriginalAccount());
		        		
		        		if(accountPortfolio == null)
		            		validationMessages=XavaResources.getString("account_portfolio_not_found", loanDTO.getOriginalAccount());
		        		
		        		if(accountPortfolio.getAccountPortfolioStatus().getAccountPortfolioStatusId().equals(AccountLoanHelper.SALE_PORTFOLIO_STATUS_ID))
		            		validationMessages=XavaResources.getString("account_already_sale", loanDTO.getOriginalAccount());
		        		
		        		if(accountLoan == null)
		            		validationMessages=XavaResources.getString("account_loan_not_found", loanDTO.getOriginalAccount());        			
		        		
		        		if(payTables==null || payTables.isEmpty())
		        			validationMessages=XavaResources.getString("sale_portfolio_not_process_paytables_not_found", loanDTO.getOriginalAccount());
		        		
	        			if(validationMessages.equals(NegotiationHelper.MESSAGE_OK))
	        			{
		        			updateAccountPortfolio();
		        			updateAccountPayTable(spreadPurchaseBalance);
		        			createAccountSoldPayTable();
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
	
	private void createAccountSoldPayTable() throws Exception
	{
		int daysPreviousPeriod = 0;
		int daysCurrentPeriod = 0;
		int fullPeriod = 0;
		int subaccount = 0;
		Date lastDueDate = null;
		
		BigDecimal saleSpread = accountPortfolio.getSaleSpread();
		
		try{
			for (AccountPaytable accountPaytable : payTables)
			{
				lastDueDate = accountPaytable.getDueDate();
			}
			
			fullPeriod = UtilApp.getDaysCountBetweenDates(currentAccountingDate, lastDueDate);
			
			for (AccountPaytable accountPaytable : payTables)
			{
				subaccount ++;
				daysCurrentPeriod = 0;
				if (accountPaytable.getSubaccount() == Integer.parseInt(loanDTO.getSaleFromSubaccount()) )
				{
					daysCurrentPeriod = UtilApp.getDaysCountBetweenDates(currentAccountingDate, accountPaytable.getDueDate());
				}
				else
					daysCurrentPeriod = accountPaytable.getProvisionDays()+daysPreviousPeriod;
				
				AccountSoldPaytable soldPaytable = new AccountSoldPaytable();
				AccountPaytableOld soldPaytableOld = new AccountPaytableOld();
				
				soldPaytable.setAccountId(accountPortfolio.getAccountId());
				soldPaytableOld.setAccountId(accountPortfolio.getAccountId());
				
				soldPaytable.setCapital(accountPaytable.getCapital());
				soldPaytableOld.setCapital(accountPaytable.getCapital());
				
				soldPaytable.setCapitalReduced(accountPaytable.getCapitalReduced());
				soldPaytableOld.setCapitalReduced(accountPaytable.getCapitalReduced());
				
				soldPaytable.setCommission(BigDecimal.ZERO);
				soldPaytableOld.setCommission(BigDecimal.ZERO);
				
				soldPaytable.setDueDate(accountPaytable.getDueDate());
				soldPaytableOld.setDueDate(accountPaytable.getDueDate());
				
				soldPaytable.setInsurance(BigDecimal.ZERO);
				soldPaytableOld.setInsurance(BigDecimal.ZERO);
				
				soldPaytable.setInsuranceMortgage(BigDecimal.ZERO);
				soldPaytableOld.setInsuranceMortgage(BigDecimal.ZERO);
				
				soldPaytable.setInterest(accountPaytable.getInterest());
				soldPaytableOld.setInterest(accountPaytable.getInterest());
				
				if (accountPaytable.getSubaccount() == Integer.parseInt(loanDTO.getSaleFromSubaccount()) )
				{
					soldPaytable.setProvisionDays(UtilApp.getDaysCountBetweenDates(currentAccountingDate, accountPaytable.getDueDate()));
					soldPaytableOld.setProvisionDays(UtilApp.getDaysCountBetweenDates(currentAccountingDate, accountPaytable.getDueDate()));
				}
				else
				{
					soldPaytable.setProvisionDays(accountPaytable.getProvisionDays());
					soldPaytableOld.setProvisionDays(accountPaytable.getProvisionDays());
				}

				soldPaytable.setSubaccount(accountPaytable.getSubaccount());
				soldPaytableOld.setSubaccount(accountPaytable.getSubaccount());
	
				soldPaytable.setPurchaseSpread(getAccumulatedProvision(fullPeriod, saleSpread, daysPreviousPeriod, daysCurrentPeriod));
				soldPaytableOld.setPurchaseSpread(soldPaytable.getPurchaseSpread());
				
				soldPaytable.setUtilitySalePortfolio(BigDecimal.ZERO);
				soldPaytableOld.setUtilitySalePortfolio(BigDecimal.ZERO);
				
				soldPaytableOld.setPaytableType(this.negotiationFile.getNegotiation().getNegotiationType().getNegotiationTypeId());
				soldPaytableOld.setFromDate(CompanyHelper.getCurrentAccountingDate());
				soldPaytableOld.setToDate(UtilApp.DEFAULT_EXPIRY_DATE);
				
				soldPaytable.setSaleSubaccount(subaccount);
				
				XPersistence.getManager().persist(soldPaytable);
				XPersistence.getManager().persist(soldPaytableOld);
				
				daysPreviousPeriod = daysCurrentPeriod;
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}
	
	private void updateAccountPortfolio() throws Exception{
		try{			
			accountPortfolio.setSaleRate(new BigDecimal(loanDTO.getSaleSpreadRate()));
			accountPortfolio.setSaleAmount(new BigDecimal(loanDTO.getAmount()));
			accountPortfolio.setSaleSpread(new BigDecimal(loanDTO.getSaleSpreadAmount()));
			accountPortfolio.setAccountPortfolioStatus(accountPortfolioStatus);
			accountPortfolio.setSaleNegotiation(negotiationFile.getNegotiation());
			accountPortfolio.setSaleStatus(portfolioStatus);
			accountPortfolio.setSalePortfolioUtilityDistribution(accountLoan.getAccount().getProduct().getSalePortfolioUtilityDistribution());
			XPersistence.getManager().merge(accountPortfolio);
			
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}
	
	private void updateAccountPayTable(BigDecimal puchaseSpread) throws Exception
	{
		int daysPreviousPeriod = 0;
		int daysCurrentPeriod = 0;
		int fullPeriod = 0;
		Date lastDueDate = null;
		BigDecimal utility = accountPortfolio.getSaleSpread().subtract(puchaseSpread);
		
		try{
			
			if (utility.compareTo(BigDecimal.ZERO)>0)
			{
				BigDecimal incomeUtility = AccountLoanHelper.getIncomeUtilityDistribution(accountPortfolio);
				
				if (incomeUtility.compareTo(BigDecimal.ZERO) > 0)
					utility = utility.subtract(incomeUtility).setScale(2, RoundingMode.HALF_UP);
				
				for (AccountPaytable accountPaytable : payTables)
				{
					lastDueDate = accountPaytable.getDueDate();
				}
				
				fullPeriod = UtilApp.getDaysCountBetweenDates(currentAccountingDate, lastDueDate);
						
				for (AccountPaytable accountPaytable : payTables)
				{
					daysCurrentPeriod = 0;
		
					if (accountPaytable.getSubaccount() == Integer.parseInt(loanDTO.getSaleFromSubaccount()) )
					{
						daysCurrentPeriod = UtilApp.getDaysCountBetweenDates(currentAccountingDate, accountPaytable.getDueDate());
					}
					else
						daysCurrentPeriod = accountPaytable.getProvisionDays()+daysPreviousPeriod;
					
					accountPaytable.setUtilitySalePortfolio(getAccumulatedProvision(fullPeriod, utility, daysPreviousPeriod, daysCurrentPeriod));
					
					XPersistence.getManager().merge(accountPaytable);
					
					daysPreviousPeriod = daysCurrentPeriod; 
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}
	
	private BigDecimal getAccumulatedProvision(int fullPeriod, BigDecimal valueToProvision, int daysPreviousPeriod, int daysCurrentPeriod )
	{
		BigDecimal dailyProvision = valueToProvision.divide(new BigDecimal(fullPeriod),6,RoundingMode.HALF_UP);
		BigDecimal oldProvision = dailyProvision.multiply(new BigDecimal(daysPreviousPeriod)).setScale(2, RoundingMode.HALF_UP);
		BigDecimal currentProvision = dailyProvision.multiply(new BigDecimal(daysCurrentPeriod));
		BigDecimal accumulatedProvision = currentProvision.subtract(oldProvision).setScale(2, RoundingMode.HALF_UP);
		return accumulatedProvision;
	}
	
	private void createTransaction() throws Exception{
		 
		try{
			Transaction transaction = TransactionHelper.getNewInitTransaction();
  			transaction.setTransactionModule(transactionModule);
  			transaction.setTransactionStatus(transactionModule.getDefaultTransactionStatus());
  			transaction.setValue(new BigDecimal(loanDTO.getAmount()));
  			transaction.setRemark(accountPortfolio.getAccount().getAccountId());
  			transaction.setCreditAccount(accountPortfolio.getAccount());
  			transaction.setCurrency(accountPortfolio.getAccount().getCurrency());
  			
  			XPersistence.getManager().persist(transaction);
  			
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}
	
}
