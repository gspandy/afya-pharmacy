import org.ofbiz.entity.GenericDelegator
import org.ofbiz.entity.GenericEntityException
import org.ofbiz.entity.GenericValue
import org.ofbiz.entity.util.EntityUtil


partyId = parameters.partyId
List postalAddressDetails = org.ofbiz.party.contact.ContactMechWorker.getPartyContactMechValueMaps(delegator, partyId, false,"POSTAL_ADDRESS");
for(Map postalAddress:postalAddressDetails){
    String contactMechId =  postalAddress.contactMech.contactMechId.toString();
   if("PRIMARY_LOCATION".equalsIgnoreCase(getContactMechPurPoseType(contactMechId))){
       context.postalAddress = postalAddress;
       break;
   }
}


def getContactMechPurPoseType(String contactMechId) throws GenericEntityException {
    List partyContactMechPurposeList = delegator.findByAnd("PartyContactMechPurpose", ["contactMechId": contactMechId]);
    GenericValue partyContactMechPurposeGv = EntityUtil.getFirst(partyContactMechPurposeList);
    String contactMechPurPoseTypeId = null;
    if (partyContactMechPurposeGv != null) {
        contactMechPurPoseTypeId = (String) partyContactMechPurposeGv.getString("contactMechPurposeTypeId");
    }
    return contactMechPurPoseTypeId;
}