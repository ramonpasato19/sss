package com.openxava.naviox.actions;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.model.*;

import com.openxava.naviox.model.*;

/**
 * 
 * @author Javier Paniza
 */

public class SaveUserAction extends SaveAction {
	
	public void execute() throws Exception {
		if (getView().isKeyEditable()) {
			Map key = getView().getKeyValues();
			super.execute();
			if (!getErrors().contains() ) {
				User user = (User) MapFacade.findEntity("User", key);
				user.addDefaultRole();
			}			
		}
		else {
			super.execute();
		}
	}

}
