import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.accounting.util.UtilAccounting;

import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

import org.ofbiz.party.party.PartyWorker;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.base.util.*;

import com.smebiz.common.PartyUtil;

import java.sql.Date;
import java.sql.Timestamp;

import javolution.util.FastList;


orderId = parameters.orderId ;
productId  = parameters.productId;
categoryName  = parameters.categoryName;
//groupName = parameters.groupName;
partyId = parameters.partyId;
orderDate = parameters.orderDate;
orderStatus =  parameters.statusCode;
String eName =  request.getParameter("ename").toString();
print "\n\n\n eName"+eName+"\n\n\n"
List SalesOrderItemFact = FastList.newInstance();

if(orderId)
	SalesOrderItemFact.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId));
if(productId)
	SalesOrderItemFact.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
if(categoryName)
	SalesOrderItemFact.add(EntityCondition.makeCondition("categoryName", EntityOperator.EQUALS, categoryName));
//if(groupName)
	//SalesOrderItemFact.add(EntityCondition.makeCondition("groupName", EntityOperator.EQUALS, groupName));
if(partyId)
	SalesOrderItemFact.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,partyId));
if(orderDate)
	SalesOrderItemFact.add(EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, orderDate));
if(orderStatus)
	SalesOrderItemFact.add(EntityCondition.makeCondition("orderStatus", EntityOperator.EQUALS, orderStatus));
List<String> orderByList = new ArrayList<String>();
orderByList.add("productId");
List SalesOrderItemFactList = delegator.findList(eName, EntityCondition.makeCondition(SalesOrderItemFact, EntityOperator.AND), null, orderByList, null, false);

rows = [];
for(int i = 0; i < SalesOrderItemFactList.size(); i++) {
	resultMap = [:];
	BigDecimal truncated = BigDecimal.ZERO;
	BigDecimal receivedAmount = BigDecimal.ZERO;
	resultMap.orderDate = UtilDateTime.format(SalesOrderItemFactList.get(i).orderDate);
	resultMap.orderId = SalesOrderItemFactList.get(i).orderId
	//resultMap.groupName = SalesOrderItemFactList.get(i).groupName
	resultMap.productId = SalesOrderItemFactList.get(i).productId
	resultMap.description = SalesOrderItemFactList.get(i).description
	resultMap.qtyShipped = receivedAmount;

	if(UtilValidate.isNotEmpty( SalesOrderItemFactList.get(i).quantityUomId)) {
		GenericValue QuantityUomGv = delegator.findByPrimaryKey("Uom", UtilMisc.toMap("uomId", SalesOrderItemFactList.get(i).quantityUomId));
		GenericValue orderitem =delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", SalesOrderItemFactList.get(i).orderId,"orderItemSeqId",SalesOrderItemFactList.get(i).orderItemSeqId));
		resultMap.quantityUomId = QuantityUomGv.get("description")==null?"_NA_": QuantityUomGv.get("description");
	} else {
		resultMap.quantityUomId="_NA_"
	}
	resultMap.categoryName = SalesOrderItemFactList.get(i).categoryName
	if(UtilValidate.isNotEmpty(SalesOrderItemFactList.get(i).quantity)) {
		truncated = SalesOrderItemFactList.get(i).quantity.setScale(0,BigDecimal.ROUND_DOWN);
		resultMap.quantity = truncated
	}
	purPeceiveList = delegator.findByAnd("ShipmentReceipt", [orderId : SalesOrderItemFactList.get(i).orderId,orderItemSeqId : SalesOrderItemFactList.get(i).orderItemSeqId]);
	Map<String,BigDecimal> map = new HashMap<String,BigDecimal>();
	for(GenericValue gv : purPeceiveList) {
		String productId = gv.getString("productId");
		if(org.ofbiz.base.util.UtilValidate.isEmpty(map.get(productId))) {
			map.put(productId,gv.quantityAccepted.setScale(0,BigDecimal.ROUND_DOWN));
		} else {
			map.put(productId, map.get(productId).add(gv.quantityAccepted!=null?gv.quantityAccepted.setScale(0,BigDecimal.ROUND_DOWN):BigDecimal.ZERO));
		}
		receivedAmount = (BigDecimal)map.get(resultMap.productId);
		resultMap.put("qtyShipped", receivedAmount);
		resultMap.qtyShipped = receivedAmount;
	}
	//resultMap.qtyShipped = map.get(resultMap.productId); 
	rows += resultMap;
}

context.resultList = rows;
context.size = SalesOrderItemFactList.size();

resultList=  [];

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
context.resultList = rows;

return "success"
