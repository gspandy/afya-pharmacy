import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator

import java.sql.Timestamp
import java.text.SimpleDateFormat;
import java.util.*;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate


facilityId = parameters.facilityId;
String departmentId = parameters.departmentId;
productId = parameters.productId;
productCategoryId = parameters.productCategoryId;
fromDate = parameters.fromDate;
thruDate = parameters.thruDate;

conditions = [];
departmentIssueReportMapGroupedByProductCategory = [:];
productCategoryDesc = null;


if(UtilValidate.isNotEmpty(departmentId)){
    facilitiesForCorrespondingDepartment = delegator.findByAnd("Facility",["ownerPartyId" : departmentId],null,false);
    facilityIdListForCorrespondingDepartment=[];
    for (GenericValue value:facilitiesForCorrespondingDepartment){
        facilityIdListForCorrespondingDepartment.add(value.facilityId)
    }
    conditions.add(EntityCondition.makeCondition("facilityIdFrom",EntityOperator.IN,facilityIdListForCorrespondingDepartment));
}


if(UtilValidate.isNotEmpty(fromDate)){
    conditions.add(EntityCondition.makeCondition("requestDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(UtilDateTime.dateStringToTimestampParser(fromDate))));
}

if(UtilValidate.isNotEmpty(thruDate)){
    conditions.add(EntityCondition.makeCondition("requestDate",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayEnd(UtilDateTime.dateStringToTimestampParser(thruDate))));
}
conditions.add(EntityCondition.makeCondition("facilityIdTo",facilityId));
conditions.add(EntityCondition.makeCondition("status",EntityOperator.IN,["Complete","Approved"]));

requisitionList = delegator.findList("InventoryRequisition", EntityCondition.makeCondition(conditions,EntityOperator.AND), ["invRequisitionId","status","requestDate"] as Set, null, null, false);

inventoryRequisitionConditionsGlobal=[];
if(UtilValidate.isNotEmpty(productId)){
    inventoryRequisitionConditionsGlobal.add(EntityCondition.makeCondition("productId",productId));
    productCategoryList = delegator.findByAnd("ProductCategoryMember",["productId":productId],null,false);
    if(productCategoryList!=null && productCategoryList.size()>0){
        productCategory = delegator.findOne("ProductCategory",["productCategoryId":productCategoryList[0].productCategoryId],false);
        productCategoryDesc = productCategory.description;
    }
    mapCategoryMap = [:];
    mapCategoryMap.put(productCategoryDesc,[])
    departmentIssueReportMapGroupedByProductCategory.put(productCategoryDesc,[]);
}else if(UtilValidate.isNotEmpty(productCategoryId)){
    productIdList=[];
    ProductCategoryMemberList = delegator.findByAnd("ProductCategoryMember",["productCategoryId":productCategoryId],null,false);
    for(GenericValue productCategoryMember:ProductCategoryMemberList){
        productIdList.add(productCategoryMember.productId);
    }
    inventoryRequisitionConditionsGlobal.add(EntityCondition.makeCondition("productId",EntityOperator.IN,productIdList));
    productCategory = delegator.findOne("ProductCategory",["productCategoryId":productCategoryId],false);
    productCategoryDesc = productCategory.description;
    mapCategoryMap = [:];
    mapCategoryMap.put(productCategoryDesc,[])
    //departmentIssueReportMapGroupedByProductCategory.add(mapCategoryMap);
    departmentIssueReportMapGroupedByProductCategory.put(productCategoryDesc,[]);
}

for(GenericValue value:requisitionList){
    invRequisitionId = value.get("invRequisitionId");
    inventoryRequisitionConditions=[];
    inventoryRequisitionConditions.addAll(inventoryRequisitionConditionsGlobal)
    inventoryRequisitionConditions.add(EntityCondition.makeCondition("invRequisitionId",invRequisitionId))
    requisitionItemList = delegator.findList("InventoryRequisitionItem", EntityCondition.makeCondition(inventoryRequisitionConditions,EntityOperator.AND), ["productId","quantity","glAccountCategoryId"] as Set, null, null, false);
    for (GenericValue items:requisitionItemList){
        requisitionItemProductId = items.get("productId");
        glAccountCategoryId = items.get("glAccountCategoryId");
        productDetails = delegator.findOne("Product",["productId":requisitionItemProductId],false);
        if(null!=productDetails && productDetails.size()!=0){
            productUnitDetails =  delegator.findOne("Uom",["uomId":productDetails.quantityUomId],false);
        }
        costCenter = delegator.findOne("GlAccountCategory",["glAccountCategoryId":glAccountCategoryId],false);
        inventoryTransferDetails = delegator.findList("InventoryTransfer", EntityCondition.makeCondition("invRequisitionId",invRequisitionId), ["inventoryItemId"] as Set, null, null, false);
        if(inventoryTransferDetails.size()==0){
            return "success"
        }
        inventoryItem = getInventoryItem(requisitionItemProductId,inventoryTransferDetails);
        unitCost = inventoryItem[0].unitCost;
        createMap(value,items,productDetails,productUnitDetails,unitCost,costCenter,productCategoryDesc);
    }
}



def createMap(value,items,productDetails,productUnitDetails,unitCost,costCenter,productCategoryDesc){
    departmentIssueReportListOfProducts = [];
    departmentIssueReportMapBasedOnFields = [:];
    departmentIssueReportMapBasedOnFields.putAll(value);
    departmentIssueReportMapBasedOnFields.putAll(items);
    departmentIssueReportMapBasedOnFields.put("productDescription",productDetails.description);
    if(null!=productUnitDetails && productUnitDetails.size()!=0){
        departmentIssueReportMapBasedOnFields.put("unitDescription",productUnitDetails.description);
    }
    departmentIssueReportMapBasedOnFields.put("unitCost",unitCost);
   if(null!=costCenter && costCenter.size()!=0){
       departmentIssueReportMapBasedOnFields.put("costCenter",costCenter.description)
   }
    if(productCategoryDesc!=null){
        /*departmentIssueReportListOfProducts = departmentIssueReportMapGroupedByProductCategory[0].get(productCategoryDesc);*/
        departmentIssueReportListOfProducts = departmentIssueReportMapGroupedByProductCategory.get(productCategoryDesc);
        departmentIssueReportListOfProducts.add(departmentIssueReportMapBasedOnFields);
    }else{
	
		List productCatAndMemberCondition = [];
		productCatAndMemberCondition.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productDetails.productId));
		productCatAndMemberCondition.add(EntityCondition.makeCondition("productCategoryTypeId",EntityOperator.NOT_EQUAL,"TAX_CATEGORY"));
		List productCategoryList = delegator.findList("ProductCategoryAndMember",EntityCondition.makeCondition(productCatAndMemberCondition,EntityOperator.AND),null,null,null,false);
        if(UtilValidate.isNotEmpty(productCategoryList)){
             productCategoryDesc = productCategoryList.get(0).description;
             addProductCategoryToMap(productCategoryDesc);

        }else{
            productCategoryDesc = "Default"
            addProductCategoryToMap(productCategoryDesc);
        }
		
    }

}

def addProductCategoryToMap(String productCategoryDesc){
    if(departmentIssueReportMapGroupedByProductCategory.get(productCategoryDesc)!=null){
        departmentIssueReportList =departmentIssueReportMapGroupedByProductCategory.get(productCategoryDesc);
        departmentIssueReportList.add(departmentIssueReportMapBasedOnFields);
    }else{
        departmentIssueReportMapGroupedByProductCategory.put(productCategoryDesc,[departmentIssueReportMapBasedOnFields]);
    }
}

def getInventoryItem(productId,inventoryTransferDetails){
    listOfInventoryReqId = getListOfInventoryReqId(inventoryTransferDetails);
    conditions = [];
    conditions.add(EntityCondition.makeCondition("inventoryItemId",EntityOperator.IN,listOfInventoryReqId));
    conditions.add(EntityCondition.makeCondition("productId",productId));
    inventoryItem = delegator.findList("InventoryItem", EntityCondition.makeCondition(conditions,EntityOperator.AND), ["unitCost"] as Set, null, null, false);
    if(inventoryItem.size()==0){
       return "success"
    }
    return inventoryItem;
}

def getListOfInventoryReqId(inventoryTransferDetail){
    list = [];
    for(GenericValue value:inventoryTransferDetail){
       list.add(value.inventoryItemId)
    }
    if(list.size()==0){
        return  "success";
    }
    return  list;
}


context.departmentIssueReportMapGroupedByProductCategory = departmentIssueReportMapGroupedByProductCategory;