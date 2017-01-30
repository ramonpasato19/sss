package com.powerfin.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.ReferenceView;
import org.openxava.annotations.Tab;
import org.openxava.annotations.View;
import org.openxava.annotations.Views;

@Entity
@Table(name="unity")
@Tab(properties="unityId, name, description")
@Views(
		{@View(members="unityId;"
				+ "name;"
				+ "description;"
				+ "address;"
				+ "unityType;"
				+ "Localizacion [locationX, locationY];"
				+ "person;"
				+ "parent"

				),
		@View(name="seleccionPadre", members="unityId, name")
		})
public class Unity {
	@Id
	@Column(name="unity_id")
	private String unityId;

	@Column(name="name")
	private String name;

	@Column(name="description")
	private String description;

	@Column(name="address")
	private String address;

	@Column(name="location_x")
	private String locationX;

	@Column(name="location_y")
	private String locationY;


	@ManyToOne
	@JoinColumn(name="person_manager_id")
	//@NoCreate
	//@NoModify
	@ReferenceView("simple")
	private Person person;

	@ManyToOne
	@JoinColumn(name="parent_unity_id")
	@ReferenceView("seleccionPadre")
	private Unity parent;

	@ManyToOne
	@JoinColumn(name="unity_type_id")
	@DescriptionsList
	private UnityType unityType;

	public String getUnityId() {
		return unityId;
	}

	public void setUnityId(String unityId) {
		this.unityId = unityId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocationX() {
		return locationX;
	}

	public void setLocationX(String locationX) {
		this.locationX = locationX;
	}

	public String getLocationY() {
		return locationY;
	}

	public void setLocationY(String locationY) {
		this.locationY = locationY;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Unity getParent() {
		return parent;
	}

	public void setParent(Unity parent) {
		this.parent = parent;
	}


	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public UnityType getUnityType() {
		return unityType;
	}

	public void setUnityType(UnityType unityType) {
		this.unityType = unityType;
	}




}
