package com.powerfin.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the level_instruction database table.
 * 
 */
@Entity
@Table(name="level_instruction")
@NamedQuery(name="LevelInstruction.findAll", query="SELECT l FROM LevelInstruction l")
public class LevelInstruction implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="level_instruction_id", unique=true, nullable=false, length=3)
	private String levelInstructionId;

	@Column(nullable=false, length=100)
	private String name;

	//bi-directional many-to-one association to NaturalPerson
	@OneToMany(mappedBy="levelInstruction")
	private List<NaturalPerson> naturalPersons;

	public LevelInstruction() {
	}

	public String getLevelInstructionId() {
		return this.levelInstructionId;
	}

	public void setLevelInstructionId(String levelInstructionId) {
		this.levelInstructionId = levelInstructionId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<NaturalPerson> getNaturalPersons() {
		return this.naturalPersons;
	}

	public void setNaturalPersons(List<NaturalPerson> naturalPersons) {
		this.naturalPersons = naturalPersons;
	}

	public NaturalPerson addNaturalPerson(NaturalPerson naturalPerson) {
		getNaturalPersons().add(naturalPerson);
		naturalPerson.setLevelInstruction(this);

		return naturalPerson;
	}

	public NaturalPerson removeNaturalPerson(NaturalPerson naturalPerson) {
		getNaturalPersons().remove(naturalPerson);
		naturalPerson.setLevelInstruction(null);

		return naturalPerson;
	}

}