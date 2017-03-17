package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity
class NetMachine extends Identifiable {
	
	@Column(length=40) @Required
	String name
	
	@Stereotype("MAC")
	String mac

}
