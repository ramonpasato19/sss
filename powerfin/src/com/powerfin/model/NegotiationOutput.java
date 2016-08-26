package com.powerfin.model;

import java.io.*;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;

/**
 * The persistent class for the negotiation_output database table
 *
 */
@Entity()
@Table(name = "negotiation_output")
@View(members = "fileName;" + "lineNumber;" + "result")
@Tab(properties = "fileName, lineNumber, result")
public class NegotiationOutput implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Hidden
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "negotiation_output_id", length = 32)
	private String negotiationOutputId;

	@Column(name = "file", length = 32)
	private String file;

	@Column(name = "file_name", length = 255)
	private String fileName;

	@Column(name = "file_type", length = 3)
	private String fileType;

	@Column(name = "line_number")
	private Integer lineNumber;

	@Column(name = "result", length = 255)
	private String result;

	@Column(name = "stack_trace")
	private String stackTrace;

	@ManyToOne
	@JoinColumn(name = "negotiation_id", nullable = false)
	@NoCreate
	@NoModify
	@Required
	private Negotiation negotiation;

	public NegotiationOutput() {

	}

	public String getNegotiationOutputId() {
		return negotiationOutputId;
	}

	public void setNegotiationOutputId(String negotiationOutputId) {
		this.negotiationOutputId = negotiationOutputId;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public Integer getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(Integer lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

	public Negotiation getNegotiation() {
		return negotiation;
	}

	public void setNegotiation(Negotiation negotiation) {
		this.negotiation = negotiation;
	}

}
