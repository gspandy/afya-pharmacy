import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.party.contact.*;
import java.sql.Timestamp;
import org.ofbiz.entity.condition.*;

EntityCondition formCondition = EntityCondition.makeCondition(UtilMisc.toMap("formId", "IND_IT_FORM12BA"));

List<GenericValue> formFields = delegator.findList("FormField", formCondition, null, UtilMisc.toList("sequenceId"), null, false);
context.formFields = formFields; //List of fields in the form 12BA

/** Find fields greater than 05.1 **/
EntityCondition seqCondition = EntityCondition.makeCondition("sequenceId", EntityOperator.GREATER_THAN_EQUAL_TO, "10.1");
cl = [];
cl.add(formCondition);
cl.add(seqCondition);
EntityConditionList conditionList = new EntityConditionList(cl, EntityOperator.AND); 
List<GenericValue> innerFields = delegator.findList("FormField", conditionList, null, UtilMisc.toList("sequenceId"), null, false);
System.out.println("innerFields ******************" + innerFields.sequenceId);
context.innerFields= innerFields;



