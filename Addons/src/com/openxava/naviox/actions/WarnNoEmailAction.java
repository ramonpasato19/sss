package com.openxava.naviox.actions;

import org.openxava.actions.*;
import org.openxava.util.*;

/**
 * @since 5.7 
 * @author Javier Paniza
 */
public class WarnNoEmailAction extends BaseAction {

	public void execute() throws Exception {
		if (getManager().isListMode() && Is.emptyString(Users.getCurrentUserInfo().getEmail())) {
			addWarning("warn_no_email");
		}
	}

}
