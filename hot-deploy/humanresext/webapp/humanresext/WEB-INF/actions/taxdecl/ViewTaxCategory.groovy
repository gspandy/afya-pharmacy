import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.UtilMisc;
import java.util.*;


context.cat = delegator.findOne("TaxCategory",UtilMisc.toMap("categoryId",parameters.categoryId),false);

EntityCondition cond = EntityCondition.makeCondition(UtilMisc.toMap("categoryId",parameters.categoryId));
context.items = delegator.findList("TaxItem",cond,null,null,null,false);