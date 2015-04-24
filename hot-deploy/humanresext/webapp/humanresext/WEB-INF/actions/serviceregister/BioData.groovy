import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import com.smebiz.common.PartyContactHelper;


String partyId = parameters.partyId;

context.primaryLocation = PartyContactHelper.getPostalAddressValueByPurpose(partyId,"PRIMARY_LOCATION",true,delegator);

context.parentLocation =  PartyContactHelper.getPostalAddressValueByPurpose(partyId,"PARENT_LOCATION",true,delegator);

context.spouseLocation = PartyContactHelper.getPostalAddressValueByPurpose(partyId,"SPOUSE_LOCATION",true,delegator);

context.permanentLocation = PartyContactHelper.getPostalAddressValueByPurpose(partyId,"PERMANENT_LOCATION",true,delegator);

context.ltcLocation = PartyContactHelper.getPostalAddressValueByPurpose(partyId,"LTC_LOCATION",true,delegator);
