package com.powerfin.model;

import java.io.*;

import javax.persistence.*;

/**
 * The persistent class for the negotiation_file_type database table
 *
 */
@Entity
@Table(name="negotiation_file_type")
public class NegotiationFileType implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="negotiation_file_type_id", unique=true, nullable=false, length=3)
	private String negotiationFileTypeId;
	
	@Column(nullable=false, length=40)
	private String name;
	
	public NegotiationFileType(){
		
	}

	public String getNegotiationFileTypeId() {
		return negotiationFileTypeId;
	}

	public void setNegotiationFileTypeId(String negotiationFileTypeId) {
		this.negotiationFileTypeId = negotiationFileTypeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
