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
			remove(metaMembers, member); 
		}
		Collection<MetaMember> readOnlyMembers = user.getReadOnlyMetaMembersForMetaModule(metaModule);
		for (MetaMember member: readOnlyMembers) {	
			view.setEditable(member.getName(), false); 
		}	
	}
	
	private void remove(Collection<MetaMember> members, MetaMember member) { 
		for (Iterator<MetaMember> it= members.iterator(); it.hasNext();) {
			MetaMember m = it.next();
			if (m.getName().equals(member.getName())) it.remove();
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
		
		Map<String, String> modelsOfQualifiedProperties = new HashMap<String, String>();
		for(String propertyName: metaTab.getRemainingPropertiesNames()) {
			if (propertyName.contains(".")) {
				MetaProperty metaProperty = metaTab.getMetaModel().getMetaProperty(propertyName);
				modelsOfQualifiedProperties.put(metaProperty.getMetaModel().getName(), Strings.noLastToken(propertyName, "."));
			}
		}
		
		for (Map.Entry<String, String> model: modelsOfQualifiedProperties.entrySet()) {
			MetaModule module = metaModule.getMetaApplication().getMetaModule(model.getKey());
			Collection<MetaMember> excludedMembersForQualifiedProperties = user.getExcludedMetaMembersForMetaModule(module);
			for (MetaMember excludedMember: excludedMembersForQualifiedProperties) {
				String excludedQualifiedProperty = model.getValue() + excludedMember.getName();
				metaTab.dropMember(excludedQualifiedProperty);
			}	
		}
	}

	public static void init() {
		if (instance == null) instance = new MembersRefiner();
		View.setPolisher(instance); 
		Tab.setRefiner(instance);
	}
	
}
