package org.openxava.actions;

import java.util.*;

import org.openxava.model.MapFacade;
import org.openxava.validators.ValidationException;

/**
 * The default action to remove an element in a @OneToMany/@ManyToMany collection.
 * 
 * For an @ElementColection use {@link RemoveSelectedInElementCollectionAction}. 
 * 
 * @author Ana Andrés
 * @author Jeromy Altuna
 * @author Javier Paniza 
 */

public class RemoveSelectedInCollectionAction extends CollectionBaseAction {
	
	public void execute() throws Exception {
		try{
			Collection selectedOnes = getMapsSelectedValues();
			validateMinimum(selectedOnes.size()); 
			if (!selectedOnes.isEmpty()){
				Iterator it = selectedOnes.iterator();
				while(it.hasNext()){
					Map values = (Map) it.next();
					removeElement(values);
				}
				commit(); // If we change this, we should run all test suite using READ COMMITED (with hsqldb 2 for example)				
				addMessage();
				getView().recalculateProperties();
				getCollectionElementView().collectionDeselectAll();
			}
		}
		catch (ValidationException ex) {			
			addErrors(ex.getErrors());
		}
	}
	
	/**
	 * Is called for each selected row with the values that includes the key
	 * values. <p>
	 */
	protected void removeElement(Map values) throws Exception {
		MapFacade.removeCollectionElement(getCollectionElementView().getParent().getModelName(), getCollectionElementView().getParent().getKeyValues(), getCollectionElementView().getMemberName(), values);
	}
	
	/**
	 * @since 5.3.2
	 */
	protected void addMessage() { 
		if (isEntityReferencesCollection() && !getCollectionElementView().getMetaCollection().isOrphanRemoval()) {
			addMessage("association_removed", getCollectionElementView().getModelName(), 
					getCollectionElementView().getParent().getModelName());
		}
		else {
			addMessage("aggregate_removed", getCollectionElementView().getModelName());
		}		
	}
		
}