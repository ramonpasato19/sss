package com.openxava.naviox.impl;

import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.application.meta.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

import com.openxava.naviox.model.*;

/**
 * 
 * @author Javier Paniza
 */
public class ModulesProvider {
	
	private static Log log = LogFactory.getLog(ModulesProvider.class);
	
	public static List<MetaModule> getAll() {
		Set<Module> all = new HashSet(Module.findUnrestrictedOnes());
		String userName = Users.getCurrent();
		if (userName != null) {
			User user = XPersistence.getManager().find(User.class, userName);
			if (user != null) {
				if (user.isForceChangePassword()) {   
					all.add(Module.findByApplicationModule(MetaModuleFactory.getApplication(), "ChangePassword"));
				}
				else {
					all.addAll(user.getNotHiddenModules());
				}
			}
		}				
		return toMetaModules(all);
	}
	
	private static List<MetaModule> toMetaModules(Collection<Module> modules) {
		List<MetaModule> metaModules = new ArrayList<MetaModule>();
		for (Module module: modules) {			
			try {
				metaModules.add(MetaModuleFactory.create(module.getApplication(), module.getName()));
			}
			catch (ElementNotFoundException ex) {
				log.warn(XavaResources.getString("module_not_added", module.getName(), module.getApplication()));
			}
		}
		return metaModules;
	}
	

}
