package com.openxava.naviox.web.dwr;

import javax.servlet.http.*;
import org.openxava.util.*;
import com.openxava.naviox.util.*;

/**
 * 
 * @since 5.2.1
 * @author Javier Paniza
 */
public class RequestReseter { 
	
	public static void reset(HttpServletRequest request) {
		Users.setCurrent(request);
		Locales.setCurrent(request); 
		Organizations.setPersistenceDefaultSchema(request.getSession()); 
		SessionData.setCurrent(request); 
	}

}
