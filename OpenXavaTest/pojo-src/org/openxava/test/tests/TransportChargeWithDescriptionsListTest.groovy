package org.openxava.test.tests;

import org.openxava.jpa.*;
import org.openxava.model.meta.*;
import org.openxava.test.model.*;


import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

class TransportChargeWithDescriptionsListTest extends ModuleTestBase {
	
	TransportChargeWithDescriptionsListTest(String testName) {
		super(testName, "TransportChargeWithDescriptionsList")		
	}
		
	void testNestedCompositeKeysInDescriptionsList()  { 
		deleteAllTransportCharges();		
		assertListRowCount 0 
		execute "CRUD.new"
		Delivery delivery = new Delivery([ 
			invoice: [ year: 2004, number: 2], 
			type: [ number: 2],
			number: 777 ])
		String key = MetaModel.getForPOJO(delivery).toString(delivery);		
		setValue "delivery.KEY", key
		String [][] deliveries = [
			[ "", "" ],
			[ "[.2.2004.666.1.]", "DELIVERY JUNIT 666 2/22/04" ],
			[ "[.9.2004.666.1.]", "DELIVERY JUNIT 666 2/22/04" ],
			[ "[.10.2004.666.1.]", "DELIVERY JUNIT 666 2/22/04" ],
			[ "[.11.2004.666.1.]", "DELIVERY JUNIT 666 2/22/04" ],
			[ "[.12.2004.666.1.]", "DELIVERY JUNIT 666 2/22/04" ],
			[ "[.2.2004.777.2.]", "FOR TEST SEARCHING BY DESCRIPTION 6/23/06" ]
		]
		assertValidValues("delivery.KEY", deliveries)
		setValue "amount", "324.28" 
		execute "CRUD.save"
		assertNoErrors()
		execute "Mode.list"
		assertListRowCount 1
		execute "Mode.detailAndFirst"
		assertValue "delivery.KEY", key
		assertDescriptionValue "delivery.KEY", "FOR TEST SEARCHING BY DESCRIPTION 6/23/06" 
		assertValue "amount", "324.28"
		execute "CRUD.delete"
		assertNoErrors()
	}	
	
	private void deleteAllTransportCharges() {
		checkAll()
		execute "CRUD.deleteSelected"
	}
	
}
