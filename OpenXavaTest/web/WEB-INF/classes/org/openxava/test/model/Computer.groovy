package org.openxava.test.model

import javax.persistence.*

import org.openxava.annotations.*;
import org.openxava.model.*;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
class Computer extends Identifiable {
	
	@Required @Column(length=40)
	String name
	
	@Column(length=40)
	String operatingSystem
	
}
