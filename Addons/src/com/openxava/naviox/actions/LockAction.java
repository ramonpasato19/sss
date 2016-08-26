package com.openxava.naviox.actions;

import javax.inject.*;

import org.openxava.actions.*;
import org.openxava.util.*;

/**
 * 
 * @since 5.4
 * @author Javier Paniza
 */
public class LockAction extends ViewBaseAction {
	
	@Inject
	private Boolean locked;
	
	@Inject
	private Boolean locking;


	public void execute() throws Exception {
		if (Users.getCurrent() == null) return;
		if (getView().getModelName().equals("SignIn") && getView().getViewName().equals("Unlock")) return;
		showDialog();
		getView().setTitle("!x:" +  XavaResources.getString("unlock_session")); 
		getView().setModelName("SignIn");
		getView().setViewName("Unlock");
		addActions("SessionLocker.unlock"); 
		locked = true;
		locking = true;
	}
	
}
