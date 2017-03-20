package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;
import org.openxava.web.editors.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
public class Incident extends Identifiable {
	
	@Column(length=50) @Required
	private String title;
	
	@Stereotype("SIMPLE_HTML_TEXT")
	private String description;
	
	@Stereotype("DISCUSSION") @Column(length=32) 
	private String discussion;
	
	@PreRemove
	private void removeDiscussion() { 
		DiscussionComment.removeForDiscussion(discussion);
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDiscussion() {
		return discussion;
	}

	public void setDiscussion(String discussion) {
		this.discussion = discussion;
	}

}
