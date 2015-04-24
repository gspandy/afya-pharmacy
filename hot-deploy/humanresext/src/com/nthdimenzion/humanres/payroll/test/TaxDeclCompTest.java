package com.nthdimenzion.humanres.payroll.test;

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import com.nthdimenzion.humanres.payroll.calc.TaxDeclarationCalculator;

public class TaxDeclCompTest extends TestCase {

	protected GenericValue userLogin = null;
	GenericDelegator delegator = null;

	public TaxDeclCompTest(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
	}

	@Override
	protected void tearDown() throws Exception {
	}

	public void testTaxDelCompute() {
		TaxDeclarationCalculator calc = new TaxDeclarationCalculator();
		BigDecimal result = null;
		try {
			result = calc.calculate(userLogin);
		} catch (GenericEntityException ge) {
			ge.printStackTrace();
		}

		System.out.println("TAX DECLARATION " + result);
	}

}
