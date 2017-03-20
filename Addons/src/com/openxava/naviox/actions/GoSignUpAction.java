package com.openxava.naviox.actions;


import org.openxava.actions.*;

import com.openxava.naviox.impl.*;

/**
 * 
 * @author Javier Paniza
 */
public class GoSignUpAction extends BaseAction implements IForwardAction {

	public void execute() throws Exception {
	}

	public String getForwardURI() {
		return SignInHelper.refineForwardURI(getRequest(), "/m/SignUp");
	}

	public boolean inNewWindow() {
		return false;
	}

}