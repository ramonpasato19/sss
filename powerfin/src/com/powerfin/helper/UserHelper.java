package com.powerfin.helper;

import org.openxava.jpa.*;
import org.openxava.util.*;

import com.openxava.naviox.model.*;

public class UserHelper {

	public static User getCurrent()
	{
		return XPersistence.getManager().find(User.class, Users.getCurrent());
	}
	
	public static String getCurrentUserName()
	{
		return Users.getCurrent();
	}
}
