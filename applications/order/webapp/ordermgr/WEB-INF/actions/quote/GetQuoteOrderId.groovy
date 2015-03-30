import org.ofbiz.base.util.UtilValidate;

import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.UtilMisc;


exprBldr = new EntityConditionBuilder();
orderItem = delegator.findList("OrderItem", exprBldr.EQUALS(quoteId: quote.quoteId), null, null, null, false);

if(UtilValidate.isNotEmpty(orderItem))
	context.orderId = orderItem.get(0).getString("orderId");
