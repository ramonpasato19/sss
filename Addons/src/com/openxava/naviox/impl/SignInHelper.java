package com.openxava.naviox.impl;

import java.util.*;

import javax.servlet.http.*;

import org.openxava.hibernate.*;
import org.openxava.jpa.*;
import org.openxava.util.*;
import org.openxava.view.*;

import com.openxava.naviox.*;
import com.openxava.naviox.model.*;
import com.openxava.naviox.util.*;

/**
 * 
 * @author Javier Paniza
 */

public class SignInHelper {
	
	public static void init(HttpServletRequest request, View view) { 
		String organization = view.getValueString("organization.id");
		if (!Is.emptyString(organization)) {
			Organizations.setCurrent(request, organization);
			XPersistence.setDefaultSchema(organization);
			XHibernate.setDefaultSchema(organization); 
		}
	}
	
	public static String refineForwardURI(HttpServletRequest request, String forwardURI) {  
		return OrganizationURIs.refine(request, forwardURI); 
	}	
	
	public static void signIn(HttpSession session, String userName) {		
		session.setAttribute("naviox.user", userName);
		session.setAttribute("xava.user", userName); 
		UserInfo userInfo = toUserInfo(userName);
		userInfo.setOrganization(Organizations.getCurrent(session));
		session.setAttribute("xava.portal.userinfo", userInfo);
		Users.setCurrentUserInfo(userInfo);
		Modules modules = (Modules) session.getAttribute("modules");
		modules.reset();		
		User user = User.find(userName);
		if (user.isForceChangePassword()) {
			modules.setCurrent(MetaModuleFactory.getApplication(), "ChangePassword", false);
		} 
		user.setLastLoginDate(new Date()); 
	}
	
	public static boolean isAuthorized(String userName, String password) { 
		return isAuthorized(userName, password, new Messages());
	}

	/**
	 * @since 5.4 
	 */
	public static boolean isAuthorized(String userName, String password, Messages errors) {  
		return isAuthorized(userName, password, errors, "unauthorized_user");
	}	
		
	/**
	 * @since 5.4 
	 */	
	public static boolean isAuthorized(String userName, String password, Messages errors, String unauthorizedMessage) { 
		User user = User.find(userName);
		if (user == null) {
			errors.add(unauthorizedMessage);
			return false;
		}
		boolean authorized = user.isAuthorized(password);
		if (!authorized) errors.add(unauthorizedMessage);
		if (Configuration.getInstance().getLoginAttemptsBeforeLocking() > 0) {
			if (authorized) {
				user.setFailedLoginAttempts(0);
			}
			else {
				if (user.isActive()) {
					if (user.getFailedLoginAttempts() < Configuration.getInstance().getLoginAttemptsBeforeLocking() - 1) {
						user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
						int remainingLoginAttempts = Configuration.getInstance().getLoginAttemptsBeforeLocking() - user.getFailedLoginAttempts(); 
						if (remainingLoginAttempts <= 3) {
							errors.add("remaining_attempts", remainingLoginAttempts); 
						}
					}
					else {
						user.setActive(false);
						errors.add("user_blocked"); 
					}
				}
			}
		}
		if (Configuration.getInstance().getInactiveDaysBeforeDisablingUser() > 0) {
			Date lastDate = user.getLastLoginDate() == null?user.getCreationDate():user.getLastLoginDate();
			if (Dates.daysInterval(lastDate, new Date(), false) > Configuration.getInstance().getInactiveDaysBeforeDisablingUser()) {
				user.setActive(false);
				errors.add("user_blocked");
				authorized = false;
			}
		}
		return authorized;
	}	 

	private static UserInfo toUserInfo(String userName) {
		User user = User.find(userName);
		UserInfo info = new UserInfo();
		info.setId(user.getName());
		info.setGivenName(user.getGivenName());
		info.setFamilyName(user.getFamilyName());
		info.setEmail(user.getEmail());
		info.setJobTitle(user.getJobTitle());
		info.setMiddleName(user.getMiddleName());
		info.setNickName(user.getNickName());
		info.setBirthDateYear(Dates.getYear(user.getBirthDate()));
		info.setBirthDateMonth(Dates.getMonth(user.getBirthDate()));
		info.setBirthDateDay(Dates.getDay(user.getBirthDate()));
		return info;
	}

}
