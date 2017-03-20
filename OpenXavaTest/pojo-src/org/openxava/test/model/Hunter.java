package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

/**
 * To test @javax.validation.constraints.Size with min and max elements on a 
 * collection of entities not embeddable.
 *
 * @author Jeromy Altuna
 */
@Entity
public class Hunter extends Nameable {
	
	@OneToMany(mappedBy="hunter")
	@javax.validation.constraints.Size(min=1, max=4) 
	private Collection<Hound> hounds;
	
	public Hunter() {
		super();
	}
	
	public Hunter(String name) {
		this();
		setName(name);
	}
	
	public void addHound(Hound hound) {
		if (hounds == null) hounds = new ArrayList<Hound>();
		hound.setHunter(this);
		hounds.add(hound);		
	}
	
	public Collection<Hound> getHounds() {
		return hounds;
	}
	public void setHounds(Collection<Hound> hounds) {
		this.hounds = hounds;
	}	
}
