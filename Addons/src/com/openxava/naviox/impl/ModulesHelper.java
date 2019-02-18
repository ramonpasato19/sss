package com.openxava.naviox.impl;

import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.application.meta.*;
import org.openxava.controller.meta.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

import com.openxava.naviox.*;
import com.openxava.naviox.model.*;
import com.openxava.naviox.util.*;

/**
 * 
 * @author Javier Paniza
 */
public class ModulesHelper { 
	
	private static Log log = LogFactory.getLog(ModulesHelper.class);
	
	public static void init(String applicationName) {

		
		MetaApplication app = MetaApplications.getMetaApplication(applicationName);

		MetaModule indexModule = new MetaModule();
		indexModule.setName("Index"); 
		indexModule.addEnvironmentVariable("XAVA_LIST_ACTION", "Organization.go"); 
		boolean existsIndexController = MetaControllers.contains("Index"); 
		if (existsIndexController) {
			indexModule.addControllerName("Index"); 
			if (MetaControllers.getMetaController("Index").containsMetaAction("search")) { 
				indexModule.addEnvironmentVariable("XAVA_SEARCH_ACTION", "Index.search");
			}
		}
		indexModule.addControllerName("WarnNoEmail"); 
		indexModule.setModelName("Organization"); 
		indexModule.setModeControllerName("Mode"); 
		indexModule.setTabName("OfCurrentUser");
		app.addMetaModule(indexModule);	
		
		MetaModule userJoinModule = new MetaModule();
		userJoinModule.setName("UserJoin"); 
		userJoinModule.setModelName("SignUp"); // Whatever
		userJoinModule.setWebViewURL("naviox/userJoin");		
		userJoinModule.addControllerName("UserJoin");
		app.addMetaModule(userJoinModule);
	}

	public static String getCurrent(HttpServletRequest request) { 
		if (Configuration.getInstance().isSharedUsersBetweenOrganizations()) {
			String organization = Organizations.getCurrentName(request); 
			return organization == null?"Index":null; 
		}
		else {
			return null;
		} 
	}
	
	/** @since 5.6 */
	public static String getUserAccessModule(ServletRequest request) { 
		if (Configuration.getInstance().isSharedUsersBetweenOrganizations()) {
			String organization = Organizations.getCurrentName(request);
			if (organization != null && Users.getCurrent() != null) {  
				return "UserJoin";
			}
		}
		return "SignIn";
	}
	
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
	
	public static boolean isPublic(HttpServletRequest request, String moduleName) { 
		String organization = Organizations.getCurrent(request); 
		if ("SignUp".equals(moduleName)) {
			if (organization == null && Configuration.getInstance().isGuestCanCreateAccount()) {
				return true;
			}
			else if (organization != null && Configuration.getInstance().isGuestCanCreateAccountInOrganizations()) {
				return true;
			}
		} 
		if (Configuration.getInstance().isSharedUsersBetweenOrganizations()) {
			if (organization == null && "Index".equals(moduleName)) {
				return Users.getCurrent() != null;
			}
			if ("FirstSteps".equals(moduleName)) {
				String userName = Users.getCurrent();
				if (userName != null) {
					User user = User.find(userName); 
					if (user != null) return true;
				}
			} 
			if ("UserJoin".equals(moduleName)) {
				return Users.getCurrent() != null;
			} 			
		}
		else {
			if ("FirstSteps".equals(moduleName)) return true;
		}
		if ("RecoverPassword".equals(moduleName)) return true; 
		if ("RestorePassword".equals(moduleName)) return true; 
		return false;
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

	public static boolean showsIndexLink() { 
		return Configuration.getInstance().isSharedUsersBetweenOrganizations();
	}	
	
	public static boolean showsSearchModules(HttpServletRequest request) {
		Modules modules = (Modules) request.getSession().getAttribute("modules");
		Folders folders = (Folders) request.getSession().getAttribute("folders");
		if (folders == null) {			
			folders = new Folders();
			request.getSession().setAttribute("folders", folders);
		}
		folders.setModules(modules);
		Boolean showsSearchModules = (Boolean) request.getSession().getAttribute("naviox.showssearchmodules"); 
		if (showsSearchModules == null) {
			if (!folders.isRoot()) return true;
			Collection<Folder> subfolders = folders.getSubfolders();
			if (subfolders.size() == 1 && subfolders.iterator().next().getName().equals("Admin")) {
				showsSearchModules = false;
			}
			else showsSearchModules = !folders.getSubfolders().isEmpty() || modules.getAll().size() > 30;
			request.getSession().setAttribute("naviox.showssearchmodules", showsSearchModules);
		}
		return showsSearchModules;
	}	


}
