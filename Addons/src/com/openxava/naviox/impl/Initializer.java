package com.openxava.naviox.impl;

import javax.servlet.*;

import com.openxava.naviox.util.*;

/**
 * 
 * @author Javier Paniza
 */
public class Initializer {
	
	public static void init(ServletRequest request) {
		Organizations.init(request);
		ActionsRefiner.init();
		MembersRefiner.init(); 
		OrganizationReseter.init(); 
	}

}
