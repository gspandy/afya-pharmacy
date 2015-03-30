import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.UtilMisc;
import java.util.*;

EntityCondition cond1 = EntityCondition.makeCondition(UtilMisc.toMap("partyId",userLogin.getString("partyId"),"contactMechPurposeTypeId","PRIMARY_LOCATION"));
List<GenericValue> contactMechValues = delegator.findList("PartyContactMechPurpose",cond1,null,null,null,false);
String countryGeoId="";
println "\n\n\n\n\n\n\n\n\n Outside: cond1"+ cond1;
println "\n\n\n\n\n\n\n\n\n Outside: contactMechValues"+ contactMechValues;
println "\n\n\n\n\n\n\n\n\n Outside: userLogin.getString(partyId):"+ userLogin.getString("partyId");

if(contactMechValues.size()>0){
	contactMechValue = contactMechValues.get(0);
	println "\n\n\n\n\n\n\n\n\n contactMechValues"+ contactMechValues;
	Map params = UtilMisc.toMap("contactMechId",contactMechValue.getString("contactMechId"),"partyId",userLogin.getString("partyId"));
	
	EntityCondition cond = EntityCondition.makeCondition(params);
	
	
	List<GenericValue> values = delegator.findList("PartyAndPostalAddress",cond,null, null,null,false);
	System.out.println(" \n\n\n\n\n\n\nvalues:  " + values);
	System.out.println(" \n\n\n\n\n\n\nvalues.get(0)  " + values.get(0));
	if(values.size()>0){
		countryGeoId = values.get(0).getString("countryGeoId");
	}
}

System.out.println(" \n\n\n\n\n\n\ncountryGeoId  " + countryGeoId);

System.out.println(" \n\n\n\n\n\n\ntesting:  " );
context.userGeoId= countryGeoId;

context.hasPermission = false;
/** Enable partyId text field for mgr and admin **/
isMgr = security.hasEntityPermission("HUMANRES", "_MGR", session);
isAdmin = security.hasEntityPermission("HUMANRES", "_ADMIN", session);
context.hasPermission = isMgr||isAdmin;

return "success";


