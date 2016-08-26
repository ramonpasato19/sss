package org.openxava.test.model

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Javier Paniza
 */
@Entity
class Skill extends Identifiable {

	@Column(length=60) @Required
	String description
		
	
}
