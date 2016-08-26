package com.openxava.naviox.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.application.meta.*;
import org.openxava.jpa.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity
@Table(name="OXMODULES")
@IdClass(ModuleKey.class)
@View(name="OnlyName", members="name") 
public class Module implements java.io.Serializable {
	
	@Id @Column(length=50) @ReadOnly @Hidden 
	private String application;
	
	@Id @Column(length=80) @ReadOnly
	private String name;
	
	@ManyToOne(fetch=FetchType.LAZY) 
	@DescriptionsList
	private Folder folder; 	
	
	@org.hibernate.annotations.Type(type="org.hibernate.type.YesNoType") 
	private boolean unrestricted;
	
	@org.hibernate.annotations.Type(type="org.hibernate.type.YesNoType") 
	private boolean hidden;
	
	@ManyToMany
	@JoinTable(name="OXROLES_OXMODULES", 
		joinColumns={
			@JoinColumn(name="modules_application", referencedColumnName="application"),
			@JoinColumn(name="modules_name", referencedColumnName="name")
		},
		inverseJoinColumns={
			@JoinColumn(name="roles_name")
		}
	)
	private Collection<Role> roles;
	
	@Hidden
	private Integer orderInFolder = 9999; 
	
	public static Module createFromMetaModule(MetaModule metaModule) {
		Module module = new Module();
		module.setApplication(metaModule.getMetaApplication().getName());
		module.setName(metaModule.getName());
		XPersistence.getManager().persist(module);
		return module;
	}


	public static Collection<Module> findAll()  {	 			
 		Query query = XPersistence.getManager().createQuery("from Module"); 
 		return query.getResultList();  		 		
 	}
	
	public static Collection<Module> findByApplication(String application)  { 	 			
 		Query query = XPersistence.getManager().createQuery("from Module m where m.application = :application");
 		query.setParameter("application", application);
 		return query.getResultList();  		 		
 	}
	
	public static Module findByMetaModule(MetaModule metaModule) { 
		return findByApplicationModule(metaModule.getMetaApplication().getName(), metaModule.getName()); 
	}
	
	public static Module findByApplicationModule(String application, String module) { 
		ModuleKey key = new ModuleKey();
		key.setApplication(application);
		key.setName(module);
		return XPersistence.getManager().find(Module.class, key);
	}

	
	public static Collection findUnrestrictedOnes()  {	 
 		Query query = XPersistence.getManager().createQuery(
 			"from Module as o where o.unrestricted = true and o.hidden = false");
 		return query.getResultList();  		 		
 	}
	
	public static Collection<Module> findInRoot() {
 		Query query = XPersistence.getManager().createQuery(
 			"from Module m where m.folder = null order by m.application asc, m.name asc");  
 		return query.getResultList();  		 		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((application == null) ? 0 : application.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Module other = (Module) obj;
		if (application == null) {
			if (other.application != null)
				return false;
		} else if (!application.equals(other.application))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	
 	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isUnrestricted() {
		return unrestricted;
	}

	public void setUnrestricted(boolean unrestricted) {
		this.unrestricted = unrestricted;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public Collection<Role> getRoles() {
		return roles;
	}
	

	public void setRoles(Collection<Role> roles) {
		this.roles = roles;
	}

	public Folder getFolder() {
		return folder;
	}


	public void setFolder(Folder folder) {
		this.folder = folder;
	}


	public Integer getOrderInFolder() {
		return orderInFolder;
	}


	public void setOrderInFolder(Integer orderInFolder) {
		this.orderInFolder = orderInFolder;
	}

}
