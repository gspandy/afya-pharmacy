import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.base.util.UtilMisc;


 
 lpartyId = loanMap.partyId; /** PartyId which submitted the loan **/
 fullName = HumanResUtil.getFullName( delegator, lpartyId," ");
 
 /** Get Employee Position Id for this party **/
GenericValue position = HumanResUtil.getEmplPositionForParty(lpartyId, delegator);
lemplPositionId = position ? position.get("emplPositionId") : "" ;	

lemplPositionTypeId = HumanResUtil.getPositionTypeId(delegator, lemplPositionId);
	

 List conditionList = UtilMisc.toList(EntityCondition.makeCondition("emplPositionTypeId", EntityOperator.EQUALS, lemplPositionTypeId),
				EntityCondition.makeCondition("loanType", EntityOperator.EQUALS, loanMap.loanType));
 EntityConditionList conditions = new EntityConditionList(conditionList, EntityOperator.AND);
	
 data = [];
 fields = [];
 fields.add("loanAmount");
 data= delegator.findByCondition("ELoanLimit", conditions, fields, null);

lLimit = 0;
 	 
 if(data.size() >0) {
 	lLimit = data.get(0).get("loanAmount");
 }

 context.lLimit = lLimit;
 context.partyName=fullName; /** Full Name of the loan Party **/
 