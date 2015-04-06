import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import java.util.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.accounting.util.UtilAccounting;
import org.ofbiz.party.party.PartyWorker;
import org.sme.order.util.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilMisc;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.sql.Timestamp;

import javolution.util.FastList;

conditions = [];
EntityCondition cn1 = EntityCondition.makeCondition("varianceReasonId",EntityOperator.EQUALS,"VAR_SAMPLE");
EntityCondition cn2 = EntityCondition.makeCondition("inventoryItemId",EntityOperator.EQUALS,parameters.inventoryItemId);
EntityCondition cn = EntityCondition.makeCondition(cn1,EntityOperator.AND,cn2);

List physicalInventoryAndVarianceList = delegator.findList("PhysicalInventoryAndVariance",cn,null,null,null,false);

List<Map<String, Object>> phyInvVarianceList = new ArrayList<Map<String,Object>>();

for(GenericValue gv : physicalInventoryAndVarianceList){
	Map<String, Object> map = new HashMap<String, Object>();

	map.put("inventoryItemId", gv.getString("inventoryItemId"));
	GenericValue inventoryGv = delegator.findOne("InventoryItem",false,UtilMisc.toMap("inventoryItemId", gv.getString("inventoryItemId")));
	GenericValue productNameGv = delegator.findOne("Product",false,UtilMisc.toMap("productId", inventoryGv.getString("productId")));
	map.put("date", gv.getString("physicalInventoryDate"));
	map.put("comments", gv.getString("comments"));
	map.put("partyId", gv.getString("partyIdTo"));
	map.put("productName", productNameGv.getString("internalName"));
	map.put("varianceReason", delegator.findOne("Enumeration",false,UtilMisc.toMap("enumId",gv.getString("varianceReasonId"))).getString("description"));

	GenericValue partyGroupGv = delegator.findOne("PartyGroup",false,UtilMisc.toMap("partyId", gv.getString("partyIdTo")));
	if(partyGroupGv != null)
		map.put("partyIdTo", partyGroupGv.getString("groupName") );
	else
		map.put("partyIdTo", "" );

	if(gv.getBigDecimal("availableToPromiseVar") != null){
		if(gv.getBigDecimal("availableToPromiseVar").compareTo(BigDecimal.ZERO) < BigDecimal.ZERO){
			map.put("quantity", gv.getBigDecimal("availableToPromiseVar").negate());
		}else{
			map.put("quantity", gv.getBigDecimal("availableToPromiseVar"));
		}
	}
	phyInvVarianceList.add(map);
}
context.phyInvVarianceList = phyInvVarianceList;

return "success"