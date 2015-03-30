
import org.ofbiz.base.util.UtilHttp;
import java.util.*;
import org.ofbiz.entity.condition.*
import org.ofbiz.widget.html.HtmlFormWrapper
import org.ofbiz.entity.*
import org.ofbiz.entity.condition.*
import org.ofbiz.entity.util.*
import org.ofbiz.entity.*
import org.ofbiz.widget.html.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.util.*


facilityId =  request.getParameter("facilityId");
shipmentId =  request.getParameter("shipmentId");
orderId =  request.getParameter("orderId");
orderIdLis = parameters.orderIdList;
print "\n\n\n orderId will generate as:"+shipmentId+"\n\n\n";


    findOptions = new EntityFindOptions();
    findOptions.setDistinct(true);
    ordersShipment = delegator.findList("OrderShipment",EntityCondition.makeCondition("orderId",EntityComparisonOperator.IN,orderIdLis),UtilMisc.toSet("shipmentId"),null,findOptions,false);
  if(ordersShipment.size()>0){
    GenericValue value = EntityUtil.getFirst(ordersShipment);
	shipmentId = value.shipmentId;
	}
result = dispatcher.runSync("createPicklistFromOrders",["orderIdList":orderIdLis,"facilityId":facilityId,"userLogin":userLogin]);
if(ordersShipment.size()>0){
GenericValue picklist = delegator.findOne("Picklist", ["picklistId":result.picklistId], false);
picklist.setString("shipmentId",shipmentId);
delegator.store(picklist);
}
request.setAttribute("picklistId", result.picklistId);
return "success";



