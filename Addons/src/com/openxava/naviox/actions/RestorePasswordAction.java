package com.openxava.naviox.actions;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.util.*;
import com.openxava.naviox.model.*;

/**
 * 
 * @author Javier Paniza
 */

public class RestorePasswordAction extends ViewBaseAction {  
	
	public void execute() throws Exception {
		String recoveringCode = (String) getView().getObject("recoveringCode");
		User user = User.findByPasswordRecoveringCode(recoveringCode);
		if (user == null) {
			addError("recovering_code_incorrect"); 
			return;
		}
		if (Dates.isDifferentDay(user.getPasswordRecoveringDate(), new Date())) {
			addError("recovering_code_incorrect"); 
			return;			
		}
		
		String newPassword = getView().getValueString("newPassword"); 
		String repeatNewPassword = getView().getValueString("repeatNewPassword");
		if (!Is.equal(newPassword, repeatNewPassword)) {
			addError("passwords_not_match");
			return;
		}
		
		user.setPassword(newPassword);
		addMessage("password_changed"); 
		getView().clear();
	}

}
