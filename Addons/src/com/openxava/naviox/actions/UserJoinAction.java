package com.openxava.naviox.actions;

import org.apache.commons.logging.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

import com.openxava.naviox.*;
import com.openxava.naviox.model.*;
import com.openxava.naviox.util.*;

/**
 * @author Javier Paniza
 */
public class UserJoinAction extends ForwardToOriginalURIBaseAction { 
	
	private static Log log = LogFactory.getLog(UserJoinAction.class);	

	public void execute() throws Exception {
		User user = new User();
		user.setName(Users.getCurrent());	
		XPersistence.getManager().persist(user);
		Role role = Role.findJoinedRole();
		if (role == null) {
			log.info(XavaResources.getString("creating_joined_role"));
			role = Role.createJoinedRole();
		}
		user.addRole(role);
		XPersistence.commit();
		XPersistence.reset();
		
		String organizationName = Organizations.getCurrent(getRequest());
		Organization organization = Organization.find(organizationName);
		User rootUser = User.find(Users.getCurrent());
		rootUser.addOrganization(organization);
		Modules modules = (Modules) getRequest().getSession().getAttribute("modules");
		modules.reset();	
		forwardToOriginalURI();
	} 
		
}
