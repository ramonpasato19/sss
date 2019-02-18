package com.openxava.naviox.actions;

import org.openxava.actions.*;
import org.openxava.util.*;

/**
 * 
 * @since 5.7
 * @author Javier Paniza
 */
public class InitMyPersonalDataAction extends ViewBaseAction {

	public void execute() throws Exception {
		getView().setModelName("User");
		getView().setViewName("PersonalData");
		getView().setValue("name", Users.getCurrent());
		getView().findObject();		
		if (Is.emptyString(getView().getValueString("email"))) {
			addWarning("enter_email_personal_data");
		}
	}

}
