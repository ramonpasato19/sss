package com.openxava.naviox.actions;

import org.openxava.actions.*;
import org.openxava.model.*;
import org.openxava.util.*;

/**
 * @since 5.7
 * @author Javier Paniza
 */
public class SaveMyPersonalDataAction extends ViewBaseAction {

	public void execute() throws Exception {
		MapFacade.setValues(getModelName(), getView().getKeyValues(), getView().getValues());
		Users.getCurrentUserInfo().setEmail(getView().getValueString("email")); 
		addMessage("personal_data_updated"); 
	}

}
