import org.ofbiz.base.util.UtilHttp;
import java.util.*;
import org.ofbiz.entity.condition.*
import org.ofbiz.widget.html.HtmlFormWrapper
import org.ofbiz.entity.*
import org.ofbiz.entity.condition.*
import org.ofbiz.entity.util.*
import org.ofbiz.entity.*


shipmentId = null;
facilityId =  request.getParameter("facilityId");
orderId =  request.getParameter("orderId");
parameterCollection =  UtilHttp.parseMultiFormData(parameters);
List orderIds = [];
String facilityId = null;
for(Map m : parameterCollection){
	orderIds.add(m.get("orderId"));
	facilityId=m.get("facilityId");
	shipmentId=m.get("shipmentId");
	
}
 if(!orderIds){
 request.setAttribute("_ERROR_MESSAGE_", "No Order selected. Cannot create Picklist");
	return "error";
 }
result = dispatcher.runSync("createPicklistFromOrders",["orderIdList":orderIds,"facilityId":facilityId,"userLogin":userLogin]);
GenericValue picklist = delegator.findOne("Picklist", ["picklistId":result.picklistId], false);
picklist.setString("shipmentId",shipmentId);
delegator.store(picklist);

itemPicklist = delegator.findOne("Picklist", ["picklistId":result.picklistId], false);
if(itemPicklist){
request.setAttribute("_EVENT_MESSAGE_", "Picklist created successfully");
}
request.setAttribute("shipmentId", picklist.shipmentId);
request.setAttribute("facilityId", facilityId);
request.setAttribute("picklistId", result.picklistId);
request.setAttribute("orderId", result.orderId);
return "success";
