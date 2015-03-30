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
import org.ofbiz.widget.html.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.widget.html.HtmlFormWrapper
import org.ofbiz.entity.*
import org.ofbiz.entity.condition.*
import org.ofbiz.entity.util.*


shipmentId = request.getParameter("shipmentId");
if (!shipmentId) {
    shipmentId = context.shipmentId;
}

shipment = null;
shipment = delegator.findOne("Shipment", [shipmentId : shipmentId], false);
whereCondition = EntityCondition.makeCondition("shipmentId", EntityOperator.EQUALS, shipmentId);
 if(shipment != null){
    if("SALES_SHIPMENT".equals(shipment.getString("shipmentTypeId"))){
     findOptions = new EntityFindOptions();
    findOptions.setDistinct(true);
    shipmentItem =   delegator.findList("ShipmentItemBilling",  whereCondition, UtilMisc.toSet("invoiceId","shipmentId"), null, findOptions, false);
    findOptions = new EntityFindOptions();
    findOptions.setDistinct(true);
    List shipItem = [];
    for(GenericValue genValue : shipmentItem ){
    shipItem.addAll(delegator.findList("OrderItemBilling",  EntityCondition.makeCondition([invoiceId : genValue.invoiceId]), UtilMisc.toSet("invoiceId","orderId"), null, findOptions, false));    
    if(shipItem.size() >0){
    context.shipmentInvoiceData= shipItem;
    }

    }
   }
   }

  if(shipment!= null){
if("PURCHASE_SHIPMENT".equals(shipment.getString("shipmentTypeId")) || "SALES_RETURN".equals(shipment.getString("shipmentTypeId"))){
 findOptions = new EntityFindOptions();
    findOptions.setDistinct(true);
    orderShipment = delegator.findList("OrderShipment",  whereCondition, UtilMisc.toSet("shipmentId","orderId"), null, findOptions, false);
    findOptions = new EntityFindOptions();
    findOptions.setDistinct(true);
    List purchInvItem = [];
    for(GenericValue valueOrder: orderShipment ){
    purchInvItem.addAll(delegator.findList("OrderItemBilling",  EntityCondition.makeCondition([orderId : valueOrder.orderId]), UtilMisc.toSet("invoiceId","orderId"), null, findOptions, false));
    if(purchInvItem.size() >0){
   context.purchaseInvoiceNewData= purchInvItem;
    }
    }
   }
   }
   
context.shipmentId = shipmentId;