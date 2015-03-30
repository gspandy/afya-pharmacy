
import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;


  
 EntityCondition genericsCondition = EntityCondition.makeCondition( EntityCondition.makeCondition("sendDate", EntityOperator.GREATER_THAN_EQUAL_TO,
		UtilDateTime.getDayStart(filterDate)),EntityOperator.AND, 
				 EntityCondition.makeCondition("sendDate", EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayEnd(filterDate)));
				 
	EntityCondition condition1 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "IXF_COMPLETE");
	EntityCondition condition2 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "IXF_REQUESTED");
	EntityCondition condition3 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "IXF_CANCELLED");
	EntityCondition condition4 = EntityCondition.makeCondition([condition1,condition2,condition3],EntityOperator.OR)  
				  
				  EntityCondition statusCondition = EntityCondition.makeCondition(genericsCondition,EntityOperator.AND,condition4);
				  
				  
	EntityCondition cond = EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId);
	EntityCondition cond1 = EntityCondition.makeCondition("facilityIdTo", EntityOperator.EQUALS, facilityId);
	EntityCondition bothFacilityCondition = EntityCondition.makeCondition([cond,cond1],EntityOperator.OR);
				 
    EntityCondition conditions = EntityCondition.makeCondition(statusCondition,EntityOperator.AND,bothFacilityCondition);
  
  inventoryTransferToday = delegator.findList("InventoryTransfer", conditions, null, null, null, false);
  

context.inventoryTransferToday = inventoryTransferToday;
context.filterDate = filterDate;
 
