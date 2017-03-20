package org.openxava.actions;

/**
 * @since 5.6
 * @author Javier Paniza
 */
public class ChangeListConfigurationNameAction extends TabBaseAction {
	
	public void execute() throws Exception {
		validateViewValues();
		getTab().setConfigurationName(getView().getValueString("name"));
		closeDialog();
	}

}
