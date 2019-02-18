package com.openxava.naviox.actions;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.model.*;
import org.openxava.util.*;
import com.openxava.naviox.model.*;
import com.openxava.naviox.util.*;

/**
 * 
 * @author Javier Paniza
 */
public class RecoverPasswordAction extends ViewBaseAction {

	public void execute() throws Exception {
		Map values = getView().getValues();
		Messages errors = MapFacade.validate("RecoverPassword", values);
		String email = (String) values.get("email");
		User user = User.findByEmail(email);
		if (user == null) { 
			errors.add("email_not_found", email);  
		}
		
		if (errors.contains()) {
			addErrors(errors);
			return;
		}
		
		String userName = Is.emptyString(user.getGivenName())?user.getEmail():user.getGivenName();
		user.generatePasswordRecoveringCode();
		String baseURL = getRequest().getRequestURL().toString().split("/dwr/")[0];
		String organization = Organizations.getCurrent(getRequest());
		String organizationURL = organization == null?"":"/o/" + organization;
		String url = baseURL + organizationURL + "/m/RestorePassword?rcvcd=" + user.getPasswordRecoveringCode(); 
		
		Emails.send(XavaPreferences.getInstance().getSMTPUserID(), email, 
			XavaResources.getString("recover_password_email_subject", getManager().getApplicationName()),  
			XavaResources.getString("recover_password_email_content", userName, url)
		);
		addMessage("password_recovery_email_sent"); 
	}

}

