package com.openxava.phone.controller;

import java.util.*;

import javax.servlet.http.*;

import org.openxava.application.meta.*;
import org.openxava.controller.*;
import org.openxava.controller.meta.*;
import org.openxava.model.meta.*;
import org.openxava.tab.*;
import org.openxava.util.*;
import org.openxava.view.*;

/**
 * 
 * @author Javier Paniza
 *
 */
public class PhoneManager {
	
	private ModuleManager manager;
	
	public PhoneManager(ModuleManager manager) {
		this.manager = manager;
	}
	
	public MetaAction getDefaultMetaAction() {
		MetaAction defaultMetaAction = null;
		Iterator it = manager.getMetaActions().iterator();
		int max = -1;
		while (it.hasNext()) {
			MetaAction a = (MetaAction) it.next();
			if (a.isHidden()) continue; 
			if (!a.appliesToMode(manager.getModeName())) continue;
			if (a.getByDefault() > max) {
				max = a.getByDefault();					
				defaultMetaAction = a;
			}
		}
		return defaultMetaAction;
	}
	
	public String getTitle(HttpServletRequest request) {
		ModuleContext context = (ModuleContext) request.getSession().getAttribute("context");
		java.util.Stack previousViews = (java.util.Stack) context.get(request, "xava_previousViews");
		if (previousViews.isEmpty()) return getMetaModule().getLabel();
		else {
			View view = (View) context.get(request, "xava_view");
			String modelName = view.getModelName();
			if (modelName == null) {
				Tab tab = (Tab) context.get(request, "xava_tab");
				modelName = tab.getModelName();  
			}
			return MetaModel.get(modelName).getLabel();
		}
	}
	
	private MetaModule getMetaModule() throws ElementNotFoundException, XavaException {
		return MetaApplications.getMetaApplication(
				manager.getApplicationName()).getMetaModule(manager.getModuleName());			
	}

	
}
