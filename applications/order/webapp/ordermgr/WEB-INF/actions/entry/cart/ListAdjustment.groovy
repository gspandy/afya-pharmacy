import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityJoinOperator
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.apache.axis.utils.StringUtils;

ShoppingCart shoppingCart = ShoppingCartEvents.getCartObject(request);
List<ShoppingCartItem> cartItemList = shoppingCart.items();


EntityCondition ec1 = EntityCondition.makeCondition("applicableOn",shoppingCart.getOrderType()=="SALES_ORDER"?"SALES":"PURCHASES")
EntityCondition ec2 = EntityCondition.makeCondition("applicableOn","BOTH");

EntityCondition ec = EntityCondition.makeCondition(EntityJoinOperator.OR,ec1,ec2)
List<GenericValue> adjustmentTypes=delegator.findList("OrderAdjustmentType",ec,null,null,null,false);
println(ec);
context.put("adjustmentTypes",adjustmentTypes);

List<GenericValue> adjustmentList = new ArrayList<GenericValue>();
cartItemList.each {
    cartItem ->
        out.println(" cartItem "+cartItem.getAdjustments());
        adjustmentList.add(cartItem.getAdjustments());
};
context.put("adjustmentList",adjustmentList);
return "success";