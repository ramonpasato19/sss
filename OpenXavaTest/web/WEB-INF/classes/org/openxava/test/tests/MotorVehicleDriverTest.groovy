package org.openxava.test.tests

import org.openxava.jpa.*
import org.openxava.test.model.*;
import org.openxava.tests.*

/**
 *
 * @author Jeromy Altuna
 */
class MotorVehicleDriverTest extends ModuleTestBase {
	
	MotorVehicle vehicle
	
	MotorVehicleDriverTest(String testName){
		super(testName, "MotorVehicleDriver")		
	}
	
	@Override
	protected void setUp() throws Exception {
		createVehicle()
		super.setUp()	
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown()
		removeVehicle()
	}
	
	void testConstraintAnnotacionMesssage(){
		execute "CRUD.new"
		setValue "name", "MAGALI AVILA"
		execute "CRUD.save"
		assertError "Driver MAGALI AVILA can not be registered: " +
			"must approved the driving test"
		setValue "approvedDrivingTest", "true"
		execute "CRUD.save"
		assertNoErrors()
		execute "Mode.list"
		assertListRowCount 1
		execute "Mode.detailAndFirst"
		assertValue "name", "MAGALI AVILA"
		setValue "approvedDrivingTest", "false"
		execute "CRUD.save"	
		assertError "Driver MAGALI AVILA can not be registered: " + 
			"must approved the driving test"
		assertValue "name", "MAGALI AVILA"
		execute "CRUD.delete"
		assertNoErrors()
	}
	
	void testNotCanAddVehicleNotRoadworthy(){
		execute "CRUD.new"
		setValue "name", "MAGALI AVILA"
		setValue "approvedDrivingTest", "true"
		execute "CRUD.save"
		assertNoErrors()
		execute "Mode.list"
		assertListRowCount 1
		execute "Mode.detailAndFirst"
		assertValue "name", "MAGALI AVILA"		
		execute "Collection.add", "viewObject=xava_view_vehicles"
		assertListRowCount 1
		execute "AddToCollection.add", "row=0"
		assertError "AUTO plate L1-0001 is not roadworthy. " +
			"It can not be assigned to the driver MAGALI AVILA"
		execute "Mode.list"
		execute "CRUD.deleteRow", "row=0"
		assertNoErrors()
		assertListRowCount 0
	}
	
	void testCanAddVehicleRoadworthy(){
		vehicle.roadworthy = true
		updateVehicle()
		
		execute "CRUD.new"
		setValue "name", "MAGALI AVILA"
		setValue "approvedDrivingTest", "true"
		execute "CRUD.save"
		assertNoErrors()
		execute "Mode.list"
		execute "Mode.detailAndFirst"
		assertValue "name", "MAGALI AVILA"		
		execute "Collection.add", "viewObject=xava_view_vehicles"
		assertListRowCount 1
		execute "AddToCollection.add", "row=0"
		assertNoErrors()
		execute "Collection.removeSelected", "row=0,viewObject=xava_view_vehicles"
		assertMessage "Association between Motor vehicle and Motor vehicle driver has been removed, " + 
			"but Motor vehicle is still in database" 
		execute "CRUD.delete"
		assertNoErrors()
	}
	
	private void createVehicle(){
		vehicle = new MotorVehicle();
		vehicle.type = "AUTO"
		vehicle.licensePlate = "L1-0001"		
		XPersistence.getManager().persist vehicle
		XPersistence.commit()  
	}
	
	private void removeVehicle(){
		XPersistence.getManager().remove(XPersistence.getManager().merge(vehicle))
		XPersistence.commit()
	}
	
	private void updateVehicle(){
		XPersistence.getManager().persist(XPersistence.getManager().merge(vehicle))
		XPersistence.commit()
	}			
}
