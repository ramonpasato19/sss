package org.openxava.test.model

import javax.persistence.*;

/**
 * To test @javax.validation.constraints.Size
 * with min and max elements on a Collection of  
 * entities  not embeddable
 * 	
 * @author Jeromy Altuna
 */
@Entity
class Hound extends Nameable {
	
	@ManyToOne
	Hunter hunter 
}
