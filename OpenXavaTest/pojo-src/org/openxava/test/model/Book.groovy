package org.openxava.test.model

import org.openxava.model.*;
import org.openxava.test.validators.*;

import javax.persistence.*;

import org.openxava.annotations.*;

@Entity
@EntityValidator(value=BookValidator.class,
	message="The synopsis does not match with the title",
	properties= [ 
		@PropertyValue(name="title"), 
		@PropertyValue(name="synopsis")
	]
)
class Book extends Identifiable {
	
	@Required(message="{title_required}") 
	@PropertyValidator(value=BookTitleValidator.class, message="{rpg_book_not_allowed}")
	String title 
	
	@ManyToOne 
	Author author
		 
	boolean outOfPrint  
	
	@Required(message="Please, enter a synopsis for the book") 
	@Stereotype("MEMO")
	String synopsis

}
