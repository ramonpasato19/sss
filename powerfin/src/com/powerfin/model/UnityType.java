package com.powerfin.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="unity_type")
public class UnityType {

	@Id
	@Column(name="unity_type_id")
	private Integer unityTypeId;

	@Column(name="name", length=50)
	private String name;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getUnityTypeId() {
		return unityTypeId;
	}

	public void setUnityTypeId(Integer unityTypeId) {
		this.unityTypeId = unityTypeId;
	}


}
