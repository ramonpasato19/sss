package com.powerfin.actions.accountLoan;

import java.util.List;
import java.util.Map;

import org.openxava.actions.OnChangePropertyBaseAction;
import org.openxava.jpa.XPersistence;

import com.powerfin.model.Account;
import com.powerfin.model.District;
import com.powerfin.model.LegalPerson;
import com.powerfin.model.NaturalPerson;
import com.powerfin.model.Person;
import com.powerfin.model.PersonalReference;

public class OnChangeSelectedAccountLoan extends OnChangePropertyBaseAction {

	@SuppressWarnings("unchecked")
	public void execute() throws Exception {
		
		
		Map<String, String> currentAccountLoan = (Map<String, String>) getView().getRoot().getValue("accountLoan");
		if (currentAccountLoan!=null && !currentAccountLoan.isEmpty() && currentAccountLoan.get("accountId")!=null  && !currentAccountLoan.get("accountId").isEmpty() ){
			
			Account account = XPersistence.getManager().find(Account.class, (String)currentAccountLoan.get("accountId"));	
			Person person =  account.getPerson();
			getView().setHidden("PersonalReference", false);
			
			if (person.getPersonType().getPersonTypeId().equals("NAT")) {
				NaturalPerson naturalPerson = XPersistence.getManager().find(NaturalPerson.class, account.getPerson().getPersonId());
				fillDataNaturalPerson(naturalPerson);
			}
			else{
				LegalPerson legalPerson = XPersistence.getManager().find(LegalPerson.class, account.getPerson().getPersonId());
				getView().setHidden("PersonalReference", true);
				fillDataLegalPerson(legalPerson);
			}
		}else {
			getView().setHidden("PersonalReference", true);
			
		}
	}


	private void fillDataLegalPerson(LegalPerson legalPerson) {
		
		getView().getRoot().setValue("identificationType", legalPerson.getIdentificationType().getIdentificationTypeId());
		getView().getRoot().setValue("identification", legalPerson.getIdentification());
		getView().getRoot().setValue("name", legalPerson.getName());
		getView().getRoot().setValue("email", legalPerson.getEmail());
		getView().getRoot().setValue("activity", legalPerson.getActivity());
		getView().getRoot().setValue("homePhoneNumber1", legalPerson.getHomePhoneNumber1());
		getView().getRoot().setValue("homePhoneNumber2", legalPerson.getHomePhoneNumber2());
		
		getView().getRoot().setValue("homeMainStreet", legalPerson.getHomeMainStreet());
		getView().getRoot().setValue("homeNumber", legalPerson.getHomeNumber());
		getView().getRoot().setValue("homeSideStreet", legalPerson.getHomeSideStreet());
		getView().getRoot().setValue("homeSector", legalPerson.getHomeSector());
		
		try {
			District district = XPersistence.getManager().find(District.class, legalPerson.getHomeDistrict().getDistrictId());
			
			getView().getRoot().setValue("country",district.getCountry().getName());
			getView().getRoot().setValue("region", district.getRegion().getName());
			getView().getRoot().setValue("state", district.getState().getName());
			getView().getRoot().setValue("city", district.getCity().getName());
			getView().getRoot().setValue("district", district.getName());
			
		} catch (NullPointerException e) {
			
		}
		
	}
	
	
	private void fillDataNaturalPerson(NaturalPerson naturalPerson) {
		
		getView().getRoot().setValue("identificationType", naturalPerson.getIdentificationType().getIdentificationTypeId());
		getView().getRoot().setValue("identification", naturalPerson.getIdentification());
		getView().getRoot().setValue("name", naturalPerson.getName());
		getView().getRoot().setValue("email", naturalPerson.getEmail());
		getView().getRoot().setValue("activity", naturalPerson.getActivity());
		getView().getRoot().setValue("homePhoneNumber1", naturalPerson.getHomePhoneNumber1());
		getView().getRoot().setValue("homePhoneNumber2", naturalPerson.getHomePhoneNumber2());
		
		getView().getRoot().setValue("homeMainStreet", naturalPerson.getHomeMainStreet());
		getView().getRoot().setValue("homeNumber", naturalPerson.getHomeNumber());
		getView().getRoot().setValue("homeSideStreet", naturalPerson.getHomeSideStreet());
		getView().getRoot().setValue("homeSector", naturalPerson.getHomeSector());
		
		try {
			List<PersonalReference> personalReferences=naturalPerson.getPersonalReferences();
			if (personalReferences!=null) {
				if (personalReferences.size()>=1) {					
					fillPersonalReference(personalReferences.get(0), 1);					 
				}
				if (personalReferences.size()>=2) {
					fillPersonalReference(personalReferences.get(1), 2);
				}
				if (personalReferences.size()>=3) {
					fillPersonalReference(personalReferences.get(2), 3);
				}
			}
			District district = XPersistence.getManager().find(District.class, naturalPerson.getHomeDistrict().getDistrictId());
			
			getView().getRoot().setValue("country",district.getCountry().getName());
			getView().getRoot().setValue("region", district.getRegion().getName());
			getView().getRoot().setValue("state", district.getState().getName());
			getView().getRoot().setValue("city", district.getCity().getName());
			getView().getRoot().setValue("district", district.getName());
			
			
		} catch (NullPointerException e) {
		}
	}
	
	private void fillPersonalReference(PersonalReference personalReference, int index) {
		Person person = personalReference.getPerson();
		if (person!=null) {
			getView().getRoot().setValue("nameReference"+index, person.getName());
		}
		getView().getRoot().setValue("addressReference"+index,personalReference.getAddress()) ;
		getView().getRoot().setValue("homePhoneReference"+index,personalReference.getHomePhone()) ;
		getView().getRoot().setValue("cellPhoneReference"+index,personalReference.getCellPhone()) ;
		getView().getRoot().setValue("workPhoneReference"+index,personalReference.getWorkPhone()) ;
		getView().getRoot().setValue("relationshipReference"+index,personalReference.getRelationship()) ;
	}
	
	
	@SuppressWarnings("unused")
	private void clearFields() {
		
		getView().getRoot().setValue("identificationType", null);
		getView().getRoot().setValue("identification",null);
		getView().getRoot().setValue("name", null);
		getView().getRoot().setValue("email", null);
		getView().getRoot().setValue("activity", null);
		getView().getRoot().setValue("homePhoneNumber1", null);
		getView().getRoot().setValue("homePhoneNumber2", null);
		getView().getRoot().setValue("homeMainStreet", null);
		getView().getRoot().setValue("homeNumber", null);
		getView().getRoot().setValue("homeSideStreet", null);
		getView().getRoot().setValue("homeSector", null);
		getView().getRoot().setValue("country",null);
		getView().getRoot().setValue("region", null);
		getView().getRoot().setValue("state", null);
		getView().getRoot().setValue("city", null);
		getView().getRoot().setValue("district", null);		
		getView().getRoot().setValue("nameReference",null);	
		getView().getRoot().setValue("addressReference",null);
		getView().getRoot().setValue("homePhoneReference",null);
		getView().getRoot().setValue("cellPhoneReference",null);
		getView().getRoot().setValue("workPhoneReference",null);
		getView().getRoot().setValue("relationshipReference",null);
		
	}	
	
}
