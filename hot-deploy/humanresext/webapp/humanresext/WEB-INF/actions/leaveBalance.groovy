import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.entity.GenericValue;

import java.util.*;

String partyId = userLogin.get("partyId");
isMgr = security.hasEntityPermission("HUMANRES", "_MGR", session); /** This is a manager login **/
isAdmin = security.hasEntityPermission("HUMANRES", "_ADMIN", session); /** This is an admin login **/

context.numberOfLeavesPendingApproval = 0;
//System.out.println("\n\n outside isAdmin:	" + isAdmin);
//System.out.println("\n\n outside isMgr:	" + isMgr);
leavesPendingApproval = [];
if (isMgr) {
	//System.out.println("\n\n Inside isMgr \n" + isMgr);
	GenericValue position = HumanResUtil.getEmplPositionForParty(partyId, delegator);
	if(position != null) {
		positionCondition = EntityCondition.makeCondition("mgrPositionId",position.get("emplPositionId"));
		statusCondition = EntityCondition.makeCondition("leaveStatusId","LT_SUBMITTED");
		condition = EntityCondition.makeCondition(positionCondition, statusCondition);
		leavesPendingApproval = delegator.findList("EmplLeave", condition, null, null, null, false);
	}
} 
if (isAdmin) {
	//System.out.println("\n\n Inside isAdmin \n" + isAdmin);
	statusCondition = EntityCondition.makeCondition("leaveStatusId","LT_MGR_APPROVED");
	leavesPendingApproval = delegator.findList("EmplLeave", statusCondition, null, null, null, false);
}

if(leavesPendingApproval)
	context.numberOfLeavesPendingApproval = leavesPendingApproval.size();
		