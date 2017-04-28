package com.powerfin.model;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;

import com.powerfin.model.types.*;

@Entity
@Table(name = "prelation_order")
@View(members="product;category;prelationOrder;"
		+ "allowPartialPayment")
@Tab(properties="product.productId, product.name, category.categoryId, category.name, prelationOrder, allowPartialPayment")
public class PrelationOrder {

	@Id
	@Column(name="prelation_order_id", unique=true, nullable=false, length=32)
	@Hidden
	@GeneratedValue(generator="system-uuid") 
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String prelationOrderId;
	
	@ManyToOne
	@JoinColumn(name="category_id", nullable=false)
	@Required
	@NoCreate
	@NoModify
	@ReferenceView("Reference")
	private Category category;

	@ManyToOne
	@JoinColumn(name="product_id", nullable=false)
	@Required
	@NoCreate
	@NoModify
	@ReferenceView("Reference")
	private Product product;
	
	@Column(name = "prelation_order")
	@Required
	private Integer prelationOrder;

	@Required
	@Column(name = "allow_partial_payment", nullable=false)
	private Types.YesNoIntegerType allowPartialPayment;
	
	public String getPrelationOrderId() {
		return prelationOrderId;
	}

	public void setPrelationOrderId(String prelationOrderId) {
		this.prelationOrderId = prelationOrderId;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Integer getPrelationOrder() {
		return prelationOrder;
	}

	public void setPrelationOrder(Integer prelationOrder) {
		this.prelationOrder = prelationOrder;
	}

	public Types.YesNoIntegerType getAllowPartialPayment() {
		return allowPartialPayment;
	}

	public void setAllowPartialPayment(Types.YesNoIntegerType allowPartialPayment) {
		this.allowPartialPayment = allowPartialPayment;
	}

}
