package org.openxava.test.model

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza 
 */
@Entity
class ConferenceTrack extends Nameable {
	
	@ManyToOne(fetch=FetchType.LAZY)
	Conference mainFor
	
	@ManyToOne(fetch=FetchType.LAZY)
	Conference secondaryFor

}
