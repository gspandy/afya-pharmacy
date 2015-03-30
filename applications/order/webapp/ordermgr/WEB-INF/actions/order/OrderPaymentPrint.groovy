import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

GenericValue userLogin = session.getAttribute("userLogin");
String partyId = userLogin.getString("partyId");
GenericValue party = delegator.findOne("Person",UtilMisc.toMap("partyId",partyId),false);

String orderId = parameters.orderId;
if (!orderId) return;

String paymentId = parameters.paymentId;
if (!paymentId) return;

GenericValue orderHeader = delegator.findOne("OrderHeader",UtilMisc.toMap("orderId",orderId),false);
GenericValue payment = delegator.findOne("Payment",UtilMisc.toMap("paymentId",paymentId),false);
GenericValue orderItem = delegator.findList("OrderItem",EntityCondition.makeCondition("orderId",orderHeader.getString("orderId")),null,null,null,false).get(0);
GenericValue partyGroup = delegator.findOne("PartyGroup",UtilMisc.toMap("partyId",payment.getString("partyIdFrom")),false);

String productId = orderItem.getString("productId");
BigDecimal quantity = orderItem.getBigDecimal("quantity");
GenericValue product = delegator.findOne("Product",UtilMisc.toMap("productId",productId),false);
GenericValue productUomView = delegator.findOne("ProductUomView",UtilMisc.toMap("productId",productId),false);
String productUomName = productUomView!=null? productUomView.getString("description"):"";
String productName = product.getString("productName");
GenericValue orderPaymentPreferenceGv = delegator.findOne("OrderPaymentPreference",false,UtilMisc.toMap("orderPaymentPreferenceId",payment.getString("paymentPreferenceId")));
println "orderPaymentPreferenceGv" +orderPaymentPreferenceGv;
context.party = party;
context.quantity = quantity;
context.productUomName = productUomName;
context.productName = productName;
context.partyFrom = partyGroup.getString("groupName");
context.orderHeader = orderHeader;
context.payment = payment;
context.orderPaymentPreferenceGv = orderPaymentPreferenceGv;