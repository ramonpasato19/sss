package com.powerfin.calculators;

import org.openxava.calculators.*;

import com.powerfin.helper.*;

public class CurrentBranchCalculator implements ICalculator {

	private static final long serialVersionUID = 1L;

	@Override
	public Object calculate() throws Exception {
		return UserHelper.getRegisteredBranch();
	}
	

}
