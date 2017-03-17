package org.openxava.test.model

import javax.persistence.*;

import org.hibernate.validator.constraints.*;
import org.openxava.model.*;

/**
 *
 * @author Javier Paniza
 */

@Entity
class Check extends Identifiable {
	
	@Column(length=40)
	String description
	
	@OneToOne(mappedBy = "check", optional=true)
	CheckTransaction transaction

}
