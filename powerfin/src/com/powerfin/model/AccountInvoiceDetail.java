package com.powerfin.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.DecimalMin;

import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.NoCreate;
import org.openxava.annotations.NoModify;
import org.openxava.annotations.PreCreate;
import org.openxava.annotations.ReferenceView;
import org.openxava.annotations.Required;
import org.openxava.annotations.SearchAction;
import org.openxava.annotations.SearchActions;
import org.openxava.annotations.View;
import org.openxava.annotations.Views;

import com.powerfin.exception.OperativeException;
import com.powerfin.util.UtilApp;

/**
 * The persistent class for the account_invoice_detail database table.
 * 
 */
@Entity
@Table(name = "account_invoice_detail")
@Views({
@View(members = "accountDetail;"
		+ "tax;"
		+ "unitPrice;"
		+ "quantity;"
		+ "discount;"
		+ "dueDate"
		),
@View(name="InvoicePurchase", members = "accountDetail;"
		+ "tax;"
		+ "unitPrice,remark;"
		+ "quantity;"
		+ "discount;"
		+ "taxSpecialConsumption;"
		+ "taxRedeemable;"
		+ "dueDate;"
		),
@View(name="InvoiceSale", members = "accountDetail;"
		+ "tax;"
		+ "unitPrice;"
		+ "quantity;"
		+ "discount;"
		),
@View(name="CreditNotePurchase", members = "accountDetail;"
		+ "tax;"
		+ "unitPrice;"
		+ "quantity;"
		),
@View(name="CreditNoteSale", members = "accountDetail;"
		+ "tax;"
		+ "unitPrice;"
		+ "quantity;"
		+ "dueDate"
		),
})
public class AccountInvoiceDetail {

	@Id
	@Column(name = "account_invoice_detail_id", unique = true, nullable = false, length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String accountInvoiceDetailId;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "account_detail_id", nullable = false)
	@ReferenceView("simple")
	@NoCreate
	@NoModify
	@SearchActions({
		@SearchAction(forViews="InvoicePurchase", value="SearchAccountDetail.SearchForInvoicePurchase"),
		@SearchAction(forViews="InvoiceSale", value="SearchAccountDetail.SearchForInvoiceSale")
	})
	private Account accountDetail;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "account_invoice_id", nullable = false)
	private AccountInvoice accountInvoice;

	@Column(name = "unit_price", nullable = false, precision=15, scale=6)
	@Required
	@DecimalMin(value="0.00")
	private BigDecimal unitPrice;

	@Column(name = "discount", nullable = false, precision=15, scale=6)
	@DecimalMin(value="0.00")
	private BigDecimal discount;
	
	@Temporal(TemporalType.DATE)
	@Column(name="due_date")
	private Date dueDate;
	
	@Column(name = "quantity", nullable = false, precision=15, scale=6)
	@Required
	@DecimalMin(value="0.01")
	private BigDecimal quantity;

	@Column(name = "original_cost", nullable = true, precision=15, scale=6)
	private BigDecimal originalCost;
	
	@Column(name = "original_price", nullable = true, precision=15, scale=6)
	private BigDecimal originalPrice;
	
	@Column(name = "tax_special_consumption", nullable = true, precision=15, scale=6)
	private BigDecimal taxSpecialConsumption;
	
	@Column(name = "tax_redeemable", nullable = true, precision=15, scale=6)
	private BigDecimal taxRedeemable;
	
	@Column(name = "remark", nullable = true)
	private String remark;
		
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "tax_id", nullable = false)
	@Required
	@NoCreate
	@NoModify
	@ReferenceView(value="Simple")
	@SearchActions({
		@SearchAction(forViews="InvoicePurchase",value="SearchTax.SearchTaxForInvoicePurchase"),
		@SearchAction(forViews="InvoiceSale",value="SearchTax.SearchTaxForInvoiceSale"),
		@SearchAction(forViews="CreditNotePurchase",value="SearchTax.SearchTaxForInvoicePurchase"),
		@SearchAction(forViews="CreditNoteSale",value="SearchTax.SearchTaxForInvoiceSale"),
	})
	private Tax tax;
	
	@Column(name = "tax_adjust", nullable = false, precision=11, scale=2)
	@DecimalMin(value="0.00")
	private BigDecimal taxAdjust;
	
	@Column(name = "tax_percentage", nullable = false, precision=11, scale=2)
	private BigDecimal taxPercentage;
	
	@Column(name = "amount", nullable = false, precision=15, scale=6)
	private BigDecimal amount;
	
	@Column(name = "tax_amount", nullable = false, precision=15, scale=6)
	private BigDecimal taxAmount;
	
	@Column(name = "final_amount", nullable = false, precision=11, scale=2)
	private BigDecimal finalAmount;
	
	@ManyToOne
	@JoinColumn(name="unity_id")
	private Unity unity;

	public String getAccountInvoiceDetailId() {
		return accountInvoiceDetailId;
	}

	public void setAccountInvoiceDetailId(String accountInvoiceDetailId) {
		this.accountInvoiceDetailId = accountInvoiceDetailId;
	}

	public Account getAccountDetail() {
		return accountDetail;
	}

	public void setAccountDetail(Account accountDetail) {
		this.accountDetail = accountDetail;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice == null ? new BigDecimal("0.00") : unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public BigDecimal getOriginalCost() {
		return originalCost;
	}

	public void setOriginalCost(BigDecimal originalCost) {
		this.originalCost = originalCost;
	}

	public BigDecimal getOriginalPrice() {
		return originalPrice;
	}

	public void setOriginalPrice(BigDecimal originalPrice) {
		this.originalPrice = originalPrice;
	}

	public AccountInvoice getAccountInvoice() {
		return accountInvoice;
	}

	public void setAccountInvoice(AccountInvoice accountInvoice) {
		this.accountInvoice = accountInvoice;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Tax getTax() {
		return tax;
	}

	public void setTax(Tax tax) {
		this.tax = tax;
	}

	public BigDecimal getTaxAdjust() {
		return taxAdjust;
	}

	public void setTaxAdjust(BigDecimal taxAdjust) {
		this.taxAdjust = taxAdjust;
	}

	public BigDecimal getTaxPercentage() {
		return taxPercentage;
	}

	public void setTaxPercentage(BigDecimal taxPercentage) {
		this.taxPercentage = taxPercentage;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	public BigDecimal getFinalAmount() {
		return finalAmount;
	}

	public void setFinalAmount(BigDecimal finalAmount) {
		this.finalAmount = finalAmount;
	}
	
	public Unity getUnity() {
		return unity;
	}

	public void setUnity(Unity unity) {
		this.unity = unity;
	}

	@PreCreate
	public void onCreate()
	{
		if (getDiscount()==null)
			setDiscount(BigDecimal.ZERO);
		if (getTaxAdjust()==null)
			setTaxAdjust(BigDecimal.ZERO);
		if (getTaxRedeemable()==null)
			setTaxRedeemable(BigDecimal.ZERO);
		if (getTaxSpecialConsumption()==null)
			setTaxSpecialConsumption(BigDecimal.ZERO);
		if (getDueDate()==null)
			setDueDate(UtilApp.DEFAULT_EXPIRY_DATE);
		updateAmounts();
	}
	
	@PreUpdate
	public void onUpdate()
	{
		if (getDiscount()==null)
			setDiscount(BigDecimal.ZERO);
		if (getTaxAdjust()==null)
			setTaxAdjust(BigDecimal.ZERO);
		if (getTaxRedeemable()==null)
			setTaxRedeemable(BigDecimal.ZERO);
		if (getTaxSpecialConsumption()==null)
			setTaxSpecialConsumption(BigDecimal.ZERO);
		if (getDueDate()==null)
			setDueDate(UtilApp.DEFAULT_EXPIRY_DATE);
		updateAmounts();
	}
	
	public void updateAmounts()
	{
		taxAdjust = BigDecimal.ZERO;
		taxPercentage = tax.getPercentage();
		amount = calculateAmount().setScale(2, RoundingMode.HALF_UP);
		finalAmount = calculateFinalAmount();
		taxAmount = finalAmount.subtract(amount);
	}
	
	public BigDecimal calculateAmount() {
		BigDecimal amount = BigDecimal.ZERO;
		
		if (getQuantity()!=null && getQuantity().compareTo(BigDecimal.ZERO)>0)
		{
			amount = getQuantity().multiply(getCompleteUnitPrice());
		}
		else
			throw new OperativeException("detail_have_incorrect_quantity");
		
		return amount.setScale(6, RoundingMode.HALF_UP);
	}
	
	public BigDecimal getCompleteUnitPrice() {
		BigDecimal amount = BigDecimal.ZERO;
		if (getUnitPrice()!=null && getUnitPrice().compareTo(BigDecimal.ZERO)>0)
		{
			amount = amount.add(getUnitPrice());
		}
		else
			throw new OperativeException("detail_have_incorrect_unit_price");
		
		if (getTaxSpecialConsumption()!=null && getTaxSpecialConsumption().compareTo(BigDecimal.ZERO)>0)
		{
			amount = amount.add(getTaxSpecialConsumption());
		}
		if (getTaxRedeemable()!=null && getTaxRedeemable().compareTo(BigDecimal.ZERO)>0)
		{
			amount = amount.add(getTaxRedeemable());
		}
		if (hasDiscount())
		{
			amount = amount.subtract(getDiscount());
		}
		return amount.setScale(6, RoundingMode.HALF_UP);
	}
		
	public BigDecimal calculateAmountForTax() {
		BigDecimal amount = BigDecimal.ZERO;
		
		if (getQuantity()!=null && getQuantity().compareTo(BigDecimal.ZERO)>0)
		{
			amount = getCompleteUnitPrice();
			if (getTaxRedeemable()!=null && getTaxRedeemable().compareTo(BigDecimal.ZERO)>0)
			{
				amount = amount.subtract(getTaxRedeemable());
			}
			amount = getQuantity().multiply(amount);
		}
		else
			throw new OperativeException("detail_have_incorrect_quantity");
		
		return amount.setScale(6, RoundingMode.HALF_UP);
	}
	
	public boolean hasDiscount()
	{
		if (getDiscount()!=null && getDiscount().compareTo(BigDecimal.ZERO)>0)
			return true;
		return false;
	}
	
	public boolean hasTax()
	{
		if (getTaxAmount()!=null && getTaxAmount().compareTo(BigDecimal.ZERO)>0)
			return true;
		return false;
	}
	
	public BigDecimal calculateFinalAmount() {
		BigDecimal finalAmountCalc = calculateAmount().setScale(2, RoundingMode.HALF_UP);
		BigDecimal aux = calculateAmountForTax().setScale(2, RoundingMode.HALF_UP);
		if(getTax()!=null)
		{
			if(getTax().getPercentage()!=null)
			{
				finalAmountCalc = finalAmountCalc.add(aux.multiply(tax.getPercentage()).divide(new BigDecimal(100)));
				finalAmountCalc = finalAmountCalc.setScale(2, RoundingMode.HALF_UP);
			}
		}
		finalAmountCalc = finalAmountCalc.subtract(taxAdjust);
		finalAmountCalc = finalAmountCalc.setScale(2, RoundingMode.HALF_UP);
		return finalAmountCalc;
	}

	public BigDecimal getTaxSpecialConsumption() {
		return taxSpecialConsumption;
	}

	public void setTaxSpecialConsumption(BigDecimal taxSpecialConsumption) {
		this.taxSpecialConsumption = taxSpecialConsumption;
	}

	public BigDecimal getTaxRedeemable() {
		return taxRedeemable;
	}

	public void setTaxRedeemable(BigDecimal taxRedeemable) {
		this.taxRedeemable = taxRedeemable;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	
}