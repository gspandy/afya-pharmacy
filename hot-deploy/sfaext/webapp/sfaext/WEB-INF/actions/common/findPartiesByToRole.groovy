import java.util.ArrayList;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import com.smebiz.sfa.party.SfaPartyHelper;
import java.util.*;
import org.ofbiz.entity.condition.EntityCondition;

listIteratorNameToUse = parameters.get("listIteratorNameToUse");
if (listIteratorNameToUse == null) return;

// possible fields we're searching by
lastName = parameters.get("lastName");
firstName = parameters.get("firstName");

// optional specific role type to
roleTypeIdTo = parameters.get("roleTypeIdTo");

// construct role conditions
roleConditions = new ArrayList();
if (roleTypeIdTo != null) {
    roleConditions.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, roleTypeIdTo));
} else {
    // construct from the default party role to list
    for (iter = SfaPartyHelper.TEAM_MEMBER_ROLES.iterator(); iter.hasNext(); ) {
        roleConditions.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, iter.next()));
    }
}
roleConditionList = EntityCondition.makeCondition(roleConditions, EntityOperator.OR);

// construct search conditions
searchConditions = new ArrayList();
if (lastName != null && !lastName.equals("")) {
    searchConditions.add(EntityCondition.makeCondition("lastName", true, EntityOperator.LIKE, "%" + lastName + "%", true));
}
if (firstName != null && !firstName.equals("")) {
    searchConditions.add(EntityCondition.makeCondition("firstName", true, EntityOperator.LIKE, "%" + firstName + "%", true));
}
if (searchConditions.size() == 0) return;
searchConditionList = EntityCondition.makeCondition(searchConditions, EntityOperator.OR);

// these conditions are specified to negate certain results
negateConditions = new ArrayList();

// combine roles, searches, and possibly negate conditions with AND
combinedConditions = UtilMisc.toList(roleConditionList, searchConditionList);
if (negateConditions.size() > 0) {
    negateConditionsList = EntityCondition.makeCondition(negateConditions, EntityOperator.AND);
    combinedConditions.add(negateConditionsList);
}
conditionList = EntityCondition.makeCondition(combinedConditions, EntityOperator.AND);

// We need to get a list iterator because 1) the forms are all set to use list iterators and 2) that seems to be the only way to find distinct records
listIt = delegator.find("PartyToSummaryByRelationship", conditionList, null, 
        (Set) UtilMisc.toList("firstName", "lastName", "partyId"), // fields to select 
        (Set) UtilMisc.toList("lastName", "firstName"), // fields to order by
        // the first true here is for "specifyTypeAndConcur"
        // the second true is for a distinct select.  Apparently this is the only way the entity engine can do a distinct query
        new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true));

// this is the iterator to use in form-widgets
context.put(listIteratorNameToUse, listIt);

// this debugging code is kind of helpful so I'll keep it around for now 

print(conditionList);
listIt = context.get(listIteratorNameToUse);
print("******* list iterator values: ***********");
if (listIt != null) { while ((next = listIt.next()) != null) { print(next); } }
else { print("No list iterator found"); }
print("*****************************************");

