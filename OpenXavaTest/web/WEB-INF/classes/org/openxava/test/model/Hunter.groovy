package org.openxava.test.model

import javax.persistence.*;

/**
 * To test @javax.validation.constraints.Size
 * with min and max elements on a collection of
 * entities not embeddable
 *
 * @author Jeromy Altuna
 */
@Entity
class Hunter extends Nameable {
	
	@OneToMany(mappedBy="hunter")
	@javax.validation.constraints.Size(min=1, max=4)
	Collection<Hound> hounds
}
