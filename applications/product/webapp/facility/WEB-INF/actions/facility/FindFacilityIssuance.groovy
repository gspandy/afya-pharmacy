import java.sql.Timestamp
import org.ofbiz.base.util.*
import org.ofbiz.entity.*
import org.ofbiz.entity.condition.*
import org.ofbiz.entity.transaction.*
import java.util.*

rows = [] as ArrayList;

//invIssuance = dispatcher.runSync("performFindList", UtilMisc.toMap("inputFields",parameters,"entityName","InventoryItemAndLocation"));

facilityId = parameters.facilityId;
productId = parameters.productIdFinishGood;

proAssocs = [];

if(productId){
	proAssocs = delegator.findByAnd("ProductAssoc",UtilMisc.toMap("productId",productId));
}

proAssocs.each {  productAssoc ->
	invItems = [];
	invItems = delegator.findByAnd("InventoryItemAndLocation",UtilMisc.toMap("productId",productAssoc.productIdTo,"facilityId",facilityId));
	invItems.each { invItem ->
		rows.add(invItem);
	}
}
context.listIt = rows;

