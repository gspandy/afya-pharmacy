import java.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;

EntityCondition condition = EntityCondition.makeCondition("operatorType", "LOGICAL");
context.logicalOperators = delegator.findList("PayrollOpEnumeration", condition, null, null, null, true);

condition = EntityCondition.makeCondition("operatorType", "ARITHMETIC");
context.arithmaticOperators = delegator.findList("PayrollOpEnumeration", condition, null, null, null, true);