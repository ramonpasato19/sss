package com.openxava.naviox.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
public class GoAddModulesRightsToRoleAction extends GoAddElementsToCollectionAction {
	
	
	public void execute() throws Exception {
		String role = getCollectionElementView().getParent().getValueString("name");		
		super.execute();
		getTab().setModelName("Module");
		getTab().setBaseCondition("${name} not in (select mr.module.name from ModuleRights mr where mr.role.name='" + role + "' )"); 
		setNextController("AddToModulesRights");		
	}

}
