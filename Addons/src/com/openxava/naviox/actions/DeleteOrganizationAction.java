package com.openxava.naviox.actions;

import java.util.*;
import org.openxava.actions.*;
import com.openxava.naviox.util.*;

/**
 * 
 * @author Javier Paniza
 */
public class DeleteOrganizationAction extends ViewBaseAction {

	public void execute() throws Exception {
		Map values = getView().getValues();
		Organizations.delete((String) values.get("id"));  
		addMessage("organization_deleted"); 		
	}

}
