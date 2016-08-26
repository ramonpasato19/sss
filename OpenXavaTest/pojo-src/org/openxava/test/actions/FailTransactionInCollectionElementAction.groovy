package org.openxava.test.actions

import org.openxava.actions.*;
import org.openxava.jpa.*;

/**
 * 
 * @author Javier Paniza
 */
class FailTransactionInCollectionElementAction extends CollectionElementViewBaseAction {
	
	void execute() {
		XPersistence.manager.transaction.setRollbackOnly()
		closeDialog()	
	}

}
