package com.openxava.naviox.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.controller.meta.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@Table(name="OXROLES_OXMODULES")
@IdClass(ModuleRightsKey.class)
@View(members="module; excludedActions")
@Tab(properties="role.name, module.name, excludedActions") 
public class ModuleRights {
	
	public static int countForApplication(String application) {
 		Query query = XPersistence.getManager().createQuery(
 			"select count(*) from ModuleRights r where r.module.application = :application");
 		query.setParameter("application", application);
 		return ((Number) query.getSingleResult()).intValue();  		 		
	}

	@Id @ManyToOne
	@JoinColumn(name="roles_name")
	private Role role;
	
	@Id @ManyToOne 
	@JoinColumns({
		@JoinColumn(name="modules_application", referencedColumnName="application"), 
		@JoinColumn(name="modules_name", referencedColumnName="name")
	})
	@ReferenceView("OnlyName") @NoFrame
	private Module module;
	
	@Column(length=500)
	private String excludedActions;
	
	@PostPersist @PostUpdate @PostRemove 
	private void resetCache() { 
		User.resetCache();
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Module getModule() {
		return module;
	}

	public void setModule(Module module) {
		this.module = module;
	}

	public String getExcludedActions() {
		return excludedActions;
	}

	public void setExcludedActions(String excludedActions) {
		this.excludedActions = excludedActions;
	}

	public Collection<MetaAction> getExcludedMetaActions() { 
		if (Is.emptyString(excludedActions)) return Collections.EMPTY_LIST;
		Collection<MetaAction> result = new ArrayList<MetaAction>();
		for (String action: excludedActions.split(",")) {
			result.add(MetaControllers.getMetaAction(action));
		}
		return result;
	}

}
