package com.powerfin.model;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;

import com.openxava.naviox.model.*;

@Entity
@Table(name = "pos_user")
@Views({ @View(members = "user; pos"), @View(name = "NewPosUser", members = "user; pos"),
		@View(name = "ChangePos", members = "user; pos") })
@Tabs({ @Tab(properties = "user.name, pos.name"), @Tab(name = "ChangePos", properties = "user.name, pos.name ") })
public class PosUser {

	@Id
	@GeneratedValue(generator = "system-uuid")
	@Hidden
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "pos_user_id", unique = true, nullable = false, length = 32)
	@ReadOnly
	private String posUserId;

	// bi-directional many-to-one association to Category
	@ManyToOne
	@JoinColumn(name = "user_name", nullable = false)
	@NoCreate
	@NoModify
	@DescriptionsList
	@Required
	@ReadOnly(forViews = "ChangeBranch")
	private User user;

	// bi-directional many-to-one association to Category
	@ManyToOne
	@JoinColumn(name = "pos_id", nullable = false)
	@Required
	@NoCreate
	@NoModify
	@DescriptionsList(descriptionProperties = "posId, name")
	private Pos pos;

	@Transient
	@ReadOnly
	private String userName;

	public PosUser() {

	}

	public String getPosUserId() {
		return posUserId;
	}

	public void setPosUserId(String posUserId) {
		this.posUserId = posUserId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Pos getPos() {
		return pos;
	}

	public void setPos(Pos pos) {
		this.pos = pos;
	}

	public String getUserName() {
		return user.getName();
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
