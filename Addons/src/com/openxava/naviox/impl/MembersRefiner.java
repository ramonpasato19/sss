package com.openxava.naviox.impl;

import java.util.*;

import org.openxava.application.meta.*;
import org.openxava.model.meta.*;
import org.openxava.tab.*;
import org.openxava.tab.meta.*;
import org.openxava.util.*;
import org.openxava.view.*;

import com.openxava.naviox.model.*;


/**
 * Members refiner, to remove the unauthorized members or to mark them as read only.
 * 
 * @author Javier Paniza
 */
public class MembersRefiner {
	
	private static MembersRefiner instance;

	public void refine(MetaModule metaModule, Collection<MetaMember> metaMembers, View view) { 
		String currentUser = Users.getCurrent();
		if (currentUser == null) return;
		User user = User.find(currentUser);
		if (user == null) return; 
		Collection<MetaMember> excludedMembers = user.getExcludedMetaMembersForMetaModule(metaModule);
		for (MetaMember member: excludedMembers) {		
			metaMembers.remove(member);
		}
		Collection<MetaMember> readOnlyMembers = user.getReadOnlyMetaMembersForMetaModule(metaModule);
		for (MetaMember member: readOnlyMembers) {	
			view.setEditable(member.getName(), false); 
		}	
	}
	
	public void polish(MetaModule metaModule, MetaTab metaTab) {
		String currentUser = Users.getCurrent();
		if (currentUser == null) return;
		User user = User.find(currentUser);
		if (user == null) return; 
		Collection<MetaMember> excludedMembers = user.getExcludedMetaMembersForMetaModule(metaModule);
		for (MetaMember excludedMember: excludedMembers) {
			metaTab.dropMember(excludedMember.getName());
		}
	}

	public static void init() {
		if (instance == null) instance = new MembersRefiner();
		View.setPolisher(instance); 
		Tab.setRefiner(instance);
	}
	
}
