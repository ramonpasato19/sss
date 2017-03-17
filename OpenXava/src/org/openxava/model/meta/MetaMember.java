package org.openxava.model.meta;

import java.util.*;

import org.openxava.util.*;
import org.openxava.util.meta.*;

/**
 * @author Javier Paniza
 */
abstract public class MetaMember extends MetaElement implements Comparable { 

	private MetaModel metaModel;
	private String labelId;
	private String qualifiedName;
	
	private String label;
	
	public String getLabel(Locale locale) {
		return Is.emptyString(label) || Labels.exists(getLabelId(), locale)?super.getLabel(locale):label;
	}
		
	public void setLabel(String newLabel) {
		super.setLabel(newLabel);
		label = newLabel;
	}
	
	/** @since 5.6 */
	static public <T extends MetaMember> List<String> toQualifiedNames(Collection<T> metaMembers) { 
		List<String> result = new ArrayList<String>();
		for (MetaMember m: metaMembers) result.add(m.getQualifiedName());
		return result;
	}

	public int compareTo(Object o) { 	
		return getName().compareTo(((MetaMember) o).getName());
	}

	public MetaModel getMetaModel() {		
		return metaModel;
	}

	public void setMetaModel(MetaModel newContainer) {
		metaModel = newContainer;		
	}
	
	public String getQualifiedName() {
		if (qualifiedName == null) {
			if (getMetaModel() == null) qualifiedName = getName();
			else qualifiedName = getMetaModel().getName() + "." + getName();
		}		
		return qualifiedName;
	}
	
	/**
	 * For can set a qualified name manually.
	 */
	public void setQualifiedName(String newQualifiedName) {
		qualifiedName = newQualifiedName;
	}
		
	public boolean isHidden() {
		return false;
	}
	
	public String getId() {
		if (!Is.emptyString(labelId)) {
			return labelId;		
		}
		MetaModel m = getMetaModel(); 
		return m==null?getName():m.getId() + "." + getName();
	}
	
	/**
	 * Id used to look up label in resource files. <p>
	 */ 	
	public String getLabelId() {
		return labelId;
	}
	public void setLabelId(String id) {
		this.labelId = id;		
	}
	
}