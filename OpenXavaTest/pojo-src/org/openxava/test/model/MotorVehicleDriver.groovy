package org.openxava.test.model

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * To test AsserTrue message on a field
 * 
 * @author Jeromy Altuna
 */

@Entity
@Tab(baseCondition="e.class = MotorVehicleDriver")
class MotorVehicleDriver extends Identifiable {
	
	@Required
	@Column(length = 40)
	String name;
	
	@javax.validation.constraints.AssertTrue(
		message = "{disapproved_driving_test}")
	boolean approvedDrivingTest
	
	@OneToMany(mappedBy = "driver")	
	Collection<MotorVehicle> vehicles = new ArrayList<MotorVehicle>()
		
}
