package org.ofbiz.humanresext.util;

import java.util.HashMap;
import java.util.Map;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;

public class PermissionUtil {

	public static Map<String, Boolean> checkPermission(
			DispatchContext dispatcher, Map<String, Object> context) {
		Map<String, Boolean> result = new HashMap<String, Boolean>();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = (String) context.get("partyId");

		String loggedInUserId = userLogin.getString("partyId");

		System.out.println("\n\n\n ************************ ");
		System.out.println(" PartyId " + partyId + " Logged In User Id "
				+ loggedInUserId);

		if (loggedInUserId.equals(partyId)) {
			result.put("hasPermission", Boolean.TRUE);
			return result;
		}

		try {
			GenericValue managerParty = HumanResUtil
					.getReportingMangerForParty(partyId, (GenericDelegator) dispatcher
							.getDelegator());
			if (managerParty != null
					&& loggedInUserId.equals(managerParty.getString("partyId"))) {
				result.put("hasPermission", Boolean.TRUE);
				return result;
			}

		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.put("hasPermission", Boolean.FALSE);
		}
		boolean permission = dispatcher.getSecurity().hasEntityPermission(
				"HUMANRES", "_ADMIN", userLogin);

		result.put("hasPermission", new Boolean(permission));
		return result;
	}
}
