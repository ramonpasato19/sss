package com.powerfin.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the frecuency database table.
 * 
 */
@Entity
@Table(name="frecuency")
@NamedQuery(name="Frecuency.findAll", query="SELECT f FROM Frecuency f")
public class Frecuency implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="frecuency_id", unique=true, nullable=false)
	private Integer frecuencyId;

	@Column(nullable=false, length=100)
	private String name;

	@Column(name="number_days", nullable=false)
	private Integer numberDays;

	//bi-directional many-to-one association to AccountLoan
	@OneToMany(mappedBy="frecuency")
	private List<AccountLoan> accountLoans;

	public Frecuency() {
	}

	public Integer getFrecuencyId() {
		return this.frecuencyId;
	}

	public void setFrecuencyId(Integer frecuencyId) {
		this.frecuencyId = frecuencyId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getNumberDays() {
		return this.numberDays;
	}

	public void setNumberDays(Integer numberDays) {
		this.numberDays = numberDays;
	}

	public List<AccountLoan> getAccountLoans() {
		return this.accountLoans;
	}

	public void setAccountLoans(List<AccountLoan> accountLoans) {
		this.accountLoans = accountLoans;
	}

	public AccountLoan addAccountLoan(AccountLoan accountLoan) {
		getAccountLoans().add(accountLoan);
		accountLoan.setFrecuency(this);

		return accountLoan;
	}

	public AccountLoan removeAccountLoan(AccountLoan accountLoan) {
		getAccountLoans().remove(accountLoan);
		accountLoan.setFrecuency(null);

		return accountLoan;
	}

}