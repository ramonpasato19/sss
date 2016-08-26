package org.openxava.test.model

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * To test AsserTrue message on a method
 * 
 * @author Jeromy Altuna
 */

@Entity
class MotorVehicle {
	
	@Hidden
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	int id
	
	@Required
	@Column(length = 15)
	String type
	
	@Required
	@Column(length = 7)
	String licensePlate
	
	boolean roadworthy
	
	@ManyToOne
	MotorVehicleDriver driver
	
	@javax.validation.constraints.AssertTrue(message="{not_roadworthy}")
	private boolean isRoadworthyToAssignTheDriver(){
		driver == null || roadworthy
	} 				
}
