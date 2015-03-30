package org.ofbiz.order.report;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.sme.order.util.OrderMgrUtil;

public class ComparativeOrderItemReport {
	public static String generateComparativeReports(HttpServletRequest request, HttpServletResponse response) throws ParseException,
			GenericEntityException {
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
		String orderTypeId = request.getParameter("orderTypeId");
		String productId = request.getParameter("productId");
		String orderStatus = request.getParameter("statusId");
		String orderFormDateStr = request.getParameter("fromOrderDate");
		String orderThurDateStr = request.getParameter("thruOrderDate");
		Timestamp orderFormDate = UtilDateTime.getDayStart(new Timestamp(new Date().getTime()));
		Timestamp orderThruDate = UtilDateTime.getDayEnd(new Timestamp(new Date().getTime()));
		List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
		List<EntityCondition> conditionListOne = new ArrayList<EntityCondition>();
		List<EntityCondition> dateConditionList = new ArrayList<EntityCondition>();
		if (UtilValidate.isNotEmpty(orderFormDateStr)) {
			orderFormDate = new Timestamp(UtilDateTime.SIMPLE_DATE_FORMAT.parse(orderFormDateStr).getTime());
		}
		if (UtilValidate.isNotEmpty(orderThurDateStr)) {
			orderThruDate = new Timestamp(UtilDateTime.SIMPLE_DATE_FORMAT.parse(orderThurDateStr).getTime());
		}
		if (UtilValidate.isNotEmpty(orderTypeId))
			conditionList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, orderTypeId));
		if (UtilValidate.isNotEmpty(productId))
			conditionList.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		if (UtilValidate.isNotEmpty(orderStatus))
			conditionList.add(EntityCondition.makeCondition("orderStatusId", EntityOperator.EQUALS, orderStatus));
		dateConditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, orderFormDate));
		dateConditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, orderThruDate));
		conditionListOne.addAll(conditionList);
		conditionListOne.addAll(dateConditionList);

		List<String> orderByList = new ArrayList<String>();
		orderByList.add("productId");
		List<GenericValue> OrderItemFactList1 = delegator.findList("OrderHeaderAndItems",
				EntityCondition.makeCondition(conditionListOne, EntityOperator.AND), null, orderByList, null, false);
		List<Map<String, Object>> mainList = new ArrayList<Map<String, Object>>();
		if (OrderItemFactList1 != null)
			for (int i = 0; i < OrderItemFactList1.size(); i++) {
				Map<String, Object> crntYearMap = new HashMap<String, Object>();
				Boolean ifNotExists = Boolean.TRUE;
				if (UtilValidate.isNotEmpty(mainList))
					for (Map<String, Object> map : mainList) {
						if (OrderItemFactList1.get(i).get("productId").equals(map.get("productId"))) {
							BigDecimal qty = ((BigDecimal) map.get("currentYearQty")).add(OrderItemFactList1.get(i).getBigDecimal("quantity"));
							map.put("currentYearQty", qty);
							ifNotExists = Boolean.FALSE;
						} else {
							ifNotExists = Boolean.TRUE;
						}
					}
				if (ifNotExists) {
					crntYearMap.put("productId", OrderItemFactList1.get(i).getString("productId"));
					crntYearMap.put("productName", getProductDescription(OrderItemFactList1.get(i).getString("productId"), delegator));
					crntYearMap.put("currentYearQty", OrderItemFactList1.get(i).getBigDecimal("quantity"));
					crntYearMap.put("lastYearQty", BigDecimal.ZERO);
					crntYearMap.put("uomId", getProductUom(OrderItemFactList1.get(i).getString("productId"),delegator));
					mainList.add(crntYearMap);
				}
			}

		List<EntityCondition> conditionListTwo = new ArrayList<EntityCondition>();
		dateConditionList = new ArrayList<EntityCondition>();
		Timestamp previousFormDate = UtilDateTime.getYearStart(orderFormDate, orderFormDate.getDate() - 1, orderFormDate.getMonth(), -1);
		Timestamp previousthru = UtilDateTime.getYearStart(orderThruDate, orderThruDate.getDate() - 1, orderThruDate.getMonth(), -1);
		dateConditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, previousFormDate));
		dateConditionList.add(EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, previousthru));
		conditionListTwo.addAll(dateConditionList);
		conditionListTwo.addAll(conditionList);

		List<GenericValue> OrderItemFactList2 = delegator.findList("OrderHeaderAndItems",
				EntityCondition.makeCondition(conditionListTwo, EntityOperator.AND), null, orderByList, null, false);
		if (OrderItemFactList2 != null)
			for (GenericValue gv : OrderItemFactList2) {
				Map<String, Object> lastYearMap = new HashMap<String, Object>();
				Boolean ifNotExits = Boolean.FALSE;
				for (Map<String, Object> map : mainList) {
					if (gv.getString("productId").equals(map.get("productId"))) {
						map.put("lastYearQty", ((BigDecimal) map.get("lastYearQty")).add(gv.getBigDecimal("quantity")));
						ifNotExits = Boolean.FALSE;
						break;
					} else {
						ifNotExits = Boolean.TRUE;
					}
				}

				if (ifNotExits) {
					lastYearMap.put("productId", gv.getString("productId"));
					lastYearMap.put("productName", getProductDescription(gv.getString("productId"), delegator));
					lastYearMap.put("currentYearQty", BigDecimal.ZERO);
					lastYearMap.put("lastYearQty", gv.getBigDecimal("quantity"));
					mainList.add(lastYearMap);
				}
			}
		String title="Comparative Purchase Order Item Report"; 	
		String header1="Ordered Quantity - Current Year";
		String header2="Ordered Quantity - Previous Year";
		if("SALES_ORDER".equals(orderTypeId)){
			title="Comparative Sales Order Item Report";
			header1="Sold Quantity - Current Year";
			header2="Sold Quantity - Previous Year";
		}
		
		Map<Object, Object> jrParameters = new HashMap<Object, Object>();
		jrParameters.put("fromDate", orderFormDate);
		jrParameters.put("thruDate", orderThruDate);
		jrParameters.put("prfromDate", previousFormDate);
		jrParameters.put("prthruDate", previousthru);
		jrParameters.put("orderTypeId", title);
		jrParameters.put("header1", header1);
		jrParameters.put("header2", header2);
		jrParameters.put("orderStatus",orderStatus );
		org.ofbiz.entity.transaction.TransactionUtil.begin();
		JRMapCollectionDataSource jrDataSource = new JRMapCollectionDataSource(mainList);
		if (jrDataSource != null) {
			request.setAttribute("jrDataSource", jrDataSource);
			request.setAttribute("jrParameters", jrParameters);
		}
		org.ofbiz.entity.transaction.TransactionUtil.commit();
		return "Success";
	}

	public static String getProductDescription(String productId, GenericDelegator genricDeligator) {
		if (UtilValidate.isEmpty(productId))
			return "";
		GenericValue product = null;
		try {
			product = genricDeligator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		if (product != null)
			return product.getString("internalName");
		else
			return "";
	}

	public static String getProductUom(String productId, GenericDelegator genericDeligator) {
		if (UtilValidate.isEmpty(productId))
			return "";
		GenericValue product = null;
		 GenericValue productUomGv =null;
		try {
			product = genericDeligator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
			productUomGv=genericDeligator.findOne("Uom", false, UtilMisc.toMap("uomId", product.getString("quantityUomId")));
			
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		if (productUomGv != null)
			return productUomGv.getString("description");
				
		else
			return "";
	}
}
