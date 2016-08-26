package com.openxava.naviox.actions;

import org.openxava.actions.*;

import com.openxava.naviox.model.*;
import com.openxava.naviox.util.*;

/**
 * @since 5.2
 * @author Javier Paniza
 */
public class InitSignInAction extends ViewBaseAction {

	public void execute() throws Exception {
		String organization = Organizations.getCurrent(getRequest());
		getView().setHidden("organization", organization != null || Organization.count() == 0);
	}

}
