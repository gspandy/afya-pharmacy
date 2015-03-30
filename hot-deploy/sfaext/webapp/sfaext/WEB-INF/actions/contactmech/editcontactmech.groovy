import java.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.base.util.*;
import org.ofbiz.securityext.login.*;
import org.ofbiz.common.*;
import org.ofbiz.party.contact.*;
import org.ofbiz.webapp.control.*;
import com.smebiz.sfa.util.UtilCommon;

String partyId = parameters.get("partyId");
context.put("partyId", partyId);

Map mechMap = new HashMap();
ContactMechWorker.getContactMechAndRelated(request, partyId, mechMap);

// put contactMech directly in the context so that the ftl can see it
context.put("contactMech", mechMap.get("contactMech"));

// set the edit title label according to the current contact mech type
contactMechTypeId = mechMap.get("contactMechTypeId");
if (UtilValidate.isNotEmpty(contactMechTypeId)) {
    contactMechType = delegator.findByPrimaryKey("ContactMechType", UtilMisc.toMap("contactMechTypeId", contactMechTypeId));
    if (contactMechType != null) {
        context.put("editContactMechLabel", contactMechType.get("description"));
    }
}

String contactMechId = (String) mechMap.get("contactMechId");
context.put("contactMechId", contactMechId);

preContactMechTypeId = parameters.get("preContactMechTypeId");
context.put("preContactMechTypeId", preContactMechTypeId);


if (preContactMechTypeId!=null) {
    if (preContactMechTypeId.equals("TELECOM_NUMBER")) {
        mechMap.put("requestName", "createTelecomNumber");
    }else if (preContactMechTypeId.equals("POSTAL_ADDRESS")) {
        mechMap.put("requestName", "createPostalAddressAndPurpose");
    }
}

context.put("mechMap", mechMap);

// set the create title label according to the pre contact mech type
if (UtilValidate.isNotEmpty(preContactMechTypeId)) {
    preContactMechType = delegator.findByPrimaryKey("ContactMechType", UtilMisc.toMap("contactMechTypeId", preContactMechTypeId));
    if (preContactMechType != null) {
        context.put("createContactMechLabel", preContactMechType.get("description"));
    }
}

paymentMethodId = parameters.get("paymentMethodId");
context.put("paymentMethodId", paymentMethodId);

cmNewPurposeTypeId = parameters.get("contactMechPurposeTypeId");
if (cmNewPurposeTypeId != null) {
    contactMechPurposeType = delegator.findByPrimaryKey("ContactMechPurposeType", UtilMisc.toMap("contactMechPurposeTypeId", cmNewPurposeTypeId));
    if (contactMechPurposeType != null) {
        context.put("contactMechPurposeType", contactMechPurposeType);
        // set the create title label according to the contact mech purpose
        context.put("createContactMechLabel", contactMechPurposeType.get("description"));
    } else {
        cmNewPurposeTypeId = null;
    }
    context.put("cmNewPurposeTypeId", cmNewPurposeTypeId);
}

context.put("donePage", parameters.get("DONE_PAGE"));
