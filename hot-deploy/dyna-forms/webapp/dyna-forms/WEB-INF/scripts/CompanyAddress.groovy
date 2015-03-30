import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericDelegator;

import com.smebiz.sfa.party.PartyContactHelper;

public void run(Map context){


GenericDelegator delegator = GenericDelegator
	.getGenericDelegator("default");

GenericValue contactValue = null;
		try {
			contactValue = PartyContactHelper.getPostalAddressValueByPurpose(
					"Company", "GENERAL_LOCATION", true, delegator);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		context.put("address",contactValue);
}