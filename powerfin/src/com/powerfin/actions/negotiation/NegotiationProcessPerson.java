package com.powerfin.actions.negotiation;

import java.io.*;
import java.util.*;

import org.openxava.jpa.*;
import com.powerfin.helper.*;
import com.powerfin.model.*;
import com.powerfin.model.dto.*;
import com.powerfin.util.*;

public class NegotiationProcessPerson {
	
	private NaturalPerson naturalPerson = null;
	private LegalPerson legalPerson = null;
	private Person person = null;
	private PersonType personType = null;
	private IdentificationType identificationType = null;
	private NegotiationFile negotiationFile = null;
	private District district = null;
	
	String identification="";
	String[] dataLine;
	String validationMessages;
	boolean bUpdate = false;
	
	NegotiationPersonDTO personDTO;
	
	public NegotiationProcessPerson(NegotiationFile negotiationFile) {
		this.negotiationFile = negotiationFile;
	}
	
	public void execute(String fileString) throws Exception{
		
		String personTypeId="";
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
	                validationMessages = NegotiationHelper.validateFieldsPersonFile(dataLine);
	                if(validationMessages.equals(NegotiationHelper.MESSAGE_OK)){
	                	personDTO = new NegotiationPersonDTO(dataLine); 
		                personTypeId = personDTO.getPersonType();
		                identification = personDTO.getIdentification();
		                identificationType = XPersistence.getManager().find(IdentificationType.class, personDTO.getIdentificationType());
		                personType = XPersistence.getManager().find(PersonType.class, personTypeId);
		                district = getDistrictByCode(personDTO.getHomeDistrict());
		                
		                if(personTypeId.equals(NegotiationHelper.NATURAL_PERSON))
		                	processByNaturalPersonType();
		                else
		                	processByLegalPersonType();
		                
		                naturalPerson=null;
		    			person=null;
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
	
	@SuppressWarnings("unchecked")
	private void processByNaturalPersonType() throws Exception{
		List<Person> persons = (List<Person>) XPersistence.getManager()
 				.createQuery("select p from Person p where p.identification = :identification")  
 				.setParameter("identification", identification)
 				.getResultList();
         
 		if(persons.isEmpty()){
 			System.out.println("create Person");
 			bUpdate=false;
			createUpdatePerson();
			createUpdateNaturalPerson();
 		}else{
 			System.out.println("update Person");
 			bUpdate=true;
 			person = (Person) persons.get(0);
 			naturalPerson = person.getNaturalPerson();
         	createUpdateNaturalPerson();
         	createUpdatePerson();
         	
 		}
	}
	
	@SuppressWarnings("unchecked")
	private void processByLegalPersonType() throws Exception{
		List<Person> persons = (List<Person>) XPersistence.getManager()
 				.createQuery("select p from Person p where p.identification = :identification")  
 				.setParameter("identification", identification)
 				.getResultList();
         
 		if(persons.isEmpty()){
 			System.out.println("create Person");
 			bUpdate=false;
     		createUpdatePerson();
            createUpdateLegalPerson();
 		}else{
 			System.out.println("update Person");
 			bUpdate=true;
 			person = (Person) persons.get(0);
 			legalPerson = person.getLegalPerson();
 			createUpdateLegalPerson();
         	createUpdatePerson();
         	
 		} 
	}
	
	private void createUpdatePerson() throws Exception{
		if(person==null){
			person = new Person();
		}
		
		try{
			person.setIdentification(identification);
			person.setIdentificationType(identificationType);
			person.setPersonType(personType);
			person.setEmail(personDTO.getEmail());
			person.setActivity(personDTO.getActivity());
			String name = "";
			name += personDTO.getPaternalSurname() + " ";
			name += personDTO.getMaternalSurname() != null ? personDTO.getMaternalSurname() + " " : "";
			name += personDTO.getFirtsName();
			name += personDTO.getSecondName() != null ? " " + personDTO.getSecondName() : "";
			person.setName(name);
			
			if(!bUpdate)
				XPersistence.getManager().persist(person);
			else
				XPersistence.getManager().merge(person);

		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}
	
	private void createUpdateNaturalPerson() throws Exception {
		if(naturalPerson==null){
			naturalPerson = new NaturalPerson();
		}
		
		try{
			naturalPerson.setGender(XPersistence.getManager().find(Gender.class, personDTO.getGender()));
			naturalPerson.setMaritalStatus(XPersistence.getManager().find(MaritalStatus.class, personDTO.getMaritalStatus()));
			naturalPerson.setBirthDate(UtilApp.formatDate.parse(personDTO.getBirthDate()));
			naturalPerson.setHomeMainStreet(personDTO.getMainStreet());
			naturalPerson.setHomeSideStreet(personDTO.getSideStreet());
			naturalPerson.setHomeNumber(personDTO.getHomeNumber());
			naturalPerson.setHomeSector(personDTO.getHomeSector());
			naturalPerson.setHomeDistrict(district);
			naturalPerson.setPersonId(person.getPersonId());
			naturalPerson.setIdentification(identification);
			naturalPerson.setIdentificationType(identificationType);
			naturalPerson.setFirstName(personDTO.getFirtsName());
			naturalPerson.setSecondName(personDTO.getSecondName());
			naturalPerson.setPaternalSurname(personDTO.getPaternalSurname());
			naturalPerson.setMaternalSurname(personDTO.getMaternalSurname());
			
			if(!bUpdate)
				XPersistence.getManager().persist(naturalPerson);
			else
				XPersistence.getManager().merge(naturalPerson);
			
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}
	
	private void createUpdateLegalPerson() throws Exception{
		if(legalPerson==null){
			legalPerson = new LegalPerson();
		}
		try{
			legalPerson.setBusinessName(personDTO.getPaternalSurname());
			legalPerson.setEmail(personDTO.getEmail());
			legalPerson.setHomeMainStreet(personDTO.getMainStreet());
			legalPerson.setIdentification(personDTO.getIdentification());
			legalPerson.setIdentificationType(identificationType);
			legalPerson.setPersonId(person.getPersonId());
			legalPerson.setHomeDistrict(district);
			
			if(!bUpdate)
				XPersistence.getManager().persist(legalPerson);
			else
				XPersistence.getManager().merge(legalPerson);
			
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}
	
	@SuppressWarnings("unchecked")
	private District getDistrictByCode(String code)
	{
		
		List<District> distrcts = XPersistence.getManager().createQuery("SELECT o FROM District o "
				+ "WHERE o.code = :code ")
		.setParameter("code", code)
		.getResultList();
		if(distrcts!=null && distrcts.size()>0)
			return distrcts.get(0);
		else
			return null;
	}
}
