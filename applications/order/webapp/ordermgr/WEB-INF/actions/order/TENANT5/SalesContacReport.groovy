import java.security.Policy.Parameters;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.Converters.GenericValueToList;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;

//despatch
orderItemShipGroup =null;
orderidshipping= parameters.orderId;

orderItemShipGroup = delegator.findByAnd("OrderItemShipGroup", ["orderId" : orderidshipping]);
//List supplierAddressList = delegator.findList("OrderItemShipGroup", EntityCondition.makeCondition(supplierAddress, EntityOperator.EQUALS), null, null, null, false);

description=null;
if(UtilValidate.isNotEmpty(orderItemShipGroup)){
	description= orderItemShipGroup.get(0).shippingInstructions;
}
context.orderItemShipGroup=description;
// getting details of attribute of products 

rows =[];
List<GenericValue> productIdList =delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId",parameters.orderId));

for(GenericValue attributeList: productIdList){
List<GenericValue> attribut1 = delegator.findByAnd("ProductAttribute", UtilMisc.toMap("productId",attributeList.get("productId")));
Set<String> attribut2= new HashSet<String>() ;
for(GenericValue attrTYpe: attribut1){
 attribut2.add(attrTYpe.getString("attrType"));
 attribut2.remove("TEST")
}

for(String attrTypeValues: attribut2){
	Map<String,List<GenericValue> > map= new HashMap<String, List<GenericValue>>();
	List attributesListCon = FastList.newInstance();

	attributesListCon.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, attributeList.get("productId")));
	attributesListCon.add(EntityCondition.makeCondition("attrType", EntityOperator.EQUALS, attrTypeValues));
 	List attributesList = delegator.findList("ProductAttribute", EntityCondition.makeCondition(attributesListCon, EntityOperator.AND), null, null, null, false);
	map.put("attrType", attrTypeValues);
	map.put("attrValue", attributesList);
	rows += map;
	}
}

context.rows=rows ;
// inspection details

List attributesListConInspection = FastList.newInstance();
List attributesList = [];
for(GenericValue attributeList: productIdList){
	attributesListConInspection.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, attributeList.getString("productId") ));
	attributesListConInspection.add(EntityCondition.makeCondition("attrType", EntityOperator.EQUALS, "TEST"));
	attributesList.add(delegator.findList("ProductAttribute", EntityCondition.makeCondition(attributesListConInspection, EntityOperator.AND), null, null, null, false));
}
context.test=attributesList;

List partyIdent = FastList.newInstance();
supplier = orderReadHelper.getBillToParty();
partyIdent.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, supplier.partyId));
partyIdent.add(EntityCondition.makeCondition("partyIdentificationTypeId", EntityOperator.EQUALS, "BankAccNo"));
List partyIdentList = delegator.findList("PartyIdentification", EntityCondition.makeCondition(partyIdent, EntityOperator.AND), null, null, null, false);

context.partyIdent=partyIdentList;
context.supplier = supplier;




