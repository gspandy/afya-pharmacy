import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.order.order.*;

/*Collection<Map<String, Object>> formData = UtilHttp
				.parseMultiFormData(UtilHttp.getParameterMap(request));
				*/
	
	inventoryItems = [];
	errorMessageList = [];
	//for(Map<String,Object> row : formData){
		
		String orderId = parameters.orderId;
		String productId =parameters.productId;
		String inventoryItemId = parameters.inventoryItemId;
		
		orderItems = delegator.findByAnd("OrderItem",UtilMisc.toMap("orderId",orderId,"productId",productId));
		anyAdded = false;
		BigDecimal diff,qtyOrdered,qtyAlreadyReserved,shippedQuantity,remqty;
if(orderItems){
		orderItems.each { orderItem ->
				OrderReadHelper orh = new OrderReadHelper(delegator.findByPrimaryKey("OrderHeader",UtilMisc.toMap("orderId",orderId)));
				qtyAlreadyReserved = orh.getItemReservedQuantity(orderItem);
				shippedQuantity = orh.getItemShippedQuantity(orderItem);
				qtyOrdered= orh.getOrderItemQuantity(orderItem);
				if(qtyAlreadyReserved > qtyOrdered){
				diff = qtyAlreadyReserved.subtract(qtyOrdered);
				}else{
				remqty = qtyOrdered.subtract(shippedQuantity);
				diff = remqty.subtract(qtyAlreadyReserved);
				
				}
				if(diff){

						anyAdded=true;
					GenericValue oisga = delegator.findByAnd("OrderItemShipGroupAssoc",[orderId:orderId,orderItemSeqId:orderItem.orderItemSeqId]).get(0);
					results = dispatcher.runSync("reserveForInventoryItemInline",
					[userLogin:userLogin,
					 inventoryItemId:parameters.inventoryItemId,
					 productId:productId,
					 orderId:orderId,
					 shipGroupSeqId:oisga.shipGroupSeqId,
					 orderItemSeqId:orderItem.orderItemSeqId,
					 quantityNotReserved:diff,
					 deductAmount:diff,
					 stockReserveId:"RESERVE_INV"
					]);
				
				}
			}
		}

else{
			errorMessageList.add("Could not reserve the Inventory with Product "+ parameters.productId +"  for the Order "+orderId);
			request.setAttribute("_ERROR_MESSAGE_LIST_",errorMessageList);
		}
		

	/*	if(!anyAdded){
			request.setAttribute("_ERROR_MESSAGE_","Could not reserve the Inventory for Order Id "+orderId);
		}
	*/
		inventoryItems.add([inventoryItemId:inventoryItemId]);
	
	//}
	
	session.setAttribute("listIt",inventoryItems);
	context.qtyOrdered = "eyrewryweyr"
	return "success";
	