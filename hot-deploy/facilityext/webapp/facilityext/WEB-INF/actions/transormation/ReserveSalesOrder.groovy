import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.order.order.*;


orderId = parameters.orderId
List orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition([orderId : orderId]), null, null, null, false);
OrderReadHelper orh = new OrderReadHelper(delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId)));
 for(GenericValue orderItem : orderItems ){
 OrderItemShipGrpInvRes = delegator.findList("OrderItemShipGroupAssoc", EntityCondition.makeCondition([orderId : orderId,orderItemSeqId : orderItem.orderItemSeqId ]), null, null, null, false).get(0);
 
  reservedQuantity = orh.getItemReservedQuantity(orderItem);
 shippedQuantity = orh.getItemShippedQuantity(orderItem);
 BigDecimal orderedQty = orderItem.getBigDecimal("quantity");
BigDecimal cancelQty = orderItem.getBigDecimal("cancelQuantity");
 remainingQuantity = cancelQty!=null?orderedQty.subtract(cancelQty):orderedQty;
  remainingQuantity = remainingQuantity.subtract(shippedQuantity);
 quantityToReserve = remainingQuantity.subtract(reservedQuantity);
 if(quantityToReserve.compareTo(BigDecimal.ZERO)>0){
 results = dispatcher.runSync("reserveProductInventoryByFacility",
		[userLogin:userLogin,
		 productId:orderItem.productId,
	     orderId:OrderItemShipGrpInvRes.orderId,
	     orderItemSeqId:orderItem.orderItemSeqId,
	     shipGroupSeqId:OrderItemShipGrpInvRes.shipGroupSeqId,
	     quantity:quantityToReserve,
		 facilityId:parameters.facilityId,
	     requireInventory:"N",
	     reserveOrderEnumId: parameters.reserveOrderEnumId
     	]
	);
 }
 }
return "success"