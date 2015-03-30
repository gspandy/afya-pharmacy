import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import java.util.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
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

facilityId = parameters.facilityId;
SimpleDateFormat dateFormat = new SimpleDateFormat(UtilDateTime.DATE_FORMAT);

if(UtilValidate.isEmpty(parameters.fromDate) && UtilValidate.isEmpty(parameters.thruDate)){
	return "error";
}

java.util.Date parsedDate1 = new java.util.Date();
if(UtilValidate.isNotEmpty(parameters.fromDate))
 parsedDate1 = dateFormat.parse(parameters.fromDate);
java.sql.Timestamp fromDate = UtilDateTime.getDayStart( new java.sql.Timestamp(parsedDate1.getTime()) );

java.util.Date parsedDate2 = new java.util.Date();
if(UtilValidate.isNotEmpty(parameters.thruDate)) 
 parsedDate2 = dateFormat.parse(parameters.thruDate);
java.sql.Timestamp thruDate =  UtilDateTime.getDayEnd( new java.sql.Timestamp(parsedDate2.getTime()) );


List manufacturingIssuanceCondition = FastList.newInstance();
List manufacturingIssuanceList1 = null;

List<Map> manuFecList;
EntityCondition cn1 = EntityCondition.makeCondition("datetimeReceived", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate); 
EntityCondition cn2 = EntityCondition.makeCondition("datetimeReceived", EntityOperator.LESS_THAN_EQUAL_TO, thruDate); 
EntityCondition makeCodition2 = EntityCondition.makeCondition(cn1,cn2); 
List inventoryItemList = delegator.findList("InventoryItem", makeCodition2, null, null, null, false);
manuFecList = new ArrayList<Map>();
inventoryItemList.each { invItem ->
	Map fgMap = new HashMap();
	proAsscList = delegator.findList("ProductAssoc", EntityCondition.makeCondition("productId", EntityOperator.EQUALS, invItem.productId), null, null, null, false);
	proAsscList.each { proAssc ->
		conditions1 = [];
		conditions1.add(EntityCondition.makeCondition("issuanceDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate ));
		conditions1.add(EntityCondition.makeCondition("issuanceDate", EntityOperator.LESS_THAN_EQUAL_TO, thruDate ));
		conditions1.add(EntityCondition.makeCondition("issuanceProductId", EntityOperator.EQUALS, proAssc.productIdTo));
		conditions1.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
		manfacComps = delegator.findList("ManufacturingIssuance", EntityCondition.makeCondition(conditions1,EntityOperator.AND), null, null, null, false);
		if( (manfacComps != null && manfacComps.size() > 0) && fgMap.size() == 0){
			issuanceQt = 0;
			fgMap.put("productIdDes", delegator.findOne("Product",false, UtilMisc.toMap("productId", invItem.productId)).getString("internalName") );
			fgMap.put("productIdToDes", " ");
			fgMap.put("issuanceQty",invItem.availableToPromiseTotal);
			fgMap.put("fixedAssetId", OrderMgrUtil.getFixedAssetName(manfacComps.get(0).fixedAssetId));
			fgMap.put("date",invItem.datetimeReceived);
			manuFecList.add(fgMap);
		}
		
		manfacComps.each { manfacComp ->
			Map m = new HashMap();
			issuanceQt = 0;
			m.put("productIdDes",delegator.findOne("Product",false, UtilMisc.toMap("productId", invItem.productId)).getString("internalName") );
			m.put("productIdToDes",delegator.findOne("Product",false, UtilMisc.toMap("productId", manfacComp.issuanceProductId)).getString("internalName") );
			m.put("issuanceQty",manfacComp.issuanceQty);
			m.put("fixedAssetId",OrderMgrUtil.getFixedAssetName(manfacComp.fixedAssetId));
			m.put("serialNumber",manfacComp.serialNumber);
			m.put("description",manfacComp.description);
			m.put("date",manfacComp.issuanceDate);
			m.put("serialzedComments",manfacComp.serialzedComments);
			manuFecList.add(m);
		}
			
		}
	
}

context.manufacturingIssuanceList = manuFecList; 
return "success"
