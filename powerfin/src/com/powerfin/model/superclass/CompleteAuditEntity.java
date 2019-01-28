package com.powerfin.model.superclass;
import java.util.Date;

import javax.persistence.*;

import org.openxava.util.*;

@MappedSuperclass
public abstract class CompleteAuditEntity{

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="registration_date", nullable=false)
	private Date registrationDate;
    
	@Column(name="user_registering", nullable=false, length=30)
	private String userRegistering;
    
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="updated_on", nullable=true)
	private Date updatedOn;
    
	@Column(name="updated_by", nullable=true, length=30)
	private String updatedBy;
	
    @PrePersist
    protected void onCreateEntity() {
    	registrationDate = new Date();
    	userRegistering = Users.getCurrent();
    }

    @PreUpdate
    protected void onUpdateEntity() {
    	updatedOn = new Date();
    	updatedBy = Users.getCurrent();
    }

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public String getUserRegistering() {
		return userRegistering;
	}

	public void setUserRegistering(String userRegistering) {
		this.userRegistering = userRegistering;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
    
    
}