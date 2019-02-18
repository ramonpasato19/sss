package com.openxava.naviox.model;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;

/**
 *
 * @since 5.4
 * @author Javier Paniza
 */

@MappedSuperclass
public class Configuration implements java.io.Serializable {
	
	private static Configuration instance;
	
	@Id @Hidden
	private int id;
	
	@Max(30)
	private int passwordMinLength;
	
	@org.hibernate.annotations.Type(type="org.hibernate.type.YesNoType")
	@Column(name="forceLetterAndNumbersInPasswd") 
	private boolean forceLetterAndNumbersInPassword;
	
	@org.hibernate.annotations.Type(type="org.hibernate.type.YesNoType")
	private boolean recentPasswordsNotAllowed; 
	
	@Max(999)
	private int forceChangePasswordDays;
	
	@Max(999)
	private int lockSessionMinutes;
	
	@Max(999)
	private int loginAttemptsBeforeLocking; 
	
	@Max(999)
	@Column(name="inactiveDaysBeforeDisUser") 
	private int inactiveDaysBeforeDisablingUser;  
	
	@org.hibernate.annotations.Type(type="org.hibernate.type.YesNoType")
	@Column(columnDefinition="varchar(1) default 'Y' not null")
	private boolean guestCanCreateAccount;  
	
	@org.hibernate.annotations.Type(type="org.hibernate.type.YesNoType")
	@Column(name="guestCanCreateAccountInOrgs", columnDefinition="varchar(1) default 'Y' not null") 
	private boolean guestCanCreateAccountInOrganizations;  
	
	@org.hibernate.annotations.Type(type="org.hibernate.type.YesNoType")
	@Column(name="sharedUsersBetweenOrgs", columnDefinition="varchar(1) default 'N' not null") 
	private boolean sharedUsersBetweenOrganizations;
	
	@org.hibernate.annotations.Type(type="org.hibernate.type.YesNoType")
	@Column(columnDefinition="varchar(1) default 'N' not null")
	private boolean useEmailAsUserName; 

	@org.hibernate.annotations.Type(type="org.hibernate.type.YesNoType")
	@Column(columnDefinition="varchar(1) default 'N' not null")
	private boolean privacyPolicyOnSignUp; 

	@Hidden
	public int getLockSessionMilliseconds() {  
		// Negative minutes are treated as seconds, a trick for testing purposes
		return lockSessionMinutes > 0?lockSessionMinutes * 60000:lockSessionMinutes * -1000; 
	}
	
	
	public static Configuration getInstance() {
		if (instance == null) {
			instance = XPersistence.getManager().find(ConfigurationRecord.class, 1);
			if (instance == null) instance = new ConfigurationRecord();
		}
		return instance;
	}
	
	public static void resetInstance() {
		instance = null;
	}
	
	@PrePersist
	private void prePersist() {
		id = 1;
	}

	public int getPasswordMinLength() {
		return passwordMinLength;
	}

	public void setPasswordMinLength(int passwordMinLength) {
		this.passwordMinLength = passwordMinLength;
	}

	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}

	public boolean isForceLetterAndNumbersInPassword() {
		return forceLetterAndNumbersInPassword;
	}

	public void setForceLetterAndNumbersInPassword(
			boolean forceLetterAndNumbersInPassword) {
		this.forceLetterAndNumbersInPassword = forceLetterAndNumbersInPassword;
	}

	public boolean isRecentPasswordsNotAllowed() {
		return recentPasswordsNotAllowed;
	}

	public void setRecentPasswordsNotAllowed(boolean recentPasswordsNotAllowed) {
		this.recentPasswordsNotAllowed = recentPasswordsNotAllowed;
	}

	public int getForceChangePasswordDays() {
		return forceChangePasswordDays;
	}

	public void setForceChangePasswordDays(int forceChangePasswordDays) {
		this.forceChangePasswordDays = forceChangePasswordDays;
	}
	
	public int getLockSessionMinutes() {
		return lockSessionMinutes;
	}

	public void setLockSessionMinutes(int lockSessionMinutes) {
		this.lockSessionMinutes = lockSessionMinutes;
	}


	public int getLoginAttemptsBeforeLocking() {
		return loginAttemptsBeforeLocking;
	}


	public void setLoginAttemptsBeforeLocking(int loginAttemptsBeforeLocking) {
		this.loginAttemptsBeforeLocking = loginAttemptsBeforeLocking;
	}


	public int getInactiveDaysBeforeDisablingUser() {
		return inactiveDaysBeforeDisablingUser;
	}


	public void setInactiveDaysBeforeDisablingUser(
			int inactiveDaysBeforeDisablingUser) {
		this.inactiveDaysBeforeDisablingUser = inactiveDaysBeforeDisablingUser;
	}


	public boolean isGuestCanCreateAccount() {
		return guestCanCreateAccount;
	}


	public void setGuestCanCreateAccount(boolean guestCanCreateAccount) {
		this.guestCanCreateAccount = guestCanCreateAccount;
	}
	
	public boolean isGuestCanCreateAccountInOrganizations() {
		return guestCanCreateAccountInOrganizations;
	}


	public void setGuestCanCreateAccountInOrganizations(boolean guestCanCreateAccountInOrganizations) {
		this.guestCanCreateAccountInOrganizations = guestCanCreateAccountInOrganizations;
	}


	public boolean isSharedUsersBetweenOrganizations() {
		return sharedUsersBetweenOrganizations;
	}


	public void setSharedUsersBetweenOrganizations(boolean sharedUsersBetweenOrganizations) {
		this.sharedUsersBetweenOrganizations = sharedUsersBetweenOrganizations;
	}


	public boolean isUseEmailAsUserName() {
		return useEmailAsUserName;
	}


	public void setUseEmailAsUserName(boolean useEmailAsUserName) {
		this.useEmailAsUserName = useEmailAsUserName;
	}


	public boolean isPrivacyPolicyOnSignUp() {
		return privacyPolicyOnSignUp;
	}


	public void setPrivacyPolicyOnSignUp(boolean privacyPolicyOnSignUp) {
		this.privacyPolicyOnSignUp = privacyPolicyOnSignUp;
	}

}
