package com.nthdimenzion.humanres.taxdecl;

import org.ofbiz.entity.GenericValue;

public class TaxItemValidator {

	public static boolean validate(double num1, GenericValue itemGV) throws ValidationException, ExceedsMaximumException {
		double minAmount = itemGV.getDouble("minAmount");
		double maxAmount = itemGV.getDouble("maxAmount");
		if (num1 < minAmount)
			throw new ValidationException();

		if (num1 > maxAmount)
			throw new ExceedsMaximumException();
		return true;
	}
}
