package com.openxava.naviox.model;

import javax.persistence.*;
import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza 
 */

@View(name="Unlock", members="password")
public class SignIn {
	
	
	@ManyToOne @DescriptionsList @LabelFormat(LabelFormatType.SMALL)
	@NoModify @NoCreate
	@Required 
	private Organization organization; 
	
	@Column(length=60) 
	@LabelFormat(LabelFormatType.SMALL)
	@Required 
	private String user; 

	@Column(length=30) @Stereotype("PASSWORD")
	@LabelFormat(LabelFormatType.SMALL)
	@Required 
	private String password;
	

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}
	
	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

}
