package org.openxava.test.model

import javax.persistence.*;

import org.hibernate.validator.constraints.*;

/**
 * 
 * @author Javier Paniza 
 */
@Entity
class ProgrammingSkill extends Skill {

	@Column(length=20)
	String language
	
}
