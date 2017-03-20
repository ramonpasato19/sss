package com.openxava.naviox.impl;

import java.util.*;

import org.openxava.application.meta.*;

/**
 * @since 5.6
 * @author Javier Paniza
 */
public class AllModulesNamesProvider extends BaseAllModulesNamesProvider {
	
	public Collection<String> getAllModulesNames(MetaApplication app) {
		Collection<String> allModulesNames = super.getAllModulesNames(app);
		allModulesNames.add("ChangePassword");
		allModulesNames.add("Configuration");
		allModulesNames.remove("UserJoin"); 
		return allModulesNames;
	}

}
