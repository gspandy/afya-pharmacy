import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.widget.html.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.widget.html.HtmlFormWrapper
import org.ofbiz.entity.*
import org.ofbiz.entity.condition.*
import org.ofbiz.entity.util.*

mode = request.getParameter("mode");
shipmentId = request.getParameter("shipmentId");
picklistId = request.getParameter("picklistId");
if (!shipmentId) {
    shipmentId = context.shipmentId;
	context.shipmentId = shipmentId;
}
action = request.getParameter("action");
shipment = null;
if (shipmentId) {

    shipment = delegator.findOne("Shipment", [shipmentId : shipmentId], false);
}  

    whereCondition = EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId);
   List orderPick = []as ArrayList;
    findOptions = new EntityFindOptions();
    findOptions.setDistinct(true);
  if (shipment != null) {
    orderPick = delegator.findList("PicklistAndBinAndItem", EntityCondition.makeCondition([shipmentId : shipment.shipmentId,statusId : "PICKLIST_PICKED"]), 
    UtilMisc.toSet("orderId","picklistBinId","facilityId"), null, findOptions, false);
    }
    
String result=null;
List pickBig = []as ArrayList;
List pick4 = [] as ArrayList;
for(GenericValue order : orderPick){
List pick = [];
pick = delegator.findList("ItemIssuance", EntityCondition.makeCondition([orderId : order.orderId]), null, null, null, false);
if (pick.size()>0 ) {
result="Yes";
pickBig.add(result);
pick4.add(true);
}
else{
result="No";
pickBig.add(result);
pick4.add(false);
}
}
Map pick2 = [:];
for(int i=0 ; i < pickBig.size() ; i++){
	pick2.put(orderPick.get(i).getString("picklistBinId"),pickBig.get(i));
}
Map pick5 = [:];
for(int i=0 ; i < pick4.size() ; i++){
	pick5.put(orderPick.get(i).getString("picklistBinId"),pick4.get(i));
}
context.pick3=pick2;
context.pick6=pick5;
context.picklistBin = orderPick;
request.setAttribute("mode", mode);
request.setAttribute("shipmentId", shipmentId);
return "success";