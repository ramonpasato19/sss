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
	
	private boolean userBeforeJoined;
		
	public void execute() throws Exception {
		String userName = Users.getCurrent();		 
		User user = User.find(userName);
		Role joined = getJoinedRole(); 
		
		if (user != null) {
			if (!user.hasRole(joined.getName())) user.addRole(joined);
			log.warn("user_exists_in_organization");			
		} else {
			user = new User();
			user.setName(userName);	
			XPersistence.getManager().persist(user);
			user.addRole(joined);
		}						
		XPersistence.commit();
		XPersistence.reset();
		
		String organizationName = Organizations.getCurrent(getRequest());
		Organization organization = Organization.find(organizationName);
		User rootUser = User.find(Users.getCurrent());
		userBeforeJoined = !rootUser.addOrganization(organization);
		if (userBeforeJoined) log.warn("user_already_joined_organization");
		Modules modules = (Modules) getRequest().getSession().getAttribute("modules");
		modules.reset();	
		forwardToOriginalURI();
	} 
	
	@Override
	public String getForwardURI() {
		String suffix = "";
		if (userBeforeJoined) suffix = "m/FirstSteps";
		return super.getForwardURI() + suffix;
	}
	
	private Role getJoinedRole() {
		Role joined = Role.findJoinedRole();
		if (joined == null) {
			log.info(XavaResources.getString("creating_joined_role"));
			joined = Role.createJoinedRole();
		}
		return joined;
	}
}
