package com.ndz.util;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.webapp.control.Infrastructure;

/**
 * @author sandeep
 */
public class ProductHelper {

	private static GenericDelegator delegator = Infrastructure.getDelegator();

	public static GenericValue getDefaultUomFor(String productId) throws GenericEntityException {
	GenericValue list = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
	return (GenericValue) (UtilValidate.isEmpty(list) ? null : list);
	}

	public static String getDefaultUomIdFor(String productId) throws GenericEntityException {
	GenericValue uom = getDefaultUomFor(productId);
	return uom == null ? null : uom.getString("uomId");
	}

	public static Double getQohFor(String productId) throws GenericEntityException {
	Double qoh = 0.0;
	final String qohField = "quantityOnHandTotal";
	EntityCondition condition = EntityCondition.makeCondition("productId", "10000");
	Set<String> fields = new HashSet<String>();
	fields.add(qohField);
	List<GenericValue> inventoryItems = delegator.findList("InventoryItem", condition, fields, null, null, false);

	if (inventoryItems == null) return qoh;
	for (GenericValue inventoryItem : inventoryItems)
		qoh += inventoryItem.getDouble(qohField);
	GenericValue list = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
	return qoh;
	}

	public static Double getConversionFactor(String productTransferId) throws GenericEntityException {
	GenericValue productTransferRequest = delegator.findOne("ProductTransfer", true, "productTransferId",
			productTransferId);
	return getConversionFactor(productTransferRequest);
	}

	public static Double getConversionFactor(GenericValue productTransferRequest) throws GenericEntityException {
	return getConversionFactor(productTransferRequest.getString("fromProductId"),
			productTransferRequest.getString("toProductId"));
	}

	public static Double getConversionFactor(String fromProductId, String toProductId) throws GenericEntityException {
	GenericValue fromUom = getDefaultUomFor(fromProductId);
	GenericValue toUom = getDefaultUomFor(toProductId);
	if (fromUom == null || toUom == null) return null;
	GenericValue uomConversion = delegator.findOne("UomConversion",
			UtilMisc.toMap("uomId", fromUom.get("quantityUomId"), "uomIdTo", toUom.get("quantityUomId")), true);
	String conversionFactor_String = uomConversion.getString("conversionFactor");
	Double conversionFactor_Double = Double.parseDouble(conversionFactor_String);
	return uomConversion == null ? null : conversionFactor_Double;
	}

	public static BigDecimal getQuantityDoneForTransferRequest(GenericValue transferRequest)
			throws GenericEntityException {
	return getQuantityDoneForTransferRequest(transferRequest.getString("productTransferId"));
	}

	public static BigDecimal getQuantityDoneForTransferRequest(String productTransferId) throws GenericEntityException {
	BigDecimal quantityDone = BigDecimal.ZERO;
	EntityCondition condition = EntityCondition.makeCondition("productTransferId", productTransferId);
	List<GenericValue> txns = delegator.findList("ProductTransferTxn", condition, null, null, null, false);
	if (txns == null) return quantityDone;
	for (GenericValue txn : txns)
		quantityDone = quantityDone.add(txn.getBigDecimal("quantityDone"));
	return quantityDone;
	}

	public static BigDecimal getQuantityDoneForProductBundle(String productBundleId, Delegator myDelegator)
			throws GenericEntityException {
	BigDecimal quantityDone = BigDecimal.ZERO;
	EntityConditionList<EntityExpr> condition = EntityCondition.makeCondition(UtilMisc.toList(
			EntityCondition.makeCondition("productBundleId", productBundleId),
			EntityCondition.makeCondition("statusId", "STATUS_COMPLETED")));

	List<GenericValue> txns = myDelegator.findList("ProductBundleTxn", condition, null, null, null, false);
	if (txns == null) return quantityDone;
	for (GenericValue txn : txns)
		quantityDone = quantityDone.add(txn.getBigDecimal("quantityToBundle"));
	return quantityDone;
	}

	public static Double getQohFor(String productId, Delegator myDelegator) throws GenericEntityException {
	Double qoh = 0.0;
	final String qohField = "quantityOnHandTotal";
	EntityCondition condition = EntityCondition.makeCondition("productId", productId);
	Set<String> fields = new HashSet<String>();
	fields.add(qohField);
	List<GenericValue> inventoryItems = myDelegator.findList("InventoryItem", condition, fields, null, null, false);

	if (inventoryItems == null) return qoh;
	for (GenericValue inventoryItem : inventoryItems)
		qoh += inventoryItem.getDouble(qohField);
	GenericValue list = myDelegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
	return qoh;
	}
	
	public static Double getQohFor(String productId,String facilityId, Delegator myDelegator) throws GenericEntityException {
		Double qoh = 0.0;
		final String qohField = "quantityOnHandTotal";
		EntityCondition condition1 = EntityCondition.makeCondition("productId", productId);
		EntityCondition condition2 = EntityCondition.makeCondition("facilityId", facilityId);
		EntityCondition condition  = EntityCondition.makeCondition(condition1, condition2);
		Set<String> fields = new HashSet<String>();
		fields.add(qohField);
		List<GenericValue> inventoryItems = myDelegator.findList("InventoryItem", condition, fields, null, null, false);

		if (inventoryItems == null) return qoh;
		for (GenericValue inventoryItem : inventoryItems)
			qoh += inventoryItem.getDouble(qohField);
		GenericValue list = myDelegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
		return qoh;
		}

	public static Double getConversionFactor(String fromProductId, String toProductId, Delegator myDelegator)
			throws GenericEntityException {
	GenericValue fromUom = getDefaultUomFor(fromProductId, myDelegator);
	GenericValue toUom = getDefaultUomFor(toProductId, myDelegator);
	if (fromUom == null || toUom == null) return null;
	GenericValue uomConversion = myDelegator.findOne("UomConversion",
			UtilMisc.toMap("uomId", fromUom.get("quantityUomId"), "uomIdTo", toUom.get("quantityUomId")), true);
	String conversionFactor_String = uomConversion.getString("conversionFactor");
	Double conversionFactor_Double = Double.parseDouble(conversionFactor_String);
	return uomConversion == null ? null : conversionFactor_Double;
	}

	public static GenericValue getDefaultUomFor(String productId, Delegator myDelegator) throws GenericEntityException {
	GenericValue list = myDelegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
	return (GenericValue) (UtilValidate.isEmpty(list) ? null : list);
	}

	public static Double getConversionFactor(GenericValue productTransferRequest, Delegator myDelegator)
			throws GenericEntityException {
	return getConversionFactor(productTransferRequest.getString("fromProductId"),
			productTransferRequest.getString("toProductId"), myDelegator);
	}

	public static GenericValue getProductUomForProduct(String productId, Delegator myDelegator) {
	List<GenericValue> productUomList = null;
	GenericValue productUom = null;
	try {
		productUomList = myDelegator.findByAnd("ProductUom", UtilMisc.toMap("productId", productId));
		productUom = productUomList.get(0);
	} catch (GenericEntityException e) {
		e.printStackTrace();
	}
	return productUom;
	}

	public static Double getAtpFor(String productId, Delegator myDelegator) throws GenericEntityException {
	Double atp = 0.0;
	final String atpField = "availableToPromiseTotal";
	EntityCondition condition = EntityCondition.makeCondition("productId", productId);
	Set<String> fields = new HashSet<String>();
	fields.add(atpField);
	List<GenericValue> inventoryItems = myDelegator.findList("InventoryItem", condition, fields, null, null, false);

	if (inventoryItems == null) return atp;
	for (GenericValue inventoryItem : inventoryItems)
		atp += inventoryItem.getDouble(atpField);

	return atp;
	}
	
	public static Double getAtpFor(String productId,String facilityId, Delegator myDelegator) throws GenericEntityException {
		Double atp = 0.0;
		final String atpField = "availableToPromiseTotal";
		EntityCondition condition1 = EntityCondition.makeCondition("productId", productId);
		EntityCondition condition2 = EntityCondition.makeCondition("facilityId", facilityId);
		EntityCondition condition  = EntityCondition.makeCondition(condition1, condition2);
		Set<String> fields = new HashSet<String>();
		fields.add(atpField);
		List<GenericValue> inventoryItems = myDelegator.findList("InventoryItem", condition, fields, null, null, false);

		if (inventoryItems == null) return atp;
		for (GenericValue inventoryItem : inventoryItems)
			atp += inventoryItem.getDouble(atpField);

		return atp;
		}
	
}