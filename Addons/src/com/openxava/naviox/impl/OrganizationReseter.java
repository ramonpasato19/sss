package com.openxava.naviox.impl;

import javax.servlet.http.*;

import org.openxava.controller.*;
import com.openxava.naviox.util.*;


/**
 * To reset organization.
 * 
 * @author Javier Paniza
 */
public class OrganizationReseter {
	
	private static OrganizationReseter instance;

	public void reset(HttpSession session) {		
		Organizations.setPersistenceDefaultSchema(session);
	}

	public static void init() {
		if (instance == null) instance = new OrganizationReseter();
		ModuleManager.setReseter(instance);
	}
	
}
