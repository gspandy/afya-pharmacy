import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.apache.axis.utils.StringUtils;

shoppingCart = ShoppingCartEvents.getCartObject(request);
selected = UtilHttp.parseMultiFormData(parameters);
selected.each { row->
		int cartIndex = row.row;
		ShoppingCartItem cartLine = shoppingCart.findCartItem(cartIndex);
		String adjustmentTypeId = row.adjustmentTypeId;
		BigDecimal adjAmount = StringUtils.isEmpty(row.adjAmount)?null:new BigDecimal(row.adjAmount);
		
		if(adjAmount==null) {
			BigDecimal sourcePercentage =StringUtils.isEmpty(row.sourcePercentage)?null:new BigDecimal(row.sourcePercentage);
			if(sourcePercentage!=null)
			adjAmount = cartLine.getDisplayItemSubTotal().multiply(sourcePercentage).divide(new BigDecimal(100));
		}
		if(adjAmount!=null) {
			GenericValue orderAdjType = delegator.findOne("OrderAdjustmentType",["orderAdjustmentTypeId":adjustmentTypeId],true);
			GenericValue glAccount = orderAdjType.getRelatedOne("GlAccount");
			GenericValue adj = delegator.makeValidValue("OrderAdjustment",UtilMisc.toMap("orderAdjustmentTypeId",adjustmentTypeId,
				"amount",adjAmount,"comments",orderAdjType.description,"overrideGlAccountId",glAccount!=null?glAccount.getString("glAccountId"):null
				,"sourcePercentage",StringUtils.isEmpty(row.sourcePercentage)?null:row.sourcePercentage));
			cartLine.addAdjustment(adj);
		}
		
}
session.setAttribute("shoppingCart",shoppingCart);
return "success";