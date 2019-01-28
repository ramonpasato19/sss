package com.powerfin.actions.person;

import java.math.BigDecimal;
import java.util.Map;

import org.openxava.actions.SaveAction;
import org.openxava.jpa.XPersistence;
import org.openxava.model.MapFacade;
import org.openxava.util.Messages;
import org.openxava.validators.ValidationException;

import com.powerfin.helper.PersonHelper;
import com.powerfin.model.IdentificationType;
import com.powerfin.model.Person;
import com.powerfin.model.PersonType;

public class NaturalPersonSaveAction extends SaveAction {

	@SuppressWarnings("unchecked")
	public void execute() throws Exception {
		
		Messages errors = MapFacade.validate(getModelName(), getValuesToSave());
		if (errors.contains()) throw new ValidationException(errors);
		
		String name;
		String firstName = getView().getValueString("firstName");
		String secondName = getView().getValueString("secondName");
		String paternalSurname = getView().getValueString("paternalSurname");
		String maternalSurname = getView().getValueString("maternalSurname");
		String identification = getView().getValueString("identification");
		String email = getView().getValueString("email");
		String activity = getView().getValueString("activity");
		BigDecimal creditLimit = (BigDecimal) getView().getValue("creditLimit");
		IdentificationType identificationType = null;
		
		Map<String, String> identificationTypeMap = (Map<String, String>) getView().getRoot().getValue("identificationType");
		if (identificationTypeMap.get("identificationTypeId")!=null) {
			identificationType = XPersistence.getManager().find(IdentificationType.class, (String)identificationTypeMap.get("identificationTypeId"));
		}
		
		name = paternalSurname + " ";
		name += maternalSurname != null ? maternalSurname + " " : "";
		name += firstName;
		name += secondName != null ? " " + secondName : "";
		
		//Create Person
		if (getView().isKeyEditable()) 
		{
			Person p = new Person();
			p.setPersonId(null);
			p.setIdentification(identification);
			p.setIdentificationType(identificationType);
			p.setPersonType(XPersistence.getManager().find(PersonType.class, PersonHelper.NATURAL_PERSON));
			p.setEmail(email.toLowerCase());
			p.setActivity(activity);
			p.setName(name);
			p.setCreditLimit(BigDecimal.ZERO);
			if (creditLimit!=null)
				p.setCreditLimit(creditLimit);
			XPersistence.getManager().persist(p);
			getView().setValue("personId", p.getPersonId());
			addMessage("person_created", p.getClass().getName());
		}
		//Update Person
		else
		{
			Integer personId = getView().getValueInt("personId");
			Person p = XPersistence.getManager().find(Person.class, personId);
			p.setIdentification(identification);
			p.setIdentificationType(identificationType);
			p.setEmail(email.toLowerCase());
			p.setActivity(activity);
			p.setName(name);
			if (creditLimit!=null)
				p.setCreditLimit(creditLimit);
			XPersistence.getManager().merge(p);
			addMessage("person_modified", p.getClass().getName());			
		}
		super.execute();
	}
}
