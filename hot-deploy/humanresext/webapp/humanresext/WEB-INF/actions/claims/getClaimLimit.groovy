import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.base.util.UtilMisc;


 
 lpartyId = claimMap.partyId; /** PartyId which submitted the Claim **/
 fullName = HumanResUtil.getFullName(delegator, lpartyId," ");
 
 /** Get Employee Position Id for this party **/
GenericValue position = HumanResUtil.getEmplPositionForParty(lpartyId, delegator);
lemplPositionId = position ? position.get("emplPositionId") : "" ;	

lemplPositionTypeId = HumanResUtil.getPositionTypeId(delegator, lemplPositionId);
	

 List conditionList = UtilMisc.toList(EntityCondition.makeCondition("emplPositionTypeId", EntityOperator.EQUALS, lemplPositionTypeId),
				EntityCondition.makeCondition("claimType", EntityOperator.EQUALS, claimMap.claimType));
 EntityConditionList conditions = new EntityConditionList(conditionList, EntityOperator.AND);
	
 data = [];
 fields = [];
 fields.add("amount");
 data= delegator.findByCondition("ClaimLimit", conditions, fields, null);

cLimit = 0;
 	 
 if(data.size() >0) {
 	cLimit = data.get(0).get("amount");
 }

 context.cLimit = cLimit;
 context.partyName=fullName; /** Full Name of the Claim Party **/
 