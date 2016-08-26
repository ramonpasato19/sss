package org.openxava.test.actions

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
class ShowAllAuthorsAction extends CollectionBaseAction {
	
	void execute() {
		getMapValues().each {
			addMessage "${it.name}, ${it.sex}"
		}
	}

}
