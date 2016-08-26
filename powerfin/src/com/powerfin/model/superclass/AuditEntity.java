package com.powerfin.model.superclass;
import java.util.Date;

import javax.persistence.*;

import org.openxava.util.*;

@MappedSuperclass
public abstract class AuditEntity{

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="registration_date", nullable=false)
	private Date registrationDate;
    
	@Column(name="user_registering", nullable=false, length=30)
	private String userRegistering;
    
    @PrePersist
    protected void onCreateEntity() {
    	registrationDate = new Date();
    	userRegistering = Users.getCurrent();
    }

    @PreUpdate
    protected void onUpdateEntity() {
    	registrationDate = new Date();
    	userRegistering = Users.getCurrent();
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
    
    
}