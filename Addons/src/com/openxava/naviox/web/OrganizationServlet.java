package com.openxava.naviox.web;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.openxava.jpa.*;
import org.openxava.util.*;
import com.openxava.naviox.model.*;
import com.openxava.naviox.util.*;

/**
 * 
 * @author Javier Paniza
 */

public class OrganizationServlet extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String [] uri = request.getRequestURI().split("/", 5);
		if (uri.length < 4) {
			response.getWriter().print(XavaResources.getString(request, "organization_name_missing"));
			return;
		}
		String organization = uri[3];
		String originalURI = uri.length < 5?"":uri[4];
		if (Is.emptyString(originalURI) && !request.getRequestURI().endsWith("/")) {
			response.sendRedirect(request.getRequestURI() + "/");
			return;
		}
		String separator = originalURI.contains("?")?"&":"?";
		if (originalURI.equals("phone")) originalURI = originalURI + "/"; 
		String url = ("/" + originalURI).replace("/m/", "/modules/") + separator + "organization=" + organization;
		Organization.setUp();
		Configuration.getInstance(); // To init Configuration before changing schema
		XPersistence.commit(); 
		RequestDispatcher dispatcher = request.getRequestDispatcher(url);
		Organizations.init(request, organization); 
		dispatcher.forward(new SecureRequest(request), response);
	}
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
}

