package com.openxava.naviox.actions;

import java.rmi.*;
import java.util.*;

import javax.ejb.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;
import org.openxava.model.*;
import org.openxava.util.*;
import org.openxava.validators.*;

import com.openxava.naviox.model.*;

/**
 * 
 * @author Javier Paniza
 */

public class AddModulesRightsToRoleAction extends AddElementsToCollectionAction {
	
	
	protected void associateEntity(Map keyValues) throws ValidationException, XavaException, ObjectNotFoundException, FinderException, RemoteException {		
		Module module = (Module) MapFacade.findEntity("Module", keyValues);
		Role role = (Role) MapFacade.findEntity("Role", getCollectionElementView().getParent().getKeyValues());
		ModuleRights rights = new ModuleRights();
		rights.setModule(module);
		rights.setRole(role);
		XPersistence.getManager().persist(rights);
	}


}
