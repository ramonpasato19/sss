package org.openxava.test.tests

import org.openxava.jpa.*;
import org.openxava.test.model.*
import org.openxava.tests.*


/**
 *
 * @author Jeromy Altuna
 */
class MotorVehicleTest extends ModuleTestBase {
	
	MotorVehicleDriver driver
	
	MotorVehicleTest(String testName){
		super(testName, "MotorVehicle")
	}
	
	@Override
	protected void setUp() throws Exception {
		createDriver()
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		removeDriver()
	}
	
	void testConstraintAnnotationMessage(){		
		execute "CRUD.new"
		setValue "type", "MOTORBIKE"
		setValue "licensePlate", "L2-0002"
		execute "CRUD.save"
		assertNoErrors()
		execute "Mode.list"
		assertListRowCount 1
		execute "Mode.detailAndFirst"
		assertValue "licensePlate", "L2-0002"
		execute "Reference.search", "keyProperty=driver.name"
		assertListRowCount 1 
		execute "ReferenceSearch.choose", "row=0"
		execute "CRUD.save"
		assertError "MOTORBIKE plate L2-0002 is not roadworthy. " +
			"It can not be assigned to the driver JUAN ALTUNA"
		setValue "roadworthy", "true"
		execute "CRUD.save"
		assertNoErrors()
		execute "Mode.list"
		assertListRowCount 1
		execute "List.viewDetail", "row=0"
		assertValue "licensePlate", "L2-0002"
		setValue "roadworthy", "false"
		execute "CRUD.save"
		assertError "MOTORBIKE plate L2-0002 is not roadworthy. " +
			"It can not be assigned to the driver JUAN ALTUNA"
		execute "CRUD.delete"
		assertNoErrors()
	}
	
	private void createDriver(){
		driver  = new MotorVehicleDriver()
		driver.name = "JUAN ALTUNA"
		driver.approvedDrivingTest = true
		XPersistence.getManager().persist driver
		XPersistence.commit()
	}
	
	private void removeDriver(){
		XPersistence.getManager().remove(
				XPersistence.getManager().merge(driver))
		XPersistence.commit()
	}	
}
