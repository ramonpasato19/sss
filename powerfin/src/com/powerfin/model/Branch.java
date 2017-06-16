package com.powerfin.model;

import java.io.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;


/**
 * The persistent class for the branch database table.
 * 
 */
@Entity
@Table(name="branch")
@Views({
	@View(members="branchId;name;branchUsers"),
	@View(name="Reference", members="branchId;name")
})

public class Branch implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="branch_id", unique=true, nullable=false)
	private Integer branchId;

	@Column(nullable=false, length=100)
	private String name;
	
	//bi-directional many-to-one association to CategoryAccount
	@OneToMany(mappedBy="branch", cascade=CascadeType.ALL)
	@ListProperties("user.name")
	private List<BranchUser> branchUsers;
	
	public Branch() {
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getBranchId() {
		return branchId;
	}

	public void setBranchId(Integer branchId) {
		this.branchId = branchId;
	}

	public List<BranchUser> getBranchUsers() {
		return branchUsers;
	}

	public void setBranchUsers(List<BranchUser> branchUsers) {
		this.branchUsers = branchUsers;
	}

}