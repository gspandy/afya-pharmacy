import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.entity.GenericValue;


String partyId = userLogin.get("partyId");
context.emplId = partyId;
context.partyId = partyId;
manager = HumanResUtil.getReportingMangerForParty(partyId, delegator);
mgrId = manager!=null? manager.get("partyId") : "_NA" ; 
context.mgrId=mgrId;
GenericValue position = HumanResUtil.getEmplPositionForParty(partyId, delegator);
context.emplPositionId = position ? position.get("emplPositionId") : "" ;
GenericValue mgrPosition = HumanResUtil.getEmplPositionForParty(context.mgrId, delegator);
context.mgrPositionId = mgrPosition ? mgrPosition.get("emplPositionId") : "" ;

EntityCondition condition = EntityCondition.makeCondition("emplPositionId", context.emplPositionId);
virtualPositionDetailsList = delegator.findList("PositionFulfillmentView", condition, null, null ,null ,false);

positionDetail=[:];
if(virtualPositionDetailsList.size()>0){
	entity = virtualPositionDetailsList.get(0);
	positionDetail.positionDesc=entity.get("positionDesc");
	positionDetail.departmentId=entity.get("departmentId");
	session.setAttribute("departmentId",positionDetail.departmentId);
}else{
	positionDetail.positionDesc="NA";
	positionDetail.departmentId="NA";	
}
	context.positionDetail=positionDetail;

primKey = [:];
primKey.partyId=partyId;

context.empFullName = HumanResUtil.getFullName(delegator,partyId,null);
context.mgrFullName = HumanResUtil.getFullName(delegator,mgrId,null);