package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza 
 */

@Embeddable
public class ReallocationDetail {
	
	@Column(length=20) @Required
	private String place; 
	
	@DefaultValueCalculator( 
		value=org.openxava.calculators.IntegerCalculator.class,
		properties={ @PropertyValue(name="value", value="1") }		
	)		
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	private Product product;

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

}
