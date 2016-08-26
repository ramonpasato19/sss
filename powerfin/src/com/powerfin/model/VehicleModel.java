package com.powerfin.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the vehicle_model database table.
 * 
 */
@Entity
@Table(name="vehicle_model")
@NamedQuery(name="VehicleModel.findAll", query="SELECT v FROM VehicleModel v")
public class VehicleModel implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="vehicle_model_id", unique=true, nullable=false)
	private Integer vehicleModelId;

	@Column(length=100)
	private String code;

	@Column(nullable=false, length=100)
	private String name;

	//bi-directional many-to-one association to VehicleMark
	@ManyToOne
	@JoinColumn(name="vehicle_mark_id", nullable=false)
	private VehicleMark vehicleMark;

	//bi-directional many-to-one association to VehicleType
	@ManyToOne
	@JoinColumn(name="vehicle_type_id", nullable=false)
	private VehicleType vehicleType;

	public VehicleModel() {
	}

	public Integer getVehicleModelId() {
		return this.vehicleModelId;
	}

	public void setVehicleModelId(Integer vehicleModelId) {
		this.vehicleModelId = vehicleModelId;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public VehicleMark getVehicleMark() {
		return this.vehicleMark;
	}

	public void setVehicleMark(VehicleMark vehicleMark) {
		this.vehicleMark = vehicleMark;
	}

	public VehicleType getVehicleType() {
		return this.vehicleType;
	}

	public void setVehicleType(VehicleType vehicleType) {
		this.vehicleType = vehicleType;
	}

}