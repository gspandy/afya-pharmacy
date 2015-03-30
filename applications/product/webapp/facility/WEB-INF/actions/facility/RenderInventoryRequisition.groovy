import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

if(request.getParameter("invRequisitionId")){
	GenericValue inventoryRequisition = delegator.findOne("InventoryRequisition", UtilMisc.toMap("invRequisitionId",request.getParameter("invRequisitionId")), false);
	EntityCondition condition = EntityCondition.makeCondition("invRequisitionId",EntityOperator.EQUALS,request.getParameter("invRequisitionId"));
	List<GenericValue> inventoryRequisitionItemList = delegator.findList("InventoryRequisitionItem",condition,null,null,null,false);
	
	context.headerMap = inventoryRequisition;
	context.requestItemList = inventoryRequisitionItemList;
	session.setAttribute("headerMap",inventoryRequisition);
	session.setAttribute("requestItemList",inventoryRequisitionItemList);
	
	context.facilityIdFrom = inventoryRequisition.getString("facilityIdFrom");
	context.requestType = inventoryRequisition.getString("requestType");
	context.requestDate = inventoryRequisition.getString("requestDate");
	return;
}else if(session.getAttribute("requestItemList") && session.getAttribute("headerMap")){
	context.headerMap = session.getAttribute("headerMap");
	context.facilityIdFrom = context.headerMap.get("facilityIdFrom");
	context.requestItemList = session.getAttribute("requestItemList");
	return;
}else{
	context.facilityIdFrom = null;
	context.requestType = null;
	context.requestDate = null;
	return;
}