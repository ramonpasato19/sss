package org.openxava.test.actions

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
class AddActionToProperyAction extends ViewBaseAction {
	
	String property
	String action
	
	void execute() {
		view.addActionForProperty(property, action)
	}

}
