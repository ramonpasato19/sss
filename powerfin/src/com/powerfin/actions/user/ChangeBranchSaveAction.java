package com.powerfin.actions.user;

import java.util.*;

import org.openxava.actions.*;

import com.powerfin.exception.*;
import com.powerfin.helper.*;

public class ChangeBranchSaveAction extends SaveAction {

	@SuppressWarnings("unchecked")
	public void execute() throws Exception 
	{
		String userSelected = null;
		String currentUser = UserHelper.getCurrentUserName();
		
		Map<String, String> usersMap = (Map<String, String>) getView().getRoot().getValue("user");
		if (usersMap.get("name")!=null) {
			userSelected = (String)usersMap.get("name");
		}
		
		if (!currentUser.equals(userSelected))
			throw new OperativeException("can_not_change_another_user_branch");
		
		super.execute();
		
		if (getErrors().isEmpty()) {
				UserHelper.registerBranchInSession(getRequest().getSession());
		}
	}
}
