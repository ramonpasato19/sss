package org.openxava.actions;

import org.openxava.web.*;

/**
 * 
 * @since 4m5
 * @author Javier Paniza
 */

public class GoSplitAction extends TabBaseAction implements IChainAction { 
	
	private String nextAction = null;
	
	public void execute() throws Exception {
		setNextMode(SPLIT);
		if (LIST.equals(getManager().getModeName())) {
			nextAction = "Navigation.first";
		}
		getTab().setEditor("List");
		Charts.release(getRequest());
	}

	public String getNextAction() throws Exception { 
		return nextAction;
	}

}
