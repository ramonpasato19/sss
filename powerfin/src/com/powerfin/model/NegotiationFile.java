package com.powerfin.model;

import java.io.*;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import org.openxava.jpa.*;

import com.powerfin.model.File;
import com.powerfin.util.*;

/**
 * The persistent class for the negotiation_file database table
 *
 */
@Entity
@Table(name="negotiation_file")
@Views({
	@View(members="negotiationFileId;"
			+ "negotiation;"
			+ "negotiationFileType;"
			+ "file; fileId"),
	@View(name="ToNegotiation", members="negotiationFileType;"
		+ "file")
})
@Tab(properties="negotiationFileType.name, file")
public class NegotiationFile  implements Serializable  {
	public static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	@Column(name="negotiation_file_id", length=32)
	@ReadOnly
	@Hidden
	private String negotiationFileId;
	
	@ManyToOne
	@JoinColumn(name = "negotiation_file_type_id", nullable = false)
	@Required
	@NoCreate
	@NoModify
	@NoFrame
	@ReadOnly(forViews="ViewProcessNegotiation")
	@SearchActions({
	@SearchAction(forViews="ToNegotiation", value="SearchFileType.SearchFileType")
	})
	private NegotiationFileType negotiationFileType;
	
	@Stereotype("FILE")
	@Column(length=32)
	@Required
	private String file;
	
	@ManyToOne
	@JoinColumn(name="negotiation_id", nullable=false)
	@ReferenceView("Reference")
	private Negotiation negotiation;
	
	public NegotiationFile(){
		
	}

	public String getNegotiationFileId() {
		return negotiationFileId;
	}

	public void setNegotiationFileId(String negotiationFileId) {
		this.negotiationFileId = negotiationFileId;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public NegotiationFileType getNegotiationFileType() {
		return negotiationFileType;
	}

	public void setNegotiationFileType(NegotiationFileType negotiationFileType) {
		this.negotiationFileType = negotiationFileType;
	}
	
	public Negotiation getNegotiation() {
		return negotiation;
	}

	public void setNegotiation(Negotiation negotiation) {
		this.negotiation = negotiation;
	}

	public String getFileId()
	{
		if (file!=null && !UtilApp.fieldIsEmpty(file))
		{
			File fileObject = (File)XPersistence.getManager().find(File.class, file);
			if (fileObject!=null)
				return fileObject.getId();
			else 
				return null;
		}
		else 
			return null;
	}
	
	public String getFileName()
	{
		if (file!=null && !UtilApp.fieldIsEmpty(file))
		{
			File fileObject = (File)XPersistence.getManager().find(File.class, file);
			if (fileObject!=null)
				return fileObject.getName();
			else 
				return null;
		}
		else 
			return null;
	}
	
	@PreRemove
	public void onPreRemove(){
		removeFile();
	}
	
	
	public void removeFile(){
		
		System.out.println("DELETE OXFILE /////////////////////////");
		File file = XPersistence.getManager().find(File.class, getFile());
		XPersistence.getManager().remove(file);
	}
	
}
