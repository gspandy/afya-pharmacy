import java.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;



List<EntityCondition> allCondition = new ArrayList();

allCondition.add(EntityCondition.makeCondition("parentGlAccountId", EntityOperator.EQUALS, "400000"));
allCondition.add(EntityCondition.makeCondition("parentGlAccountId", EntityOperator.EQUALS, "4000"));
allCondition.add(EntityCondition.makeCondition("parentGlAccountId", EntityOperator.EQUALS, "3000"));

EntityCondition whereEntityCondition = EntityCondition.makeCondition(allCondition,EntityOperator.OR);

List<GenericValue> glAccountList = delegator.findList("GlAccount",whereEntityCondition,null,null,null,false);

context.glAccountList = glAccountList;