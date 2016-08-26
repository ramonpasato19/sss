package org.openxava.test.model

import javax.persistence.*
import org.openxava.model.*

@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
class Shop extends Identifiable {

	String name
	
}
