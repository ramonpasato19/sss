package com.openxava.naviox.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;
import org.openxava.model.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@Table(name="OXFOLDERS")
@View(members="name, parent, icon; calculatedSubfolders; calculatedModules") 
public class Folder extends Identifiable implements java.io.Serializable {
		
	@Column(length=25) @Required
	private String name;
	
	@Column(length=40) 
	@Stereotype("ICON") 
	private String icon; 
	
	@ManyToOne 
	@DescriptionsList
	private Folder parent;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="parent")
	@OrderBy("orderInFolder") 
	private List<Folder> subfolders; 
	
	@OneToMany(mappedBy="folder")
	@OrderBy("orderInFolder") 
	private List<Module> modules; 
	
	@Hidden
	private Integer orderInFolder = 9999; 
	
	@RowActions({ 
		@RowAction("Folder.subfolderUp"),
		@RowAction("Folder.subfolderDown")
	})	
	@AsEmbedded
	@SaveAction("Folder.saveSubfolder") 
	public Collection<Folder> getCalculatedSubfolders() { 
		return subfolders;
	}	
	
	@RowActions({ 
		@RowAction("Folder.moduleUp"),
		@RowAction("Folder.moduleDown")
	})	
	public Collection<Module> getCalculatedModules() {
		return modules;
	}
	
	@Hidden
	public String getLabel() {
		String id = Strings.naturalLabelToIdentifier(getName());
		if (Labels.existsExact(id, Locales.getCurrent())) return Labels.get(id);
		return getName();
	}
	
	public static Folder find(String oid) {
		return XPersistence.getManager().find(Folder.class, oid);
	}
	
	public static Folder findByName(String name) { 
 		Query query = XPersistence.getManager().createQuery("from Folder f where f.name = :name");
 		query.setParameter("name", name);
 		return (Folder) query.getSingleResult();  		 				
	}
	
	public static Collection<Folder> findByParent(Folder parent) {
		String condition = parent == null?"is null":"= :parent";
		Query query = XPersistence.getManager().createQuery(
			"from Folder f where f.parent " + condition + " order by f.orderInFolder"); 
	 	if (parent != null) query.setParameter("parent", parent);
	 	return query.getResultList();  		 		
	}
	
	public void moduleUp(int index) { 
		elementUp(modules, index);
	}
	
	public void subfolderUp(int index) { 
		elementUp(subfolders, index);
	}	
	
	private void elementUp(List list, int index) { 
		if (index == 0) return;
		if (list == null) return;
		if (index >= list.size()) return;
		Collections.swap(list, index, index - 1);
		updateOrder(list);
	}
	
	public void moduleDown(int index) { 
		elementDown(modules, index);
	}
	
	public void subfolderDown(int index) { 
		elementDown(subfolders, index);
	}
	
	private void elementDown(List list, int index) { 
		if (list == null) return;
		if (index >= list.size() - 1) return;
		Collections.swap(list, index, index + 1);
		updateOrder(list);
	}
	
	
	private void updateOrder(List list) {
		int i = 0;
		for (Object element: list) {
			// An instaceof is always ugly, but for this case creating an IOrderable interface
			// or using introspection are more complex solutions
			if (element instanceof Module) ((Module) element).setOrderInFolder(i++);
			else ((Folder) element).setOrderInFolder(i++);
		}

	}
	
	@PreRemove
	private void annulModulesReferences() {
		// Because some database does not annul by default
		for (Module m: getModules()) m.setFolder(null);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Folder getParent() {
		return parent;
	}

	public void setParent(Folder parent) {
		this.parent = parent;
	}

	public List<Folder> getSubfolders() { 
		return subfolders;
	}

	public void setSubfolders(List<Folder> subfolders) { 	
		this.subfolders = subfolders;
	}

	public List<Module> getModules() { 
		return modules;
	}

	public void setModules(List<Module> modules) {
		this.modules = modules;
	}

	public Integer getOrderInFolder() {
		return orderInFolder;
	}

	public void setOrderInFolder(Integer orderInFolder) {
		this.orderInFolder = orderInFolder;
	}

	public String getIcon() {
		return Is.emptyString(icon)?"folder":icon; 
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
	
}
