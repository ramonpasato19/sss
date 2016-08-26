package org.openxava.test.model

import javax.persistence.*

import org.openxava.annotations.*;

@Entity
class MiniOrder {
	
	@Id
	int number
	
	@Column(length=40) @Required
	String description
	
	@Required
	BigDecimal productPrice 
	
	@Required
	int qtyOrdered
	
	BigDecimal amount
	
}
