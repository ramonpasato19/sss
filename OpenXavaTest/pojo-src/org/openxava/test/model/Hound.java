package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

/**
 * To test @javax.validation.constraints.Size with min and max elements on a 
 * Collection of entities  not embeddable.
 * 	
 * @author Jeromy Altuna
 */
@Entity
public class Hound extends Nameable {
	
	private Date birth;
	
	public Hound() { 
		super(); 
	}
	
	public Hound(String name) {
		this();
		setName(name);
	}
	
	public Hound(String name, Date birth) {
		this(name);
		setBirth(birth);
	}
	
	@ManyToOne
	private Hunter hunter;

	public Hunter getHunter() {
		return hunter;
	}
	public void setHunter(Hunter hunter) {
		this.hunter = hunter;
	}

	public Date getBirth() {
		return birth;
	}
	public void setBirth(Date birth) {
		this.birth = birth;
	}	
}
