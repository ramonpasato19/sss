/**
 * 
 */
package org.openxava.test.model;

import javax.persistence.Entity;

import org.openxava.annotations.View;

/**
 * @author federico
 *
 */
@Entity
@View(name = "Simple", members = "id, name, leather")
public class Couch extends BaseSitting {
	private static final long serialVersionUID = 1L;

	private Boolean leather;

	/**
	 * @return the leather
	 */
	public Boolean getLeather() {
		return leather;
	}

	/**
	 * @param leather the leather to set
	 */
	public void setLeather(Boolean leather) {
		this.leather = leather;
	}

}
