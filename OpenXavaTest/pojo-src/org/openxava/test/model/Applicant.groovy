package org.openxava.test.model

import javax.persistence.*;

import org.hibernate.validator.constraints.*;
import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Javier Paniza 
 */
@Entity
class Applicant extends Identifiable {
	
	@Column(length=40) @Required
	String name
	
	@ManyToOne(fetch=FetchType.LAZY)
	Skill skill

}
