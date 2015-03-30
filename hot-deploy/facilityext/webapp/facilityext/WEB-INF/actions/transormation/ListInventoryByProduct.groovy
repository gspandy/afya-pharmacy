
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
productId                 =request.getParameter("productId");
facilityId 				  =request.getParameter("facilityId");
expireDateStart           =request.getParameter("expireDate_fld0_value");
expireDateEnd             =request.getParameter("expireDate_fld1_value");
datetimeManufacturedStart =request. getParameter("datetimeManufactured_fld0_value");
datetimeManufacturedEnd   =request. getParameter("datetimeManufactured_fld1_value");
primaryProductCategoryId  =request. getParameter("primaryProductCategoryId");
ProductInventoryFacilityLocationDivision = []
Timestamp fromDate,ToDate,startDate,endDate;
Date date1,date2;
List<EntityCondition> allCondition = [] as ArrayList;
EntityCondition condition = null;
if   (productId){
	condition=  EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId);
	allCondition.add(condition);
}
if   (primaryProductCategoryId){
	condition=  EntityCondition.makeCondition("primaryProductCategoryId", EntityOperator.EQUALS, primaryProductCategoryId);
	allCondition.add(condition);
}
if   (request. getParameter("inventoryItemId")){
	condition=  EntityCondition.makeCondition("inventoryItemId", EntityOperator.EQUALS, request.getParameter("inventoryItemId"));
	allCondition.add(condition);
}
if   (request.getParameter("facilityId")){
	condition=  EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, request.getParameter("facilityId"));
	allCondition.add(condition);
}
if(datetimeManufacturedStart || datetimeManufacturedEnd){

	 
	 
	if(datetimeManufacturedStart){
		date1 = sdf.parse(datetimeManufacturedStart);
		fromDate = new Timestamp(date1.getTime());
		startDate   =UtilDateTime.getDayStart(fromDate);
		startDateCondition   = EntityCondition.makeCondition("datetimeManufactured",EntityOperator.GREATER_THAN_EQUAL_TO,startDate);
		allCondition.add(startDateCondition);
	}
	if(expireDateEnd){
		date2 = sdf.parse(datetimeManufacturedEnd);
		ToDate = new Timestamp(date2.getTime());
		endDate     =UtilDateTime.getDayEnd(ToDate);
		endDateCondition     = EntityCondition.makeCondition("datetimeManufactured",EntityOperator.LESS_THAN_EQUAL_TO,endDate);
		allCondition.add(endDateCondition);
	}
	
}
if(expireDateStart ||expireDateEnd){
	
	if(expireDateStart){
	  date1 = sdf.parse(expireDateStart);
	  fromDate = new Timestamp(date1.getTime());
	  startDate   =UtilDateTime.getDayStart(fromDate);
	  startDateCondition   = EntityCondition.makeCondition("expireDate",EntityOperator.GREATER_THAN_EQUAL_TO,startDate);
	  allCondition.add(startDateCondition);
	 }
	if(expireDateEnd){
	  date2 = sdf.parse(expireDateEnd);
	  ToDate = new Timestamp(date2.getTime());
	  endDate     =UtilDateTime.getDayEnd(ToDate);
	  endDateCondition     = EntityCondition.makeCondition("expireDate",EntityOperator.LESS_THAN_EQUAL_TO,endDate);
	  allCondition.add(endDateCondition);
	}
	
	DynamicViewEntity dve = new DynamicViewEntity();
	dve.addMemberEntity("PIFD", "ProductInventoryFacilityLocationDivision");
	dve.addMemberEntity("PA", "ProductAttribute");
	
	dve.addAliasAll("PIFD", "");
	dve.addAliasAll("PA", "");
	dve.addViewLink("PIFD", "PA", false, UtilMisc.toList(
	new ModelKeyMap("productId", "productId")));

	EntityCondition c1 = EntityCondition.makeCondition(UtilMisc.toMap(
			"attrName", "EXPIRE_DATE_REQ"));
	EntityCondition c2 = EntityCondition.makeCondition(UtilMisc.toMap(
			"attrValue", "Y"));
	EntityCondition whereEntityCondition = EntityCondition.makeCondition(c1,c2);
	EntityListIterator eli = delegator.findListIteratorByCondition(dve,whereEntityCondition, null, null, null, null);
	List<GenericValue> suppliers = eli.getCompleteList();
	eli.close();
	ProductInventoryFacilityLocationDivision = suppliers;
}
if(!expireDateStart || !expireDateEnd){
ProductInventoryFacilityLocationDivision = delegator.findList("ProductInventoryItem", EntityCondition.makeCondition( allCondition ),null, null, null, false);
}
rows = []
for(int i = 0; i < ProductInventoryFacilityLocationDivision.size(); i++)   {
	resultMap = [:];
	resultMap.inventoryItemId  = ProductInventoryFacilityLocationDivision.get(i).inventoryItemId
	resultMap.productId  = ProductInventoryFacilityLocationDivision.get(i).description
	resultMap. items = ProductInventoryFacilityLocationDivision.get(i).productId
	resultMap. productId = ProductInventoryFacilityLocationDivision.get(i).productId
	resultMap.expireDate  = ProductInventoryFacilityLocationDivision.get(i).expireDate
	resultMap.datetimeManufactured  = ProductInventoryFacilityLocationDivision.get(i).datetimeManufactured
	resultMap.availableToPromiseTotal  = ProductInventoryFacilityLocationDivision.get(i).availableToPromiseTotal
	resultMap.quantityOnHandTotal  = ProductInventoryFacilityLocationDivision.get(i).quantityOnHandTotal
	resultMap.locationSeqId  = ProductInventoryFacilityLocationDivision.get(i).locationSeqId
	rows += resultMap;
} 
context.resultList = rows;
