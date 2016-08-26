package com.powerfin.model;

import java.io.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;

import com.powerfin.util.*;

/**
 * The persistent class for the country database table.
 * 
 */
@Entity
@Table(name = "report")
@View(members = "reportId; name; file; format; fileId")
public class Report implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "report_id", unique = true, nullable = false, length = 50)
	private String reportId;

	@Column(nullable = false, length = 100)
	@DisplaySize(40)
	@Required
	private String name;

	@Stereotype("FILE")
	@Column(length = 32)
	@Required
	private String file;

	@Column(nullable = false, length = 6)
	@Required
	private String format;
	
	public Report() {
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
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

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

}