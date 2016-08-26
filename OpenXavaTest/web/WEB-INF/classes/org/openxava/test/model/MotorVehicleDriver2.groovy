package org.openxava.test.model

import javax.persistence.*
import org.openxava.annotations.*
import org.openxava.model.*

/**
 * To test the AsserTrue message of MotorVehicle
 * on a collection of embeddables
 *
 * @author Jeromy Altuna
 */

@Entity
@View(members="name; approvedDrivingTest; vehicles")
class MotorVehicleDriver2 extends MotorVehicleDriver {
	
	@OneToMany(mappedBy="driver", cascade=CascadeType.REMOVE)
	Collection<MotorVehicle> getVehicles() {
		return vehicles
	}
}
