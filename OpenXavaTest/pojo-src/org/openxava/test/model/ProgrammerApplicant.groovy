package org.openxava.test.model

import javax.persistence.*;

import org.hibernate.validator.constraints.*;

/**
 * 
 * @author Javier Paniza 
 */
@Entity
class ProgrammerApplicant extends Applicant {
	
	@Column(length=60)
	String platform
	
}
