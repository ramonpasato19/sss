package com.powerfin.actions.person;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;
import org.openxava.model.*;
import org.openxava.util.*;
import org.openxava.validators.*;

import com.powerfin.helper.*;
import com.powerfin.model.*;

public class LegalPersonSaveAction extends SaveAction {

	@SuppressWarnings("unchecked")
	public void execute() throws Exception {
		
		Messages errors = MapFacade.validate(getModelName(), getValuesToSave());
		if (errors.contains()) throw new ValidationException(errors);
		
		
		String businessName = getView().getValueString("businessName");
		String identification = getView().getValueString("identification");
		String email = getView().getValueString("email");
		IdentificationType identificationType = null;
		
		Map<String, String> identificationTypeMap = (Map<String, String>) getView().getRoot().getValue("identificationType");
		if (identificationTypeMap.get("identificationTypeId")!=null) {
			identificationType = XPersistence.getManager().find(IdentificationType.class, (String)identificationTypeMap.get("identificationTypeId"));
		}
		
		if (getView().isKeyEditable()) { //Create Account
			
			Person p = new Person();
			p.setPersonId(null);
			p.setIdentification(identification);
			p.setIdentificationType(identificationType);
			p.setPersonType(XPersistence.getManager().find(PersonType.class, PersonHelper.LEGAL_PERSON));
			p.setEmail(email.toLowerCase());
			p.setName(businessName);
			XPersistence.getManager().persist(p);
			
			getView().setValue("personId", p.getPersonId());
			addMessage("person_created", p.getClass().getName());
		}
		else
		{
			Integer personId = getView().getValueInt("personId");
			Person p = XPersistence.getManager().find(Person.class, personId);
			p.setIdentification(identification);
			p.setIdentificationType(identificationType);
			p.setEmail(email.toLowerCase());
			p.setName(businessName);
			XPersistence.getManager().merge(p);
			addMessage("person_modified", p.getClass().getName());			
		}

		
		
		super.execute();
	}
}
