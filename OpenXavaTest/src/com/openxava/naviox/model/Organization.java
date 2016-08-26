package com.openxava.naviox.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
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
@Tab(properties="name")
public class Organization implements java.io.Serializable {  
	
	private static HashMap<String, String> names;
	
	@Id @Hidden @Column(length=50) 
	private String id;
	
	@Column(length=50) @Required
	private String name;
	
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
		int length = name.length();
		StringBuffer sb = new StringBuffer();		
		for (int i=0; i<length; i++) {
			char c = name.charAt(i);
			if (Character.isLetter(c) || Character.isDigit(c)) {
				sb.append(c);
			}
		}
		String result = Strings.removeAccents(sb.toString());
		return result.replace("\u00D1", "N").replace("\u00F1", "n");
	}
	
	public static String getName(String id) {
		if (id == null) return null;
		return names.get(id);
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

	private static Collection<Organization> findAll() {
		return XPersistence.getManager().createQuery("from Organization").getResultList();
	}

	public static int count() {
		if (names == null) setUp();
		return names.size();
	}

	
}
