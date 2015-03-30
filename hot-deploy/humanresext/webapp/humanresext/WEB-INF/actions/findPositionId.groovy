import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityOperator;

import java.util.*;

String partyId = userLogin.get("partyId");

GenericValue position = HumanResUtil.getLatestEmplPositionFulfillmentForParty(partyId, delegator);
context.positionId = null;
if(position)
	context.positionId = position.get("emplPositionId");