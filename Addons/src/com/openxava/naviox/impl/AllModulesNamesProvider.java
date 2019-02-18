package com.openxava.naviox.impl;

import java.util.*;

import org.openxava.application.meta.*;
import org.openxava.jpa.XPersistence;
import org.openxava.util.Is;

/**
 * @since 5.6
 * @author Javier Paniza
 */
public class AllModulesNamesProvider extends BaseAllModulesNamesProvider {
	
	public Collection<String> getAllModulesNames(MetaApplication app) {
		Collection<String> allModulesNames = super.getAllModulesNames(app);
		allModulesNames.add("ChangePassword");
		allModulesNames.add("MyPersonalData"); 
		allModulesNames.remove("UserJoin");
		allModulesNames.remove("Index"); 
		
		if (Is.equalAsStringIgnoreCase(XPersistence.getDefaultSchema(), DB.ROOT_SCHEMA)) {
			allModulesNames.add("Configuration");
		} else {
			allModulesNames.remove("Organization");
		}
		return allModulesNames;
	}

}
