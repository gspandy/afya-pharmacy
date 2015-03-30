import java.util.List;
import org.ofbiz.entity.condition.EntityCondition;
facilityList = [];
facilityId = parameters.facilityId;
EntityCondition condition = EntityCondition.makeCondition("facilityId", facilityId);
facilityList = delegator.findList("ProductStoreFacility", condition, null, null, null, false);
if(facilityList){
  ProductStoreList = delegator.findByPrimaryKey("ProductStore",  [productStoreId:facilityList.get(0).productStoreId]);
  context.reserveInventory = ProductStoreList.reserveInventory ;
 }