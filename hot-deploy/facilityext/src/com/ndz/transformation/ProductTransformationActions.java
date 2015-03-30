package com.ndz.transformation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.control.Infrastructure;

import com.ndz.FacilityUtil;
import com.ndz.WhmConstants;
import com.ndz.util.ProductHelper;

/**
 * @author sandeep
 */
public class ProductTransformationActions {

	// private GRNService grnService;

	private static GenericDelegator delegator = Infrastructure.getDelegator();

	/*
	 * public void setGrnService(GRNService grnService) { this.grnService =
	 * grnService; }
	 */

	public static String loadQoh(HttpServletRequest request,
			HttpServletResponse response) throws GenericEntityException {
		Delegator delegator1 = (Delegator) request.getAttribute("delegator");
		//String productId = request.getQueryString();
		String productId = request.getParameter("productId");
		String facilityId = request.getParameter("facilityId");
		Double qoh = ProductHelper.getQohFor(productId, facilityId, delegator1);
		request.setAttribute("qoh", qoh);
		return "success";
	}

	public static String loadConversionFactor(HttpServletRequest request,
			HttpServletResponse response) throws GenericEntityException {
		Delegator delegator1 = (Delegator) request.getAttribute("delegator");
		String fromProductId = request.getParameter("fromProductId");
		String toProductId = request.getParameter("toProductId");
		Double conversionFactor = ProductHelper.getConversionFactor(
				fromProductId, toProductId, delegator1);
		request.setAttribute("conversionFactor", conversionFactor);
		return "success";
	}

	public static String saveTransformationRequest(HttpServletRequest request,
			HttpServletResponse response) throws GenericEntityException {
		Delegator delegator1 = (Delegator) request.getAttribute("delegator");
		BigDecimal quantityRequired = new BigDecimal(request.getParameter("quantityRequired"));
		GenericValue transferRequest = delegator1.makeValue("ProductTransfer");
		transferRequest.setNextSeqId();
		transferRequest.set("fromProductId", request.getParameter("fromProductId"));
		transferRequest.set("toProductId", request.getParameter("toProductId"));
		transferRequest.set("status", "Requested");
		transferRequest.set("quantityRequired", quantityRequired);
		if(UtilValidate.isNotEmpty(request.getParameter("scheduledOn")))
			transferRequest.set("scheduledOn", Timestamp.valueOf(request.getParameter("scheduledOn")));
		if(UtilValidate.isNotEmpty(request.getParameter("estimatedCompletion")))
			transferRequest.set("estimatedCompletion", Timestamp.valueOf(request.getParameter("estimatedCompletion")));
		transferRequest.set("createdStamp", UtilDateTime.nowTimestamp());
		transferRequest.set("pendingQuantity", quantityRequired);
		transferRequest.create();
		request.setAttribute("productTransferId", transferRequest.getString("productTransferId"));
		return "success";
	}

	public static String saveTransformationTxn(HttpServletRequest request,HttpServletResponse response) throws GenericEntityException, GenericServiceException {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		TransactionUtil.begin();
		GenericValue txnRequest = delegator.makeValue("ProductTransferTxn");
		String productTransferId = request.getParameter("productTransferId");
		GenericValue productTransferRequest = findOne(delegator,"ProductTransfer", "productTransferId", productTransferId);
		String fromInventoryItemId = request.getParameter("fromInventoryItemId");
		GenericValue invItemGv = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId",fromInventoryItemId));
		String toProductId = productTransferRequest.getString("toProductId"); //request.getParameter("toProductId");
		GenericValue toProductGv = delegator.findOne("Product", false, UtilMisc.toMap("productId",toProductId));
		Double conversionFactor = ProductHelper.getConversionFactor(productTransferRequest,delegator);
		if(UtilValidate.isEmpty(invItemGv.getBigDecimal("unitCost"))){
			request.setAttribute("_ERROR_MESSAGE_","Please provide product inventory unit price");
			return "error";
		}
		if(UtilValidate.isEmpty(conversionFactor)){
			request.setAttribute("_ERROR_MESSAGE_","Conversion factor can not be empty");
			return "error";
		}
		BigDecimal toUnitPrice = invItemGv.getBigDecimal("unitCost").divide(new BigDecimal(conversionFactor), 2, RoundingMode.HALF_EVEN);
		BigDecimal quantityDone = productTransferRequest.getBigDecimal("quantityRequired"); //BigDecimal quantityDone = new BigDecimal(request.getParameter("quantityDone"));
		Timestamp expireDate = getExpireDate(fromInventoryItemId, delegator);
		txnRequest.setNextSeqId();
		txnRequest.set("productTransferId", productTransferId);
		txnRequest.set("quantityDone", quantityDone);
		txnRequest.set("fromLocationId", request.getParameter("fromLocationId"));
		txnRequest.set("fromInventoryItemId", fromInventoryItemId);
		txnRequest.set("toLocationId", request.getParameter("toLocationId"));
		txnRequest.set("txnStatus", "Pending");
		txnRequest.set("createdStamp", UtilDateTime.nowTimestamp());
		String inventoryItemId = null;
		Map<String, Object> serviceContext = UtilMisc.<String, Object>toMap("userLogin", userLogin,"productId", toProductId,
				"inventoryItemTypeId", invItemGv.get("inventoryItemTypeId"));
		serviceContext.put("facilityId", request.getParameter("facilityId"));
		serviceContext.put("uomId", toProductGv.getString("quantityUomId"));
		serviceContext.put("locationSeqId", request.getParameter("toLocationId"));
		serviceContext.put("unitCost", toUnitPrice);
		Map<String, Object> resultService = dispatcher.runSync("createInventoryItem", serviceContext);
		inventoryItemId = (String) resultService.get("inventoryItemId");
		
		String toInvItemId = inventoryItemId;
		String txnId = txnRequest.get("txnId").toString();
		boolean reduced = reduceInventoryItem(request, fromInventoryItemId,quantityDone.negate(), txnId);
		boolean increased = reduceInventoryItem(request, toInvItemId,quantityDone.multiply(new BigDecimal(conversionFactor)), txnId);

		if (!reduced || !increased)
			return "error";

		txnRequest.set("toInventoryItemId", toInvItemId);
		txnRequest.create();
		updateTransferRequestStatus(productTransferId, delegator, quantityDone);
		TransactionUtil.commit();
		request.setAttribute("productTransferId", productTransferId);
		// request.setAttribute("grnId", result.get("grnId"));
		// return "Y".equalsIgnoreCase(palletize) ? "palletization" : "putaway";
		return "success";
	}

	private static boolean reduceInventoryItem(HttpServletRequest request,
			String inventoryItemId, BigDecimal quantity, String txnId)
			throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");

		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		try {
			Map<String,Object> createNewInventoryDetailMap = 
					UtilMisc.toMap("userLogin", userLogin, "inventoryItemId",inventoryItemId, "quantityOnHandDiff",BigDecimal.ZERO, "availableToPromiseDiff", quantity);
			LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
			Map<String, Object> resultNew = dispatcher.runSync("createInventoryItemDetail", createNewInventoryDetailMap);
			if (ServiceUtil.isError(resultNew)) {
				throw new GenericServiceException();
			}
		} catch (GenericServiceException e1) {
			request.setAttribute("_ERROR_MESSAGE_","Problem in Reserving Inventory Item");
			TransactionUtil.rollback();
			return false;
		}

		return true;
	}

	private static void updateTransferRequestStatus(String productTransferId,
			Delegator delegator1, BigDecimal quantityDone)
			throws GenericEntityException {
		GenericValue transferRequest = delegator1.findOne("ProductTransfer",
				false, "productTransferId", productTransferId);
		BigDecimal actualQuantityDone = quantityDone;
		BigDecimal quantityRequired = new BigDecimal(transferRequest
				.getString("quantityRequired"));
		Double zero = 0.0;
		BigDecimal pending = quantityRequired.subtract(actualQuantityDone);
		quantityRequired = pending;
		//String status = "Scheduled";
		if (pending.doubleValue() == zero) {
			//status = "Completed";
			transferRequest
					.set("actualCompletion", UtilDateTime.nowTimestamp());
		}
		//transferRequest.set("status", status);
		transferRequest.set("pendingQuantity", pending);
		transferRequest.set("quantityRequired", quantityRequired);
		transferRequest.store();
	}

	public static String addTransformationLocation(HttpServletRequest request,
			HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");

		GenericValue location = delegator.makeValue("ProductTransferLocation");
		location.setNextSeqId();
		String productTransferId = request.getParameter("productTransferId");
		location.set("productTransferId", productTransferId);
		location.set("type", request.getParameter("type"));
		location
				.set("inventoryItemId", request.getParameter("inventoryItemId"));
		location.set("locationId", request.getParameter("locationId"));
		location.set("createdStamp", UtilDateTime.nowTimestamp());
		location.set("facilityId", request.getParameter("facilityId"));
		location.create();
		request.setAttribute("productTransferId", productTransferId);
		request.setAttribute("facilityId", request.getParameter("facilityId"));
		return "success";
	}

	public static String removeProductBundleTxnDetail(
			HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException, GenericServiceException {
		String productBundleTxnId = request.getParameter("productBundleTxnId");
		String baseSkuId = request.getParameter("baseSkuId");
		GenericValue productBundleTxnDetail = delegator.findOne(
				"ProductBundleTxnDetail", false, "productBundleTxnId",
				productBundleTxnId, "baseSkuId", baseSkuId);
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session
				.getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request
				.getAttribute("dispatcher");
		TransactionUtil.begin();
		/**
		 * Create Inventory Item Detail
		 **/
		try {
			Map<String, ? extends Object> createNewInventoryDetailMap = UtilMisc
					.toMap(
							"userLogin",
							userLogin,
							"inventoryItemId",
							productBundleTxnDetail.getString("inventoryItemId"),
							"quantityOnHandDiff", BigDecimal.ZERO,
							"availableToPromiseDiff", productBundleTxnDetail
									.getBigDecimal("quantityToUse")
					// "productBundleTxnId", productBundleTxnId
					);
			Map<String, Object> resultNew = dispatcher.runSync(
					"createInventoryItemDetail", createNewInventoryDetailMap);
			if (ServiceUtil.isError(resultNew)) {
				throw new GenericServiceException();
			}
		} catch (GenericServiceException e1) {
			request.setAttribute("_ERROR_MESSAGE_",
					"Problem in Unreserving Inventory Item ");
			TransactionUtil.rollback();
			return "error";
		}
		/**
		 * Delete from prodbundletxndtl
		 * */
		productBundleTxnDetail.remove();
		TransactionUtil.commit();
		request.setAttribute("_ERROR_MESSAGE_",
				"Inventory Item Succesfully Unreserved");
		return "success";
	}

	public static String saveBundlingTxn(HttpServletRequest request,
			HttpServletResponse response) throws GenericEntityException,
			GenericServiceException {
		Delegator delegator1 = (Delegator) request.getAttribute("delegator");
		String productBundleTxnId = request.getParameter("productBundleTxnId");
		GenericValue bundleTxn = delegator1.findOne("ProductBundleTxn", false,
				"productBundleTxnId", productBundleTxnId);
		TransactionUtil.begin();
		GenericValue productBundle = bundleTxn.getRelatedOne("ProductBundle");
		String productBundleId = productBundle.getString("productBundleId");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session
				.getAttribute("userLogin");
		List<GenericValue> txnDetails = delegator1.findByAnd(
				"ProductBundleTxnDetail", "productBundleTxnId",
				productBundleTxnId);
		/* sudarshan#1 start */
		LocalDispatcher dispatcher = (LocalDispatcher) request
				.getAttribute("dispatcher");
		String strProductId = productBundle.getString("makeSku");

		List<EntityExpr> exprs = FastList.newInstance();
		exprs.add(EntityCondition.makeCondition("productId",
				EntityOperator.EQUALS, strProductId));
		exprs.add(EntityCondition.makeCondition("productAssocTypeId",
				EntityOperator.EQUALS, "BUNDLED_ASSOC"));

		List<GenericValue> prodAssocs = delegator1.findList("ProductAssoc",
				EntityCondition.makeCondition(exprs, EntityOperator.AND), null,
				null, null, false);
		if (txnDetails == null || prodAssocs == null
				|| prodAssocs.size() != txnDetails.size()) {
			request.setAttribute("_ERROR_MESSAGE_",
					"Reserve all inventory items before Finalize Bundling");
			TransactionUtil.rollback();
			return "error";
		}

		bundleTxn.set("statusId", "Completed");
		bundleTxn.store();

		try {
			for (GenericValue txnDetail : txnDetails) {
				BigDecimal xferQty = txnDetail.getBigDecimal("quantityToUse");
				if (xferQty != null) {
					xferQty = xferQty.negate();
				} else {
					throw new GenericServiceException();
				}
				Map<String, Object> inventoryMovementDetail = UtilMisc.toMap(
						"availableToPromiseDiff", BigDecimal.ZERO,
						"quantityOnHandDiff", xferQty, "inventoryItemId",
						txnDetail.get("inventoryItemId"),
						// "productBundleTxnId",productBundleTxnId,
						"userLogin", userLogin);

				Map<String, Object> resultNew = dispatcher.runSync(
						"createInventoryItemDetail", inventoryMovementDetail);
				if (ServiceUtil.isError(resultNew)) {
					throw new GenericServiceException();
				}
			}
		} catch (GenericServiceException e1) {
			request
					.setAttribute("_ERROR_MESSAGE_",
							"Inventory Item Detail create problem in prepare inventory transfer");
			TransactionUtil.rollback();
			return "error";
		}
		/* sudarshan#1 end */

		TransactionUtil.commit();
		request.setAttribute("_EVENT_MESSAGE_",
				"Your bundling transaction processed successfully");
		request.setAttribute("productBundleId", productBundleId);
		String quantityToBundle = request.getParameter("quantityToBundle");
		updateProductBundleRequestStatus(productBundleId, delegator1,
				quantityToBundle);
		String makeSku = productBundle.getString("makeSku");

		/* List<EntityExpr> exprs1 = FastList.newInstance();
		exprs1.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS, makeSku));
		List<GenericValue> invItems = delegator1.findList("InventoryItem",
				EntityCondition.makeCondition(exprs1), null, null, null, false);
		GenericValue inventoryItemList = null;
		inventoryItemList = (GenericValue) invItems.get(0);
		String invItemId = (String) inventoryItemList.get("inventoryItemId"); */
	    
		Map<String,? extends Object> createInventoryItem = UtilMisc.toMap("userLogin", userLogin,"inventoryItemTypeId","NON_SERIAL_INV_ITEM",
				"productId",makeSku,"facilityId",session.getAttribute("facilityId"));
		Map<String, Object> inventoryItemResult = dispatcher.runSync("createInventoryItem", createInventoryItem);
		String invItemId = (String) inventoryItemResult.get("inventoryItemId");
		
		BigDecimal raise = new BigDecimal(quantityToBundle);
		try {
			Map<String, ? extends Object> createNewInventoryDetailMap1 = UtilMisc
					.toMap("userLogin", userLogin, "inventoryItemId",
							invItemId, "quantityOnHandDiff", raise,
							"availableToPromiseDiff", raise//BigDecimal.ZERO
					// ,"productBundleTxnId", productBundleTxnId
					);
			Map<String, Object> resultNew = dispatcher.runSync(
					"createInventoryItemDetail", createNewInventoryDetailMap1);
			if (ServiceUtil.isError(resultNew)) {
				throw new GenericServiceException();
			}
		} catch (GenericServiceException e1) {
			request.setAttribute("_ERROR_MESSAGE_", "Problem in Bundling__ ");
			TransactionUtil.rollback();
			return "error";
		}
		return "success";
	}

	private static void updateProductBundleRequestStatus(
			String productBundleId, Delegator myDelegator,
			String quantityToBundle) throws GenericEntityException {
		GenericValue productBundle = myDelegator.findOne("ProductBundle",
				false, "productBundleId", productBundleId);
		BigDecimal actualQuantityDone = ProductHelper
				.getQuantityDoneForProductBundle(productBundleId, myDelegator);
		Double quantityRequired = Double.parseDouble(productBundle
				.getString("quantityRequired"));
		Double zero = 0.0;
		BigDecimal pendingQuantity = new BigDecimal(productBundle
				.getString("pendingQuantity"));
		BigDecimal qtyBund = new BigDecimal(quantityToBundle);

		BigDecimal pending = pendingQuantity.subtract(qtyBund);
		String status = "Requested";

		if (pending.doubleValue() == zero) {
			status = "Completed";
			productBundle.set("actualCompletion", UtilDateTime.nowTimestamp());
		}
		productBundle.set("statusId", status);
		productBundle.set("pendingQuantity", pending);
		productBundle.store();
	}

	private static Timestamp getExpireDate(String inventoryItemId)
			throws GenericEntityException {
		GenericValue inventoryItem = delegator.findOne("InventoryItem", false,
				"inventoryItemId", inventoryItemId);
		if (inventoryItem == null)
			return null;
		return inventoryItem.getTimestamp("expireDate");
	}

	private static GenericValue findOne(String entityName, Object... fields)
			throws GenericEntityException {
		List<GenericValue> result = delegator.findByAnd(entityName, fields);
		if (UtilValidate.isEmpty(result))
			return null;
		if (result.size() > 1)
			throw new GenericEntityException(
					"Expecting one but found more than one entity");
		return result.get(0);
	}

	private static Timestamp getExpireDate(String inventoryItemId,
			Delegator myDelegator) throws GenericEntityException {
		GenericValue inventoryItem = myDelegator.findOne("InventoryItem",
				false, "inventoryItemId", inventoryItemId);
		if (inventoryItem == null)
			return null;
		return inventoryItem.getTimestamp("expireDate");
	}

	private static GenericValue findOne(Delegator myDelegator,
			String entityName, Object... fields) throws GenericEntityException {
		List<GenericValue> result = myDelegator.findByAnd(entityName, fields);
		if (UtilValidate.isEmpty(result))
			return null;
		if (result.size() > 1)
			throw new GenericEntityException(
					"Expecting one but found more than one entity");
		return result.get(0);
	}

	public static String getBundlingTxns(HttpServletRequest request,
			HttpServletResponse response) throws GenericEntityException,
			GenericServiceException {
		return "success";
	}

	private static boolean reduceInventoryItemFinal(HttpServletRequest request,
			String inventoryItemId, BigDecimal quantity,
			String productTransferId) throws GenericEntityException {

		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session
				.getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request
				.getAttribute("dispatcher");

		try {
			Map<String, ? extends Object> createNewInventoryDetailMap = UtilMisc
					.toMap("userLogin", userLogin, "inventoryItemId",
							inventoryItemId, "quantityOnHandDiff", quantity,
							"availableToPromiseDiff", BigDecimal.ZERO
					// "productBundleTxnId", productTransferId
					);
			Map<String, Object> resultNew = dispatcher.runSync(
					"createInventoryItemDetail", createNewInventoryDetailMap);
			if (ServiceUtil.isError(resultNew)) {
				throw new GenericServiceException();
			}
		} catch (GenericServiceException e1) {
			request.setAttribute("_ERROR_MESSAGE_",
					"Problem in Creating Inventory Item Detail");
			TransactionUtil.rollback();
			return false;
		}
		return true;
	}

	public static String savePalletizationTxn(HttpServletRequest request,
			HttpServletResponse response) throws GenericEntityException,
			GenericServiceException {

		String xferQty = request.getParameter("xferQty");
		String casesPerPalle = request.getParameter("casesPerPallet");

		BigDecimal quantity = new BigDecimal(xferQty);
		BigDecimal casesPerPallet = new BigDecimal(casesPerPalle);

		GenericValue transferRequest = delegator
				.makeValue("PalletizeTransferTxn");
		transferRequest.setNextSeqId();

		transferRequest.set("inventoryItemIdFrom", request
				.getParameter("inventoryItemId"));
		transferRequest.set("locationSeqIdTo", request
				.getParameter("locationSeqIdTo"));
		transferRequest.set("facilityId", request.getParameter("facilityId"));
		transferRequest.set("quantity", quantity);
		transferRequest.set("casesPerPallet", casesPerPallet);
		transferRequest.set("statusId", "Requested");
		transferRequest.create();

		Map<String, Object> ctx = FastMap.newInstance();
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session
				.getAttribute("userLogin");
		ctx.put("inventoryItemId", request.getParameter("inventoryItemId"));
		ctx.put("xferQty", request.getParameter("xferQty"));
		ctx.put("statusId", request.getParameter("statusId"));
		ctx.put("facilityId", request.getParameter("facilityId"));
		ctx.put("facilityIdTo", "WebStoreWarehouse");
		ctx.put("sendDate", request.getParameter("sendDate"));
		ctx.put("locationSeqIdTo", request.getParameter("locationSeqIdTo"));
		ctx.put("comments", request.getParameter("comments"));
		ctx.put("userLogin", userLogin);
		ctx.put("txnId", transferRequest.getString("txnId"));
		LocalDispatcher dispatcher = (LocalDispatcher) request
				.getAttribute("dispatcher");

		String atp = request.getParameter("xferQty");
		BigDecimal ATP = new BigDecimal(atp);

		try {
			Map<String, ? extends Object> createNewInventoryDetailMap = UtilMisc
					.toMap("userLogin", userLogin, "inventoryItemId", request
							.getParameter("inventoryItemId"),
							"quantityOnHandDiff", BigDecimal.ZERO,
							"availableToPromiseDiff", ATP.negate());
			Map<String, Object> resultNew = dispatcher.runSync(
					"createInventoryItemDetail", createNewInventoryDetailMap);
			if (ServiceUtil.isError(resultNew)) {
				throw new GenericServiceException();
			}
		} catch (GenericServiceException e1) {
			request.setAttribute("_ERROR_MESSAGE_",
					"Problem in Reserving Inventory Item Detail");
			TransactionUtil.rollback();
			return "error";
		}

		return "success";
	}

	public static String PalletizationTxnStatusChange(
			HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException, GenericServiceException {
		LocalDispatcher dispatcher = (LocalDispatcher) request
				.getAttribute("dispatcher");

		String qoh = request.getParameter("quantity");
		BigDecimal QOH = new BigDecimal(qoh);
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session
				.getAttribute("userLogin");
		try {
			Map<String, ? extends Object> createNewInventoryDetailMap = UtilMisc
					.toMap("userLogin", userLogin, "inventoryItemId", request
							.getParameter("inventoryItemId"),
							"quantityOnHandDiff", QOH.negate(),
							"availableToPromiseDiff", BigDecimal.ZERO);
			Map<String, Object> resultNew = dispatcher.runSync(
					"createInventoryItemDetail", createNewInventoryDetailMap);
			if (resultNew.size() > 0) {
				GenericValue PalletizeTransfer = findOne(
						"PalletizeTransferTxn", "txnId", request
								.getParameter("txnId"));
				String status = "Completed";
				PalletizeTransfer.set("statusId", status);
				PalletizeTransfer.store();
			}

		} catch (GenericServiceException e1) {
			request.setAttribute("_ERROR_MESSAGE_",
					"Problem in Reserving Inventory Item Detail");
			TransactionUtil.rollback();
			return "error";
		}

		return "success";

	}

	public static String TransformationTxnStatusChange(
			HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException, GenericServiceException {
		Delegator delegator1 = (Delegator) request.getAttribute("delegator");
		//
		// GenericValue inventoryItem = findOne(delegator1,"InventoryItem",
		// "inventoryItemId", request.getParameter("fromInventoryItemId"));
		// BigDecimal qoh = inventoryItem.getBigDecimal("quantityOnHandTotal");
		// BigDecimal atp =
		// inventoryItem.getBigDecimal("availableToPromiseTotal");
		// GenericValue InventoryItemDetail = null;
		// InventoryItemDetail = GenericValue.create(inventoryItem);
		// delegator1.createSetNextSeqId(InventoryItemDetail);

		BigDecimal quantityDone = new BigDecimal(request.getParameter("quantityDone"));

		/*EntityCondition condition = EntityCondition.makeCondition(UtilMisc
				.toList(EntityCondition.makeCondition("locationSeqId",
						EntityOperator.EQUALS, request
								.getParameter("toLocationId"))));
		List locationSeqId = delegator1.findList("InventoryItem", condition,
				null, null, null, false);
		GenericValue locationSeqIdList = null;
		if (locationSeqId.size() > 0) {
			locationSeqIdList = (GenericValue) locationSeqId.get(0);
		}*/

		String toInvItemId = (String)request.getParameter("toInventoryItemId");

		String productTransferId = request.getParameter("productTransferId");
		GenericValue productTransferRequest = findOne(delegator1,"ProductTransfer", "productTransferId", productTransferId);
		Double conversionFactor = ProductHelper.getConversionFactor(productTransferRequest,delegator1);

		boolean reduced = reduceInventoryItemFinal(request, request
				.getParameter("fromInventoryItemId"), quantityDone.negate(),
				request.getParameter("productTransferId"));
		boolean increased = reduceInventoryItemFinal(request, toInvItemId,
				quantityDone.multiply(new BigDecimal(conversionFactor)), request.getParameter("productTransferId"));

		GenericValue ProductTransfer = findOne(delegator1, "ProductTransfer",
				"productTransferId", request.getParameter("productTransferId"));
		
		String txnId = request.getParameter("txnId");
		GenericValue ProductTransferTxn = findOne(delegator1,"ProductTransferTxn", "txnId", txnId);
		
		String status = "Completed";
		ProductTransfer.set("status", status);
		ProductTransferTxn.set("txnStatus", status);
		ProductTransfer.store();
		ProductTransferTxn.store();

		return "success";

	}

	public static BigDecimal[] getPackedAndLooseQty(BigDecimal qty,
			String productId, String uomId, Delegator delegator) {
		BigDecimal[] results = null;
		GenericValue productUom = null;

		// / $$$ QUICK-FIX-08-01-2010 Ghanda: Currently we are reading the first
		// value with "/" in the database. This will work
		// for MHE use case but later it needs to be fetched from the context in
		// which the product is being
		// used.
		productUom = getProductUomForProduct(productId, delegator);
		if (productUom != null) {
			BigDecimal cf = BigDecimal.ONE;
			cf = productUom.getBigDecimal("conversionFactor");
			qty = qty.divide(cf);
			String factorStr = productUom.getString("packFactor");
			BigDecimal factor = new BigDecimal(factorStr);
			results = qty.divideAndRemainder(factor);
		}
		return results;
	}

	public static GenericValue getProductUomForProduct(String productId,
			Delegator myDelegator) {

		List<GenericValue> productUomList = null;
		GenericValue productUom = null;
		try {
			productUomList = myDelegator.findByAnd("ProductUom", UtilMisc
					.toMap("productId", productId));
			productUom = productUomList.get(0);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return productUom;
	}

}