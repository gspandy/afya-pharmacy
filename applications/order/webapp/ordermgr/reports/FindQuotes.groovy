package org.ofbiz.order.report;
import javolution.util.FastList;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
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
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.sme.order.util.OrderMgrUtil;

GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
List conditions = FastList.newInstance();
if (UtilValidate.isNotEmpty(request.getParameter("quoteId"))) {
	conditions.add(EntityCondition.makeCondition("quoteId", EntityOperator.EQUALS, request.getParameter("quoteId")));
}
if (UtilValidate.isNotEmpty(request.getParameter("quoteTypeId"))) {
	conditions.add(EntityCondition.makeCondition("quoteTypeId", EntityOperator.EQUALS, request.getParameter("quoteTypeId")));
}
if (UtilValidate.isNotEmpty(request.getParameter("salesChannelEnumId"))) {
	conditions.add(EntityCondition.makeCondition("salesChannelEnumId", EntityOperator.EQUALS, request.getParameter("salesChannelEnumId")));
}
if (UtilValidate.isNotEmpty(request.getParameter("partyId"))) {
	conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, request.getParameter("partyId")));
}
if (UtilValidate.isNotEmpty(request.getParameter("statusId"))) {
	conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, request.getParameter("statusId")));
}
if (UtilValidate.isNotEmpty(request.getParameter("productStoreId"))) {
	conditions.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, request.getParameter("productStoreId")));
}
if (UtilValidate.isNotEmpty(request.getParameter("quoteName"))) {
	conditions.add(EntityCondition.makeCondition("quoteName", EntityOperator.LIKE, "%" + request.getParameter("quoteName") + "%"));
}
conditions.add(EntityCondition.makeCondition("createdBy", EntityOperator.EQUALS, userLogin.getString("partyId") ));
quoteList = []
List quoteInfoList = delegator.findList("Quote",EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
grandTotalQuoteAmount = 0;
if(UtilValidate.isNotEmpty(quoteInfoList)){
	for(GenericValue quoteHeader:quoteInfoList){
		quoteMap = [:];
		quoteItemAmount = BigDecimal.ZERO;
		totalQuoteAmount = BigDecimal.ZERO;
		totalQuoteItemAdjustmentAmount = BigDecimal.ZERO;
		quoteMap.put("quoteId",quoteHeader.quoteId);
		quoteItems = delegator.findByAnd("QuoteItem",["quoteId":quoteHeader.quoteId]);
		if(UtilValidate.isNotEmpty(quoteItems)){
			for(GenericValue quoteItem:quoteItems){
				if(UtilValidate.isNotEmpty(quoteItem.selectedAmount) && quoteItem.selectedAmount == null && quoteItem.selectedAmount != 0.00) {
					selectedAmount = quoteItem.selectedAmount;
				}
				else {
					selectedAmount = BigDecimal.ONE;
				}
				quoteItemAmount = quoteItemAmount + (quoteItem.getBigDecimal("quantity").multiply(quoteItem.getBigDecimal("quoteUnitPrice").multiply(selectedAmount)));
			}
			quoteItemAdjustments = delegator.findByAnd("QuoteAdjustment",["quoteId":quoteHeader.quoteId]);
			if(UtilValidate.isNotEmpty(quoteItemAdjustments)){
				for(GenericValue quoteItemAdjustment:quoteItemAdjustments){
					totalQuoteItemAdjustmentAmount = totalQuoteItemAdjustmentAmount + quoteItemAdjustment.amount;
				}
			}
			totalQuoteItemAmount = quoteItemAmount + totalQuoteItemAdjustmentAmount;
			totalQuoteAmount = totalQuoteAmount + totalQuoteItemAmount;
			grandTotalQuoteAmount = totalQuoteAmount;
		}
		quoteMap.put("quoteTypeId",quoteHeader.quoteTypeId);
		quoteMap.put("partyId",quoteHeader.partyId);
		quoteMap.put("issueDate",quoteHeader.issueDate);
		quoteMap.put("statusId",quoteHeader.statusId);
		quoteMap.put("grandTotal",grandTotalQuoteAmount);
		quoteMap.put("currencyUomId",quoteHeader.currencyUomId);
		quoteMap.put("productStoreId",quoteHeader.productStoreId);
		quoteMap.put("salesChannelEnumId",quoteHeader.salesChannelEnumId);
		quoteMap.put("validFromDate",quoteHeader.validFromDate);
		quoteMap.put("validThruDate",quoteHeader.validThruDate);
		quoteMap.put("quoteName",quoteHeader.quoteName);
		quoteMap.put("createdBy",quoteHeader.createdBy);
		quoteMap.put("description",quoteHeader.description);
		quoteList.add(quoteMap);
	}
}

List<String> quoteCreators = new ArrayList<String>();
quoteCreators.add(userLogin.getString("partyId"));
Boolean quoteManager = OrderMgrUtil.isQuoteManger(userLogin.getString("partyId"));
Boolean quoteReprensentative = OrderMgrUtil.isQuoteReprensentative(userLogin.getString("partyId"));
Boolean salesReprensentative =OrderMgrUtil.isSalesReprensentative(userLogin.getString("partyId"));
Boolean salesManager = OrderMgrUtil.isSalesManger(userLogin.getString("partyId"));
Boolean purchaseReprensentative = OrderMgrUtil.isPurchaseReprensentative(userLogin.getString("partyId"));
Boolean purchaseManager = OrderMgrUtil.isPurchaseManger(userLogin.getString("partyId"));
if (salesManager || salesReprensentative ||quoteManager || purchaseManager ||purchaseReprensentative ) {
	quoteCreators.addAll(OrderMgrUtil.getManagerRelationship(userLogin.getString("partyId")));

	quoteCreators = OrderMgrUtil.getUserLoginIds(quoteCreators);

	List conditions1 = FastList.newInstance();
	if (UtilValidate.isNotEmpty(request.getParameter("quoteId"))) {
		conditions1.add(EntityCondition.makeCondition("quoteId", EntityOperator.EQUALS, request.getParameter("quoteId")));
	}
	if (UtilValidate.isNotEmpty(request.getParameter("quoteTypeId"))) {
		conditions1.add(EntityCondition.makeCondition("quoteTypeId", EntityOperator.EQUALS, request.getParameter("quoteTypeId")));
	}
	if (UtilValidate.isNotEmpty(request.getParameter("salesChannelEnumId"))) {
		conditions1.add(EntityCondition.makeCondition("salesChannelEnumId", EntityOperator.EQUALS, request.getParameter("salesChannelEnumId")));
	}
	if (UtilValidate.isNotEmpty(request.getParameter("partyId"))) {
		conditions1.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, request.getParameter("partyId")));
	}
	if (UtilValidate.isNotEmpty(request.getParameter("statusId"))) {
		conditions1.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, request.getParameter("statusId")));
	}
	if (UtilValidate.isNotEmpty(request.getParameter("productStoreId"))) {
		conditions1.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, request.getParameter("productStoreId")));
	}
	if (UtilValidate.isNotEmpty(request.getParameter("quoteName"))) {
		conditions1.add(EntityCondition.makeCondition("quoteName", EntityOperator.LIKE, "%" + request.getParameter("quoteName") + "%"));
	}
	if((salesManager || salesReprensentative) && ( purchaseManager || purchaseReprensentative)){
		quotesList = []
		List<GenericValue> quoteHeaderList = new ArrayList<GenericValue>();
	try {
		quoteHeaderList = delegator.findList("Quote", EntityCondition.makeCondition(conditions1,EntityOperator.AND), null, null, null, false);
		if(UtilValidate.isNotEmpty(quoteHeaderList)){
			for(GenericValue quoteHeader:quoteHeaderList){
				quoteMap = [:];
				quoteItemAmount = BigDecimal.ZERO;
				totalQuoteAmount = BigDecimal.ZERO;
				totalQuoteItemAdjustmentAmount = BigDecimal.ZERO;
				quoteMap.put("quoteId",quoteHeader.quoteId);
				quoteItems = delegator.findByAnd("QuoteItem",["quoteId":quoteHeader.quoteId]);
				if(UtilValidate.isNotEmpty(quoteItems)){
					for(GenericValue quoteItem:quoteItems){
						if(UtilValidate.isNotEmpty(quoteItem.selectedAmount) && quoteItem.selectedAmount == null && quoteItem.selectedAmount != 0.00) {
							selectedAmount = quoteItem.selectedAmount;
						}
						else {
							selectedAmount = BigDecimal.ONE;
						}
						quoteItemAmount = quoteItemAmount + (quoteItem.getBigDecimal("quantity").multiply(quoteItem.getBigDecimal("quoteUnitPrice").multiply(selectedAmount)));
					}
					quoteItemAdjustments = delegator.findByAnd("QuoteAdjustment",["quoteId":quoteHeader.quoteId]);
					if(UtilValidate.isNotEmpty(quoteItemAdjustments)){
						for(GenericValue quoteItemAdjustment:quoteItemAdjustments){
							totalQuoteItemAdjustmentAmount = totalQuoteItemAdjustmentAmount + quoteItemAdjustment.amount;
						}
					}
					totalQuoteItemAmount = quoteItemAmount + totalQuoteItemAdjustmentAmount;
					totalQuoteAmount = totalQuoteAmount + totalQuoteItemAmount;
					grandTotalQuoteAmount = totalQuoteAmount;
				}
				quoteMap.put("quoteTypeId",quoteHeader.quoteTypeId);
				quoteMap.put("partyId",quoteHeader.partyId);
				quoteMap.put("issueDate",quoteHeader.issueDate);
				quoteMap.put("statusId",quoteHeader.statusId);
				quoteMap.put("grandTotal",grandTotalQuoteAmount);
				quoteMap.put("currencyUomId",quoteHeader.currencyUomId);
				quoteMap.put("productStoreId",quoteHeader.productStoreId);
				quoteMap.put("salesChannelEnumId",quoteHeader.salesChannelEnumId);
				quoteMap.put("validFromDate",quoteHeader.validFromDate);
				quoteMap.put("validThruDate",quoteHeader.validThruDate);
				quoteMap.put("quoteName",quoteHeader.quoteName);
				quoteMap.put("createdBy",quoteHeader.createdBy);
				quoteMap.put("description",quoteHeader.description);
				quotesList.add(quoteMap);
			}
			
			context.listIt = quotesList;
		}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return "success";
	}

	if((salesManager || salesReprensentative)){
		quotesList = []
		conditions1.add(EntityCondition.makeCondition("quoteTypeId", EntityOperator.NOT_EQUAL, "PURCHASE_QUOTE"));
		List<GenericValue> quoteHeaderList = new ArrayList<GenericValue>();
	try {
		quoteHeaderList = delegator.findList("Quote", EntityCondition.makeCondition(conditions1,EntityOperator.AND), null, null, null, false);
		if(UtilValidate.isNotEmpty(quoteHeaderList)){
			for(GenericValue quoteHeader:quoteHeaderList){
				quoteMap = [:];
				quoteItemAmount = BigDecimal.ZERO;
				totalQuoteAmount = BigDecimal.ZERO;
				totalQuoteItemAdjustmentAmount = BigDecimal.ZERO;
				quoteMap.put("quoteId",quoteHeader.quoteId);
				quoteItems = delegator.findByAnd("QuoteItem",["quoteId":quoteHeader.quoteId]);
				if(UtilValidate.isNotEmpty(quoteItems)){
					for(GenericValue quoteItem:quoteItems){
						if(UtilValidate.isNotEmpty(quoteItem.selectedAmount) && quoteItem.selectedAmount == null && quoteItem.selectedAmount != 0.00) {
							selectedAmount = quoteItem.selectedAmount;
						}
						else {
							selectedAmount = BigDecimal.ONE;
						}
						quoteItemAmount = quoteItemAmount + (quoteItem.getBigDecimal("quantity").multiply(quoteItem.getBigDecimal("quoteUnitPrice").multiply(selectedAmount)));
					}
					quoteItemAdjustments = delegator.findByAnd("QuoteAdjustment",["quoteId":quoteHeader.quoteId]);
					if(UtilValidate.isNotEmpty(quoteItemAdjustments)){
						for(GenericValue quoteItemAdjustment:quoteItemAdjustments){
							totalQuoteItemAdjustmentAmount = totalQuoteItemAdjustmentAmount + quoteItemAdjustment.amount;
						}
					}
					totalQuoteItemAmount = quoteItemAmount + totalQuoteItemAdjustmentAmount;
					totalQuoteAmount = totalQuoteAmount + totalQuoteItemAmount;
					grandTotalQuoteAmount = totalQuoteAmount;
				}
				quoteMap.put("quoteTypeId",quoteHeader.quoteTypeId);
				quoteMap.put("partyId",quoteHeader.partyId);
				quoteMap.put("issueDate",quoteHeader.issueDate);
				quoteMap.put("statusId",quoteHeader.statusId);
				quoteMap.put("grandTotal",grandTotalQuoteAmount);
				quoteMap.put("currencyUomId",quoteHeader.currencyUomId);
				quoteMap.put("productStoreId",quoteHeader.productStoreId);
				quoteMap.put("salesChannelEnumId",quoteHeader.salesChannelEnumId);
				quoteMap.put("validFromDate",quoteHeader.validFromDate);
				quoteMap.put("validThruDate",quoteHeader.validThruDate);
				quoteMap.put("quoteName",quoteHeader.quoteName);
				quoteMap.put("createdBy",quoteHeader.createdBy);
				quoteMap.put("description",quoteHeader.description);
				quotesList.add(quoteMap);
			}
			
			context.listIt = quotesList;
		}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return "success";
	}
	if(( purchaseManager || purchaseReprensentative)){
		quotesList = []
		conditions1.add(EntityCondition.makeCondition("quoteTypeId", EntityOperator.EQUALS, "PURCHASE_QUOTE"));
		List<GenericValue> quoteHeaderList = new ArrayList<GenericValue>();
	try {
		quoteHeaderList = delegator.findList("Quote", EntityCondition.makeCondition(conditions1,EntityOperator.AND), null, null, null, false);
		if(UtilValidate.isNotEmpty(quoteHeaderList)){
			for(GenericValue quoteHeader:quoteHeaderList){
				quoteMap = [:];
				quoteItemAmount = BigDecimal.ZERO;
				totalQuoteAmount = BigDecimal.ZERO;
				totalQuoteItemAdjustmentAmount = BigDecimal.ZERO;
				quoteMap.put("quoteId",quoteHeader.quoteId);
				quoteItems = delegator.findByAnd("QuoteItem",["quoteId":quoteHeader.quoteId]);
				if(UtilValidate.isNotEmpty(quoteItems)){
					for(GenericValue quoteItem:quoteItems){
						if(UtilValidate.isNotEmpty(quoteItem.selectedAmount) && quoteItem.selectedAmount == null && quoteItem.selectedAmount != 0.00) {
							selectedAmount = quoteItem.selectedAmount;
						}
						else {
							selectedAmount = BigDecimal.ONE;
						}
						quoteItemAmount = quoteItemAmount + (quoteItem.getBigDecimal("quantity").multiply(quoteItem.getBigDecimal("quoteUnitPrice").multiply(selectedAmount)));
					}
					quoteItemAdjustments = delegator.findByAnd("QuoteAdjustment",["quoteId":quoteHeader.quoteId]);
					if(UtilValidate.isNotEmpty(quoteItemAdjustments)){
						for(GenericValue quoteItemAdjustment:quoteItemAdjustments){
							totalQuoteItemAdjustmentAmount = totalQuoteItemAdjustmentAmount + quoteItemAdjustment.amount;
						}
					}
					totalQuoteItemAmount = quoteItemAmount + totalQuoteItemAdjustmentAmount;
					totalQuoteAmount = totalQuoteAmount + totalQuoteItemAmount;
					grandTotalQuoteAmount = totalQuoteAmount;
				}
				quoteMap.put("quoteTypeId",quoteHeader.quoteTypeId);
				quoteMap.put("partyId",quoteHeader.partyId);
				quoteMap.put("issueDate",quoteHeader.issueDate);
				quoteMap.put("statusId",quoteHeader.statusId);
				quoteMap.put("grandTotal",grandTotalQuoteAmount);
				quoteMap.put("currencyUomId",quoteHeader.currencyUomId);
				quoteMap.put("productStoreId",quoteHeader.productStoreId);
				quoteMap.put("salesChannelEnumId",quoteHeader.salesChannelEnumId);
				quoteMap.put("validFromDate",quoteHeader.validFromDate);
				quoteMap.put("validThruDate",quoteHeader.validThruDate);
				quoteMap.put("quoteName",quoteHeader.quoteName);
				quoteMap.put("createdBy",quoteHeader.createdBy);
				quoteMap.put("description",quoteHeader.description);
				quotesList.add(quoteMap);
			}
			
			context.listIt = quotesList;
		}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return "success";
	}
	quotesList = []
	conditions1.add(EntityCondition.makeCondition("createdBy", EntityOperator.IN, quoteCreators));
	List<GenericValue> quoteHeaderList = new ArrayList<GenericValue>();
	try {
		quoteHeaderList = delegator.findList("Quote", EntityCondition.makeCondition(conditions1,EntityOperator.AND), null, null, null, false);
		if(UtilValidate.isNotEmpty(quoteHeaderList)){
			for(GenericValue quoteHeader:quoteHeaderList){
				quoteMap = [:];
				quoteItemAmount = BigDecimal.ZERO;
				totalQuoteAmount = BigDecimal.ZERO;
				totalQuoteItemAdjustmentAmount = BigDecimal.ZERO;
				quoteMap.put("quoteId",quoteHeader.quoteId);
				quoteItems = delegator.findByAnd("QuoteItem",["quoteId":quoteHeader.quoteId]);
				if(UtilValidate.isNotEmpty(quoteItems)){
					for(GenericValue quoteItem:quoteItems){
						if(UtilValidate.isNotEmpty(quoteItem.selectedAmount) && quoteItem.selectedAmount == null && quoteItem.selectedAmount != 0.00) {
							selectedAmount = quoteItem.selectedAmount;
						}
						else {
							selectedAmount = BigDecimal.ONE;
						}
						quoteItemAmount = quoteItemAmount + (quoteItem.getBigDecimal("quantity").multiply(quoteItem.getBigDecimal("quoteUnitPrice").multiply(selectedAmount)));
					}
					quoteItemAdjustments = delegator.findByAnd("QuoteAdjustment",["quoteId":quoteHeader.quoteId]);
					if(UtilValidate.isNotEmpty(quoteItemAdjustments)){
						for(GenericValue quoteItemAdjustment:quoteItemAdjustments){
							totalQuoteItemAdjustmentAmount = totalQuoteItemAdjustmentAmount + quoteItemAdjustment.amount;
						}
					}
					totalQuoteItemAmount = quoteItemAmount + totalQuoteItemAdjustmentAmount;
					totalQuoteAmount = totalQuoteAmount + totalQuoteItemAmount;
					grandTotalQuoteAmount = totalQuoteAmount;
				}
				quoteMap.put("quoteTypeId",quoteHeader.quoteTypeId);
				quoteMap.put("partyId",quoteHeader.partyId);
				quoteMap.put("issueDate",quoteHeader.issueDate);
				quoteMap.put("statusId",quoteHeader.statusId);
				quoteMap.put("grandTotal",grandTotalQuoteAmount);
				quoteMap.put("currencyUomId",quoteHeader.currencyUomId);
				quoteMap.put("productStoreId",quoteHeader.productStoreId);
				quoteMap.put("salesChannelEnumId",quoteHeader.salesChannelEnumId);
				quoteMap.put("validFromDate",quoteHeader.validFromDate);
				quoteMap.put("validThruDate",quoteHeader.validThruDate);
				quoteMap.put("quoteName",quoteHeader.quoteName);
				quoteMap.put("createdBy",quoteHeader.createdBy);
				quoteMap.put("description",quoteHeader.description);
				quotesList.add(quoteMap);
			}
			
			context.listIt = quotesList;
		}
	} catch (GenericEntityException e) {
		e.printStackTrace();
	}
	return "success";
} else {
	context.listIt = quoteList;
}