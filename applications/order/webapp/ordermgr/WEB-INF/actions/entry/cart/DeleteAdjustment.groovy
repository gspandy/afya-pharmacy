import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.entity.GenericValue
import org.ofbiz.order.shoppingcart.ShoppingCart;
import org.ofbiz.order.shoppingcart.ShoppingCartEvents;
import org.ofbiz.order.shoppingcart.ShoppingCartItem;
import org.apache.axis.utils.StringUtils;

ShoppingCart shoppingCart = ShoppingCartEvents.getCartObject(request);
int cartIndex = Integer.parseInt(request.getParameter('ci')).intValue();
ShoppingCartItem cartItemList = shoppingCart.findCartItem(cartIndex);
int adjIdx = Integer.parseInt(request.getParameter('adj_idx')).intValue();
cartItemList.removeAdjustment(adjIdx);
return "success";