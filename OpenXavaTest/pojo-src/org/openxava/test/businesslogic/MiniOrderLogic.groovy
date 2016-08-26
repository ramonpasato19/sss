package org.openxava.test.businesslogic

import com.autobizlogic.abl.annotations.*;

class MiniOrderLogic {	

	
	@Formula("productPrice * qtyOrdered")
	public void deriveAmount() { }
	
	
}
