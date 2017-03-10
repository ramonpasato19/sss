package com.powerfin.model;

import javax.persistence.*;

@Entity
@Table(name = "parameter")

public class Parameter {

	@Id
	@Column(name = "parameter_id", nullable = false, length = 100)
	private String parameterId;

	@Column(name = "value", nullable = false, length = 100)
	private String value;

	@Column(name = "description", nullable = true, length = 100)
	private String description;

	@Column(name = "type", nullable = true, length = 100)
	private String type;

	public Parameter() {

	}

	public String getParameterId() {
		return parameterId;
	}

	public void setParameterId(String parameterId) {
		this.parameterId = parameterId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
