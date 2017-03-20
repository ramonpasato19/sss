package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
public class TestSessionAttributeAction extends BaseAction {

	private String attribute;
	
	public void execute() throws Exception {
		Object o = getRequest().getSession().getAttribute(attribute);
		if (o == null) {
			addMessage("attribute_not_exists", "'" + attribute + "'");
		}
		else {
			addMessage("attribute_exists", "'" + attribute + "'");
		}
	}
	
	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

}
