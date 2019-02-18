package com.openxava.naviox.actions;

import org.openxava.actions.*;
import org.openxava.util.*;
import com.openxava.naviox.*;
import com.openxava.naviox.impl.*;
import com.openxava.naviox.model.*;

/**
 * 
 * @author Javier Paniza
 */

public class ChangePasswordAction extends ViewBaseAction implements IForwardAction {  

	private String forwardURI; 
	
	public void execute() throws Exception {
		User user = User.find(Users.getCurrent());
		String currentPassword = getView().getValueString("currentPassword");
		if (!user.passwordMatches(currentPassword)) {
			addError("passwords_not_match");
			return;
		}
		
		String newPassword = getView().getValueString("newPassword"); 
		String repeatNewPassword = getView().getValueString("repeatNewPassword");
		if (!Is.equal(newPassword, repeatNewPassword)) {
			addError("passwords_not_match");
			return;
		}
		
		user.setPassword(newPassword);
		addMessage("password_changed"); 
		getView().clear();
		if (user.isForceChangePassword()) {
			user.setForceChangePassword(false);
			Modules modules = (Modules) getRequest().getSession().getAttribute("modules");
			modules.reset();
			String originalURI = getRequest().getParameter("originalURI");
			String module = Strings.lastToken(originalURI, "/");
			String uri = Is.emptyString(module)?"/":"/m/" + module; 
			forwardURI = OrganizationURIs.refine(getRequest(), uri);
			addInfo("prompt_start_button");
		}		
	}

	public String getForwardURI() { 
		return forwardURI;
	}

	public boolean inNewWindow() { 
		return false;
	}

}
