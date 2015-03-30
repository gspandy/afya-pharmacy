import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;

import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.accounting.util.UtilAccounting;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.order.test.SalesOrderTest;
import org.ofbiz.party.party.PartyWorker;

import java.math.BigDecimal;

import org.ofbiz.base.util.*;

import com.smebiz.common.PartyUtil;

import javolution.util.FastList;

facilityId = parameters.facilityId;
orderId = parameters.orderId ;
productId  = parameters.productId;
partyId = parameters.supplier;
orderStatus = parameters.statusCode;
List filterStatus = new ArrayList();
if(orderStatus == null){
filterStatus.add("ORDER_COMPLETED");
filterStatus.add("ORDER_APPROVED");
}else{
filterStatus.add(orderStatus);
}
groupName = parameters.groupName;
deliveryDate=parameters.deliveryDate;
ename=request.getParameter("ename").toString();
parameterMap=[:];
GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

List PurchaseOrderItem = FastList.newInstance();
if(UtilValidate.isNotEmpty(orderId))
	PurchaseOrderItem.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
if(UtilValidate.isNotEmpty(productId))
	PurchaseOrderItem.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
if(UtilValidate.isNotEmpty(groupName))
	PurchaseOrderItem.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,groupName));
if(UtilValidate.isNotEmpty(orderStatus))
	PurchaseOrderItem.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, filterStatus));
if(UtilValidate.isNotEmpty(deliveryDate)){
	deliveryDate=new Timestamp(UtilDateTime.SIMPLE_DATE_FORMAT.parse(deliveryDate).getTime());
	PurchaseOrderItem.add(EntityCondition.makeCondition("deliveryDate", EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(deliveryDate)));
	PurchaseOrderItem.add(EntityCondition.makeCondition("deliveryDate", EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayEnd(deliveryDate)));
}
if("PurchaseOrderItemFact".equals(ename)){
	PurchaseOrderItem.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "PURCHASE_ORDER"));
	PurchaseOrderItem.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS,"BILL_FROM_VENDOR"));
}
if("SalesOrderItemFact".equals(ename)){
	PurchaseOrderItem.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
	PurchaseOrderItem.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS,"BILL_TO_CUSTOMER"));
}
List<String> orderByList = new ArrayList<String>();
orderByList.add("productId");

List PurchaseOrderItemList = delegator.findList("OrderHeaderItemAndRoles", EntityCondition.makeCondition(PurchaseOrderItem, EntityOperator.AND), null, orderByList, null, false);

rows = [];

if(UtilValidate.isNotEmpty(PurchaseOrderItemList)){
	for(GenericValue SalesOrder :PurchaseOrderItemList)   {
		Map resultMap = new HashMap();
		
		BigDecimal truncated = BigDecimal.ZERO;
		BigDecimal receivedAmount = BigDecimal.ZERO;
		List purPeceiveList = new ArrayList();
		GenericValue product = delegator.findOne("Product",false ,UtilMisc.toMap("productId",SalesOrder.getString("productId")));
		
		GenericValue productUomGv =null;
		String productUom = "_NA_";
		if(UtilValidate.isNotEmpty(product.getString("quantityUomId"))){
			productUomGv=delegator.findOne("Uom", false, UtilMisc.toMap("uomId", product.getString("quantityUomId")));
			productUom=productUomGv.getString("description");
		}

		String categoryName="_NA_";
		if(UtilValidate.isNotEmpty(product.getString("primaryProductCategoryId"))){
			categoryName = product.getString("primaryProductCategoryId");
		}

		GenericValue partyNameGv= delegator.findOne("PartyNameView", UtilMisc.toMap("partyId",SalesOrder.getString("partyId")), false)
		String partyName="";
		if(UtilValidate.isNotEmpty(partyNameGv)){
			partyName =partyNameGv.getString("groupName") == null ?partyNameGv.getString("firstName") +(partyNameGv.getString("lasttName")== null?"":partyNameGv.getString("lasttName")):partyNameGv.getString("groupName");
		}
		resultMap.orderDate  = UtilDateTime.format(SalesOrder.getTimestamp("orderDate"));
		resultMap.orderId  = SalesOrder.getString("orderId");
		resultMap.groupName  = partyName == null ?"_NA_":partyName
		resultMap.productId  = SalesOrder.getString("productId");
		resultMap.description  = SalesOrder.getString("itemDescription");
		resultMap.quantityUomId  =productUom;
		resultMap.categoryName  = categoryName;
		resultMap.status  =("ORDER_APPROVED".equals(SalesOrder.getString("statusId"))?"Approved":"Completed");
		resultMap.deliveryDate= UtilDateTime.format(SalesOrder.getTimestamp("deliveryDate"));
		resultMap.orderName=(SalesOrder.getString("orderName")== null?"_NA_":SalesOrder.getString("orderName"));
		
		
		truncated = SalesOrder.getBigDecimal("quantity").setScale(0,BigDecimal.ROUND_DOWN);
		resultMap.qtyShipped = BigDecimal.ZERO;
		resultMap.qtyOrdered = truncated;
		
		GenericValue orderitem =delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", SalesOrder.getString("orderId"),"orderItemSeqId",SalesOrder.getString("orderItemSeqId")));
		orderReadHelper = new OrderReadHelper();
		if(UtilValidate.isNotEmpty(SalesOrder) && "SALES_ORDER".equals(SalesOrder.orderTypeId)){
		  receivedAmount =orderReadHelper.getItemShippedQuantity(orderitem).setScale(0,BigDecimal.ROUND_DOWN);
		  resultMap.qtyShipped = receivedAmount;
		
		}
		else{
			      purPeceiveList = delegator.findByAnd("ShipmentReceipt", [orderId : SalesOrder.orderId,orderItemSeqId : SalesOrder.orderItemSeqId]);
							    Map<String,BigDecimal> map = new HashMap<String,BigDecimal>();
		                          for(GenericValue gv : purPeceiveList){
			                          String productId = gv.getString("productId");
			                          if(org.ofbiz.base.util.UtilValidate.isEmpty(map.get(productId))){
			                          	map.put(productId,gv.quantityAccepted.setScale(0,BigDecimal.ROUND_DOWN));
			                          }
			                          else{
			                          	map.put(productId, map.get(productId).add(gv.quantityAccepted!=null?gv.quantityAccepted.setScale(0,BigDecimal.ROUND_DOWN):BigDecimal.ZERO));
			                         }
			                         receivedAmount = (BigDecimal)map.get(productId);
	                          		 resultMap.put("qtyShipped", receivedAmount);
		        }
		    } 
		
		rows += resultMap;
	}
}
context.resultList = rows;

Map<String,Object> companyHeaderInfoMap = PartyUtil.getCompanyHeaderInfo(delegator);
String headOfficeImg = "./framework/images/webapp/images/head_office.png";
String verticalLineImg = "./framework/images/webapp/images/vertical_line_bar.png";

Map<Object, Object> jrParameters = new HashMap<Object, Object>();
jrParameters.put("fromDate", "title");
jrParameters.put("headOfficeImg", headOfficeImg);
jrParameters.put("verticalLineImg", verticalLineImg);
jrParameters.put("logoImageUrl", companyHeaderInfoMap.get("logoImageUrl"));
jrParameters.put("companyName", companyHeaderInfoMap.get("companyName"));
jrParameters.put("address1", companyHeaderInfoMap.get("address1"));
jrParameters.put("address2", companyHeaderInfoMap.get("address2"));
jrParameters.put("companyPostalAddr", companyHeaderInfoMap.get("companyPostalAddr"));
jrParameters.put("postalCode", companyHeaderInfoMap.get("postalCode"));
jrParameters.put("city", companyHeaderInfoMap.get("city"));
jrParameters.put("stateProvinceAbbr", companyHeaderInfoMap.get("stateProvinceAbbr"));
jrParameters.put("countryName", companyHeaderInfoMap.get("countryName"));
jrParameters.put("telephoneNumber", companyHeaderInfoMap.get("telephoneNumber"));
jrParameters.put("faxNumber", companyHeaderInfoMap.get("faxNumber"));
jrParameters.put("email", companyHeaderInfoMap.get("email"));
jrParameters.put("companyRegNo", companyHeaderInfoMap.get("companyRegNo"));
jrParameters.put("vatRegNo", companyHeaderInfoMap.get("vatRegNo"));
jrParameters.put("tpinNo", companyHeaderInfoMap.get("tpinNo"));

org.ofbiz.entity.transaction.TransactionUtil.begin();
JRMapCollectionDataSource jrDataSource = new JRMapCollectionDataSource(rows);
if (jrDataSource != null) {
	request.setAttribute("jrDataSource", jrDataSource);
	request.setAttribute("jrParameters", jrParameters);
}
org.ofbiz.entity.transaction.TransactionUtil.commit();
return "success"
