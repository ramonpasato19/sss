package com.openxava.naviox.util;

import javax.servlet.*;
import javax.servlet.http.*;

import org.openxava.jpa.*;
import org.openxava.util.*;

import com.openxava.naviox.model.*;

/**
 * Access to the organization associated to the current session. <p>
 * 
 * @since 5.2
 * @author Javier Paniza
 */

public class Organizations {
	
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
		if (organization != null) XPersistence.setDefaultSchema(organization); 
	}

	public static String getCurrentName(HttpServletRequest request) {
		String organizationId = getCurrent(request);
		return Organization.getName(organizationId);
	}

	public static void init(ServletRequest request) {
		String organization = request.getParameter("organization");		
		String currentOrganization = getCurrent(request);		
		if (!Is.equal(organization, currentOrganization)) {
			((HttpServletRequest) request).getSession().invalidate();
			setCurrent(request, organization);
		}
		if (organization != null) {
			XPersistence.setDefaultSchema(organization);
		}							
	}

}
