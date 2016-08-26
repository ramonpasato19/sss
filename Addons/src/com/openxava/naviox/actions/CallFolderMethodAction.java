package com.openxava.naviox.actions;

import org.openxava.actions.*;
import org.openxava.jpa.*;
import org.openxava.util.*;
import org.openxava.view.*;

import com.openxava.naviox.model.*;

/**
 * 
 * @author Javier Paniza
 */
public class CallFolderMethodAction extends CollectionBaseAction { 
	
	private String method;
	
	public void execute() throws Exception {
		View view = getCollectionElementView().getParent();
		view.setKeyEditable(false);
		Folder folder = (Folder) view.getEntity();
		XPersistence.getManager().refresh(folder);
		XObjects.execute(folder, method, int.class, getRow());
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

}
