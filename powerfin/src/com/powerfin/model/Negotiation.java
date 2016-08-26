package com.powerfin.model;

import java.io.*;
import java.math.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;

import com.powerfin.helper.*;
import com.powerfin.model.superclass.*;

/**
 * The persistent class for the negotiation database table.
 * 
 */
@Entity
@Table(name="negotiation")
@Views({
@View(members="negotiationId, accountingDate;"
	+ "brokerPerson; "
	+ "debitCreditAccount; "
	+ "negotiationType;"
	+ "negotiationStatus;"
	+ "negotiationFiles"),
@View(name="Reference", members="negotiationId, accountingDate;"),
@View(name="NewNegotiation", members="generalInformation[#"
		+ "negotiationId, accountingDate; "
		+ "negotiationType, negotiationStatus; "
		+ "brokerSequence; "
		+ "brokerPerson; "
		+ "debitCreditAccount; "
		+ "];"
		+ "negotiationFiles"),
@View(name="ProcessPurchaseNegotiation", members="generalInformation[#"
		+ "negotiationId, accountingDate;"
		+ "negotiationType, negotiationStatus;"
		+ "brokerSequence;"
		+ "brokerPerson; "
		+ "debitCreditAccount; "
		+ "]"
		+ "results{#"
		+ "persons{negotiationOutputsPersons}"
		+ "loans{negotiationOutputsLoans}"
		+ "paytable{negotiationOutputsPaytable}"
		+ "disbursement{negotiationOutputsDisbursement}"
		//+ "assets{negotiationOutputsAssets}"
		+ "}"
		),
@View(name="ProcessSaleNegotiation", members="generalInformation[#"
		+ "negotiationId, accountingDate;"
		+ "negotiationType, negotiationStatus;"
		+ "brokerSequence;"
		+ "brokerPerson; "
		+ "debitCreditAccount; "
		+ "]"
		+ "results{#"
		+ "loans{negotiationOutputsSale}"
		+ "}"
		),
@View(name="ConsultPurchaseNegotiation", members="generalInformation[#"
		+ "negotiationId, accountingDate;"
		+ "negotiationType, negotiationStatus;"
		+ "brokerSequence;"
		+ "brokerPerson; "
		+ "debitCreditAccount; "
		+ "]"
		+ "purchaseInformation{#"
		+ "totalPurchaseCapital;"
		+ "totalPurchaseSpread;"
		+ "numberOfPurchaseLoans"
		+ "}"
		+ "negotiationFiles{negotiationFiles}"
		+ "loans{purchaseLoans}"
		),
@View(name="ConsultSaleNegotiation", members="generalInformation[#"
		+ "negotiationId, accountingDate;"
		+ "negotiationType, negotiationStatus;"
		+ "brokerSequence;"
		+ "brokerPerson; "
		+ "debitCreditAccount; "
		+ "]"
		+ "saleInformation{#"
		+ "totalSaleCapital;"
		+ "totalSaleSpread;"
		+ "numberOfSaleLoans"
		+ "}"
		+ "negotiationFiles{negotiationFiles}"
		+ "loans{saleLoans}"
		)
})
@Tabs({ 
	@Tab(properties="negotiationId, accountingDate, brokerPerson.name, negotiationType.name, negotiationStatus.name " ),
	@Tab(name="NewNegotiation", properties="negotiationId, accountingDate, brokerPerson.name, negotiationType.name, negotiationStatus.name"),
	@Tab(name="ProcessPurchaseNegotiation", properties="negotiationId, accountingDate, negotiationType.name, negotiationStatus.name", baseCondition = "${negotiationType.negotiationTypeId} = '001' and ${negotiationStatus.negotiationStatusId} = '001'"),
	@Tab(name="ProcessSaleNegotiation", properties="negotiationId, accountingDate, negotiationType.name, negotiationStatus.name", baseCondition = "${negotiationType.negotiationTypeId} = '002' and ${negotiationStatus.negotiationStatusId} = '001'"),
	@Tab(name="ConsultPurchaseNegotiation", properties="negotiationId, accountingDate, brokerPerson.name, brokerSequence, negotiationType.name, negotiationStatus.name", baseCondition = "${negotiationType.negotiationTypeId} = '001'"),
	@Tab(name="ConsultSaleNegotiation", properties="negotiationId, accountingDate, brokerPerson.name, brokerSequence, negotiationType.name, negotiationStatus.name", baseCondition = "${negotiationType.negotiationTypeId} = '002'")
})
public class Negotiation extends AuditEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="negotiation_id", unique=true, nullable=false)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "sequence_id")
    @SequenceGenerator(name="sequence_id", sequenceName = "negotiation_sequence", allocationSize = 1)
	@ReadOnly(notForViews="Reference")
	private Integer negotiationId;
	
	@Temporal(TemporalType.TIMESTAMP)
    @Column(name = "accounting_date", length=15, nullable = false)
	@ReadOnly
	private Date accountingDate;
	
	@ManyToOne
	@JoinColumn(name = "broker_person_id", nullable = false)
	@NoCreate
	@NoModify
	@Required
	@ReferenceView("simple")
	@ReadOnly(notForViews="DEFAULT, NewNegotiation")
	private Person brokerPerson;
	
	@Column(name="broker_sequence")
	@ReadOnly(notForViews="DEFAULT, NewNegotiation")
	private Integer brokerSequence;
	
	@ManyToOne
	@JoinColumn(name = "negotiation_type_id", nullable = false)
	@DescriptionsList(descriptionProperties = "name", order = "name")
	@NoCreate
	@NoModify
	@Required
	@ReadOnly(notForViews="DEFAULT, NewNegotiation")
	private NegotiationType negotiationType;
	
	@ManyToOne
	@JoinColumn(name = "negotiation_status_id", nullable = false)
	@DescriptionsList(descriptionProperties = "name", order = "name")
	@NoCreate
	@NoModify
	@Required
	@ReadOnly(notForViews="DEFAULT, NewNegotiation")
	private NegotiationStatus negotiationStatus;
	
	@ManyToOne
	@JoinColumn(name = "debit_credit_account_id", nullable = false)
	@NoCreate
	@NoModify
	@Required
	@ReadOnly(notForViews="DEFAULT, NewNegotiation")
	@ReferenceView("simple")
	private Account debitCreditAccount;
	
	@OneToMany(mappedBy="negotiation", cascade=CascadeType.ALL)
	@AsEmbedded
	@ListProperties("negotiationFileType.name, file, fileId")
	@ReadOnly(notForViews="DEFAULT, NewNegotiation")
	@CollectionView("ToNegotiation")
	private List<NegotiationFile> negotiationFiles;
	
	
	//ojo adicionar uno por cada tipo de archivo
	
	//bi-directional many-to-one association to NegotiationOutput
	@Transient
	@OneToMany
	@NoCreate
	@ReadOnly
	@OrderBy("negotiation.negotiationId")
	@ListProperties("fileName, lineNumber, result")
	@Condition("${negotiation.negotiationId} = ${this.negotiationId} AND ${fileType} = '001' " //001 - PERSONAS
			+ "AND ${file} IN "
			+ " (select nf.file from NegotiationFile nf "
			+ "where nf.negotiation.negotiationId=${this.negotiationId} "
			+ "and nf.negotiationFileType.negotiationFileTypeId='001')")
	private List<NegotiationOutput> negotiationOutputsPersons;
	
	//bi-directional many-to-one association to NegotiationOutput
	@Transient
	@OneToMany
	@NoCreate
	@ReadOnly
	@OrderBy("negotiation.negotiationId")
	@ListProperties("fileName, lineNumber, result")
	@Condition("${negotiation.negotiationId} = ${this.negotiationId} AND ${fileType} = '002' " //002 - PRESTAMOS
			+ "AND ${file} IN "
			+ " (select nf.file from NegotiationFile nf "
			+ "where nf.negotiation.negotiationId=${this.negotiationId} "
			+ "and nf.negotiationFileType.negotiationFileTypeId='002')")
	private List<NegotiationOutput> negotiationOutputsLoans;
	
	//bi-directional many-to-one association to NegotiationOutput
	@Transient
	@OneToMany
	@NoCreate
	@ReadOnly
	@OrderBy("negotiation.negotiationId")
	@ListProperties("fileName, lineNumber, result")
	@Condition("${negotiation.negotiationId} = ${this.negotiationId} AND ${fileType} = '003' " //003 - TABLA PAGOS
			+ "AND ${file} IN "
			+ " (select nf.file from NegotiationFile nf "
			+ "where nf.negotiation.negotiationId=${this.negotiationId} "
			+ "and nf.negotiationFileType.negotiationFileTypeId='003')")
	private List<NegotiationOutput> negotiationOutputsPaytable;
	
	//bi-directional many-to-one association to NegotiationOutput
	@Transient
	@OneToMany
	@NoCreate
	@ReadOnly
	@OrderBy("negotiation.negotiationId")
	@ListProperties("fileName, lineNumber, result")
	@Condition("${negotiation.negotiationId} = ${this.negotiationId} AND ${fileType} = '004' " //004 - DESEMBOLSO
			+ "AND ${file} IN "
			+ " (select nf.file from NegotiationFile nf "
			+ "where nf.negotiation.negotiationId=${this.negotiationId} "
			+ "and nf.negotiationFileType.negotiationFileTypeId='004')")
	private List<NegotiationOutput> negotiationOutputsDisbursement;
	
	
	//bi-directional many-to-one association to NegotiationOutput
	@Transient
	@OneToMany
	@NoCreate
	@ReadOnly
	@OrderBy("negotiation.negotiationId")
	@ListProperties("fileName, lineNumber, result")
	@Condition("${negotiation.negotiationId} = ${this.negotiationId} AND ${fileType} = '101' " //101 - VENTA
			+ "AND ${file} IN "
			+ " (select nf.file from NegotiationFile nf "
			+ "where nf.negotiation.negotiationId=${this.negotiationId} "
			+ "and nf.negotiationFileType.negotiationFileTypeId='101')")
	private List<NegotiationOutput> negotiationOutputsSale;
	
	//bi-directional many-to-one association to NegotiationOutput
	@Transient
	@OneToMany
	@NoCreate
	@ReadOnly
	@OrderBy("negotiation.negotiationId")
	@ListProperties("fileName, lineNumber, result")
	@Condition("${negotiation.negotiationId} = ${this.negotiationId} AND ${fileType} = '005' ")//005 - ACTIVOS
	private List<NegotiationOutput> negotiationOutputsAssets;
	
	@Transient
	@ReadOnly
	@ListProperties("accountId, account.name, purchaseAmount, purchaseSpread, purchaseRate")
	@Condition("${purchaseNegotiation.negotiationId} = ${this.negotiationId}")
	private List<AccountPortfolio> purchaseLoans;
	
	@Transient
	@ReadOnly
	@ListProperties("accountId, account.name, saleAmount, saleSpread, saleRate")
	@Condition("${saleNegotiation.negotiationId} = ${this.negotiationId}")
	private List<AccountPortfolio> saleLoans;
	
	public Negotiation(){
		
	}

	public Integer getNegotiationId() {
		return negotiationId;
	}

	public void setNegotiationId(Integer negotiationId) {
		this.negotiationId = negotiationId;
	}

	public Date getAccountingDate() {
		return accountingDate;
	}

	public void setAccountingDate(Date accountingDate) {
		this.accountingDate = accountingDate;
	}

	public Person getBrokerPerson() {
		return brokerPerson;
	}

	public void setBrokerPerson(Person brokerPerson) {
		this.brokerPerson = brokerPerson;
	}

	public NegotiationType getNegotiationType() {
		return negotiationType;
	}

	public void setNegotiationType(NegotiationType negotiationType) {
		this.negotiationType = negotiationType;
	}

	public NegotiationStatus getNegotiationStatus() {
		return negotiationStatus;
	}

	public void setNegotiationStatus(NegotiationStatus negotiationStatus) {
		this.negotiationStatus = negotiationStatus;
	}

	public List<NegotiationFile> getNegotiationFiles() {
		return negotiationFiles;
	}

	public void setNegotiationFiles(List<NegotiationFile> negotiationFiles) {
		this.negotiationFiles = negotiationFiles;
	}
	
	public List<NegotiationOutput> getNegotiationOutputsPersons() {
		return negotiationOutputsPersons;
	}

	public void setNegotiationOutputsPersons(List<NegotiationOutput> negotiationOutputsPersons) {
		this.negotiationOutputsPersons = negotiationOutputsPersons;
	}

	public List<NegotiationOutput> getNegotiationOutputsLoans() {
		return negotiationOutputsLoans;
	}

	public void setNegotiationOutputsLoans(List<NegotiationOutput> negotiationOutputsLoans) {
		this.negotiationOutputsLoans = negotiationOutputsLoans;
	}

	public List<NegotiationOutput> getNegotiationOutputsPaytable() {
		return negotiationOutputsPaytable;
	}

	public void setNegotiationOutputsPaytable(List<NegotiationOutput> negotiationOutputsPaytable) {
		this.negotiationOutputsPaytable = negotiationOutputsPaytable;
	}

	public List<NegotiationOutput> getNegotiationOutputsAssets() {
		return negotiationOutputsAssets;
	}

	public void setNegotiationOutputsAssets(List<NegotiationOutput> negotiationOutputsAssets) {
		this.negotiationOutputsAssets = negotiationOutputsAssets;
	}

	public Integer getBrokerSequence() {
		return brokerSequence;
	}

	public void setBrokerSequence(Integer brokerSequence) {
		this.brokerSequence = brokerSequence;
	}

	public Account getDebitCreditAccount() {
		return debitCreditAccount;
	}

	public void setDebitCreditAccount(Account debitCreditAccount) {
		this.debitCreditAccount = debitCreditAccount;
	}

	public List<NegotiationOutput> getNegotiationOutputsSale() {
		return negotiationOutputsSale;
	}

	public void setNegotiationOutputsSale(List<NegotiationOutput> negotiationOutputsSale) {
		this.negotiationOutputsSale = negotiationOutputsSale;
	}
	
	public BigDecimal getTotalPurchaseCapital()
	{
		String query = "SELECT sum(COALESCE(ap.purchaseAmount, 0)) FROM AccountPortfolio ap "
				+ "WHERE ap.purchaseNegotiation.negotiationId = :negotiationId ";
		return (BigDecimal) XPersistence.getManager()
			.createQuery(query)
			.setParameter("negotiationId", this.negotiationId)
			.getSingleResult();
	}
	
	public BigDecimal getTotalPurchaseSpread()
	{
		String query = "SELECT sum(COALESCE(ap.purchaseSpread, 0)) FROM AccountPortfolio ap "
				+ "WHERE ap.purchaseNegotiation.negotiationId = :negotiationId ";
		return (BigDecimal) XPersistence.getManager()
			.createQuery(query)
			.setParameter("negotiationId", this.negotiationId)
			.getSingleResult();
	}
	
	public BigDecimal getTotalSaleCapital()
	{
		String query = "SELECT sum(COALESCE(ap.saleAmount, 0)) FROM AccountPortfolio ap "
				+ "WHERE ap.saleNegotiation.negotiationId = :negotiationId ";
		return (BigDecimal) XPersistence.getManager()
			.createQuery(query)
			.setParameter("negotiationId", this.negotiationId)
			.getSingleResult();
	}
	
	public BigDecimal getTotalSaleSpread()
	{
		String query = "SELECT sum(COALESCE(ap.saleSpread, 0)) FROM AccountPortfolio ap "
				+ "WHERE ap.saleNegotiation.negotiationId = :negotiationId ";
		return (BigDecimal) XPersistence.getManager()
			.createQuery(query)
			.setParameter("negotiationId", this.negotiationId)
			.getSingleResult();
	}
	
	public Long getNumberOfPurchaseLoans()
	{
		String query = "SELECT count(ap) FROM AccountPortfolio ap "
				+ "WHERE ap.purchaseNegotiation.negotiationId = :negotiationId ";
		return (Long) XPersistence.getManager()
			.createQuery(query)
			.setParameter("negotiationId", this.negotiationId)
			.getSingleResult();
	}
	
	public Long getNumberOfSaleLoans()
	{
		String query = "SELECT count(ap) FROM AccountPortfolio ap "
				+ "WHERE ap.saleNegotiation.negotiationId = :negotiationId ";
		return (Long) XPersistence.getManager()
			.createQuery(query)
			.setParameter("negotiationId", this.negotiationId)
			.getSingleResult();
	}

	public List<AccountPortfolio> getPurchaseLoans() {
		return purchaseLoans;
	}

	public void setPurchaseLoans(List<AccountPortfolio> purchaseLoans) {
		this.purchaseLoans = purchaseLoans;
	}

	public List<AccountPortfolio> getSaleLoans() {
		return saleLoans;
	}

	public void setSaleLoans(List<AccountPortfolio> saleLoans) {
		this.saleLoans = saleLoans;
	}

	@PreCreate
	public void onCreate()
	{
		accountingDate = CompanyHelper.getCurrentAccountingDate();
	}

	public List<NegotiationOutput> getNegotiationOutputsDisbursement() {
		return negotiationOutputsDisbursement;
	}

	public void setNegotiationOutputsDisbursement(List<NegotiationOutput> negotiationOutputsDisbursement) {
		this.negotiationOutputsDisbursement = negotiationOutputsDisbursement;
	}
	
}
