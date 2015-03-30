import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;

reqStatus = ["REQ_PROPOSED","REQ_PENDING","REQ_CREATED"];
condition1 =  EntityCondition.makeCondition("statusId", EntityOperator.IN, reqStatus);
condition2 =  EntityCondition.makeCondition("description", EntityOperator.LIKE, "MRP_%");	
condition3 = EntityCondition.makeCondition(condition1,condition2);

requirementsList = delegator.findList("RequirementProductUom", condition3, null, null, null, false);

context.requirementsList = requirementsList;