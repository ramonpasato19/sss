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
import com.powerfin.util.UtilApp;

public class NegotiationSaleLoan {
	
	private NegotiationFile negotiationFile = null;
	
	String[] dataLine;
	String validationMessages;
	
	NegotiationSalePortfolioDTO loanDTO;
	
	public NegotiationSaleLoan(NegotiationFile negotiationFile) {
		this.negotiationFile = negotiationFile;
	}
	
	@SuppressWarnings({ "unchecked" })
	public void execute(String fileString) throws Exception{
        BufferedReader br = null;
        String delimiter = "\t";
        int lineNumber = 1;
        int row=0;
        
        BigDecimal capitalBalance = BigDecimal.ZERO;
		BigDecimal spreadPurchaseBalance = BigDecimal.ZERO;
		BigDecimal utilitySalePortfolio = BigDecimal.ZERO;

		Account account = null;
		AccountLoan accountLoan = null;
		AccountPortfolio accountPortfolio = null;
		TransactionAccount ta = null;
		Transaction transaction = null;
		List<TransactionAccount> transactionAccounts = null;
		TransactionModule transactionModule = null;
		TransactionStatus transactionStatus = null;
		Category capitalCategory = null;
		Category purchaseSpreadCategory = null;
		
    	try {
    		transactionModule = XPersistence.getManager().find(TransactionModule.class,	AccountLoanHelper.SALE_PORTFOLIO_TRANSACTION_MODULE);
    		transactionStatus =XPersistence.getManager().find(TransactionStatus.class, "002");
    		
    		capitalCategory = CategoryHelper.getCategoryById(CategoryHelper.CAPITAL_CATEGORY);
    		purchaseSpreadCategory = CategoryHelper.getCategoryById(CategoryHelper.PURCHASE_SPREAD_PR_CATEGORY);
    		
			br = new BufferedReader(new StringReader(fileString));
	        for(String line; (line = br.readLine()) != null; ) {
	        	
	        	if(row>0)
	        	{
	        		
	        		lineNumber++;
	                dataLine = line.split(delimiter);
	                System.out.println("line: "+line );
	                validationMessages = NegotiationHelper.MESSAGE_OK;
	                
	                if(validationMessages.equals(NegotiationHelper.MESSAGE_OK))
	                {
	                	loanDTO = new NegotiationSalePortfolioDTO(dataLine);

	                	transactionAccounts = new ArrayList<TransactionAccount>();
	                	account = XPersistence.getManager().find(Account.class, loanDTO.getOriginalAccount());
	                	accountLoan = XPersistence.getManager().find(AccountLoan.class, account.getAccountId());
	            		accountPortfolio = XPersistence.getManager().find(AccountPortfolio.class, account.getAccountId());
	            		            		
	            		if (accountPortfolio == null)
	                		throw new OperativeException("account_portfolio_not_found", account.getAccountId());
	            		
	            		if (accountLoan == null)
	                		throw new OperativeException("account_loan_not_found", account.getAccountId());
	            		
	            		List<Transaction> transactions =  XPersistence.getManager()
        						.createQuery("SELECT o FROM Transaction o "
        								+ "WHERE o.creditAccount.accountId = :accountId "
        								+ "AND o.transactionModule.transactionModuleId = :transactionModuleId "
        								+ "AND o.transactionStatus.transactionStatusId = :transactionStatusId")
        						.setParameter("accountId", account.getAccountId())
        						.setParameter("transactionModuleId", transactionModule.getTransactionModuleId())
        						.setParameter("transactionStatusId", transactionModule.getDefaultTransactionStatus().getTransactionStatusId())
        						.getResultList();
	            		
	            		if (transactions!=null && !transactions.isEmpty())
	            			transaction = transactions.get(0);
	            			            		
	            		List<Balance> balances = XPersistence.getManager()
	            				.createQuery("SELECT o FROM Balance o "
	            						+ "WHERE o.account.accountId = :accountId "
	            						+ "AND o.category.categoryId = :categoryId "
	            						+ "AND o.toDate = :toDate "
	            						+ "ORDER BY o.subaccount DESC")
	            				.setParameter("accountId", account.getAccountId())
	            				.setParameter("categoryId", capitalCategory.getCategoryId())
	            				.setParameter("toDate", UtilApp.DEFAULT_EXPIRY_DATE)
	            				.getResultList();
	            		
	            		List<Balance> spreadBalances = XPersistence.getManager()
	            				.createQuery("SELECT o FROM Balance o "
	            						+ "WHERE o.account.accountId = :accountId "
	            						+ "AND o.category.categoryId = :categoryId "
	            						+ "AND o.toDate = :toDate ")
	            				.setParameter("accountId", account.getAccountId())
	            				.setParameter("categoryId", purchaseSpreadCategory.getCategoryId())
	            				.setParameter("toDate", UtilApp.DEFAULT_EXPIRY_DATE)
	            				.getResultList();
        				
	            		capitalBalance = BigDecimal.ZERO;
	            		spreadPurchaseBalance = BigDecimal.ZERO;
	            		utilitySalePortfolio = BigDecimal.ZERO;
	            		
	            		if (balances==null || balances.isEmpty())
	                		throw new OperativeException("sale_not_processed_with_out_balances");
	            		else
	            			for (Balance balance : balances)
	            				capitalBalance = capitalBalance.add(balance.getBalance());
	            		
	            		if (capitalBalance.compareTo(BigDecimal.ZERO)<=0)
	                		throw new OperativeException("sale_not_processed_with_balance_zero");
	            		
	            		if (capitalBalance.compareTo(accountPortfolio.getSaleAmount())!=0)
	                		throw new OperativeException("sale_not_processed_balance_not_equal_sale_amount",capitalBalance,accountPortfolio.getSaleAmount());
	            		
	            		if (spreadBalances!=null && !spreadBalances.isEmpty())
	            			for (Balance spreadBalance : spreadBalances)
	            				spreadPurchaseBalance = spreadPurchaseBalance.add(spreadBalance.getBalance());
	            		
	            		if (accountPortfolio.getSaleSpread().compareTo(spreadPurchaseBalance)<0)
	            			throw new OperativeException("sale_not_processed_sale_spread_is_less_than_spread_purchase_balance");

	        			if(validationMessages.equals(NegotiationHelper.MESSAGE_OK))
	        			{
	        				try
		                	{
	        					//transfer amount broker to customer
	        					ta = TransactionAccountHelper.createCustomDebitTransactionAccount(accountPortfolio.getSaleNegotiation().getDebitCreditAccount(), accountPortfolio.getSaleAmount(), transaction);
	        					ta.setRemark(XavaResources.getString("transfer_to_customer_for_sale_portfolio"));
	        					transactionAccounts.add(ta);
	        					ta = TransactionAccountHelper.createCustomCreditTransactionAccount(accountLoan.getDisbursementAccount(), accountPortfolio.getSaleAmount(), transaction);
	        					ta.setRemark(XavaResources.getString("transfer_from_broker_for_sale_portfolio"));
	        					transactionAccounts.add(ta);
	        					
	        					//capital
	        					for (Balance balance : balances)
	        					{
	        						ta = TransactionAccountHelper.createCustomCreditTransactionAccount(account, balance.getSubaccount(), balance.getBalance(), transaction, capitalCategory, balance.getDueDate());
	        						ta.setRemark(XavaResources.getString("quota_number", balance.getSubaccount()));
	        						transactionAccounts.add(ta);
	        						
	        						ta = TransactionAccountHelper.createCustomDebitTransactionAccount(accountLoan.getDisbursementAccount(), balance.getBalance(), transaction);
	        						ta.setRemark(XavaResources.getString("quota_number", balance.getSubaccount()));
	        						transactionAccounts.add(ta);

	        					}
		        				
	        					//cancel spread purchase
	        					if (spreadPurchaseBalance.compareTo(BigDecimal.ZERO)>0)
	        					{
	        						ta = TransactionAccountHelper.createCustomCreditTransactionAccount(account, spreadPurchaseBalance, transaction, purchaseSpreadCategory);
	        						ta.setRemark(XavaResources.getString("cancel_purchase_spread"));
	        						transactionAccounts.add(ta);
	        						
	        						ta = TransactionAccountHelper.createCustomDebitTransactionAccount(accountPortfolio.getSaleNegotiation().getDebitCreditAccount(), spreadPurchaseBalance, transaction);
	        						ta.setRemark(XavaResources.getString("cancel_purchase_spread"));
	        						transactionAccounts.add(ta);
	        						
	        						utilitySalePortfolio = accountPortfolio.getSaleSpread().subtract(spreadPurchaseBalance);
	        					}
	        					else
	        					{
	        						utilitySalePortfolio = accountPortfolio.getSaleSpread();
	        					}
	        					
	        					//utility log
	        					if (utilitySalePortfolio.compareTo(BigDecimal.ZERO)>0)
	        					{
	        						BigDecimal incomeUtilitySalePortfolio = AccountLoanHelper.getIncomeUtilityDistribution(accountPortfolio);
	        						
	        						ta = TransactionAccountHelper.createCustomCreditTransactionAccount(account, utilitySalePortfolio, transaction, CategoryHelper.getCategoryById(CategoryHelper.UTILITY_SALE_PORTFOLIO_PR_CATEGORY));
	        						ta.setRemark(XavaResources.getString("utility_sale_portfolio"));
	        						transactionAccounts.add(ta);
	        						
	        						ta = TransactionAccountHelper.createCustomDebitTransactionAccount(accountPortfolio.getSaleNegotiation().getDebitCreditAccount(), utilitySalePortfolio, transaction);
	        						ta.setRemark(XavaResources.getString("utility_sale_portfolio"));
	        						transactionAccounts.add(ta);
	        						
	        						if (incomeUtilitySalePortfolio.compareTo(BigDecimal.ZERO) > 0)
	        						{
		        						ta = TransactionAccountHelper.createCustomDebitTransactionAccount(account, incomeUtilitySalePortfolio, transaction, CategoryHelper.getCategoryById(CategoryHelper.UTILITY_SALE_PORTFOLIO_PR_CATEGORY));
		        						ta.setRemark(XavaResources.getString("distribute_utility_sale_portfolio"));
		        						transactionAccounts.add(ta);
		        						
		        						ta = TransactionAccountHelper.createCustomCreditTransactionAccount(account, incomeUtilitySalePortfolio, transaction, CategoryHelper.getCategoryById(CategoryHelper.UTILITY_SALE_PORTFOLIO_IN_CATEGORY));
		        						ta.setRemark(XavaResources.getString("distribute_utility_sale_portfolio"));
		        						transactionAccounts.add(ta);
	        						}
	        					}

		        				transaction.setTransactionStatus(transactionStatus);
		        				TransactionHelper.processTransaction(transaction, transactionAccounts);
		        				
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
