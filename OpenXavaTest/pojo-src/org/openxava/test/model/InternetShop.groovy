package org.openxava.test.model

import javax.persistence.*;

import org.openxava.annotations.*;

@Entity
class InternetShop extends Shop {
	
	@Column(length=60) @Stereotype("WEBURL")
	String url

}
