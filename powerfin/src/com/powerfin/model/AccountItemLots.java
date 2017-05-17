package com.powerfin.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.AsEmbedded;
import org.openxava.annotations.CollectionView;
import org.openxava.annotations.DescriptionsList;
import org.openxava.annotations.Hidden;
import org.openxava.annotations.ListProperties;
import org.openxava.annotations.NoCreate;
import org.openxava.annotations.NoModify;
import org.openxava.annotations.ReadOnly;
import org.openxava.annotations.ReferenceView;
import org.openxava.annotations.SearchAction;
import org.openxava.annotations.Tab;
import org.openxava.annotations.View;


@Entity
@Table(name="account_item_lots")
@View(members="account;numberLot;code;manufacturingDate;expireDate;discountDays;discount,discountValue;"
		 + "quantity; currentQuantity, manualQuantity; comment; unity;cellarLocation;active;accountItemLotsInvoice")
@Tab(properties="account.accountId, code, numberLot, account.name, expireDate, currentQuantity, manualQuantity")  
public class AccountItemLots {
	@Id
	@Column(name="account_item_lots_id", unique=true, nullable=false, length=32)
	@Hidden
	@GeneratedValue(generator="system-uuid") 
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String accountItemLotsId; 
	
	@ManyToOne
	@JoinColumn(name="account_id")
	@SearchAction(value="SearchAccount.SearchAccountItem")
	@NoCreate
	@NoModify
	@ReferenceView("report")
	private Account account;
	
	@Column(name="number_lot")
	private String numberLot;
	
	@Column(name="code")
	private String code;
	
	@Temporal(TemporalType.DATE)
	@Column(name="manufacturing_date")
	private Date manufacturingDate;
	
	@Temporal(TemporalType.DATE)
	@Column(name="expire_date")
	private Date expireDate;
	
	@Column(name="discount_days")
	private int discountDays;
	
	@Column(name="discount")
	private BigDecimal discount;
	
	@Column(name="discount_value")
	private BigDecimal discountValue;
	
	@Column(name="quantity")
	@ReadOnly
	private BigDecimal quantity;
	
	@Column(name="current_quantity")
	@ReadOnly
	private BigDecimal currentQuantity;
	
	@Column(name="manual_quantity")
	private BigDecimal manualQuantity;
	
	@Column(name="comment")
	private String comment;
	
	@Column(name="cellar_location")
	private String cellarLocation;
	
	@ManyToOne
	@JoinColumn(name="unity_id")
	@NoCreate
	@NoModify
	@DescriptionsList(descriptionProperties = "name")
	private Unity unity;

	@Column
	private Boolean active;
	
	@OneToMany(
			mappedBy="accountItemLots",
			cascade=CascadeType.ALL)
	@AsEmbedded
	@ListProperties("account.accountId;account.name; quantity")
	@CollectionView("addInvoiceLots")
	private List<AccountItemLotsInvoice> accountItemLotsInvoice;
	
	public String getAccountItemLotsId() {
		return accountItemLotsId;
	}

	public void setAccountItemLotsId(String accountItemLotsId) {
		this.accountItemLotsId = accountItemLotsId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getManufacturingDate() {
		return manufacturingDate;
	}

	public void setManufacturingDate(Date manufacturingDate) {
		this.manufacturingDate = manufacturingDate;
	}

	public int getDiscountDays() {
		return discountDays;
	}

	public void setDiscountDays(int discountDays) {
		this.discountDays = discountDays;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getCurrentQuantity() {
		return currentQuantity;
	}

	public void setCurrentQuantity(BigDecimal currentQuantity) {
		this.currentQuantity = currentQuantity;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCellarLocation() {
		return cellarLocation;
	}

	public void setCellarLocation(String cellarLocation) {
		this.cellarLocation = cellarLocation;
	}

	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	public Unity getUnity() {
		return unity;
	}

	public void setUnity(Unity unity) {
		this.unity = unity;
	}

	public String getNumberLot() {
		return numberLot;
	}

	public void setNumberLot(String numberLot) {
		this.numberLot = numberLot;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public List<AccountItemLotsInvoice> getAccountItemLotsInvoice() {
		return accountItemLotsInvoice;
	}

	public void setAccountItemLotsInvoice(List<AccountItemLotsInvoice> accountItemLotsInvoice) {
		this.accountItemLotsInvoice = accountItemLotsInvoice;
	}

	public BigDecimal getDiscountValue() {
		return discountValue;
	}

	public void setDiscountValue(BigDecimal discountValue) {
		this.discountValue = discountValue;
	}

	public BigDecimal getManualQuantity() {
		return manualQuantity;
	}

	public void setManualQuantity(BigDecimal manualQuantity) {
		this.manualQuantity = manualQuantity;
	}
}
