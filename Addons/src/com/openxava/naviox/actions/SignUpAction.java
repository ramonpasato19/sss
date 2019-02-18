package com.openxava.naviox.actions;

import java.util.*;

import org.apache.commons.lang.*;
import org.apache.commons.logging.*;
import org.openxava.actions.*;
import org.openxava.jpa.*;
import org.openxava.model.*;
import org.openxava.util.*;
import org.openxava.validators.*;

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
			errors.add(Configuration.getInstance().isUseEmailAsUserName()?"email_already_in_use":"username_already_in_use", "userName"); 
		}
		
		String password = (String) values.get("password"); 
		String repeatPassword = (String) values.get("repeatPassword");
		if (!Is.equal(password, repeatPassword)) {
			errors.add("passwords_not_match", "password");
		}
		
		if (Configuration.getInstance().isPrivacyPolicyOnSignUp()) {
			validatePrivacyPolicy(errors);
		}
		
		if (!errors.contains() && Configuration.getInstance().isUseEmailAsUserName()) { 
			validateEmail(errors, userName);
		}

		if (errors.contains()) {
			addErrors(errors);
			return;
		}

		User user = new User();
		user.setName(userName);
		user.setPassword(password);
		user.setRepeatPassword(repeatPassword);
		if (Configuration.getInstance().isUseEmailAsUserName()) {
			user.setEmail(user.getName());
			user.setName(StringUtils.abbreviate(user.getName(), 30));
		}
		else {
			if (user.getName().length() > 30) { 
				addError("max_length_exceed", "userName", "30"); 
				return;
			}
		}

		if (Configuration.getInstance().isPrivacyPolicyOnSignUp()) {
			user.setPrivacyPolicyAcceptanceDate(new Date());
		}		

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
	
	private void validatePrivacyPolicy(Messages errors) { 
		Boolean privacyPolicy = (Boolean) getView().getValue("privacyPolicy");
		if (privacyPolicy == null || !privacyPolicy) {
			errors.add("privacy_policy_required_to_sign_up"); 
		}
	}

	private void validateEmail(Messages errors, String userName) throws Exception {
		IPropertyValidator validator = createEmailValidator(); 
		validator.validate(errors, userName, "email", "User");
	}

	protected IPropertyValidator createEmailValidator() throws Exception{
		return (IPropertyValidator) Class.forName(NaviOXPreferences.getInstance().getEmailValidatorForSignUpClass()).newInstance(); 
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
