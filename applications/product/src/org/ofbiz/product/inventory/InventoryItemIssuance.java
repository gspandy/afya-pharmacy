package org.ofbiz.product.inventory;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.LocalDispatcher;

/**
 * @author Nafis
 * Dec 16, 2011
 */
public class InventoryItemIssuance {

	public static String createManufacturingIssuanceAndInvItemDetails(HttpServletRequest request,
			HttpServletResponse response) throws GenericEntityException {
	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
	GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
	SimpleDateFormat formatter = new SimpleDateFormat(UtilDateTime.DATE_FORMAT);
	String inventoryItemId = request.getParameter("inventoryItemId");
	String issuanceDateStr = request.getParameter("issuanceDate");
	String issuanceQtyStr = request.getParameter("issuanceQty");
	String issuanceProductId = request.getParameter("issuanceProductId");
	String issuanceId = request.getParameter("issuanceId");
	String facilityId = request.getParameter("facilityId");
	String fixedAssetId = request.getParameter("fixedAssetId");
	String productId = request.getParameter("productId");
	String description = request.getParameter("description");
	String serialNumber = request.getParameter("serialNumber");

	if (UtilValidate.isEmpty(inventoryItemId)) {
		request.setAttribute("_ERROR_MESSAGE_", "Inventory Item Id cannot be empty.");
		return "error";
	}

	if (UtilValidate.isEmpty(issuanceQtyStr)) {
		request.setAttribute("_ERROR_MESSAGE_", "Please Provide a valid Quantity To Issue.");
		return "error";
	}
	BigDecimal issuanceQty = BigDecimal.ZERO;
	try {
		issuanceQty = new BigDecimal(issuanceQtyStr);
	} catch (NumberFormatException e) {
		request.setAttribute("_ERROR_MESSAGE_", "Quantity To Issue must be numeric.");
		return "error";
	}

	GenericValue invItemGv = delegator.findByPrimaryKey("InventoryItem",
			UtilMisc.toMap("inventoryItemId", inventoryItemId));
	if (UtilValidate.isEmpty(invItemGv)) {
		request.setAttribute("_ERROR_MESSAGE_", "Please select a valid Inventory Item Id.");
		return "error";
	}
	BigDecimal availableToPromiseTotal = invItemGv.getBigDecimal("availableToPromiseTotal");
	if (availableToPromiseTotal.compareTo(issuanceQty) < 0) {
		request.setAttribute("_ERROR_MESSAGE_", "Quantity To Issue cannot be greater than Available to Promise (ATP).");
		return "error";
	}
	if (UtilValidate.isEmpty(issuanceDateStr)) {
		request.setAttribute("_ERROR_MESSAGE_", "Please Provide a valid Issue Date.");
		return "error";
	}
	Timestamp issuanceDate = null;
	try {
		issuanceDate = new Timestamp(UtilDateTime.SIMPLE_DATE_FORMAT.parse(issuanceDateStr).getTime());
	} catch (ParseException e) {
		request.setAttribute("_ERROR_MESSAGE_", "Please Provide a valid Issue Date.");
		return "error";
	}

	if (UtilValidate.isEmpty(issuanceId)) issuanceId = delegator.getNextSeqId("ManufacturingIssuance");

	if (UtilValidate.isNotEmpty(productId)) {
		GenericValue productGv = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
		if ("FG_SERIALIZED".equals(productGv.getString("productTypeId"))) {
			List<GenericValue> list = delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productId", productId, "productIdTo", issuanceProductId));
			if (UtilValidate.isNotEmpty(list)) {
				if(UtilValidate.isEmpty(serialNumber)){
					request.setAttribute("_ERROR_MESSAGE_", "Serial Number cannot be empty.");
					return "error";
				}
				if (issuanceQty.compareTo(list.get(0).getBigDecimal("quantity")) != 0) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("inventoryItemId", inventoryItemId);
					map.put("issuanceDate", issuanceDate);
					map.put("issuanceQty", issuanceQty);
					map.put("issuanceProductId", issuanceProductId);
					map.put("issuanceId", issuanceId);
					map.put("facilityId", facilityId);
					map.put("fixedAssetId", fixedAssetId);
					map.put("productId", productId);
					map.put("description", description);
					map.put("serialNumber", serialNumber);
					request.getSession().setAttribute("issuanceMap", map);
					return "serialize";
				}
			}
		}

	}

	boolean beganTransaction = false;
	try {
		beganTransaction = TransactionUtil.begin();

		GenericValue gv = delegator.makeValidValue("ManufacturingIssuance", UtilMisc.toMap("issuanceId", issuanceId,
				"facilityId", facilityId, "fixedAssetId", fixedAssetId, "productId", productId, "issuanceDate",
				issuanceDate, "issuanceQty", issuanceQty, "description", description, "serialNumber", serialNumber,
				"issuanceProductId", issuanceProductId));

		delegator.createOrStore(gv);

		Map<String, Object> inputMap = UtilMisc.<String, Object> toMap("inventoryItemId", inventoryItemId,
				"quantityOnHandDiff", issuanceQty.negate(), "availableToPromiseDiff", issuanceQty.negate(),
				"issuanceId", issuanceId, "userLogin", userLogin);
		dispatcher.runSync("createInventoryItemDetail", inputMap);

		TransactionUtil.commit(beganTransaction);
	} catch (Exception e) {
		try {
			TransactionUtil.rollback(beganTransaction, "Error occurred while creating Manufacturing Issuance.", e);
		} catch (GenericTransactionException e1) {
			e1.printStackTrace();
		}
	}
	return "success";
	}
	
	public static String createSerializedMenufecIssuance(HttpServletRequest request,
			HttpServletResponse response) throws GenericEntityException {
	
	LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
	GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
	GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
	Map<String, Object> paramMap = (Map<String, Object>) request.getSession().getAttribute("issuanceMap");
	String inventoryItemId = (String) paramMap.get("inventoryItemId");
	String issuanceProductId = (String)paramMap.get("issuanceProductId");
	String issuanceId = (String)paramMap.get("issuanceId");
	String facilityId = (String)paramMap.get("facilityId");
	String fixedAssetId = (String)paramMap.get("fixedAssetId");
	String productId = (String)paramMap.get("productId");
	String description = (String)paramMap.get("description");
	String serialNumber = (String)paramMap.get("serialNumber");
	BigDecimal issuanceQty = (BigDecimal) paramMap.get("issuanceQty");
	Timestamp issuanceDate = (Timestamp) paramMap.get("issuanceDate");
	String serializedComments = (String)request.getParameter("serializedComments");
	
	if(UtilValidate.isEmpty(serializedComments)){
		request.setAttribute("_ERROR_MESSAGE_", "Description cannot be empty.");
		request.setAttribute("facilityId", facilityId);
		return "error";
	}

	boolean beganTransaction = false;
	try {
		beganTransaction = TransactionUtil.begin();

		GenericValue gv = delegator.makeValidValue("ManufacturingIssuance", UtilMisc.toMap("issuanceId", issuanceId,
				"facilityId", facilityId, "fixedAssetId", fixedAssetId, "productId", productId, "issuanceDate",
				issuanceDate, "issuanceQty", issuanceQty, "description", description, "serialNumber", serialNumber,
				"issuanceProductId", issuanceProductId,"serialzedComments",serializedComments));

		delegator.createOrStore(gv);

		Map<String, Object> inputMap = UtilMisc.<String, Object> toMap("inventoryItemId", inventoryItemId,
				"quantityOnHandDiff", issuanceQty.negate(), "availableToPromiseDiff", issuanceQty.negate(),
				"issuanceId", issuanceId, "userLogin", userLogin);
		dispatcher.runSync("createInventoryItemDetail", inputMap);

		TransactionUtil.commit(beganTransaction);
	} catch (Exception e) {
		try {
			TransactionUtil.rollback(beganTransaction, "Error occurred while creating Manufacturing Issuance.", e);
		} catch (GenericTransactionException e1) {
			e1.printStackTrace();
		}
	}
	request.setAttribute("facilityId", facilityId);
	request.getSession().removeAttribute("issuanceMap");
	return "success";
	}
	

}
