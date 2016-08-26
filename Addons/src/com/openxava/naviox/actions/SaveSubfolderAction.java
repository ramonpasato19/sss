package com.openxava.naviox.actions;

import java.util.*;

import javax.ejb.*;

import org.openxava.actions.*;
import org.openxava.model.*;

/**
 * 
 * @author Javier Paniza
 */
public class SaveSubfolderAction extends SaveElementInCollectionAction {
	
	protected void saveCollectionElement(Map containerKey) throws Exception {
		Map parentKey = new HashMap();
		parentKey.put("parent", containerKey);
		Map values = getValuesToSave();			
		values.putAll(parentKey);			
		try {
			MapFacade.setValues("Folder", getCollectionElementView().getKeyValues(), values);
			addMessage("subfolder_modified", values.get("name")); 
		}
		catch (ObjectNotFoundException ex) {
			MapFacade.create(getCollectionElementView().getModelName(), values);
			addMessage("subfolder_created", values.get("name")); 			
		}		
	}


}
