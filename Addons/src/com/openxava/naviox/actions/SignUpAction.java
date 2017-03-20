package com.openxava.naviox.actions;

import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.actions.*;
import org.openxava.jpa.*;
import org.openxava.model.*;
import org.openxava.util.*;

import com.openxava.naviox.impl.*;
import com.openxava.naviox.model.*;
import com.openxava.naviox.util.*;


/**
 * 
 * @author Javier Paniza
 */
public class SignUpAction extends ViewBaseAction implements IForwardAction {
	
	private static Log log = LogFactory.getLog(SignUpAction.class);	
	private String forwardURI;

	public void execute() throws Exception {
		Map values = getView().getValues();
		Messages errors = MapFacade.validate("SignUp", values);
		String userName = (String) values.get("userName");
		if (User.find(userName) != null) {
			errors.add("username_already_in_use", "userName"); 
		}
		
		String password = (String) values.get("password"); 
		String repeatPassword = (String) values.get("repeatPassword");
		if (!Is.equal(password, repeatPassword)) {
			errors.add("passwords_not_match", "password");
		}

		if (errors.contains()) {
			addErrors(errors);
			return;
		}

		User user = new User();
		user.setName(userName);
		user.setPassword(password);
		user.setRepeatPassword(repeatPassword);
		Role role = Role.findSelfSignUpRole();
		if (role == null) {
			log.info(XavaResources.getString("creating_self_sign_up_role")); 
			role = Roles.createSelfSignUpRole();
		}
		user.addRole(role);
		XPersistence.getManager().persist(user);
		XPersistence.commit(); 
		
		SignInHelper.signIn(getRequest().getSession(), userName);
		forwardURI = SignInHelper.refineForwardURI(getRequest(), getInitialModuleURI()); // refineForwardURI to work in organizations 
	}
	
	private String getInitialModuleURI() { 
		String organization = Organizations.getCurrent(getRequest());
		return organization == null && Configuration.getInstance().isSharedUsersBetweenOrganizations()?"/m/Index":"/m/FirstSteps";
	}

	public String getForwardURI() {
		return forwardURI;
	}

	public boolean inNewWindow() {
		return false;
	}

}
