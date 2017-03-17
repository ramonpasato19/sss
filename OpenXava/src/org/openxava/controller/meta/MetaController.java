package org.openxava.controller.meta;


import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;
import org.openxava.util.meta.*;


public class MetaController extends MetaElement {
	private static Log log = LogFactory.getLog(MetaController.class);
	private String className; // Only for spanish/swing version
	private Collection metaActions = new ArrayList();
	private Collection parentsNames = new ArrayList();
	private Collection parents = new ArrayList();
	private Map mapMetaActions = new HashMap();
	private Collection<MetaSubcontroller> metaSubcontrollers = new ArrayList<MetaSubcontroller>();
	private Collection<MetaControllerElement> metaControllerElements = new ArrayList<MetaControllerElement>();	// actions and subcontroller order by occurrence
	
	public void addMetaControllerElement(MetaControllerElement controllerElement){
		metaControllerElements.add(controllerElement);
	}
	
	public Collection<MetaControllerElement> getMetaControllerElements(){
		return metaControllerElements;
	}
	
	private void addMetaControllerElementOverWritteActions(List result, Collection<MetaControllerElement> elements){
	    for (MetaControllerElement controllerElement : elements){
	        if (controllerElement instanceof MetaAction){
	            // overwritte actions
	            MetaAction metaAction = (MetaAction)controllerElement;
	            int pos = -1;
	            for (int i=0; i<result.size(); i++) {
	                if (result.get(i) instanceof MetaAction && ((MetaAction)result.get(i)).getName().equals(metaAction.getName())) {
	                    pos = i;
	                }
	            }
	            if (pos < 0) result.add(metaAction);
	            else {
	                result.remove(pos);
	                result.add(pos, metaAction);
	            }
	        }
	        else result.add(controllerElement); // MetaSubcontroller
	    }
	}

	private void getMetaControllerElementParents(List result, Collection<MetaController> parents){
	    for(MetaController parent : parents) {
	        if (!parent.getParents().isEmpty()) getMetaControllerElementParents(result, parent.getParents());
	        addMetaControllerElementOverWritteActions(result, parent.getMetaControllerElements());
	    }
	}

	public Collection<MetaControllerElement> getAllMetaControllerElements(){
	    List<MetaControllerElement> result = new ArrayList();
	    getMetaControllerElementParents(result, getParents());
	    addMetaControllerElementOverWritteActions(result, getMetaControllerElements());
	    return result;
	}
	
	/**
	 * Only for spanish/swing version
	 */
	public String getClassName() {
		return Is.emptyString(className)?"puntocom.xava.xcontrolador.tipicos.ControladorVacio":className;
	}
	/**
	 * Only for spanish/swing version
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @since 4.8
	 */
	public void addMetaSubcontroller(MetaSubcontroller subcontroller){
		metaSubcontrollers.add(subcontroller);
	}

	public void addMetaAction(MetaAction action) {
		metaActions.add(action);
		action.setMetaController(this);
		mapMetaActions.put(action.getName(), action);
	}
	
	public void addParentName(String parentName) {
		if (parentsNames == null) parentsNames = new ArrayList();
		parentsNames.add(parentName);  	
		parents = null;	
	}
	
	public MetaAction getMetaAction(String name) throws ElementNotFoundException {
		MetaAction a = (MetaAction) mapMetaActions.get(name);
		if (a == null) throw new ElementNotFoundException("action_not_found", name, getName());		
		return a; 
	}
	
	/**
	 * @return Not null, of type <tt>MetaAction</tt> and read only.
	 */
	
	public Collection getMetaActions() {
		return Collections.unmodifiableCollection(metaActions);
	}
	
	/**
	 * @since 4.8
	 */
	public Collection<MetaSubcontroller> getMetaSubcontrollers(){
		return metaSubcontrollers;
	}

	public boolean containsMetaAction(String actionName) {
		return mapMetaActions.containsKey(actionName); 
	}	

	// Search by name, so ignoring the controller name
	private int indexOf(List<MetaAction> actions, MetaAction metaAction) { 
		for (int i=0; i<actions.size(); i++) {
			if (actions.get(i).getName().equals(metaAction.getName())) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * The MetaActions of this controller and all its parents. <p>
	 */
	public Collection getAllMetaActions() throws XavaException { 
		return getAllMetaActions(false, false);
	}
	
	/**
	 * The not hidden MetaActions of this controller and all its parents. <p>
	 */
	public Collection getAllNotHiddenMetaActions() throws XavaException {  
		return getAllMetaActions(true, false);
	}
	
	/**
	 * The not hidden MetaActions of this controller and all its parents and subcontrollers recursively. <p>
	 * @since 5.5.1
	 */
	public Collection getAllNotHiddenMetaActionsRecursive() throws XavaException {   
		return getAllMetaActions(true, true);
	}

	
	private Collection getAllMetaActions(boolean excludeHidden, boolean recursive) throws XavaException {  
		List result = new ArrayList();
		// Adding parents
		Iterator itParents = getParents().iterator();
		while (itParents.hasNext()) {
			MetaController parent = (MetaController) itParents.next();
			result.addAll(parent.getAllMetaActions(excludeHidden, recursive));
		}
				
		// and now ours 
		Iterator it = metaActions.iterator();
		while (it.hasNext()) {
			MetaAction metaAction = (MetaAction) it.next();
			if (excludeHidden && metaAction.isHidden()) continue;
			int pos = indexOf(result, metaAction); 
			if (pos < 0) result.add(metaAction);
			else {
				result.remove(pos);
				result.add(pos, metaAction);
			} 			
		}		
		
		// The subcontrollers
		if (recursive) {
			for (MetaSubcontroller subcontroller: metaSubcontrollers) {
				result.addAll(subcontroller.getMetaController().getAllMetaActions(excludeHidden, recursive));
			}
		}
		
		return result;
	}
	
		
	
	public String getId() {
		return getName();
	}
	
	/**
	 * Actions from father and subcontrollers are included.  
	 */
	public Collection getMetaActionsOnInit() throws XavaException {
		Collection result = new ArrayList();
		
		// Adding parents
		Iterator itParents = getParents().iterator();
		while (itParents.hasNext()) {
			MetaController parent = (MetaController) itParents.next();
			result.addAll(parent.getMetaActionsOnInit());
		}

		// Ours
		Iterator it = metaActions.iterator();		
		while (it.hasNext()) {
			MetaAction metaAction = (MetaAction) it.next();			
			if (metaAction.isOnInit()) {
				result.add(metaAction);
			}
		}

		// Subcontrollers
		for (MetaSubcontroller subcontroller: getMetaSubcontrollers()) {
			result.addAll(subcontroller.getMetaController().getMetaActionsOnInit());
		}
		
		return result;
	}
	public boolean hasParents() {
		return parentsNames != null;		 	
	}		
	
	/**
	 * @return of type MetaController
	 */
	public Collection getParents() throws XavaException {
		if (!hasParents()) return Collections.EMPTY_LIST;
		if (parents == null) {
			parents = new ArrayList();
			Iterator it = parentsNames.iterator();
			while (it.hasNext()) {
				String name = (String) it.next();
				parents.add(MetaControllers.getMetaController(name));
			}
		}
		return parents;
	}
	
	/**
	 * Actions from father and subcontrollers are included.  
	 */
	public Collection getMetaActionsOnEachRequest() throws XavaException {
		Collection result = new ArrayList();

		Iterator itParents = getParents().iterator();
		while (itParents.hasNext()) {
			MetaController parent = (MetaController) itParents.next();
			result.addAll(parent.getMetaActionsOnEachRequest());
		}

		Iterator it = metaActions.iterator();		
		while (it.hasNext()) {
			MetaAction metaAction = (MetaAction) it.next();			
			if (metaAction.isOnEachRequest()) {
				result.add(metaAction);
			}
		}
		
		for (MetaSubcontroller subcontroller: getMetaSubcontrollers()) {
			result.addAll(subcontroller.getMetaController().getMetaActionsOnEachRequest());
		}

		return result;
	}
	
	/**
	 * Actions from father and subcontrollers are included.  
	 */
	public Collection getMetaActionsAfterEachRequest() throws XavaException { 
		Collection result = new ArrayList();

		Iterator itParents = getParents().iterator();
		while (itParents.hasNext()) {
			MetaController parent = (MetaController) itParents.next();
			result.addAll(parent.getMetaActionsAfterEachRequest());
		}

		Iterator it = metaActions.iterator();		
		while (it.hasNext()) {
			MetaAction metaAction = (MetaAction) it.next();			
			if (metaAction.isAfterEachRequest()) {
				result.add(metaAction);
			}
		}
		
		for (MetaSubcontroller subcontroller: getMetaSubcontrollers()) {
			result.addAll(subcontroller.getMetaController().getMetaActionsAfterEachRequest());
		}
		
		return result;
	}
	
	/**
	 * Actions from father and subcontrollers are included.  
	 */
	public Collection getMetaActionsBeforeEachRequest() throws XavaException { 
		Collection result = new ArrayList();

		Iterator itParents = getParents().iterator();
		while (itParents.hasNext()) {
			MetaController parent = (MetaController) itParents.next();
			result.addAll(parent.getMetaActionsBeforeEachRequest());
		}

		Iterator it = metaActions.iterator();		
		while (it.hasNext()) {
			MetaAction metaAction = (MetaAction) it.next();			
			if (metaAction.isBeforeEachRequest()) {
				result.add(metaAction);
			}
		}
		
		for (MetaSubcontroller subcontroller: getMetaSubcontrollers()) {
			result.addAll(subcontroller.getMetaController().getMetaActionsBeforeEachRequest());
		}
		
		return result;
	}	
	
}


