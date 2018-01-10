package com.powerfin.actions.negotiation;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;
import org.openxava.model.*;

import com.powerfin.helper.*;
import com.powerfin.model.*;

public class NegotiationProcess extends SaveAction {

	private String negotiationFileTypeId;
	private Negotiation negotiation = null;
	private NegotiationFile negotiationFile = null;
	private String fileString = null;
	
	@SuppressWarnings("rawtypes")
	public void execute() throws Exception {
		//super.execute();

		try{
			if(getErrors().isEmpty()){
				Map keyValues = getView().getKeyValues();
				negotiation= (Negotiation) MapFacade.findEntity(getView()
						.getModelName(), keyValues);
				
				negotiationFile = (NegotiationFile) XPersistence.getManager()
						.createQuery("select nf from NegotiationFile nf "
								+ "where nf.negotiation.negotiationId=:negotiationId "
								+ "and nf.negotiationFileType.negotiationFileTypeId=:negotiationFileTypeId")
						.setParameter("negotiationId", negotiation.getNegotiationId())
						.setParameter("negotiationFileTypeId", this.negotiationFileTypeId)
						.getSingleResult();
						
				if(negotiationFile.getFile()!=null){
					NegotiationReadFile negotiationReadFile = 
							new NegotiationReadFile(negotiationFile.getFile());
					fileString = negotiationReadFile.execute();
					NegotiationHelper.deleteNegotiationOutput(negotiationFile);
					selectReadFileProcess();
				}else
					addError("negotiation_file_not_found", getModelName());
			}
		}catch(Exception ex){
			ex.printStackTrace();
			if(ex.getMessage().contains("No entity found for query"))
				addError("no_entity_found_for_query");
		}
	}
	
	
	public void selectReadFileProcess() throws Exception{
		try{
			 if(negotiationFileTypeId.equals(NegotiationHelper.FILE_TYPE_ID_PERSON)){
				 NegotiationProcessPerson negotiationProcessPerson =
						 new NegotiationProcessPerson(negotiationFile);
				 negotiationProcessPerson.execute(fileString);
				
			 }else if(negotiationFileTypeId.equals(NegotiationHelper.FILE_TYPE_ID_PROCESS_PURCHASE_PORTFOLIO)){
				 NegotiationProcessPurchasePortfolio negotiationProcessLoan =
						 new NegotiationProcessPurchasePortfolio(negotiationFile);
				 negotiationProcessLoan.execute(fileString);
				 
			 }else if(negotiationFileTypeId.equals(NegotiationHelper.FILE_TYPE_ID_PAYTABLE)){ 
				 NegotiationProcessPayTable negotiationProcessPayTable =
						 new NegotiationProcessPayTable(negotiationFile);
				 negotiationProcessPayTable.execute(fileString);
			 
			 }else if(negotiationFileTypeId.equals(NegotiationHelper.FILE_TYPE_ID_DISBURSEMENT)){ 
				 NegotiationDisbursementLoan negotiationDisbursementLoan =
						 new NegotiationDisbursementLoan(negotiationFile);
				 negotiationDisbursementLoan.execute(fileString);
				 
			 }else if(negotiationFileTypeId.equals(NegotiationHelper.FILE_TYPE_ID_PROCESS_SALE_PORTFOLIO)){ 
				 NegotiationProcessSalePortfolio negotiationProcessPayTable =
						 new NegotiationProcessSalePortfolio(negotiationFile);
				 negotiationProcessPayTable.execute(fileString);
			 }else if(negotiationFileTypeId.equals(NegotiationHelper.FILE_TYPE_ID_SALE)){ 
				 NegotiationSaleLoan negotiationProcessPayTable =
						 new NegotiationSaleLoan(negotiationFile);
				 negotiationProcessPayTable.execute(fileString);
			 }
			
			 addMessage("negotiation_process_ok", negotiationFile.getFileName());
			
			 getView().refresh();
			 
		}catch(Exception ex){
			ex.printStackTrace();
			if(ex.getMessage().contains("could not execute statement"))
				addError("could_not_execute_statement");
		}
	}
	
	public String getNegotiationFileTypeId() {
		return negotiationFileTypeId;
	}

	public void setNegotiationFileTypeId(String negotiationFileTypeId) {
		this.negotiationFileTypeId = negotiationFileTypeId;
	}
}
