import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;



GenericValue inventoryRequisition = delegator.findOne("InventoryRequisition", UtilMisc.toMap("invRequisitionId",request.getParameter("invRequisitionId")), false);

EntityCondition condition = EntityCondition.makeCondition("invRequisitionId",EntityOperator.EQUALS,request.getParameter("invRequisitionId"));

List<GenericValue> inventoryRequisitionItemList = delegator.findList("InventoryRequisitionItem",condition,null,null,null,false);

context.inventoryRequisition = inventoryRequisition;
context.inventoryRequisitionItemList = inventoryRequisitionItemList;