import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator

import java.sql.Timestamp
import java.text.SimpleDateFormat;
import java.util.*;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;

productId = parameters.productId;
fromDate = parameters.fromDate;
thruDate = parameters.thruDate;
employeeId = parameters.employeeId
departmentId = parameters.departmentId

inventoryItemIdList =[];
individualPpeRecordList = [];
if(UtilValidate.isNotEmpty(productId)){
    List conditions = [];
	conditions.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
    getDateFilterConditions(conditions);
    getListOfEmployeesToFilter(conditions);
	EntityCondition cn = EntityCondition.makeCondition(conditions,EntityOperator.AND);
    employeeItemIssuanceList = delegator.findList("EmployeeItemIssuanceInventoryItem", EntityCondition.makeCondition(conditions,EntityOperator.AND), ["employeeId","quantity","issuedOn","productId"] as Set, null, null, false);
    generateIndividualPpeRecordList(employeeItemIssuanceList);
}else{
    commonConditions = [];
    getDateFilterConditions(commonConditions);
    getListOfEmployeesToFilter(commonConditions);
    inventoryItem = delegator.findList("InventoryItem",null,null,null,null,false);
    for(GenericValue item:inventoryItem){
        conditions = [];
        conditions.addAll(commonConditions);
        conditions.add(EntityCondition.makeCondition("inventoryItemId",item.inventoryItemId));
        employeeItemIssuanceList = delegator.findList("EmployeeItemIssuanceInventoryItem", EntityCondition.makeCondition(conditions,EntityOperator.AND), ["employeeId","quantity","issuedOn","productId"] as Set, null, null, false);
        generateIndividualPpeRecordList(employeeItemIssuanceList);
    }
}

def getListOfEmployeesToFilter(List conditions){
    if(UtilValidate.isNotEmpty(employeeId)){
        conditions.add(EntityCondition.makeCondition("employeeId",EntityOperator.EQUALS,employeeId))
    }else if(UtilValidate.isNotEmpty(departmentId)){
        employeeIdList=[];
        partyRelationshipList = delegator.findByAnd("PartyRelationship", ["partyIdFrom":departmentId,"roleTypeIdTo":"EMPLOYEE"],null,false);
        for(GenericValue partyRelationship:partyRelationshipList){
            employeeIdList.add(partyRelationship.partyIdTo)
        }
        conditions.add(EntityCondition.makeCondition("employeeId",EntityOperator.IN,employeeIdList));
    }
}

def getDateFilterConditions(List conditions){
    if(UtilValidate.isNotEmpty(fromDate)){
        conditions.add(EntityCondition.makeCondition("issuedOn",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(dateParser(fromDate))));
    }

    if(UtilValidate.isNotEmpty(thruDate)){
        conditions.add(EntityCondition.makeCondition("issuedOn",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayEnd(dateParser(thruDate))));
    }
}

def dateParser(String date){
    SimpleDateFormat dateFormat = new SimpleDateFormat(UtilDateTime.DATE_FORMAT);
    formattedDate =  dateFormat.parse(date);
    return new Timestamp(formattedDate.getTime());
}


def generateIndividualPpeRecordList(employeeItemIssuanceList){

    for(GenericValue employeeItemIssuance:employeeItemIssuanceList){
        individualPpeRecordMap = [:];
        GenericValue employeeDetail = delegator.findOne("Person",["partyId":employeeItemIssuance.employeeId],false);
        if(employeeDetail!=null && employeeDetail.size()!=0){
			GenericValue product = delegator.findOne("Product",["productId":employeeItemIssuance.productId],false);
            individualPpeRecordMap.put("employeeName",employeeDetail.firstName);
            individualPpeRecordMap.put("employeeNumber",employeeItemIssuance.employeeId);
            individualPpeRecordMap.put("quantity",employeeItemIssuance.quantity);
            individualPpeRecordMap.put("issuedOn",employeeItemIssuance.issuedOn);
            individualPpeRecordMap.put("materialDescription",product.productId + " - " + product.description);
            individualPpeRecordList.add(individualPpeRecordMap);
        }
    }
}

context.individualPpeRecordList = individualPpeRecordList;