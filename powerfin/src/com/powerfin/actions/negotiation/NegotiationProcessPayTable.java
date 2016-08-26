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

public class NegotiationProcessPayTable {
	
	private AccountPortfolio accountPortfolio = null;
	private AccountLoan accountLoan = null;
	private NegotiationFile negotiationFile = null;
	
	String[] dataLine;
	String validationMessages;
	
	NegotiationPaytableDTO tableDTO;
	
	public NegotiationProcessPayTable(NegotiationFile negotiationFile) {
		this.negotiationFile = negotiationFile;
	}
	
	@SuppressWarnings("unchecked")
	public void execute(String fileString) throws Exception{
		List<AccountPortfolio> accountsPortfolio = null;
		List<AccountLoan> accountsLoan = null;
        BufferedReader br = null;
        String delimiter = "\t";
        int daysPreviousPeriod = 0;
        int lineNumber = 1;
        int row=0;

    	try {
			br = new BufferedReader(new StringReader(fileString));
	        for(String line; (line = br.readLine()) != null; ) {
	        	if(row>0){//informacion desde la 2da linea
	        		lineNumber++;
	        		dataLine = line.split(delimiter);
	                validationMessages = NegotiationHelper.validateFieldsLoanPayTable(dataLine);
	                if(validationMessages.equals(NegotiationHelper.MESSAGE_OK)){
	                	tableDTO = new NegotiationPaytableDTO(dataLine); 
	        		
	                	if(Integer.parseInt(tableDTO.getQuotaNumber()) == 1)
	                	{
	                		accountsPortfolio = (List<AccountPortfolio>) XPersistence.getManager()
			     				.createQuery("select a from AccountPortfolio a where a.accountId = :accountId")  
			     				.setParameter("accountId", tableDTO.getOriginalAccount())
			     				.getResultList();
	                		
	                		accountsLoan = (List<AccountLoan>) XPersistence.getManager()
			     				.createQuery("select a from AccountLoan a where a.accountId = :accountId")  
			     				.setParameter("accountId", tableDTO.getOriginalAccount())
			     				.getResultList();
	                	
		                	if(!accountsPortfolio.isEmpty() && !accountsLoan.isEmpty()){
		                		accountPortfolio = (AccountPortfolio) accountsPortfolio.get(0);
		                		accountLoan = (AccountLoan) accountsLoan.get(0);		                		
			        		}
		                	else
			        			validationMessages=XavaResources.getString("account_loan_not_found_for_create_account_paytable", tableDTO.getOriginalAccount());
		                	
		                	deleteAccountPayTable();
	                	}
	                	
	                	daysPreviousPeriod = createAccountPayTable(daysPreviousPeriod);	                	
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

	private int createAccountPayTable(int daysPreviousPeriod) throws Exception
	{
		
		AccountPaytable accountPaytable = new AccountPaytable();
		AccountPaytableOld accountPaytableOld = new AccountPaytableOld(); 
		Integer daysCurrentPeriod = 0;
		try{
			accountPaytable.setAccountId(accountPortfolio.getAccountId());
			accountPaytableOld.setAccountId(accountPortfolio.getAccountId());
			
			accountPaytable.setCapital(new BigDecimal(tableDTO.getCapital()));
			accountPaytableOld.setCapital(new BigDecimal(tableDTO.getCapital()));
			
			accountPaytable.setCapitalReduced(new BigDecimal(tableDTO.getCapitalReduced()));
			accountPaytableOld.setCapitalReduced(new BigDecimal(tableDTO.getCapitalReduced()));
			
			accountPaytable.setCommission(BigDecimal.ZERO);
			accountPaytableOld.setCommission(BigDecimal.ZERO);
			
			accountPaytable.setDueDate(UtilApp.formatDate.parse(tableDTO.getDueDate()));
			accountPaytableOld.setDueDate(UtilApp.formatDate.parse(tableDTO.getDueDate()));
			
			accountPaytable.setInsurance(new BigDecimal(tableDTO.getInsurance()));
			accountPaytableOld.setInsurance(new BigDecimal(tableDTO.getInsurance()));
			
			accountPaytable.setInsuranceMortgage(new BigDecimal(tableDTO.getInsuranceMortgage()));
			accountPaytableOld.setInsuranceMortgage(new BigDecimal(tableDTO.getInsuranceMortgage()));
			
			accountPaytable.setInterest(new BigDecimal(tableDTO.getInterest()));
			accountPaytableOld.setInterest(new BigDecimal(tableDTO.getInterest()));
			
			accountPaytable.setProvisionDays(Integer.parseInt(tableDTO.getProvisionDays()));
			accountPaytableOld.setProvisionDays(Integer.parseInt(tableDTO.getProvisionDays()));
			
			accountPaytable.setSubaccount(Integer.parseInt(tableDTO.getQuotaNumber()));
			accountPaytableOld.setSubaccount(Integer.parseInt(tableDTO.getQuotaNumber()));
			
			daysCurrentPeriod = accountPaytable.getProvisionDays()+daysPreviousPeriod;

			accountPaytable.setPurchaseSpread(getAccumulatedProvision(accountLoan.getPeriod(), accountPortfolio.getPurchaseSpread(), daysPreviousPeriod, daysCurrentPeriod));
			accountPaytableOld.setPurchaseSpread(accountPaytable.getPurchaseSpread());
			
			accountPaytable.setUtilitySalePortfolio(BigDecimal.ZERO);
			accountPaytableOld.setUtilitySalePortfolio(BigDecimal.ZERO);
			
			accountPaytableOld.setPaytableType(this.negotiationFile.getNegotiation().getNegotiationType().getNegotiationTypeId());
			accountPaytableOld.setFromDate(CompanyHelper.getCurrentAccountingDate());
			accountPaytableOld.setToDate(UtilApp.DEFAULT_EXPIRY_DATE);
			
			XPersistence.getManager().persist(accountPaytable);
			XPersistence.getManager().persist(accountPaytableOld);
			
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}

		return daysCurrentPeriod;
	}
	
	private BigDecimal getAccumulatedProvision(int fullPeriod, BigDecimal valueToProvision, int daysPreviousPeriod, int daysCurrentPeriod )
	{
		BigDecimal dailyProvision = valueToProvision.divide(new BigDecimal(fullPeriod),6,RoundingMode.HALF_UP);
		BigDecimal oldProvision = dailyProvision.multiply(new BigDecimal(daysPreviousPeriod)).setScale(2, RoundingMode.HALF_UP);
		BigDecimal currentProvision = dailyProvision.multiply(new BigDecimal(daysCurrentPeriod));
		BigDecimal accumulatedProvision = currentProvision.subtract(oldProvision).setScale(2, RoundingMode.HALF_UP);
		return accumulatedProvision;
		
	}
	private void deleteAccountPayTable(){
				
		XPersistence.getManager().createQuery("DELETE FROM AccountPaytable o "
				+ "WHERE o.accountId=:accountId ")
		.setParameter("accountId", accountPortfolio.getAccountId())
		.executeUpdate();
		
		XPersistence.getManager().createQuery("DELETE FROM AccountPaytableOld o "
				+ "WHERE o.accountId=:accountId ")
		.setParameter("accountId", accountPortfolio.getAccountId())
		.executeUpdate();
	}

}
