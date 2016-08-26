package com.powerfin.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the vehicle_type database table.
 * 
 */
@Entity
@Table(name="vehicle_type")
@NamedQuery(name="VehicleType.findAll", query="SELECT v FROM VehicleType v")
public class VehicleType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="vehicle_type_id", unique=true, nullable=false, length=3)
	private String vehicleTypeId;

	@Column(nullable=false, length=100)
	private String name;

	//bi-directional many-to-one association to VehicleModel
	@OneToMany(mappedBy="vehicleType")
	private List<VehicleModel> vehicleModels;

	public VehicleType() {
	}

	public String getVehicleTypeId() {
		return this.vehicleTypeId;
	}

	public void setVehicleTypeId(String vehicleTypeId) {
		this.vehicleTypeId = vehicleTypeId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<VehicleModel> getVehicleModels() {
		return this.vehicleModels;
	}

	public void setVehicleModels(List<VehicleModel> vehicleModels) {
		this.vehicleModels = vehicleModels;
	}

	public VehicleModel addVehicleModel(VehicleModel vehicleModel) {
		getVehicleModels().add(vehicleModel);
		vehicleModel.setVehicleType(this);

		return vehicleModel;
	}

	public VehicleModel removeVehicleModel(VehicleModel vehicleModel) {
		getVehicleModels().remove(vehicleModel);
		vehicleModel.setVehicleType(null);

		return vehicleModel;
	}

}