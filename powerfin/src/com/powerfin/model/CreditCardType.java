package com.powerfin.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the credit_card_type database table.
 * 
 */
@Entity
@Table(name="credit_card_type")
@NamedQuery(name="CreditCardType.findAll", query="SELECT c FROM CreditCardType c")
public class CreditCardType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="credit_card_type_id", unique=true, nullable=false, length=3)
	private String creditCardTypeId;

	@Column(nullable=false, length=100)
	private String name;

	//bi-directional many-to-one association to PersonCreditCard
	@OneToMany(mappedBy="creditCardType")
	private List<PersonCreditCard> personCreditCards;

	public CreditCardType() {
	}

	public String getCreditCardTypeId() {
		return this.creditCardTypeId;
	}

	public void setCreditCardTypeId(String creditCardTypeId) {
		this.creditCardTypeId = creditCardTypeId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PersonCreditCard> getPersonCreditCards() {
		return this.personCreditCards;
	}

	public void setPersonCreditCards(List<PersonCreditCard> personCreditCards) {
		this.personCreditCards = personCreditCards;
	}

	public PersonCreditCard addPersonCreditCard(PersonCreditCard personCreditCard) {
		getPersonCreditCards().add(personCreditCard);
		personCreditCard.setCreditCardType(this);

		return personCreditCard;
	}

	public PersonCreditCard removePersonCreditCard(PersonCreditCard personCreditCard) {
		getPersonCreditCards().remove(personCreditCard);
		personCreditCard.setCreditCardType(null);

		return personCreditCard;
	}

}