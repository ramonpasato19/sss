package org.openxava.test.model

import javax.persistence.*;

/**
 *
 * @author Javier Paniza
 */
@Entity
class Hobbyist extends Nameable {

	@ManyToMany(mappedBy="hobbyists")
	Collection<Hobby> hobbies
	
}
