package com.powerfin.model;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;

import com.openxava.naviox.model.*;
import com.powerfin.helper.*;

@Entity
@Table(name="branch_user")
@Views({
	@View(members="branchUserId;user;branch"),
	@View(name="ChangeBranch", members="userName; branch")
})
@Tabs({
	@Tab(properties="user.name, branch.name"),
	@Tab(name="ChangeBranch", properties="user.name, branch.name"),
})
public class BranchUser {

	@Id @GeneratedValue(generator="system-uuid") 
	@Hidden
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	@Column(name="branch_user_id", unique=true, nullable=false, length=32)
	private String branchUserId;
	
	//bi-directional many-to-one association to Category
	@ManyToOne
	@JoinColumn(name="user_name", nullable=false)
	@NoCreate
	@NoModify
	@NoFrame
	@ReadOnly
	private User user;
	
	//bi-directional many-to-one association to Category
	@ManyToOne
	@JoinColumn(name="branch_id", nullable=false)
	@Required
	@NoCreate
	@NoModify
	@ReferenceView("Reference")
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

	@PrePersist
	public void onPrePersist()
	{
		if (getUser()==null)
			setUser(UserHelper.getCurrent());
	}
}
