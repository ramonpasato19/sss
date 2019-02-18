package com.openxava.naviox.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.filters.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

/**
 * 
 * @since 5.2
 * @author Javier Paniza
 */

@Entity
@Table(name="OXORGANIZATIONS")
@View(members="name, url")
@Tabs({
	@Tab(properties="name"),	
	@Tab(name="OfCurrentUser", filter=UserFilter.class, 
		properties="name", editors="Cards", 
		baseCondition="from Organization e, in (e.users) u where u.name = ?") 
})
public class Organization implements java.io.Serializable {
	
	private static final long serialVersionUID = -5904310527593026919L;

	private static HashMap<String, String> names;
	
	@Id @Hidden @Column(length=50) 
	private String id;
	
	@Column(length=50) @Required
	private String name;
	
	@ManyToMany(mappedBy="organizations")
	@ReadOnly
	private Collection<User> users; 

	
	/** @since 5.6 */
	public static Organization find(String id) { 
		return XPersistence.getManager().find(Organization.class, id);
	}
	
	/** @since 5.6.1 */
	public static boolean existsWithName(String name) { 
		return XPersistence.getManager().find(Organization.class, normalize(name)) != null;
	}
	
	@LabelFormat(LabelFormatType.NO_LABEL)
	public String getUrl() {  
		return "/o/" + getId();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static String normalize(String name) {
		return Strings.naturalLabelToIdentifier(name); 
	}
	
	public static String getName(String id) {
		if (id == null) return null;
		return names.get(id);
	}
	
	/**
	 * 
	 * @since 5.3.2
	 */
	public static Collection<String> getAllIds() { 
		if (names == null) setUp();
		return names.keySet();
	}
	
	@PrePersist
	private void normalizeId() {
		this.id = normalize(name);
	}
	
	public static void resetCache() {
		names = null;
	}

	public static void setUp() { 
		if (names != null) return;
		names = new HashMap<String, String>();
		for (Organization o: findAll()) {
			names.put(o.getId(), o.getName());
		}		
	}
	
	@SuppressWarnings("unchecked")
	private static Collection<Organization> findAll() {
		return XPersistence.getManager().createQuery("from Organization").getResultList();
	}

	public static int count() {
		if (names == null) setUp();
		return names.size();
	}

	public Collection<User> getUsers() {
		return users;
	}

	public void setUsers(Collection<User> users) {
		this.users = users;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof Organization)) return false;
		return getId().equals(((Organization) obj).getId());
	}
	
	@Override
	public int hashCode() {
		return getId().hashCode();
	}
}
