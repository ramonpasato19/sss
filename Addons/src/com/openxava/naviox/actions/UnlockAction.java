package com.openxava.naviox.actions;

import javax.inject.*;

import org.openxava.actions.*;
import org.openxava.util.*;

import com.openxava.naviox.impl.*;

/**
 *
 * @since 5.4
 * @author Javier Paniza
 */
public class UnlockAction extends ViewBaseAction {
	
	@Inject
	private Boolean locked;

	@Inject
	private Boolean locking;

	@Override
	public void execute() throws Exception {
		if (SignInHelper.isAuthorized(Users.getCurrent(), getView().getValueString("password"), getErrors(), "invalid_password")) {
			locked = false;
			locking = false;
			closeDialog();
		}		
	}

}
