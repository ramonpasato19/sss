package com.openxava.naviox.util;

import java.sql.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.hibernate.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

import com.openxava.naviox.*;
import com.openxava.naviox.impl.*;
import com.openxava.naviox.model.*;

/**
 * Access to the organization associated to the current session. <p>
 * 
 * @since 5.2
 * @author Javier Paniza
 */

public class Organizations {
	
	public static Log log = LogFactory.getLog(Organizations.class);
	
	public static String getCurrent(ServletRequest request) {
		return getCurrent(((HttpServletRequest) request).getSession()); 
	}
	
	public static String getCurrent(HttpSession session) {
		return (String) session.getAttribute("naviox.organization"); 
	}
	
	public static void setCurrent(ServletRequest request, String organization) {
		((HttpServletRequest) request).getSession().setAttribute("naviox.organization", organization);
	}
	
	public static void setPersistenceDefaultSchema(HttpSession session) {
		String organization = getCurrent(session);
		if (organization != null) { 
			Modules modules = (Modules) session.getAttribute("modules");
			boolean sharedUsers = Configuration.getInstance().isSharedUsersBetweenOrganizations();
			if (!sharedUsers || 
					(
						sharedUsers && 
						!"SignIn".equals(modules.getCurrentModuleName()) && 
						!"SignUp".equals(modules.getCurrentModuleName())		
					)
				)
			{ 
				XPersistence.setDefaultSchema(organization);
				XHibernate.setDefaultSchema(organization);
			}
		}
	}

	public static String getCurrentName(ServletRequest request) { 
		String organizationId = getCurrent(request);
		return Organization.getName(organizationId);
	}
	
	public static void init(ServletRequest request, String organization) {
		String currentOrganization = getCurrent(request);		
		if (!Is.equal(organization, currentOrganization)) {
			if (Configuration.getInstance().isSharedUsersBetweenOrganizations()) {
				Object user = ((HttpServletRequest) request).getSession().getAttribute("naviox.user"); 
				UserInfo userInfo = (UserInfo) ((HttpServletRequest) request).getSession().getAttribute("xava.portal.userinfo"); 
				((HttpServletRequest) request).getSession().invalidate();
				((HttpServletRequest) request).getSession().setAttribute("naviox.user", user); 
				((HttpServletRequest) request).getSession().setAttribute("xava.user", user);
				((HttpServletRequest) request).getSession().setAttribute("xava.portal.userinfo", userInfo);
				if (userInfo != null) userInfo.setOrganization(organization);
			}
			else {
				((HttpServletRequest) request).getSession().invalidate();
				Users.setCurrent((String)null); 
			}
			setCurrent(request, organization);	
		}
		if (organization != null) {
			XPersistence.setDefaultSchema(organization);
			XHibernate.setDefaultSchema(organization); 
		}
	}

	public static void init(ServletRequest request) { 
		init(request, request.getParameter("organization"));
	}

	/** @since 5.6 */
	public static Organization create(String name) { 
		return Organizations.create(name, null);
	}

	/** @since 5.6 */
	public static Organization create(String name, String adminUser) { 
		return create(name, adminUser, false);  
	}
	
	/** @since 6.0 */
	public static Organization createWithBlankDB(String name, String adminUser) {  
		return create(name, adminUser, true);
	}
	
	private static Organization create(String name, String adminUser, boolean blankDB) { 
		String schema = Organization.normalize(name);
		Organizations.createSchema(schema);
		if (!blankDB) DB.createTenancy(schema, adminUser); 
		Organization organization = new Organization();
		organization.setName(name);
		XPersistence.getManager().persist(organization);
		Organization.resetCache();
		Organization.setUp(); 
		return organization;
	}

	
	/** @since 6.0 */
	public static void delete(String id) { 
		deleteSchema(id);
		Organization organization = Organization.find(id);
		for (User user: organization.getUsers()) {
			Collection organizations = user.getOrganizations();
			if (organizations != null) organizations.remove(organization);
		}
		XPersistence.getManager().remove(organization);
		Organization.resetCache();
		Organization.setUp(); 
	}

	private static void createSchema(String schema) { 
		try {
			Connection con = DataSourceConnectionProvider.getByComponent("Organization").getConnection();
			PreparedStatement ps = null;
			try {
				String database = con.getMetaData().getDatabaseProductName().split(" ")[0];
				String sentenceTemplate = NaviOXPreferences.getInstance().getCreateSchema(database);
				String sentence = sentenceTemplate.replace("${schema}", schema);
				log.debug(XavaResources.getString("executing_on_database", database, sentence)); 
				ps = con.prepareStatement(sentence);
				ps.executeUpdate();
			}
			finally {
				if (ps != null) {
					try { ps.close(); } catch (Exception ex) {}
				}
				con.close();
			}
		}	
		catch (Exception ex) {
			log.error(XavaResources.getString("schema_creation_error", schema), ex);
			throw new XavaException("schema_creation_error", schema);
		}
	}
	
	private static void deleteSchema(String schema) { 
		try {
			Connection con = DataSourceConnectionProvider.getByComponent("Organization").getConnection();
			PreparedStatement ps = null;
			try {
				String database = con.getMetaData().getDatabaseProductName().split(" ")[0];
				String sentenceTemplate = NaviOXPreferences.getInstance().getDropSchema(database);
				String sentence = sentenceTemplate.replace("${schema}", schema);
				ps = con.prepareStatement(sentence);
				ps.executeUpdate();
			}
			finally {
				if (ps != null) {
					try { ps.close(); } catch (Exception ex) {}
				}
				con.close();
			}
		}	
		catch (Exception ex) {
			log.error(XavaResources.getString("schema_deletion_error", schema), ex); 
			throw new XavaException("schema_deletion_error", schema); 
		}	
	}


}
