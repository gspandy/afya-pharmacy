import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.order.order.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.sme.order.util.OrderMgrUtil;

GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
userLoginId = userLogin.userLoginId;
partyId = userLogin.partyId;
Boolean isSubOrdinate = Boolean.FALSE;

EntityCondition genericsCondition = EntityCondition.makeCondition( EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"),EntityOperator.AND, 
									EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_CREATED") );

// No role based restrictions
salesOrderHeaders = delegator.findList("OrderHeader", genericsCondition, null, null, null, false);

/*EntityCondition conditions = EntityCondition.makeCondition(genericsCondition,EntityOperator.AND,
								EntityCondition.makeCondition("createdBy", EntityOperator.EQUALS, userLoginId));

salesOrderHeaders = delegator.findList("OrderHeader", conditions, null, null, null, false);

if("admin".equals(partyId)){
    salesOrderHeaders = delegator.findList("OrderHeader", genericsCondition, null, null, null, false);
}

partyRelationshipList = delegator.findByAnd("PartyRelationship", ["partyIdTo" : partyId]);

List partyList = new ArrayList();
List subOrdinateUserLogin = new ArrayList();
subOrdinateUserLogin.add(userLoginId);
for(GenericValue gv : partyRelationshipList){
    partyList.add(gv.roleTypeIdTo);
    userLoginList = delegator.findByAnd("UserLogin",["partyId":gv.partyIdFrom]);
    if(UtilValidate.isNotEmpty(userLoginList)){
        userLoginGv = EntityUtil.getFirst(userLoginList);
        subOrdinateUserLogin.add(userLoginGv.userLoginId);
    }
}
EntityCondition managerCondition = EntityCondition.makeCondition(genericsCondition,EntityOperator.AND,
									EntityCondition.makeCondition("createdBy",EntityOperator.IN,subOrdinateUserLogin) );
if(partyList.contains("SALES_MGR")){
    isSubOrdinate = true;
} 
if(isSubOrdinate == true)
    salesOrderHeaders = delegator.findList("OrderHeader", managerCondition, null, null, null, false);*/

context.salesOrderHeaders = salesOrderHeaders;
