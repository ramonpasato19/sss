package org.openxava.test.model

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza 
 */
@Entity
class Conference extends Nameable {
	
	@OneToMany(mappedBy = "mainFor", cascade=CascadeType.REMOVE)	
	Collection<ConferenceTrack> mainTracks

	@OneToMany(mappedBy = "secondaryFor", cascade=CascadeType.REMOVE)	
	Collection<ConferenceTrack> secondaryTracks

}
