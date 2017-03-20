package com.openxava.naviox.actions;

import org.openxava.actions.*;
import org.openxava.model.*;

import com.openxava.naviox.model.*;

/**
 *
 * @author Javier Paniza
 */

public class GoOrganizationAction extends TabBaseAction implements IForwardAction {
	
	private String forwardURI; 

	public void execute() throws Exception {
		Organization organization = (Organization) MapFacade.findEntity("Organization", getSelectedKeys()[0]);
		forwardURI = organization.getUrl(); 
	}

	public String getForwardURI() {
		return forwardURI;
	}

	public boolean inNewWindow() {
		return false;
	}

}
