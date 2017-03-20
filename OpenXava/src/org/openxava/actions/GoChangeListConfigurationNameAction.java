package org.openxava.actions;

import org.openxava.model.transients.*;

/**
 * tmp
 * @author Javier Paniza
 */
public class GoChangeListConfigurationNameAction extends TabBaseAction {
	
	public void execute() throws Exception {
		showDialog();
		getView().setTitleId("List.changeConfigurationName");
		WithRequiredLongName dialog = new WithRequiredLongName();
		getView().setModel(dialog);
		getView().setValue("name", getTab().getConfigurationName());
		setControllers("ChangeListConfigurationName");
	}

}
