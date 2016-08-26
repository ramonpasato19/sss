package com.powerfin.model;

import java.io.*;

import javax.persistence.*;

import org.openxava.annotations.*;

@Entity
@Table(name="oxfiles")
@View(members="id;name;libraryid")
@Tab(properties="id, name")
public class File implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="id", unique=true, nullable=false, length=32)
	private String id;

	@Column(nullable=true, length=255)
	@DisplaySize(40)
	private String name;

	@Column(name="data")
	private byte[] data;
	
	@Column(length=32)
	private String libraryid;

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

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getLibraryid() {
		return libraryid;
	}

	public void setLibraryid(String libraryid) {
		this.libraryid = libraryid;
	}
	
	
}
