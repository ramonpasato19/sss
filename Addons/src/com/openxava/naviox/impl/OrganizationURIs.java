package com.openxava.naviox.impl;

import javax.servlet.http.*;

import org.openxava.util.*;

import com.openxava.naviox.util.*;

/**
 * Utility class to manager Organization URIS.
 * 
 * @since 5.4
 * @author Javier Paniza
 */
public class OrganizationURIs {
	
	public static String refine(HttpServletRequest request, String forwardURI) { 
		String organization = Organizations.getCurrent(request);
		if (!Is.emptyString(organization) && !forwardURI.startsWith("/o/")) {
			return "/o/" + organization + forwardURI;
		}
		return forwardURI;
	}	

}
