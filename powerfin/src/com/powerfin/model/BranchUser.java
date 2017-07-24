package com.powerfin.model;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;

import com.openxava.naviox.model.*;

@Entity
@Table(name="branch_user")
@Views({
	@View(members="user; branch"),
	@View(name="NewUserBranch", members="user; branch"),
	@View(name="ChangeBranch", members="user; branch")
})
@Tabs({
	@Tab(properties="user.name, branch.name"),
	@Tab(name="ChangeBranch", properties="user.name, branch.name ")
})
public class BranchUser {

	@Id @GeneratedValue(generator="system-uuid") 
	@Hidden
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	@Column(name="branch_user_id", unique=true, nullable=false, length=32)
	@ReadOnly
	private String branchUserId;
	
	//bi-directional many-to-one association to Category
	@ManyToOne
	@JoinColumn(name="user_name", nullable=false)
	@NoCreate
	@NoModify
	@DescriptionsList
	@Required
	@ReadOnly(forViews="ChangeBranch")
	private User user;
	
	//bi-directional many-to-one association to Category
	@ManyToOne
	@JoinColumn(name="branch_id", nullable=false)
	@Required
	@NoCreate
	@NoModify
	@DescriptionsList(descriptionProperties="branchId, name")
	private Branch branch;
	
	@Transient
	@ReadOnly
	private String userName;
	
	public BranchUser()
	{
		
	}

	public String getBranchUserId() {
		return branchUserId;
	}

	public void setBranchUserId(String branchUserId) {
		this.branchUserId = branchUserId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}
	
	public String getUserName()
	{
		return user.getName();
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
}
