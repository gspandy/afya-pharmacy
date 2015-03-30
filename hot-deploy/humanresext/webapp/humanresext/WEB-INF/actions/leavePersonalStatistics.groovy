import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.entity.condition.EntityOperator;

import java.util.*;

String partyId = userLogin.get("partyId");
System.out.println("\n\n groovy leave stats \n\n");
partyCondition = EntityCondition.makeCondition("partyId",partyId);
statusNotRejectedCondition = EntityCondition.makeCondition("leaveStatusId",EntityOperator.NOT_EQUAL, "MGR_REJECTED");
statusNotCancelledCondition = EntityCondition.makeCondition("leaveStatusId",EntityOperator.NOT_EQUAL, "ADM_REJECTED");
condition = EntityCondition.makeCondition(partyCondition, statusNotRejectedCondition, statusNotCancelledCondition);
context.leaveBalanceByTypeForIndividual = HumanResUtil.findLeaveDetailsByType(delegator, condition);
System.out.println("\n\n leaveBalanceByTypeForIndividual.size() :" + leaveBalanceByTypeForIndividual.size());