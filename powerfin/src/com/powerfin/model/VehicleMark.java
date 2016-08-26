package com.powerfin.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the vehicle_mark database table.
 * 
 */
@Entity
@Table(name="vehicle_mark")
@NamedQuery(name="VehicleMark.findAll", query="SELECT v FROM VehicleMark v")
public class VehicleMark implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="vehicle_mark_id", unique=true, nullable=false, length=5)
	private String vehicleMarkId;

	@Column(nullable=false, length=100)
	private String name;

	//bi-directional many-to-one association to VehicleModel
	@OneToMany(mappedBy="vehicleMark")
	private List<VehicleModel> vehicleModels;

	public VehicleMark() {
	}

	public String getVehicleMarkId() {
		return this.vehicleMarkId;
	}

	public void setVehicleMarkId(String vehicleMarkId) {
		this.vehicleMarkId = vehicleMarkId;
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
		vehicleModel.setVehicleMark(this);

		return vehicleModel;
	}

	public VehicleModel removeVehicleModel(VehicleModel vehicleModel) {
		getVehicleModels().remove(vehicleModel);
		vehicleModel.setVehicleMark(null);

		return vehicleModel;
	}

}