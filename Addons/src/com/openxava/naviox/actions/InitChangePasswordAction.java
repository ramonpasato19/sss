package com.openxava.naviox.actions;

import org.openxava.actions.*;
import org.openxava.util.*;

import com.openxava.naviox.model.*;

/**
 * 
 * @since 5.4
 * @author Javier Paniza
 */
public class InitChangePasswordAction extends BaseAction {

	public void execute() throws Exception {
		User user = User.find(Users.getCurrent());
		if (user.isForceChangePassword()) { 
			addWarning("must_change_password"); 
		}
	}

}
