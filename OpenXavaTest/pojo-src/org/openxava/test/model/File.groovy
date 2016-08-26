package org.openxava.test.model

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity
class File extends Nameable {
	
	@ManyToOne
	FilesFolder folder

}
