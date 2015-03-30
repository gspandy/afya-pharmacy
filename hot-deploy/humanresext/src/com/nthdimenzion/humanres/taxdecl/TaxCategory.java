package com.nthdimenzion.humanres.taxdecl;

import java.util.ArrayList;
import java.util.Collection;
import org.ofbiz.entity.GenericValue;

public class TaxCategory {

	private String categoryId;
	private String categoryName;
	private String description;
	private double maxAmount;
	public double getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(double maxAmount) {
		this.maxAmount = maxAmount;
	}

	private Collection<TaxItem> taxItems = new ArrayList<TaxItem>(0);

	public TaxCategory() {

	}

	public TaxCategory(GenericValue genericVal) {
		this.categoryId = genericVal.getString("categoryId");
		this.categoryName = genericVal.getString("categoryName");
		this.setDescription(genericVal.getString("description"));
		this.maxAmount=genericVal.getDouble("maxAmount");
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Collection<TaxItem> getTaxItems() {
		return taxItems;
	}

	public void setTaxItems(Collection<TaxItem> taxItems) {
		this.taxItems = taxItems;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof TaxCategory)) {
			return false;
		}

		TaxCategory category = (TaxCategory) obj;

		return category.getCategoryId().equals(getCategoryId());

	}

	@Override
	public int hashCode() {
		return getCategoryId().hashCode();
	}
}
