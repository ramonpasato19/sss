package com.powerfin.model;

import java.io.*;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * The persistent class for the negotation_type database table.
 * 
 */
@Entity
@Table(name="negotiation_type")
@View(members="negotiationTypeId;"
		+ "name")
public class NegotiationType implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="negotiation_type_id", unique=true, nullable=false, length=3)
	private String negotiationTypeId;
	
	@Column(nullable=false, length=100)
	private String name;
	
	public NegotiationType(){
		
	}

	public String getNegotiationTypeId() {
		return negotiationTypeId;
	}

	public void setNegotiationTypeId(String negotiationId) {
		this.negotiationTypeId = negotiationId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
