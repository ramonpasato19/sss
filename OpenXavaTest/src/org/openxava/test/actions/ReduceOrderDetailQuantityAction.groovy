package org.openxava.test.actions

import org.openxava.actions.*;
import org.openxava.test.model.*;

/**
 * 
 * @author Javier Paniza
 */
class ReduceOrderDetailQuantityAction extends CollectionBaseAction {

	public void execute() throws Exception {
		if (row < 0) return
		OrderDetail detail = (OrderDetail) getSelectedObjects().get(0)
		detail.quantity--
		addMessage "The order detail quantity is ${detail.quantity}"		
	}

}
