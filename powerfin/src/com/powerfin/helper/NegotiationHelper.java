package com.powerfin.helper;


import org.openxava.jpa.*;
import org.openxava.util.*;

import com.powerfin.model.*;
import com.powerfin.model.dto.*;
import com.powerfin.util.*;

public class NegotiationHelper {
	
	public final static String FILE_TYPE_ID_PERSON = "001"; 
	public final static String FILE_TYPE_ID_LOAN = "002"; 
	public final static String FILE_TYPE_ID_PAYTABLE = "003";
	public final static String FILE_TYPE_ID_DISBURSEMENT = "004";
	public final static String FILE_TYPE_ID_SALE_PORTFOLIO = "101";
		
	public final static Integer PERSON_FILE_NUM_COLUMNS = 18;
	public final static Integer LOAN_FILE_NUM_COLUMNS = 19;
	public final static Integer PAYTABLE_FILE_NUM_COLUMNS = 9;
	public final static Integer SALE_PORTFOLIO_FILE_NUM_COLUMNS = 4;
		
	public final static String NATURAL_PERSON = "NAT";
	public final static String LEGAL_PERSON = "LEG";
		
	public final static String MESSAGE_OK = "OK"; 
	
	public static String validateFieldsPersonFile(String[] dataLine) throws Exception {
		
		try{
			if(dataLine.length == PERSON_FILE_NUM_COLUMNS){
		    	
				NegotiationPersonDTO personDTO = new NegotiationPersonDTO(dataLine); 
				
				if(personDTO.getPersonType()==null || personDTO.getPersonType().isEmpty())
					return XavaResources.getString("person_type_is_requerid");
				else if(XPersistence.getManager().find(PersonType.class, personDTO.getPersonType())==null)
					return XavaResources.getString("person_type_not_found",personDTO.getPersonType());
				
				if(personDTO.getNationality()==null || personDTO.getNationality().isEmpty())
					return XavaResources.getString("nationality_is_required");
				else if(XPersistence.getManager().find(Country.class, personDTO.getNationality())==null)
					return XavaResources.getString("nationality_not_found", personDTO.getNationality());
				
				if(personDTO.getIdentificationType()==null || personDTO.getIdentificationType().isEmpty())
					return XavaResources.getString("identification_type_is_required");
				else if(XPersistence.getManager().find(IdentificationType.class, personDTO.getIdentificationType())==null)
					return XavaResources.getString("identification_type_not_found", personDTO.getIdentificationType());
				
				if(personDTO.getIdentification()==null || personDTO.getIdentification().isEmpty())
					return XavaResources.getString("identification_is_required");
				
				if(personDTO.getPaternalSurname()==null || personDTO.getPaternalSurname().isEmpty())
					return XavaResources.getString("paternal_surname_is_required");

				if(personDTO.getPersonType().equals(NATURAL_PERSON)){
					if(personDTO.getGender()==null || personDTO.getGender().isEmpty())
						return XavaResources.getString("gender_is_required");
					else if(XPersistence.getManager().find(Gender.class, personDTO.getGender())==null)
						return XavaResources.getString("gender_not_found", personDTO.getGender());
					
					if(personDTO.getFirtsName()==null || personDTO.getFirtsName().isEmpty())
						return XavaResources.getString("first_name_is_required");
					
					if(personDTO.getMaritalStatus()==null || personDTO.getMaritalStatus().isEmpty())
						return XavaResources.getString("marital_status_is_required");
					else if(XPersistence.getManager().find(MaritalStatus.class, personDTO.getMaritalStatus())==null)
						return XavaResources.getString("marital_status_not_found", personDTO.getMaritalStatus());
					
	    			if(personDTO.getBirthDate()==null || personDTO.getBirthDate().isEmpty())
	    				return XavaResources.getString("birth_date_is_required");
	    			else if(!UtilApp.isValidDate(personDTO.getBirthDate()))
						return XavaResources.getString("birth_date_format_is_yyyyMMdd");
				}//cierra condiciones por tipo persona NAT
												
				if(personDTO.getHomeDistrict()==null || personDTO.getHomeDistrict().isEmpty())
					return XavaResources.getString("district_is_required");
				else if(XPersistence.getManager().find(City.class, Integer.parseInt(personDTO.getHomeDistrict()))==null)
					return XavaResources.getString("district_not_found", personDTO.getHomeDistrict());
				
				if(personDTO.getMainStreet()==null || personDTO.getMainStreet().isEmpty())
					return XavaResources.getString("main_street_is_required");
				
				if(personDTO.getSideStreet()==null  || personDTO.getSideStreet().isEmpty())
					return XavaResources.getString("side_street_is_required");
				
				if(personDTO.getHomeNumber()==null || personDTO.getHomeNumber().isEmpty())
					return XavaResources.getString("home_number_is_required");
				
				if(personDTO.getHomeSector()==null || personDTO.getHomeSector().isEmpty())
					return XavaResources.getString("home_sector_is_required");
				
				if(personDTO.getActivity()==null || personDTO.getActivity().isEmpty())
					return XavaResources.getString("customer_activity_is_required");
				
				if(personDTO.getEmail()==null || personDTO.getEmail().isEmpty())
					return XavaResources.getString("email_is_required");
				
	        }else
	        	return XavaResources.getString("not_number_columns_required");
		
			return MESSAGE_OK;
		}catch(Exception e){
			e.printStackTrace();
			return e.getMessage();
		}
	}
	
	public static String validateFieldsLoanFile(String[] dataLine) throws Exception{
		
		try{
			if(dataLine.length == LOAN_FILE_NUM_COLUMNS){
	    	
				NegotiationLoanDTO loanDTO = new NegotiationLoanDTO(dataLine); 
				
				if(UtilApp.isNullOrEmpty(loanDTO.getIdentification()))
					return XavaResources.getString("customer_identification_is_required");

				if(UtilApp.isNullOrEmpty(loanDTO.getOriginalAccount()))
					return XavaResources.getString("loan_original_account_is_required");
            	
				if(UtilApp.isNullOrEmpty(loanDTO.getOriginalAmount()))
					return XavaResources.getString("amount_original_loan_is_required");
				else if(!UtilApp.isValidDecimalNumber(loanDTO.getOriginalAmount()))
					return XavaResources.getString("number_decimal_format_is_required","amount_original");
				
				if(UtilApp.isNullOrEmpty(loanDTO.getAmount()))
					return XavaResources.getString("amount_loan_is_required");
				else if(!UtilApp.isValidDecimalNumber(loanDTO.getAmount()))
					return XavaResources.getString("number_decimal_format_is_required","amount_loan");
				
				if(UtilApp.isNullOrEmpty(loanDTO.getPeriod()))
					return XavaResources.getString("period_is_required");
				else if(!UtilApp.isValidIntegerNumber(loanDTO.getPeriod()))
					return XavaResources.getString("number_integer_format_is_required","period");
				
				if(UtilApp.isNullOrEmpty(loanDTO.getQuotasNumber()))
					return XavaResources.getString("quotas_number_is_required");
				else if(!UtilApp.isValidIntegerNumber(loanDTO.getQuotasNumber()))
					return XavaResources.getString("number_integer_format_is_required","quotas_number");
				
				if(UtilApp.isNullOrEmpty(loanDTO.getPaymentDay()))
					return XavaResources.getString("payment_day_is_required");
				else if(!UtilApp.isValidIntegerNumber(loanDTO.getPaymentDay()))
					return XavaResources.getString("number_integer_format_is_required","payment_day");
				
				if(UtilApp.isNullOrEmpty(loanDTO.getPurchaseSpreadAmount()))
					return XavaResources.getString("spread_value_loan_is_required");
				else if(!UtilApp.isValidDecimalNumber(loanDTO.getPurchaseSpreadAmount()))
					return XavaResources.getString("number_decimal_format_is_required","spread");
				
				if(UtilApp.isNullOrEmpty(loanDTO.getPurchaseSpreadRate()))
					return XavaResources.getString("spread_rate_loan_is_required");
				else if(!UtilApp.isValidDecimalNumber(loanDTO.getPurchaseSpreadRate()))
					return XavaResources.getString("number_decimal_format_is_required","spread_rate");
				
				if(UtilApp.isNullOrEmpty(loanDTO.getStartDatePayment()))
					return XavaResources.getString("start_date_payment_is_required");
				else if(!UtilApp.isValidDate(loanDTO.getStartDatePayment()))
					return XavaResources.getString("start_date_payment_format_is_yyyyMMdd");
				
				if(UtilApp.isNullOrEmpty(loanDTO.getInterestRate()))
					return XavaResources.getString("interest_rate_loan_is_required");
				else if(!UtilApp.isValidDecimalNumber(loanDTO.getInterestRate()))
					return XavaResources.getString("number_decimal_format_is_required","interest_rate");
								
				if(UtilApp.isNullOrEmpty(loanDTO.getProductLoan()))
					return XavaResources.getString("product_is_required");
				else if((XPersistence.getManager().find(Product.class, loanDTO.getProductLoan()) == null))
					return XavaResources.getString("product_not_found", loanDTO.getProductLoan());
				
				if(UtilApp.isNullOrEmpty(loanDTO.getProductPayable()))
					return XavaResources.getString("product_payable_is_required");
				else if((XPersistence.getManager().find(Product.class, loanDTO.getProductPayable()) == null))
					return XavaResources.getString("product_payable_not_found", loanDTO.getProductPayable());
				
				if(UtilApp.isNullOrEmpty(loanDTO.getFrecuency()))
					return XavaResources.getString("frecuency_is_required");
				else if((XPersistence.getManager().find(Frecuency.class, Integer.parseInt(loanDTO.getFrecuency())) == null))
					return XavaResources.getString("frecuency_not_found", loanDTO.getFrecuency());
				
				if(UtilApp.isNullOrEmpty(loanDTO.getDaysGrace()))
					return XavaResources.getString("days_grace_is_required");
				else if(!UtilApp.isValidIntegerNumber(loanDTO.getDaysGrace()))
					return XavaResources.getString("number_integer_format_is_required","days_grace");
				
				if(!UtilApp.isNullOrEmpty(loanDTO.getInsuranceAccountId()))
				{
					if (XPersistence.getManager().find(Account.class, loanDTO.getInsuranceAccountId()) == null )
						return XavaResources.getString("insurance_account_not_found", loanDTO.getInsuranceAccountId());
				}
				
				if(!UtilApp.isNullOrEmpty(loanDTO.getMortgageAccountId()))
				{
					if (XPersistence.getManager().find(Account.class, loanDTO.getMortgageAccountId()) == null )
						return XavaResources.getString("mortgage_account_not_found", loanDTO.getMortgageAccountId());
				}
				
				if(!UtilApp.isNullOrEmpty(loanDTO.getInsuranceAmount()))
				{
					if(!UtilApp.isValidDecimalNumber(loanDTO.getInsuranceAmount()))
						return XavaResources.getString("number_decimal_format_is_required","insurance_amount");
				}
				
				if(!UtilApp.isNullOrEmpty(loanDTO.getMortgageAmount()))
				{
					if(!UtilApp.isValidDecimalNumber(loanDTO.getMortgageAmount()))
						return XavaResources.getString("number_decimal_format_is_required","mortgage_amount");
				}
				
	        }else{
	        	return XavaResources.getString("not_number_columns_required");
	        }

			return MESSAGE_OK;
			
		}catch(Exception e){
			e.printStackTrace();
			return e.getMessage();
		}
	}

	public static String validateFieldsSalePortfolio(String[] dataLine) throws Exception{
		
		try{
			if(dataLine.length == SALE_PORTFOLIO_FILE_NUM_COLUMNS){
	    	
				NegotiationSalePortfolioDTO loanDTO = new NegotiationSalePortfolioDTO(dataLine); 

				if(UtilApp.isNullOrEmpty(loanDTO.getOriginalAccount()))
					return XavaResources.getString("loan_original_account_is_required");
				
				if(UtilApp.isNullOrEmpty(loanDTO.getAmount()))
					return XavaResources.getString("amount_loan_is_required");
				else if(!UtilApp.isValidDecimalNumber(loanDTO.getAmount()))
					return XavaResources.getString("number_decimal_format_is_required","amount_loan");

				if(UtilApp.isNullOrEmpty(loanDTO.getSaleSpreadAmount()))
					return XavaResources.getString("sale_spread_value_loan_is_required");
				else if(!UtilApp.isValidDecimalNumber(loanDTO.getSaleSpreadAmount()))
					return XavaResources.getString("number_decimal_format_is_required","spread");
				
				if(UtilApp.isNullOrEmpty(loanDTO.getSaleSpreadRate()))
					return XavaResources.getString("spread_rate_loan_is_required");
				else if(!UtilApp.isValidDecimalNumber(loanDTO.getSaleSpreadRate()))
					return XavaResources.getString("number_decimal_format_is_required","spread_rate");
								
	        }else{
	        	return XavaResources.getString("not_number_columns_required");
	        }

			return MESSAGE_OK;
			
		}catch(Exception e){
			e.printStackTrace();
			return e.getMessage();
		}
	}

	public static String validateFieldsLoanPayTable(String[] dataLine) throws Exception{
		
		try{
			if(dataLine.length == PAYTABLE_FILE_NUM_COLUMNS){
	    	
				NegotiationPaytableDTO tableDTO = new NegotiationPaytableDTO(dataLine); 
				
				if(UtilApp.isNullOrEmpty(tableDTO.getOriginalAccount()))
					return XavaResources.getString("loan_original_account_is_required");
				
				if(UtilApp.isNullOrEmpty(tableDTO.getQuotaNumber()))
					return XavaResources.getString("quota_is_required");
				else if(!UtilApp.isValidIntegerNumber(tableDTO.getQuotaNumber()))
					return XavaResources.getString("number_integer_format_is_required","quota");
				
				if(UtilApp.isNullOrEmpty(tableDTO.getDueDate()))
					return XavaResources.getString("start_date_payment_is_required");
				else if(!UtilApp.isValidDate(tableDTO.getDueDate()))
					return XavaResources.getString("start_date_payment_format_is_yyyyMMdd");
				
				if(UtilApp.isNullOrEmpty(tableDTO.getProvisionDays()))
					return XavaResources.getString("number_days_quota_is_required");
				else if(!UtilApp.isValidIntegerNumber(tableDTO.getProvisionDays()))
					return XavaResources.getString("number_integer_format_is_required","provision_days");
				
				if(UtilApp.isNullOrEmpty(tableDTO.getCapitalReduced()))
					return XavaResources.getString("capital_reduce_is_required");
				else if(!UtilApp.isValidDecimalNumber(tableDTO.getCapitalReduced()))
					return XavaResources.getString("number_decimal_format_is_required","capital_reduce");
				
				if(UtilApp.isNullOrEmpty(tableDTO.getCapital()))
					return XavaResources.getString("capital_is_required");
				else if(!UtilApp.isValidDecimalNumber(tableDTO.getCapital()))
					return XavaResources.getString("number_decimal_format_is_required","capital");
				
				if(UtilApp.isNullOrEmpty(tableDTO.getInterest()))
					return XavaResources.getString("interest_is_required");
				else if(!UtilApp.isValidDecimalNumber(tableDTO.getInterest()))
					return XavaResources.getString("number_decimal_format_is_required","interest");
				
				if(UtilApp.isNullOrEmpty(tableDTO.getInsurance()))
					return XavaResources.getString("insurance_is_required");
				else if(!UtilApp.isValidDecimalNumber(tableDTO.getInsurance()))
					return XavaResources.getString("number_decimal_format_is_required","insurance");
				
				if(UtilApp.isNullOrEmpty(tableDTO.getInsuranceMortgage()))
					return XavaResources.getString("insurance_mortgage_is_required");
				else if(!UtilApp.isValidDecimalNumber(tableDTO.getInsuranceMortgage()))
					return XavaResources.getString("number_decimal_format_is_required","insurance_mortgage");
	        }else{
	        	return XavaResources.getString("not_number_columns_required");
	        }
			
			return MESSAGE_OK; 
		}catch(Exception e){
			e.printStackTrace();
			return e.getMessage();
		}
	}
	
	
	public static NegotiationOutput createNegotiationOutput(NegotiationFile negotiationFile, Integer lineNumber, String result, String stackTrace ) throws Exception 
	{
		NegotiationOutput no = new NegotiationOutput();
		no.setFile(negotiationFile.getFile());
        no.setFileName(XPersistence.getManager().find(File.class, negotiationFile.getFile()).getName());
        no.setFileType(negotiationFile.getNegotiationFileType().getNegotiationFileTypeId());
        no.setLineNumber(lineNumber);
        no.setNegotiation(negotiationFile.getNegotiation());
        no.setResult(result);
        no.setStackTrace(stackTrace);
        
        XPersistence.getManager().persist(no);
		return no;
		
	}
	
	public static void deleteNegotiationOutput(NegotiationFile negotiationFile)
	{
		XPersistence.getManager().createQuery("DELETE FROM NegotiationOutput o "
				+ "WHERE o.fileType=:fileType "
				+ "AND o.negotiation.negotiationId=:negotiationId")
		.setParameter("fileType", negotiationFile.getNegotiationFileType().getNegotiationFileTypeId())
		.setParameter("negotiationId", negotiationFile.getNegotiation().getNegotiationId())
		.executeUpdate();
	}
	
}
