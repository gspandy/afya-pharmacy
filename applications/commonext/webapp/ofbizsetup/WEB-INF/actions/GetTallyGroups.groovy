import org.ofbiz.entity.GenericDelegator;

List company=delegator.findByAnd("PartyGroup",["company":"Y"]);
context.listit=delegator.findByAnd("PartyRoleAndContactMechDetail",["roleTypeId":"SUPPLIER"]);
print context.listit;