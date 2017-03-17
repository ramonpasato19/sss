/**
 * 
 */
package org.openxava.test.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.NoCreate;
import org.openxava.annotations.NoFrame;
import org.openxava.annotations.ReferenceView;
import org.openxava.annotations.Required;
import org.openxava.annotations.View;
import org.openxava.annotations.Views;
import org.openxava.model.Identifiable;

/**
 * @author federico
 *
 */
@Entity
@Views({
	@View(members = "chair; couch"),
	@View(name = "WithRequiredDescriptionsList", members = "chair; couch")
})
public class FurnitureSet extends Identifiable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@ReferenceView("Simple")
	@NoFrame @NoCreate
	@ManyToOne
	private Chair chair;
	
	@Required
	@ReferenceView("Simple")
	@DescriptionsList(forViews = "WithRequiredDescriptionsList", descriptionProperties = "name")
	@NoFrame @NoCreate
	@ManyToOne
	private Couch couch;

	/**
	 * @return the chair
	 */
	public Chair getChair() {
		return chair;
	}

	/**
	 * @param chair the chair to set
	 */
	public void setChair(Chair chair) {
		this.chair = chair;
	}

	/**
	 * @return the couch
	 */
	public Couch getCouch() {
		return couch;
	}

	/**
	 * @param couch the couch to set
	 */
	public void setCouch(Couch couch) {
		this.couch = couch;
	}

}
