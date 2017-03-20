package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity
public class Team extends Nameable {
	
	@OneToMany(mappedBy="team", cascade=CascadeType.ALL)
	private Collection<TeamMember> members;
	
	public Collection<TeamMember> getMembers() {
		return members;
	}

	public void setMembers(Collection<TeamMember> members) {
		this.members = members;
	}
	
}
