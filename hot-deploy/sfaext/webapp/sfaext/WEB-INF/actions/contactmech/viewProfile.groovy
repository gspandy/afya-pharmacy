import org.ofbiz.party.contact.*;
import org.ofbiz.base.util.UtilMisc;
import java.util.Collections;

delegator = request.getAttribute("delegator");
partyId = parameters.get("partyId");

// this should be set by the .BSH for viewing accounts, contacts, etc. to allow contact mechs to display
displayContactMechs = request.getAttribute("displayContactMechs");

println "\n\n\n displayContactMechs \n\n" + displayContactMechs + "\n\n\n";

if ((displayContactMechs != null) && (displayContactMechs.equals("Y"))) {
	println "\n\n\n Inside If \n\n";
    partyContactMechValueMaps = ContactMechWorker.getPartyContactMechValueMaps(delegator, partyId, false);
	
	println "\n\n\n partyContactMechValueMaps \n\n" + partyContactMechValueMaps + "\n\n\n";

    List userLogins = delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", partyId));

    context.put("contactMeches", partyContactMechValueMaps);
    context.put("userLogins", userLogins);
}

