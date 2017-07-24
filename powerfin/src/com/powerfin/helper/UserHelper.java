package com.powerfin.helper;

import java.util.*;

import javax.servlet.http.*;

import org.openxava.jpa.*;
import org.openxava.util.*;

import com.openxava.naviox.model.*;
import com.powerfin.model.*;

public class UserHelper {

	public static User getCurrent() throws Exception
	{
		return XPersistence.getManager().find(User.class, Users.getCurrent());
	}
	
	public static String getCurrentUserName()
	{
		return Users.getCurrent();
	}
	
	public static Branch getSessionBranch(HttpServletRequest request)
	{
		return (Branch) request.getSession().getAttribute("powerfin.branch");
	}
	
	public static Branch getRegisteredBranch() throws Exception
	{
		return getRegisteredBranch(getCurrentUserName());
	}
	
	@SuppressWarnings("unchecked")
	public static Branch getRegisteredBranch(String userName) throws Exception
	{
		List<BranchUser> users = XPersistence.getManager().createQuery("SELECT o FROM BranchUser o "
				+ "WHERE o.user.name = :userName")
				.setParameter("userName", userName)
				.getResultList();
		
		if (!users.isEmpty())
			return ((BranchUser)users.get(0)).getBranch();
		
		return null;
	}
	
	public static void registerBranchInSession(HttpSession session) throws Exception {
		Branch branch = getRegisteredBranch();
		if (branch!=null)
		{
			session.setAttribute("powerfin.branch", branch);
			System.out.println("Set Session Branch: "+branch.getBranchId()+", Name: "+branch.getName());
		}
	}
}
