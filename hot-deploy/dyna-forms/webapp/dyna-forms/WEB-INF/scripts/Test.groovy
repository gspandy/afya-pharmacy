import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericDelegator;

import com.smebiz.sfa.party.PartyContactHelper;

public void run(Map context){


GenericDelegator delegator = GenericDelegator
	.getGenericDelegator("default");
System.out.println(" PARTY ID "+context.get("partyId")[0]);

GenericValue contactValue = null;
		try {
			contactValue = PartyContactHelper.getPostalAddressValueByPurpose(
					context.get("partyId")[0], "PRIMARY_LOCATION", true, delegator);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		context.put("address",contactValue);
}