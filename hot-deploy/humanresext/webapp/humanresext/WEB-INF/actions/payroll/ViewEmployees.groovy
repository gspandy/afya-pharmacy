import org.ofbiz.entity.condition.*;
import java.util.*;
import javolution.util.FastList;

EntityCondition entityCondition = EntityCondition.makeCondition("roleTypeIdTo", "EMPLOYEE");
List<GenericValue> employees = delegator.findList("Employment", entityCondition, null, null, null, false);

List<String> employeeIds = FastList.newInstance();
for (GenericValue employee : employees) {
	employeeIds.add(employee.getString("partyIdTo"));
}

context.employeeIds = employeeIds;