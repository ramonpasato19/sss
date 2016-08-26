package com.openxava.naviox.model;

import java.io.*;
import java.math.*;
import java.security.*;
import java.util.*;

import javax.naming.*;
import javax.naming.directory.*;
import javax.persistence.*;

import org.apache.commons.collections.*;
import org.apache.commons.logging.*;
import org.openxava.annotations.*;
import org.openxava.application.meta.*;
import org.openxava.calculators.*;
import org.openxava.controller.meta.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity
@Table(name="OXUSERS")
@View(members=
	"name;" +
	"password, repeatPassword;" + 
	"active;" + 
	"authenticateWithLDAP;" +	
	"personalData [" +
	"	email;" +
	"	givenName;" +
	"	familyName;" +
	"	jobTitle;" +
	"	middleName;" +
	"	nickName;" +
	"	birthDate;" +
	"];" +
	"roles;" +
	"modules;"
)
public class User implements java.io.Serializable {
	
	private final static String PROPERTIES_FILE = "naviox.properties";
	private static Log log = LogFactory.getLog(User.class);
	private static Properties properties;
	private static Map<String, Boolean> actionsByModules; 

	
	public static User find(String name) {
		return XPersistence.getManager().find(User.class, name);
	}
	
	public static int count() {
 		Query query = XPersistence.getManager().createQuery(
 			"select count(*) from User");
 		return ((Number) query.getSingleResult()).intValue();  		 		
	}
	
	static void resetCache() {
		actionsByModules = null;
	}
	
	@Id @Column(length=30) 
	private String name;

	@Column(length=41) @DisplaySize(30) 
	@Stereotype("PASSWORD")	
	private String password;

	@Transient
	@Column(length=41) @DisplaySize(30) 
	@Stereotype("PASSWORD")		
	private String repeatPassword; 
	
	@org.hibernate.annotations.Type(type="org.hibernate.type.YesNoType")
	@DefaultValueCalculator(TrueCalculator.class) 
	private boolean active = true; 
	
	@org.hibernate.annotations.Type(type="org.hibernate.type.YesNoType")
	private boolean authenticateWithLDAP; 
	
	@Column(length=60) @Stereotype("EMAIL")
	private String email;
	
	@Column(length=30)
	private String givenName;
		
	@Column(length=30)
	private String familyName;
	
	@Column(length=30)
	private String jobTitle;
	
	@Column(length=30)
	private String middleName;
	
	@Column(length=30)
	private String nickName;
	
	private Date birthDate;

	@ManyToMany
	private Collection<Role> roles;
	
	@ReadOnly
	public Collection<Module> getModules() {
		if (roles == null) return Collections.EMPTY_LIST;
		Collection<Module> modules = new ArrayList<Module>();
		for (Role role: roles) {
			modules.addAll(role.getModules());
		}
		return modules;
	}
	
	private boolean hasModule(MetaModule metaModule) { 
		for (Module module: getModules()) {
			if (metaModule.getName().equals(module.getName()) && 
				metaModule.getMetaApplication().getName().equals(module.getApplication()))
			{
				return true;
			}
		}
		return false;
	}
	
	@PrePersist @PreUpdate
	private void verifyPasswordsMatch() { 
		if (repeatPassword == null) return;
		if (!repeatPassword.equals(password)) {
			Messages errors = new Messages();
			errors.add("passwords_not_match", "password");
			throw new org.openxava.validators.ValidationException(errors);
		}
	}
	
	private void encryptPassword() {
		password = encrypt(password);
	}
	
	private void encryptRepeatPassword() { 
		repeatPassword = encrypt(repeatPassword);
	}
	
	public boolean isAuthorized(String password) { 
		if (!isActive()) return false;
		return passwordMatches(password);
	}
	
	public boolean passwordMatches(String password) {
		if (isAuthenticateWithLDAP()) return isValidLoginWithLDAP(password);
		return encrypt(password).equals(this.password);
	}
	
	private boolean isValidLoginWithLDAP(String password) { 
        Hashtable<String, String> props = new Hashtable<String, String>();
        String securityPrincipal = getProperties().getProperty("ldapDomain", "").trim() + "\\" + this.name;
        String ldapURL = "ldap://" + getProperties().getProperty("ldapHost", "").trim() +
                ":" + getProperties().getProperty("ldapPort", "").trim() + 
                "/" + getProperties().getProperty("ldapDN", "").trim();
        props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        props.put(Context.PROVIDER_URL, ldapURL);
        props.put(Context.SECURITY_AUTHENTICATION, "simple");
        props.put(Context.SECURITY_PRINCIPAL, securityPrincipal);
        props.put(Context.SECURITY_CREDENTIALS, password);
        try {
            DirContext ctx = new InitialDirContext(props);
            ctx.close();
            return true;
        } catch (NamingException ex) {
            log.error(XavaResources.getString("ldap_authentication_error"), ex);  
        }
        return false;
    }
	
	private String encrypt(String source) {
		if (!isEncryptPassword()) return source;
		try {
	      MessageDigest md = MessageDigest.getInstance("SHA");
	      byte[] bytes = source.getBytes();
	      md.update(bytes);
	      byte[] encrypted= md.digest();
	      if (isStorePasswordAsHex()) {
	    	  return new BigInteger(encrypted).toString(16);
	      }
	      else {
	    	  return new String(encrypted);
	      } 
		}
		catch (Exception ex) {
			log.error(XavaResources.getString("encrypting_password_problem"), ex); 
			throw new RuntimeException(XavaResources.getString("encrypting_password_problem"), ex);  
		}
	}
	
	private static boolean isEncryptPassword() {
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"encryptPassword", "true").trim());
	}
	
	private static boolean isStorePasswordAsHex() { 
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"storePasswordAsHex", "true").trim());
	}
	
	// We don't use NaviOXProperties here to not create a compilation dependency from naviox jar from User.java
	private static Properties getProperties() {
		if (properties == null) {
			PropertiesReader reader = new PropertiesReader(
					User.class, PROPERTIES_FILE);
			try {
				properties = reader.get();
			} catch (IOException ex) {
				log.error(XavaResources.getString("properties_file_error",
						PROPERTIES_FILE), ex);
				properties = new Properties();
			}
		}
		return properties;
	}
		
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return "********";
	}

	public void setPassword(String password) {
		if (getPassword().equals(password)) return;		
		this.password = password;
		encryptPassword(); 
	}

	public Collection<Role> getRoles() {
		return roles;
	}

	public void setRoles(Collection<Role> roles) {
		this.roles = roles;
	}


	public Collection<Module> getNotHiddenModules() {
		Collection<Module> result = new ArrayList<Module>();
		for (Module module: getModules()) {
			if (!module.isHidden()) {
				result.add(module);
			}
		}
		return result;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public boolean isAuthenticateWithLDAP() {
		return authenticateWithLDAP;
	}

	public void setAuthenticateWithLDAP(boolean authenticateWithLDAP) {
		this.authenticateWithLDAP = authenticateWithLDAP;
	}

	public String getRepeatPassword() {
		return getPassword();
	}

	public void setRepeatPassword(String repeatPassword) {
		if (repeatPassword == null) return; 
		if (repeatPassword.equals(getPassword())) return; 
		if (repeatPassword.equals(this.repeatPassword)) return; 
		this.repeatPassword = repeatPassword;
		encryptRepeatPassword(); 
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	/**
	 * @since 5.3
	 */
	static public boolean isActionForMetaModule(String userName, String actionName, MetaModule metaModule) { 
		if (actionsByModules == null) actionsByModules = new HashMap<String, Boolean>();
		String key = userName + ":" + actionName + ":" + metaModule.getMetaApplication().getName() + ":" + metaModule.getName();
		Boolean result = actionsByModules.get(key);
		if (result == null) {
			User user = User.find(userName);
			if (user.hasModule(metaModule)) {
				result = true;
				Collection<MetaAction> excludedActions =  user.getExcludedMetaActionsForMetaModule(metaModule);				
				for (MetaAction action: excludedActions) {		
					if (action.getName().equals(actionName)) {
						result = false;
						break;
					}
				}
			}
			else {
				result = false;
			}
			actionsByModules.put(key, result);
		}
		return result; 
	}

	public Collection<MetaAction> getExcludedMetaActionsForMetaModule(MetaModule metaModule) { 
		Collection<MetaAction> result = null; 
		for (Role role: roles) {
			ModuleRights rights = role.getModulesRightsForMetaModule(metaModule);
			if (rights == null) continue;
			if (result == null) result = rights.getExcludedMetaActions();
			else result = CollectionUtils.intersection(result, rights.getExcludedMetaActions());
		}
		return result==null?Collections.EMPTY_LIST:result;
	}

}
