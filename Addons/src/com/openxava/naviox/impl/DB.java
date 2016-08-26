package com.openxava.naviox.impl;

import java.util.*;

import javax.persistence.*;

import org.apache.commons.logging.*;
import org.openxava.application.meta.*;
import org.openxava.jpa.*;
import org.openxava.tools.*;
import org.openxava.util.*;

import com.openxava.naviox.model.*;
import com.openxava.naviox.util.*;

/**
 * 
 * @author Javier Paniza
 */
public class DB {
	
	private static Log log = LogFactory.getLog(DB.class);
	
	
	public static void init() {
		try {
			try {		
				populateDB();
			}
			catch (PersistenceException ex) {
				log.info(XavaResources.getString("xavapro_first_time_execution"));
				XPersistence.rollback();
				createDB(false);  
				populateDB();
			}
			populateAllTenancies(); 
		}
		catch (RuntimeException ex) {
			XPersistence.rollback(); // This is not a total rollback, but at least we release the connection
			throw ex;
		}
	}
	
 	private static void populateAllTenancies() {
		for (String schema: Organization.getAllIds()) {
			XPersistence.commit(); 
			XPersistence.setDefaultSchema(schema);
			populateDB(); 		
			XPersistence.reset(); 			
		}			
	}

	public static void createTenancy(String schema) { 		
		XPersistence.commit(); 
		XPersistence.setDefaultSchema(schema);
		createDB(true);
		populateDB(); 		
		XPersistence.reset(); 
	}

	private static void populateDB() {
		createModules();
		createUserRolesConfiguration(); 
		setupRights(); 
	}
	
	private static void setupRights() {  
		if (ModuleRights.countForApplication(MetaModuleFactory.getApplication()) > 0) return;
		Folder adminFolder = Folder.findByName("Admin");
		Collection<Module> adminModules = new ArrayList<Module>();
		Collection<Module> userModules = new ArrayList<Module>();		
		for (Module module: Module.findByApplication(MetaModuleFactory.getApplication())) { 
			if (module.getName().equals("User") || 
				module.getName().equals("Role") || 
				module.getName().equals("Module") ||
				module.getName().equals("Folder") ||
				module.getName().equals("ModuleRights") ||
				module.getName().equals("Organization") ||
				module.getName().equals("Configuration") ||  
				module.getName().equals("ConfigurationRecord")) 
			{
				adminModules.add(module);
				module.setFolder(adminFolder);
				if (module.getName().equals("ModuleRights") || module.getName().equals("ConfigurationRecord")) {
					module.setHidden(true);					
				}
			}
			else {
				userModules.add(module);
				if (module.getName().equals("ChangePassword")) {
					module.setFolder(adminFolder);
				}
			}
		}
		Role adminRole = Role.find("admin");
		adminRole.setModules(adminModules);
		Role userRole = Role.find("user");
		userRole.setModules(userModules);
		
		XPersistence.commit();		
	}

	private static void createModules() { 
		Collection<MetaModule> inApp = MetaModuleFactory.createAll();
		Collection<Module> inDB = Module.findByApplication(MetaModuleFactory.getApplication());
		try {
			for (Iterator<MetaModule> it = inApp.iterator(); it.hasNext(); ) {
				MetaModule metaModule = it.next();
				Module module = Module.findByMetaModule(metaModule);
				if (module == null) {
					module = Module.createFromMetaModule(metaModule);
				}
				inDB.remove(module);
			}
			for (Module module: inDB) {
				XPersistence.getManager().remove(module); 
			}	
			XPersistence.commit();
		}
		catch (Exception ex) { 
			log.warn(XavaResources.getString("updating_modules_database_problem"), ex);
			XPersistence.rollback();
		}
	}
	
	private static void createDB(boolean allEntities) {
		if (isAutoUpdateSchema()) return; 
		
		log.info(XavaResources.getString("creating_xavapro_tables"));
		SchemaTool tool = new SchemaTool();
		tool.setCommitOnFinish(false);
		if (!allEntities) {
			tool.addAnnotatedClass(User.class);
			tool.addAnnotatedClass(Role.class);
			tool.addAnnotatedClass(Module.class);
			tool.addAnnotatedClass(Folder.class);
			tool.addAnnotatedClass(ModuleRights.class);
			tool.addAnnotatedClass(Organization.class);
			tool.addAnnotatedClass(ConfigurationRecord.class); 
		}		
		tool.generateSchema();
	}
	
	private static boolean isAutoUpdateSchema() {
		String ddlAuto = (String) XPersistence.getManager().getEntityManagerFactory().getProperties().get("hibernate.hbm2ddl.auto");
		if (ddlAuto == null) return false;
		return "update".equalsIgnoreCase(ddlAuto.trim());
	}

	private static void createUserRolesConfiguration() {
		if (User.count() > 0) return; 
		log.info(XavaResources.getString("creating_default_user_roles_configuration"));
		Collection<Role> roles = new ArrayList<Role>();
		Role adminRole = new Role();
		adminRole.setName("admin");
		roles.add(adminRole);
		
		Role userRole = new Role();		
		userRole.setName("user");
		roles.add(userRole);
		
		User admin = new User();		
		admin.setName("admin");
		admin.setPassword(NaviOXPreferences.getInstance().getInitialPasswordForAdmin());
		admin.setRoles(roles);
		
		Folder adminFolder = new Folder();
		adminFolder.setName("Admin");
		XPersistence.getManager().persist(adminRole);
		XPersistence.getManager().persist(userRole);
		XPersistence.getManager().persist(admin);		
		XPersistence.getManager().persist(adminFolder); 
		XPersistence.commit();
	}
			
}
