import java.math.BigDecimal;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityCondition;

import com.ndz.util.ProductHelper;

String user = userLogin.userLoginId
String inventoryItemId = parameters.inventoryItemId;
String action = parameters.action;

GenericValue inventoryItem = delegator.findByPrimaryKey("InventoryItem",[inventoryItemId: parameters.inventoryItemId]);
BigDecimal qoh = inventoryItem.getBigDecimal("quantityOnHandTotal");
BigDecimal atp = inventoryItem.getBigDecimal("availableToPromiseTotal");

String quantityToReserve = parameters.quantityToCancel;
if(quantityToReserve){
BigDecimal quantity = new BigDecimal(quantityToReserve);
BigDecimal atpDiff = atp.add(quantity);




String OrderItemShipGrpInvResQuant = parameters.quantity;
BigDecimal OrderItemShipGrpInvResQuantity = new BigDecimal(OrderItemShipGrpInvResQuant);
BigDecimal qtyDiff; 

	qtyDiff= OrderItemShipGrpInvResQuantity.subtract(quantity);

Map<String, Object> resultNew =null;


try{
   Map<String, Object> inventoryMovementDetail = UtilMisc.toMap(
			"availableToPromiseDiff",quantity ,
			"quantityOnHandDiff", BigDecimal.ZERO, 
			"inventoryItemId",inventoryItemId,
			"userLogin", userLogin);

   resultNew = dispatcher.runSync(
			"createInventoryItemDetail", inventoryMovementDetail);
}catch(all){ 
}

GenericValue OrderItemShipGrpInvRes = delegator.makeValue("OrderItemShipGrpInvRes",UtilMisc.toMap("orderId",parameters.orderId,"shipGroupSeqId",parameters.shipGroupSeqId,
		                                                  "orderItemSeqId",parameters.orderItemSeqId,"inventoryItemId",inventoryItemId));
OrderItemShipGrpInvRes.set("quantity", qtyDiff	);
OrderItemShipGrpInvRes.store()
}
return "success";
		