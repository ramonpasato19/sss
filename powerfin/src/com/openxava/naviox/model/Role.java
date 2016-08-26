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
@Table(name="OXROLES")
@View(members="name;modulesRights")
public class Role implements java.io.Serializable {	

	@Id @Column(length=30)
	private String name;
		
	@OneToMany(mappedBy="role", cascade=CascadeType.REMOVE)
	@ListProperties("module.name, excludedActions") 
	@NewAction("Role.addModulesRights")
	private Collection<ModuleRights> modulesRights; 
	
	public static Role find(String name) { 
		return XPersistence.getManager().find(Role.class, name);
	}	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<Module> getModules() {
		if (modulesRights == null) return null;
		Collection<Module> modules = new ArrayList<Module>();
		for (ModuleRights rights: modulesRights) {
			modules.add(rights.getModule());
		}
		return modules;
	}

	public void setModules(Collection<Module> modules) { 
		if (modules == null) {
			modulesRights = null;
			return;
		}
		modulesRights = new ArrayList<ModuleRights>();
		for (Module module: modules) {
			ModuleRights rights = new ModuleRights();
			rights.setRole(this);
			rights.setModule(module);
			XPersistence.getManager().persist(rights);
			modulesRights.add(rights);
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		Role other = (Role) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public Collection<ModuleRights> getModulesRights() {
		return modulesRights;
	}

	public void setModulesRights(Collection<ModuleRights> modulesRights) {
		this.modulesRights = modulesRights;
	}

	public ModuleRights getModulesRightsForMetaModule(MetaModule metaModule) {  
		Query query = XPersistence.getManager().createQuery(
			"from ModuleRights r where r.module.application = :application and r.module.name = :module and r.role.name = :role");
		query.setParameter("application", metaModule.getMetaApplication().getName());
		query.setParameter("module", metaModule.getName());
		query.setParameter("role", name);
		List result = query.getResultList();
		if (result.isEmpty()) return null;
		return (ModuleRights) result.get(0);
	}
	
}
