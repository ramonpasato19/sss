package org.openxava.test.model

import javax.persistence.*;

/**
 * To test @javax.validation.constraints.Size
 * with min and max elements on a collection that
 * simulate embedding
 * 
 * @author Jeromy Altuna
 */
@Entity
class Question extends Nameable {
	
	@ManyToOne
	Exam exam	
}
