package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity
public class ProductsEvaluation extends Identifiable {
	
	@Column(length=40) @Required
	private String description;
	
	@ElementCollection
	@ListProperties("product, evaluation")
	private Collection<ProductEvaluation> evaluations;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Collection<ProductEvaluation> getEvaluations() {
		return evaluations;
	}

	public void setEvaluations(Collection<ProductEvaluation> evaluations) {
		this.evaluations = evaluations;
	}
	
}
