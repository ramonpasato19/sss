package com.openxava.naviox.actions;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.model.*;
import org.openxava.util.*;

import com.openxava.naviox.model.*;
import com.openxava.naviox.util.*;

/**
 * 
 * @author Javier Paniza
 */
public class CreateNewOrganizationAction extends ViewBaseAction {
	
	public void execute() throws Exception {
		Map values = getView().getValues();
		Messages errors = MapFacade.validate(getModelName(), values);
		if (errors.contains()) {
			addErrors(errors);
			return;
		}
		String name = (String) values.get("name");
		Organization organization = Organizations.create(name);
		String url = getRequest().getContextPath() + "/o/" + organization.getId(); 
		addMessage(name + " " + XavaResources.getString("organization_created") + " <a href='" + url + "'>" + url + "</a>");
	}

}
