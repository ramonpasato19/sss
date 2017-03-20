package com.openxava.naviox.actions;

import org.openxava.actions.*;
import com.openxava.naviox.impl.*;

/**
 * 
 * @author Javier Paniza 
 */
abstract public class ForwardToOriginalURIBaseAction extends ViewBaseAction implements IForwardAction {
	
	private String forwardURI = null;

	protected void forwardToOriginalURI() throws Exception {		
		String originalURI = getRequest().getParameter("originalURI");
		if (originalURI == null) {
			forwardURI = "/";
		}
		else {
			int idx = originalURI.indexOf("/", 1);			
			if (!originalURI.endsWith("/SignIn") && idx > 0 && idx < originalURI.length()) {
				forwardURI = originalURI.substring(idx);
			}
			else {
				forwardURI = "/";
			}
		}
		forwardURI = SignInHelper.refineForwardURI(getRequest(), forwardURI); 
	}
	
	public String getForwardURI() {
		return forwardURI;
	}

	public boolean inNewWindow() {
		return false;
	}

}
