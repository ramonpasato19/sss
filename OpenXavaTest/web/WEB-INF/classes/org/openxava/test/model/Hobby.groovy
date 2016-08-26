package org.openxava.test.model

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza
 */
@Entity
class Hobby extends Nameable {
	
	@ManyToMany
	Collection<Hobbyist> hobbyists

}
