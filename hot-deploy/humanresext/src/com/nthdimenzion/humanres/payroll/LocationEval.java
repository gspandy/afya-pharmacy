package com.nthdimenzion.humanres.payroll;

import java.util.List;
import java.util.Map;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import com.nthdimenzion.humanres.payroll.calc.PayrollContext;

public class LocationEval {

	public static Object execute() {
		PayrollContext ctx = PayrollContext.getInstance();
		GenericValue userLogin = (GenericValue) ctx.get(PayrollContext.USER_LOGIN);
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
		String partyId = userLogin.getString("partyId");
		EntityCondition cond1 = EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_LOCATION"));
		List<GenericValue> contactMechValues = null;
		try {
			contactMechValues = delegator.findList("PartyContactMechPurpose", cond1, null, null, null, false);
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String city = "";
		GenericValue contactMechValue = null;

		if (contactMechValues.size() > 0) {
			contactMechValue = contactMechValues.get(0);
			Map<String, String> params = UtilMisc.toMap("contactMechId", contactMechValue.getString("contactMechId"), "partyId", partyId);

			EntityCondition cond = EntityCondition.makeCondition(params);

			List<GenericValue> values = null;
			try {
				values = delegator.findList("PartyAndPostalAddress", cond, null, null, null, false);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (values.size() > 0) {
				city = values.get(0).getString("city");
			}
		}

		return city;
	}
}
