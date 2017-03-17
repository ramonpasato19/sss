package org.openxava.test.actions

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
class ShowSelectedAuthorsAction extends CollectionBaseAction {
	
	void execute() {
		getMapsSelectedValues().each {
			addMessage "${it.name}, ${it.sex}"
		}
	}

}
