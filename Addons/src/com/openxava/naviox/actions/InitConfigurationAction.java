package com.openxava.naviox.actions;

import org.openxava.actions.*;
import com.openxava.naviox.model.*;

/**
 * 
 * @since 5.4
 * @author Javier Paniza
 */
public class InitConfigurationAction extends ViewBaseAction {
	
	public void execute() throws Exception { 
		if (getManager().getDialogLevel() > 0) return; 
		getView().setModel(Configuration.getInstance());
		if (Configuration.getInstance().getId() > 0) getView().setKeyEditable(false);
	}

}
