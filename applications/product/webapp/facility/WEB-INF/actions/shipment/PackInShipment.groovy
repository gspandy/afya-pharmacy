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
import org.ofbiz.order.order.*;


shipmentId = request.getParameter("shipmentId");
orderId = request.getParameter("orderId");
facilityId = request.getParameter("facilityId");
picklistBinId  = request.getParameter("picklistBinId");
packSession = session.getAttribute("packingSession");

Set<Object> packedOrderItems = new HashSet<Object>();
if(packSession)
for(org.ofbiz.shipment.packing.PackingSessionLine pslSession : packSession.getLines()){
	packedOrderItems.add(pslSession.getOrderId());
}
List<Object> packList = new ArrayList<Object>(packedOrderItems);
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
    if (shipmentId) {
    orderPick = delegator.findList("PicklistAndBinAndItem", whereCondition, UtilMisc.toSet("orderId","picklistBinId","facilityId","shipmentId"), null, findOptions, false);
    }
String result=null;
List pickBig = []as ArrayList;
List pick4 = [] as ArrayList;
for(GenericValue order : orderPick){
List pick = [];
pick = delegator.findList("ItemIssuance", EntityCondition.makeCondition([orderId : order.orderId,shipmentId:shipmentId]), null, null, null, false);
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
String resultNew=null;
List pickBigNew = []as ArrayList;
for(GenericValue order : orderPick){
	if(packList.contains(order.getString("orderId"))){
	result="Yes";
	pickBigNew.add(result);
	}
	else{
	result="No";
	pickBigNew.add(result);
	}
}
		
Map pick7 = [:];
for(int i=0 ; i < pickBigNew.size() ; i++){
pick7.put(orderPick.get(i).getString("picklistBinId"),pickBigNew.get(i));
}
context.pick8=pick7;	
context.pick3=pick2;
context.pick6=pick5;
request.setAttribute("facilityId", facilityId);
context.pickLists = orderPick;
request.setAttribute("shipmentId", shipmentId);





