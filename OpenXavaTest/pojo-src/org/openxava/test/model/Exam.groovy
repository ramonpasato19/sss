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
class Exam extends Nameable{
	
	@javax.validation.constraints.Size(min=1, max=4)
	@OneToMany(mappedBy="exam", cascade=CascadeType.ALL)
	Collection<Question> questioning
}