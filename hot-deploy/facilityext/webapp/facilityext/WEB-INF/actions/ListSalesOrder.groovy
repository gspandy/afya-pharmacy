import java.util.*;
import java.text.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.product.inventory.InventoryWorker;
import org.ofbiz.entity.GenericValue;
import java.sql.Timestamp;
import java.text.*;
import javolution.util.FastMap;
import org.ofbiz.base.util.*;

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

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
Timestamp fromDate,ToDate,startDate,endDate;

orderId                =  request.getParameter("orderId");
orderDateStart         =  request.getParameter("orderDate_fld0_value");
EndDateEnd             =  request.getParameter("orderDate_fld1_value");
routeId                =  request.getParameter("routeId")
partyId                =  request.getParameter("partyId")

List<EntityCondition> allCondition = [] as ArrayList;
EntityCondition condition = null;

if   (routeId){
	condition=  EntityCondition.makeCondition("routeId", EntityOperator.EQUALS, routeId);
    allCondition.add(condition);


} 
if   (orderId){
	condition=  EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId);
	allCondition.add(condition);
} 
if   (partyId){
	condition=  EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
	allCondition.add(condition);
}

condition=  EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_APPROVED");
allCondition.add(condition);
condition=  EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_FROM_VENDOR");
allCondition.add(condition);

if(orderDateStart || EndDateEnd){
	if(orderDateStart){
		date1 = sdf.parse(orderDateStart);
		fromDate = new Timestamp(date1.getTime());
		startDate   =UtilDateTime.getDayStart(fromDate);
		startDateCondition   = EntityCondition.makeCondition("orderDate",EntityOperator.GREATER_THAN_EQUAL_TO,startDate);
		allCondition.add(startDateCondition);
	}
	if(EndDateEnd){
		date2 = sdf.parse(EndDateEnd);
		ToDate = new Timestamp(date2.getTime());
		endDate     =UtilDateTime.getDayEnd(ToDate);
		endDateCondition     = EntityCondition.makeCondition("orderDate",EntityOperator.LESS_THAN_EQUAL_TO,endDate);
		allCondition.add(endDateCondition);
	}
	
}
EntityCondition whereEntityCondition = EntityCondition.makeCondition(allCondition);

DynamicViewEntity dve = new DynamicViewEntity();
dve.addMemberEntity("OHAR", "OrderHeaderAndRoles");
dve.addMemberEntity("PG", "PartyGroup");

dve.addAliasAll("OHAR", "");
dve.addAliasAll("PG", "");
dve.addViewLink("OHAR", "PG", false, UtilMisc.toList(
new ModelKeyMap("partyId", "partyId")));


EntityListIterator eli = delegator.findListIteratorByCondition(dve, whereEntityCondition, null,  null, null, null);
List<GenericValue> route = eli.getCompleteList();
eli.close();

rows = []
newRoute = []
HashSet ref = new HashSet(route);  
newRoute = new ArrayList<Object>(ref);

for(int i = 0; i < newRoute.size(); i++)   {
	resultMap = [:];
	resultMap.orderId  = newRoute.get(i).orderId
	resultMap.partyId  = newRoute.get(i).partyId
	resultMap.priority  = newRoute.get(i).priority
	resultMap.entryDate  = newRoute.get(i).entryDate
	resultMap.orderDate  = newRoute.get(i).orderDate
	resultMap.desc       = delegator.findByPrimaryKey("StatusItem",[statusId:newRoute.get(i).statusId]).description ;
	resultMap.statusId  = newRoute.get(i).statusId
	resultMap.roleTypeId  = newRoute.get(i).roleTypeId
	resultMap.transactionId  = newRoute.get(i).transactionId
    rows += resultMap;
} 

context.resultList = rows;
context.size = route.size();
return "success"