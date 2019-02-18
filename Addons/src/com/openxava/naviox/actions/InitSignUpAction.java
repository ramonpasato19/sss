package com.openxava.naviox.actions;

import org.openxava.actions.*;

import com.openxava.naviox.model.*;

/**
 * 
 * @author Javier Paniza
 */
public class InitSignUpAction extends ViewBaseAction {

	public void execute() throws Exception {
		if (Configuration.getInstance().isUseEmailAsUserName()) {
			getView().setLabelId("userName", "email");
		}
		if (!Configuration.getInstance().isPrivacyPolicyOnSignUp()) {
			getView().setHidden("privacyPolicy", true);
		}
	}

}
