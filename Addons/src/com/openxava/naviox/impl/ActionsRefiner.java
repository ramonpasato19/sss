package com.openxava.naviox.impl;

import java.util.*;

import org.openxava.application.meta.*;
import org.openxava.controller.*;
import org.openxava.controller.meta.*;
import org.openxava.util.*;
import org.openxava.view.*;

import com.openxava.naviox.model.*;


/**
 * Actions refiner, to remove the unauthorized actions.
 * 
 * @author Javier Paniza
 */
public class ActionsRefiner {
	
	private static ActionsRefiner instance;

	public void refine(MetaModule metaModule, Collection metaActions) {
		String currentUser = Users.getCurrent();
		if (currentUser == null) return;
		User user = User.find(currentUser);
		if (user == null) return; 
		Collection<MetaAction> excludedActions =  user.getExcludedMetaActionsForMetaModule(metaModule);	
		for (MetaAction action: excludedActions) {		
			metaActions.remove(action);
		}
	}

	/** 
	 * 
	 * @since 5.3 
	 */
	public boolean accept(String model, String unqualifiedAction) {
		MetaModule metaModule = MetaApplications.getMetaApplication(MetaModuleFactory.getApplication()).getMetaModule(model);
		String currentUser = Users.getCurrent();
		if (currentUser == null) return true; 
		return User.isActionForMetaModule(currentUser,unqualifiedAction, metaModule);
	}

	public static void init() {
		if (instance == null) instance = new ActionsRefiner();
		ModuleManager.setRefiner(instance);
		View.setRefiner(instance); 
	}
	
}
