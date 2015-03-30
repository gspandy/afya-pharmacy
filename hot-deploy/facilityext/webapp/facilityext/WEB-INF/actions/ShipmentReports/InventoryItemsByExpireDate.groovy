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

fiscalYear = ( parameters.fiscalYear) ;
month = parameters.month;
quarterMonth = (parameters.quarterMonth);
 
EntityCondition condition = null;
List<EntityCondition> allCondition = [] as ArrayList;
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

tempList = [];
monthList = [];
quarterMonthList = [];
fiscalList  = [];
allDateTempList = [];
InventoryItemFactList = [];
 def Quarters = [
 "First" : 1,
 "Second" : 2,
 "Third" :3 ,
 "Fourth" :4]
    java.sql.Date date1;

   Timestamp actualDate,beforeThreeMonths,tempDateStart,tempDateEnd;
if   (fiscalYear){
   		    long fiscalYearLong = Long.parseLong( parameters.fiscalYear) ;
	         condition = EntityCondition.makeCondition("yearName", EntityOperator.EQUALS, fiscalYearLong);
           	 tempList = delegator.findList("DateDimension", condition,null, null, null, false);
             fiscalList +=  tempList;  
} 
if   (month){
	          condition=  EntityCondition.makeCondition("monthName", EntityOperator.EQUALS, month);
        	  tempList = delegator.findList("DateDimension", condition,null, null, null, false);
        	  monthList += tempList;
} 
if   (quarterMonth){
	         condition=  EntityCondition.makeCondition("yearAndMonth", EntityOperator.EQUALS, quarterMonth);
        	 tempList = delegator.findList("DateDimension", condition,null, null, null, false);
        	 quarterMonthList += tempList;
} 

 allDateTempList = monthList + fiscalList + quarterMonthList ;


  if(monthList || month )  {
  allDateTempList  = allDateTempList.grep(monthList);
  }
  
  if( fiscalList || fiscalYear )  {
   allDateTempList  =allDateTempList.grep(fiscalList);
  }
  
  if( quarterMonthList || quarterMonth )  {
   allDateTempList  =allDateTempList.grep(quarterMonthList);
   }
 
for(int i = 0; i < allDateTempList.size(); i++)   {

           
            if(monthList && (allDateTempList.get(i).monthName)){
            date1 = (allDateTempList.get(i).dateValue);
                   if(date1)
                     actualDate  = new Timestamp(date1.getTime()); 
                     tempDateEnd= UtilDateTime.getMonthEnd(actualDate,timeZone,locale);
                     tempDateStart= UtilDateTime.getMonthStart(actualDate);
                       condition = EntityCondition.makeCondition(
                                          EntityCondition.makeCondition("expireDate", EntityOperator.GREATER_THAN_EQUAL_TO,tempDateStart),
                                                 EntityOperator.AND,
                                          EntityCondition.makeCondition("expireDate", EntityOperator.LESS_THAN_EQUAL_TO,tempDateEnd)
                                  );
                      InventoryItemFactList +=   delegator.findList("InventoryItemFact", condition,null, null, null, false);         
           }
     
                
            if(quarterMonthList && (allDateTempList.get(0).yearAndMonth)){
                date1 = (allDateTempList.get(i).dateValue);
                   if(date1)
                     actualDate  = new Timestamp(date1.getTime()); 
                     tempDateEnd= UtilDateTime.getMonthEnd(actualDate,timeZone,locale);
                     tempDateStart= UtilDateTime.getMonthStart(actualDate);
                     tempDateStart.setMonth(tempDateStart.getMonth()-3);
                     
                      condition = EntityCondition.makeCondition(
                                          EntityCondition.makeCondition("expireDate", EntityOperator.GREATER_THAN_EQUAL_TO,tempDateStart),
                                                 EntityOperator.AND,
                                          EntityCondition.makeCondition("expireDate", EntityOperator.LESS_THAN_EQUAL_TO,tempDateEnd)
                                  );
                      InventoryItemFactList +=   delegator.findList("InventoryItemFact", condition,null, null, null, false);      
         
            }
            
            if(fiscalList && (allDateTempList.get(0).yearName)){
    
                date1 = (allDateTempList.get(i).dateValue);
                   if(date1)
                     actualDate  = new Timestamp(date1.getTime()); 
                     tempDateEnd= UtilDateTime.getYearEnd(actualDate,timeZone,locale);
                     tempDateStart= UtilDateTime.getYearStart(actualDate);
                    
                      condition = EntityCondition.makeCondition(
                                          EntityCondition.makeCondition("expireDate", EntityOperator.GREATER_THAN_EQUAL_TO,tempDateStart),
                                                 EntityOperator.AND,
                                          EntityCondition.makeCondition("expireDate", EntityOperator.LESS_THAN_EQUAL_TO,tempDateEnd)
                                  );
                      InventoryItemFactList +=   delegator.findList("InventoryItemFact", condition,null, null, null, false);      
         }
}
           rows = [];
                        for(int i = 0; i < InventoryItemFactList.size(); i++)   {
						  		resultMap = [:];
					 			resultMap.inventoryItemId  = InventoryItemFactList.get(i).inventoryItemId
								resultMap.expireDate  = InventoryItemFactList.get(i).expireDate
								resultMap.datetimeReceived  = InventoryItemFactList.get(i).datetimeReceived
								resultMap.productId  = InventoryItemFactList.get(i).productId
								resultMap.locationSeqId  = InventoryItemFactList.get(i).locationSeqId
							    
						        BigDecimal truncated= InventoryItemFactList.get(i).quantityOnHandTotal.setScale(0,BigDecimal.ROUND_DOWN);
								resultMap.quantityOnHandTotal  = truncated
								resultMap.productId  = InventoryItemFactList.get(i).productId
								truncated= InventoryItemFactList.get(i).availableToPromiseTotal.setScale(0,BigDecimal.ROUND_DOWN);
								resultMap.availableToPromiseTotal  = truncated
						
 					   rows += resultMap;
						} 

context.resultList = rows;
context.size = InventoryItemFactList.size();

resultList=  [];


context.resultList = rows; 
session.setAttribute("Inventory_Summary_List", rows);
return "success"