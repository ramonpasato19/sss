package org.openxava.test.model

import javax.persistence.*

import org.openxava.annotations.*
import org.openxava.jpa.*
import org.openxava.model.*

/**
 * Model to test concatenated reports using {@link org.openxava.test.actions.MovieReportAction}
 * and to test stereotypes FILE and FILES. <p>
 * 
 * @author Jeromy Altuna
 */
@Entity			
class Movie extends Identifiable {

	String title
	String director
	String writers
	String starring
	
	@Stereotype("FILE")
	@Column(length=32)
	String trailer
	
	@Stereotype("FILES")
	@Column(length=32)	
	String scripts
	
	@Stereotype("IMAGES_GALLERY")
	@Column(length=32)
	String photographs
	
	static Movie findById(String id) {
		Query query = XPersistence.getManager().createQuery "from Movie m where m.id = :id"
		query.setParameter "id", id
		return (Movie) query.getSingleResult()
	}
}
