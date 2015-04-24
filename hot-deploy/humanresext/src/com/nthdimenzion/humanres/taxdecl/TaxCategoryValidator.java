package com.nthdimenzion.humanres.taxdecl;

import org.ofbiz.entity.GenericValue;

public class TaxCategoryValidator {

	public static boolean validate(TaxCategory cat, GenericValue catGV) throws ValidationException {
		double minAmount = catGV.getDouble("minAmount");
		double maxAmount = catGV.getDouble("maxAmount");

		double totalVal = 0.0d;
		System.out.println("*********** TOTAL ITEM for " + cat.getTaxItems().size());
		for (TaxItem taxItem : cat.getTaxItems()) {
			totalVal += taxItem.getNumValue();
		}
		System.out.println("*********** TOTAL VALUE for " + cat.getCategoryId() + "-----------" + totalVal);
		if (totalVal < minAmount || totalVal > maxAmount)
			throw new ValidationException();

		return true;
	}

}
