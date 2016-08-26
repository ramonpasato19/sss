package org.openxava.test.model

import javax.persistence.*;

import org.hibernate.validator.*;
import org.hibernate.validator.constraints.Length;
import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Javier Paniza 
 */
@Entity
class Debt extends Identifiable {
	
	@Column(length=40) @Required
	String description
		
	boolean paid

	@Column(length=9) @Editor("Important")	
	String important
	
	@Depends("paid, important")
	String getStatus() {
		"${important}: ${paid}"
	}


}
