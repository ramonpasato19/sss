package com.powerfin.model;

import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;

@Entity
@Table(name="insurer")
@Views({
	@View(members="insurerId;"
			+ "person;mortgageRate;insuranceMortgageAmortization"),
	@View(name="Reference", members = "insurerId; name"),
	@View(name="RefQuotation", members = "insurerId, name")
})
@Tab(properties="insurerId, person.name, mortgageRate")
public class Insurer {

    @Id
    @Required
    @Column(name = "insurer_id", length = 3, nullable = true)
    private String insurerId;

    @Transient
	@DisplaySize(40)
	private String name;

    @Column(name="insurance_mortgage_amortization")
    @DisplaySize(10)
	private String insuranceMortgageAmortization;
    
    
    @Column(name = "minimum_to_ensure", length = 11, precision = 2, nullable = true)
    private java.math.BigDecimal minimumToEnsure;

    @Column(name="mortgage_rate", precision=13, scale=10)
	private BigDecimal mortgageRate;
    
    @ManyToOne
	@JoinColumn(name="person_id", nullable=false)
	@NoCreate
	@NoModify
	@ReferenceView("Reference")
	private Person person;
    
    public void setInsurerId(String aValue) {
    	insurerId = aValue;
    }

    public String getInsurerId() {
        return insurerId;
    }

    public void setMinimumToEnsure(java.math.BigDecimal aValue) {
    	minimumToEnsure = aValue;
    }

    public java.math.BigDecimal getMinimumToEnsure() {
        return minimumToEnsure;
    }

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public String getName() {
		return person.getName();
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getMortgageRate() {
		return mortgageRate;
	}

	public void setMortgageRate(BigDecimal mortgageRate) {
		this.mortgageRate = mortgageRate;
	}

	public String getInsuranceMortgageAmortization() {
		return insuranceMortgageAmortization;
	}

	public void setInsuranceMortgageAmortization(
			String insuranceMortgageAmortization) {
		this.insuranceMortgageAmortization = insuranceMortgageAmortization;
	}
    
    
}
