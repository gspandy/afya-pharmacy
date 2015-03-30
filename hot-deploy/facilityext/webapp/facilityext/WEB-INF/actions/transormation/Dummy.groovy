import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

import javolution.util.FastList;

list = []
String productId  = "10000"



EntityCondition condition = EntityCondition.makeCondition("productId",
		productId);

List<GenericValue> inventoryItems = delegator.findList("InventoryItem",

		condition, null, null, null, false);


