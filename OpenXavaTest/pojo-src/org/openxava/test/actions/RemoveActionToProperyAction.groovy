package org.openxava.test.actions

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
class RemoveActionToProperyAction extends ViewBaseAction {
	
	String property
	String action
	
	void execute() {
		view.removeActionForProperty(property, action)
	}

}
