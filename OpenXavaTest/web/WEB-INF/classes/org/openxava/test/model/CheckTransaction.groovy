package org.openxava.test.model

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;
import org.openxava.provaox.actions.*;
import org.openxava.provaox.model.*;
import org.openxava.test.actions.*;

/**
 *
 * @author Javier Paniza
 */

@Entity
class CheckTransaction extends Identifiable {
	
	@Column(length=40)
	String description
	
	@OneToOne(optional = true, cascade=CascadeType.ALL)
	@OnChange(OnChangeVoidAction.class) 
	Check check

}
