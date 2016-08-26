package org.openxava.test.calculators

import org.openxava.util.*;
import org.openxava.calculators.*;
import javax.persistence.*;

class MixtureDescriptionCalculator implements ICalculator {
	
	
	String colorName1	
	
	String colorName2
	
	String description
	
	Object calculate(){
		String one = colorName1.trim()
		one = Strings.repeat(10 - one.length(), "-") + colorName1.trim()
		String two = colorName2.trim()
		two = Strings.repeat(10 - two.length(), "-") + colorName2.trim()
		
		return one + "&" + two + ":" + description
	}
	
}
