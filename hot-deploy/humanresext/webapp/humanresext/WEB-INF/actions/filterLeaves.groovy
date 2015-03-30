import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;

String lpartyId = userLogin.get("partyId");

condition = EntityCondition.makeCondition("partyId", lpartyId);
Set<String> fields = new HashSet<String>();
fields.add("leaveTypeId"); fields.add("availed"); fields.add("numDays");
fields.add("balance");
leavesBalance = delegator.findList("EmplLeaveLimit", condition, null, null, null, false);

context.listIt=leavesBalance;