import org.ofbiz.base.util.UtilHttp;
import java.util.*;
import org.ofbiz.entity.condition.*
import org.ofbiz.widget.html.HtmlFormWrapper
import org.ofbiz.entity.*
import org.ofbiz.entity.condition.*
import org.ofbiz.entity.util.*
import org.ofbiz.entity.*



facilityId =  request.getParameter("facilityId");

parameterCollection =  UtilHttp.parseMultiFormData(parameters);

List orderIds = [];
String facilityId = null;
for(Map m : parameterCollection){
	orderIds.add(m.get("orderId"));
	facilityId=m.get("facilityId");
	shipmentId=m.get("shipmentId");
	
}
if(!orderIds){
 request.setAttribute("_ERROR_MESSAGE_", "No Order selected. Cannot print PickSheet");
	return "error";
 }
orderHeaders = delegator.findList("OrderHeader",EntityCondition.makeCondition("orderId",EntityComparisonOperator.IN,orderIds),null,null,null,false);
result = dispatcher.runSync("findOrdersToPickMove",["orderHeaderList":orderHeaders,"facilityId":facilityId,"userLogin":userLogin]);
request.setAttribute("pickMoveInfoList",result.pickMoveInfoList);

return "success";
request.setAttribute("shipmentId", shipmentId);
