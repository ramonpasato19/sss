package com.powerfin.model;

import java.io.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * The persistent class for the negotiation_status database table.
 * 
 */
@Entity
@Table(name="negotiation_status")
@View(members="negotiationStatusId;"
		+ "name")
public class NegotiationStatus implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="negotiation_status_id", unique=true, nullable=false, length=3)
	private String negotiationStatusId;

	@Column(nullable=false, length=50)
	@Required
	@DisplaySize(20)
	private String name;

	//bi-directional many-to-one association to Negotiation
	@OneToMany(mappedBy="negotiationStatus")
	private List<Negotiation> negotiations;


	public NegotiationStatus() {
	}

	public String getNegotiationStatusId() {
		return negotiationStatusId;
	}

	public void setNegotiationStatusId(String negotiationStatusId) {
		this.negotiationStatusId = negotiationStatusId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Negotiation> getNegotiations() {
		return this.negotiations;
	}

	public void setNegotiations(List<Negotiation> negotiations) {
		this.negotiations = negotiations;
	}

	public Negotiation addNegotiation(Negotiation negotiation) {
		getNegotiations().add(negotiation);
		negotiation.setNegotiationStatus(this);

		return negotiation;
	}

	public Negotiation removeNegotiation(Negotiation negotiation) {
		getNegotiations().remove(negotiation);
		negotiation.setNegotiationStatus(null);

		return negotiation;
	}
}
