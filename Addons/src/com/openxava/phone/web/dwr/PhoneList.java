package com.openxava.phone.web.dwr;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxava.controller.ModuleManager;
import org.openxava.util.XavaResources;
import org.openxava.web.servlets.Servlets;

import com.openxava.naviox.web.dwr.RequestReseter;

/**
 * @author Jeromy Altuna
 * @since  5.8
 */
public class PhoneList {
	
	private static Log log = LogFactory.getLog(PhoneList.class);
	
	public String filter(HttpServletRequest request, HttpServletResponse response, String application, String module, String searchWord) {
		try {
			RequestReseter.reset(request);
			request.setAttribute("style", com.openxava.phone.web.PhoneStyle.getInstance());
			return Servlets.getURIAsString(request, response, "/phone/listCore.jsp?application=" + application + "&module=" + module + "&searchWord=" + searchWord);
		} catch (Exception ex) {
			log.error(XavaResources.getString("display_phone_list_error"), ex);
			return null;
		}		
		finally {
			ModuleManager.commit();
		}
	}
}