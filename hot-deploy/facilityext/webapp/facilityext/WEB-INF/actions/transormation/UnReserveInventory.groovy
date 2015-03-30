import org.ofbiz.base.util.*;
import org.ofbiz.entity.transaction.*;
import java.util.*

	
String quantityToCancel = parameters.quantityToCancel;

if(quantityToCancel){
try{
   TransactionUtil.begin();
	 dispatcher.runSync("cancelOrderItemShipGrpInvRes",
					[
						orderId:parameters.orderId,
						orderItemSeqId:parameters.orderItemSeqId,
						shipGroupSeqId:parameters.shipGroupSeqId,
						inventoryItemId:parameters.inventoryItemId,
						cancelQuantity:new BigDecimal(quantityToCancel),
						userLogin:userLogin
					]
	);
		errorMessageList = [];

		
		String orderId = parameters.orderId;
		String inventoryItemId = parameters.inventoryItemId;
		
		orderItem = delegator.findOne("OrderItem",false,UtilMisc.toMap("orderId",orderId,"orderItemSeqId",parameters.orderItemSeqId));
		
		 GenericValue oisga  = delegator.findByAnd("OrderItemShipGroupAssoc",[orderId:orderId,orderItemSeqId:orderItem.orderItemSeqId]).get(0);
				results = dispatcher.runSync("reserveForInventoryItemInline",
				[userLogin:userLogin,
				 inventoryItemId:parameters.inventoryItemId,
				 productId:orderItem.productId,
				 orderId:orderId,
				 shipGroupSeqId:oisga.shipGroupSeqId,
				 orderItemSeqId:orderItem.orderItemSeqId,
				 quantityNotReserved:new BigDecimal(parameters.quantityNotAvailable),
				 deductAmount:orderItem.quantity.subtract(new BigDecimal(parameters.quantityToCancel)),
				 stockReserveId:"RESERVE_INV"
				]);
           } catch (NumberFormatException nfe) {
        }finally {
		TransactionUtil.commit();
		}  
}
		return "success";
		