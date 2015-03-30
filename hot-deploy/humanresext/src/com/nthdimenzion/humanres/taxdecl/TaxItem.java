package com.nthdimenzion.humanres.taxdecl;

import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

public class TaxItem {

	private String itemName;
	private String itemId;
	private String itemTypeId;
	private String categoryId;
	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	private double numValue;
	private double acceptedValue;
	private String stringValue;
	private TaxCategory category;
	private double maxAmount;
	private String description;

	public TaxItem() {}

	public TaxItem(GenericValue val) throws GenericEntityException {
		GenericValue taxItemVal;
		if ("EmplTaxDecl".equals(val.getEntityName())) {
			taxItemVal = val.getRelatedOne("TaxItem");
		} else {
			taxItemVal = val;
		}

		this.itemName = taxItemVal.getString("itemName");
		this.itemId = taxItemVal.getString("itemId");
		this.itemTypeId = taxItemVal.getString("itemTypeId");
		this.maxAmount = taxItemVal.getDouble("maxAmount");
		this.setDescription(taxItemVal.getString("description"));
		this.categoryId=taxItemVal.getString("categoryId");
		
		category = new TaxCategory(val.getRelatedOne("TaxCategory"));

		if ("EmplTaxDecl".equals(val.getEntityName())) {
			if ("NUMERIC".equals(this.itemTypeId)) {
				if(val.getString("numValue") == null){
					this.numValue = 0d;
				}else{
				this.numValue = val.getDouble("numValue");
				}
				
			} else {
				this.stringValue = val.getString("stringValue");
			}
		}
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemId() {
		return itemId;
	}

	public void setTaxItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getItemTypeId() {
		return itemTypeId;
	}

	public void setItemTypeId(String itemTypeId) {
		this.itemTypeId = itemTypeId;
	}

	public double getNumValue() {
		return numValue;
	}

	public void setNumValue(double numValue) {
		this.numValue = numValue;
	}

	public double getAcceptedValue() {
		return acceptedValue;
	}

	public void setAcceptedValue(double acceptedValue) {
		this.acceptedValue = acceptedValue;
	}
	
	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public TaxCategory getCategory() {
		return category;
	}

	public void setCategory(TaxCategory category) {
		this.category = category;
	}

	
	public double getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(double maxAmount) {
		this.maxAmount = maxAmount;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof TaxItem)) {
			return false;
		}

		TaxItem item = (TaxItem) obj;

		return item.getItemId().equals(getItemId());

	}

	@Override
	public int hashCode() {
		return getItemId().hashCode();
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
