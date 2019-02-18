package com.openxava.phone.controller;

import org.openxava.controller.meta.*;

/**
 * 
 * @author Javier Paniza
 */
@SuppressWarnings("serial")
public class PhoneModuleManager extends org.openxava.controller.ModuleManager {
	
	private int dialogLevel = 0; 
	private boolean callingAddSimpleMetaAction = false; 
		
	public void addSimpleMetaAction(MetaAction action) { 		
		if (dialogLevel > 0 && !callingAddSimpleMetaAction) {
			callingAddSimpleMetaAction = true;
			addMetaAction(action);
			callingAddSimpleMetaAction = false;
		}
		else super.addSimpleMetaAction(action);
	}
	
	public void showDialog() {
		dialogLevel++;
	}
	
	public void closeDialog() {
		dialogLevel--;
	}		
}
