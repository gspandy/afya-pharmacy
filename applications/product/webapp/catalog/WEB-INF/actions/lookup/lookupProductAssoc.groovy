import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;



String productId = parameters.productId;
List condition = [];
condition.add(EntityCondition.makeCondition("productAssocTypeId",EntityOperator.EQUALS,"MANUF_COMPONENT"));
if(UtilValidate.isNotEmpty(productId))
	condition.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,productId));
List<GenericValue> productAssocList = delegator.findList("ProductAssoc",EntityCondition.makeCondition(condition),null,null,null,false);
Set<String> productIdSet = new HashSet<String>();
for(GenericValue gv : productAssocList){
	productIdSet.add(gv.getString("productId"));
}

List<GenericValue> listIt = new ArrayList<GenericValue>();
Iterator iter = productIdSet.iterator();
while (iter.hasNext()) {
  listIt.add(delegator.findOne("Product",UtilMisc.toMap("productId",iter.next()),false));
}

context.listIt = listIt;