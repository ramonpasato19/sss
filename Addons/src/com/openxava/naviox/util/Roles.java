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
		Module changePasswordModule = Module.findByApplicationModule(MetaModuleFactory.getApplication(), "ChangePassword");	
		selfSignUpRole.setModules(Collections.singleton(changePasswordModule));
		XPersistence.getManager().persist(selfSignUpRole);		
		return selfSignUpRole;
	}

}
