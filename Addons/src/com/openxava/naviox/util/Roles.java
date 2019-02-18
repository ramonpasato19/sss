package com.openxava.naviox.util;

import java.util.*;

import org.openxava.jpa.*;

import com.openxava.naviox.impl.*;
import com.openxava.naviox.model.*;

/**
 * 
 * @author Javier Paniza
 */

public class Roles {

	public static Role createSelfSignUpRole() {
		Role selfSignUpRole = new Role();		
		selfSignUpRole.setName("self sign up");
		selfSignUpRole.setDescription("Add to this role the modules available for the self sign up users"); 
		Collection<Module> modules = new ArrayList<Module>();
		Module changePasswordModule = Module.findByApplicationModule(MetaModuleFactory.getApplication(), "ChangePassword");	
		if (changePasswordModule != null) { 
			modules.add(changePasswordModule);
		}
		Module myPersonalDataModule = Module.findByApplicationModule(MetaModuleFactory.getApplication(), "MyPersonalData");	
		if (myPersonalDataModule != null) { 
			modules.add(myPersonalDataModule);
		}
		selfSignUpRole.setModules(modules);
		XPersistence.getManager().persist(selfSignUpRole);		
		return selfSignUpRole;
	}

}
