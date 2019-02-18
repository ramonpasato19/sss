package com.openxava.naviox.actions;

import org.openxava.actions.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */
public class InitRestorePasswordAction extends ViewBaseAction {

	public void execute() throws Exception {
		String code = getRequest().getParameter("rcvcd");
		if (!Is.emptyString(code)) {
			getView().putObject("recoveringCode", code);
		}
	}

}

