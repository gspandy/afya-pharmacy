package org.ofbiz.product.facility.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceValidationException;

public class FacilityReportUtil {


    public static BigDecimal getOpeningOrClosingBalance(GenericDispatcher dispatcher, String productId, Timestamp date, String facilityId) throws ServiceValidationException, GenericServiceException, GenericEntityException {
        GenericDelegator delegator = (GenericDelegator) dispatcher.getDelegator();
        if (UtilValidate.isEmpty(productId) || UtilValidate.isEmpty(date))
            return BigDecimal.ZERO;
        Map<String, Object> resultOutput = dispatcher.runSync("countProductInventoryOnHand", UtilMisc.toMap("userLogin",delegator.findOne("UserLogin",false,UtilMisc.toMap("userLoginId","system")),"inventoryCountDate",date,"productId",productId,"facilityId",facilityId));
        BigDecimal quantityOnHand = (BigDecimal) resultOutput.get("quantityOnHandTotal");
        return quantityOnHand;
    }

   /* public static BigDecimal getOpeningOrClosingBalance(GenericDispatcher dispatcher, String productId, Timestamp fromDate, Timestamp thruDate, String facilityId) throws ServiceValidationException, GenericServiceException, GenericEntityException {
        GenericDelegator delegator = (GenericDelegator) dispatcher.getDelegator();
        Map<String, Object> resultOutput = dispatcher.runSync("countProductInventoryOnHand", UtilMisc.toMap("userLogin",delegator.findOne("UserLogin",false,UtilMisc.toMap("userLoginId","system")), "inventoryCountDate",thruDate,"productId",productId,"facilityId",facilityId));
        BigDecimal quantityOnHand = (BigDecimal) resultOutput.get("quantityOnHandTotal");
        return quantityOnHand;
    }*/


    public static BigDecimal getReceivedQty(GenericDispatcher dispatcher, String productId, Timestamp fromDate, Timestamp thruDate, String facilityId) throws GenericEntityException, ServiceValidationException, GenericServiceException {
        GenericDelegator delegator = (GenericDelegator) dispatcher.getDelegator();
        DynamicViewEntity dve = new DynamicViewEntity();
        dve.setEntityName("ShipmentReceiptSum");
        dve.addMemberEntity("SR","ShipmentReceipt");
        dve.addAlias("SR","productId","productId","",false,true,"");
        dve.addAlias("SR","quantityAcceptedTotal","quantityAccepted","",false,false,"sum");
        dve.addAlias("SR","datetimeReceived","datetimeReceived","",false,false,"");

        List<EntityCondition> entityConditions = new ArrayList<EntityCondition>(5);
        if(UtilValidate.isNotEmpty(productId))
            entityConditions.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));

        entityConditions.add(EntityCondition.makeCondition("datetimeReceived", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(fromDate)));
        entityConditions.add(EntityCondition.makeCondition("datetimeReceived", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thruDate)));

        EntityCondition condition = EntityCondition.makeCondition(entityConditions, EntityOperator.AND);

        EntityListIterator iterator = delegator.findListIteratorByCondition(dve,condition,null,null,null,null);
        List r = iterator.getCompleteList();
        BigDecimal quantity = BigDecimal.ZERO;
        GenericValue gv = EntityUtil.getFirst(r);
        if(gv != null)
            quantity = gv.getBigDecimal("quantityAcceptedTotal");

        iterator.close();
        return quantity;
    }
    
    public static BigDecimal getProductionQty(GenericDispatcher dispatcher, String productId, Timestamp fromDate, Timestamp thruDate, String facilityId) throws GenericEntityException, ServiceValidationException, GenericServiceException {
        GenericDelegator delegator = (GenericDelegator) dispatcher.getDelegator();
        DynamicViewEntity dve = new DynamicViewEntity();
        dve.setEntityName("WorkEffortInventoryProducedSum");
        dve.addMemberEntity("WE","WorkEffort");
        dve.addMemberEntity("WEIP","WorkEffortInventoryProduced");
        dve.addMemberEntity("IID","InventoryItemDetail");
        dve.addMemberEntity("II","InventoryItem");
        dve.addAlias("II","productId","","",false,true,"");
        dve.addAlias("II","facilityId","","",false,false,"");
        dve.addAlias("IID","effectiveDate","","",false,false,"");
        dve.addAlias("IID","quantityOnHandDiff","","",false,false,"sum");
        dve.addViewLink("WEIP","WE",false, UtilMisc.toList(new ModelKeyMap("workEffortId",null)));
        dve.addViewLink("WEIP","IID",false, UtilMisc.toList(new ModelKeyMap("workEffortId",null),new ModelKeyMap("inventoryItemId",null)));
        dve.addViewLink("IID","II",false, UtilMisc.toList(new ModelKeyMap("inventoryItemId",null)));

        List<EntityCondition> entityConditions = new ArrayList<EntityCondition>(5);

        EntityCondition cn1 = null;
        if(UtilValidate.isNotEmpty(productId))
            entityConditions.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));

        entityConditions.add(EntityCondition.makeCondition("effectiveDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getDayStart(fromDate)));
        entityConditions.add(EntityCondition.makeCondition("effectiveDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.getDayEnd(thruDate)));
        if(UtilValidate.isNotEmpty(facilityId))
            entityConditions.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));

        EntityCondition condition = EntityCondition.makeCondition(entityConditions, EntityOperator.AND);

        EntityListIterator iterator = delegator.findListIteratorByCondition(dve,condition,null,null,null,null);
        List r = iterator.getCompleteList();
        BigDecimal quantity = BigDecimal.ZERO;
        GenericValue gv = EntityUtil.getFirst(r);
        if(gv != null)
        	quantity = gv.getBigDecimal("quantityOnHandDiff");

        iterator.close();
        return quantity;
    }
    
    public static BigDecimal getReceivedAndProducedQty(GenericDispatcher dispatcher, String productId, Timestamp fromDate, Timestamp thruDate, String facilityId) throws GenericEntityException, ServiceValidationException, GenericServiceException {
    	BigDecimal receivedQty = FacilityReportUtil.getReceivedQty(dispatcher, productId, fromDate, thruDate, facilityId);
    	BigDecimal productionQty = FacilityReportUtil.getProductionQty(dispatcher, productId, fromDate, thruDate, facilityId);
    	return receivedQty.add(productionQty);
    }
    
    public static BigDecimal getRequisitionIssuedQtyDuringPeriod(GenericDelegator delegator, String productId, String facilityId, Timestamp fromDate, Timestamp thruDate) throws GenericEntityException{
    	List<EntityCondition> conditions = new ArrayList<EntityCondition>();
    	BigDecimal qty = BigDecimal.ZERO;
    	if(UtilValidate.isNotEmpty(productId))
    		conditions.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
    	if(UtilValidate.isNotEmpty(facilityId))
    		conditions.add(EntityCondition.makeCondition("facilityIdFrom",EntityOperator.EQUALS,facilityId));
    	if(UtilValidate.isNotEmpty(fromDate))
    		conditions.add(EntityCondition.makeCondition("transferDate",EntityOperator.GREATER_THAN_EQUAL_TO,UtilDateTime.getDayStart(fromDate)));
    	if(UtilValidate.isNotEmpty(thruDate))
    		conditions.add(EntityCondition.makeCondition("transferDate",EntityOperator.LESS_THAN_EQUAL_TO,UtilDateTime.getDayEnd(thruDate)));
    	conditions.add(EntityCondition.makeCondition("requestType",EntityOperator.EQUALS,"Request"));
    	List<GenericValue> invRequisitionInvReqItems = delegator.findList("InvRequisitionInvReqItem", EntityCondition.makeCondition(conditions), null, null, null, false);
    	for(GenericValue gv : invRequisitionInvReqItems){
    		if(gv.getBigDecimal("acceptedQuantity") != null)
    			qty = qty.add(gv.getBigDecimal("acceptedQuantity"));
    	}
    	
    	return qty;
    }
    
    
    

    public static double getStandardDeviation(List<BigDecimal> numbers) {
        double sum = 0;
        for (int i = 0; i < numbers.size(); i++) {
            sum = sum + numbers.get(i).doubleValue();
        }
        double mean = sum / numbers.size();
        double[] deviations = new double[numbers.size()];
        for (int i = 0; i < numbers.size(); i++) {
            deviations[i] = numbers.get(i).doubleValue() - mean;
        }
        double[] squares = new double[numbers.size()];
        for (int i = 0; i < squares.length; i++) {
            squares[i] = deviations[i] * deviations[i];
        }
        sum = 0;
        for (int i = 0; i < squares.length; i++) {
            sum = sum + squares[i];
        }
        double result = sum / (numbers.size() - 1);
        double standardDeviation = Math.sqrt(result);
        return standardDeviation;
    }

    public static BigDecimal getMinValue(List<BigDecimal> array) {
        BigDecimal minValue = array.get(0);
        for (int i = 1; i < array.size(); i++) {
            if (array.get(i).compareTo(minValue) < 0) {
                minValue = array.get(i);
            }
        }
        return minValue;
    }

    public static BigDecimal getMaxValue(List<BigDecimal> array) {
        BigDecimal maxValue = array.get(0);
        for (int i = 1; i < array.size(); i++) {
            if (array.get(i).compareTo(maxValue) > 0) {
                maxValue = array.get(i);

            }
        }
        return maxValue;
    }

}
