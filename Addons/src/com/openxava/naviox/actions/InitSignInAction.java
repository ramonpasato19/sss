package com.openxava.naviox.actions;

import org.openxava.actions.*;
import com.openxava.naviox.model.*;
import com.openxava.naviox.util.*;

/**
 * @since 5.2
 * @author Javier Paniza
 */
public class InitSignInAction extends ViewBaseAction {  

	public void execute() throws Exception {
		String organization = Organizations.getCurrent(getRequest()); 
		if (NaviOXPreferences.getInstance().isShowOrganizationOnSignIn() &&
			!Configuration.getInstance().isSharedUsersBetweenOrganizations()) { 
			getView().setHidden("organization", organization != null || Organization.count() == 0);			
		}
		else {
			getView().setHidden("organization", true);
		}
		if (organization == null && Configuration.getInstance().isGuestCanCreateAccount()) {
			addActions("GoSignUp.signUp"); 
		}
		else if (organization != null && Configuration.getInstance().isGuestCanCreateAccountInOrganizations()) {
			addActions("GoSignUp.signUp"); 
		}
	}

}
