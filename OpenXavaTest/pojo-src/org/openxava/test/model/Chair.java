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
@View(name = "Simple", members = "id, name, plastic")
public class Chair extends BaseSitting {
	private static final long serialVersionUID = 1L;

	private Boolean plastic;

	/**
	 * @return the plastic
	 */
	public Boolean getPlastic() {
		return plastic;
	}

	/**
	 * @param plastic the plastic to set
	 */
	public void setPlastic(Boolean plastic) {
		this.plastic = plastic;
	}
}
