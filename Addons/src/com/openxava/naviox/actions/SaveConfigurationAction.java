package com.openxava.naviox.actions;

import org.openxava.actions.*;

import com.openxava.naviox.model.*;

/**
 * 
 * @since 5.4
 * @author Javier Paniza
 */
public class SaveConfigurationAction extends SaveAction {

	public void execute() throws Exception {
		super.execute();
		Configuration.resetInstance();
		if (getMessages().contains("entity_created")) {
			getMessages().remove("entity_created");
			addMessage("entity_modified", getModelName());
		}
	}

}
