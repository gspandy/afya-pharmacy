package com.ndz;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityFindOptions;

public class FacilityUtil {

	public static String getDefaultFacilityId() {
		return "MHEWarehouse";
	}

	public static List<String> getReceivingAreas(HttpServletRequest request
			,String buildingId) {
		GenericDelegator delegator = (GenericDelegator) request
				.getAttribute("delegator");
		List<String> locations = new ArrayList<String>();

		try {
			EntityFindOptions findOptions = new EntityFindOptions();
			findOptions.setDistinct(Boolean.TRUE);
			List<GenericValue> locs = delegator.findList("FacilityLocation",
					EntityCondition.makeCondition(UtilMisc.toMap("buildingId", buildingId,"locationTypeEnumId","RECEIVING")),
					UtilMisc.toSet("locationSeqId"), UtilMisc
							.toList("locationSeqId"), findOptions, false);


			for (GenericValue loc : locs) {
				locations.add(loc.getString("locationSeqId"));
			}

		} catch (GenericEntityException ge) {

		}
		return  locations;
	}

	public static List<String> getDistinctBuildings(HttpServletRequest request) {
		GenericDelegator delegator = (GenericDelegator) request
				.getAttribute("delegator");
		List<String> locations = new ArrayList<String>();
		EntityFindOptions findOptions = new EntityFindOptions();
		findOptions.setDistinct(Boolean.TRUE);
		try {
			List<GenericValue> locs = delegator.findList("FacilityLocation",
					null, UtilMisc.toSet("buildingId"), UtilMisc
							.toList("buildingId"), findOptions, false);

			for (GenericValue loc : locs) {
				locations.add(loc.getString("buildingId"));
			}

		} catch (GenericEntityException ge) {

		}
		
		
		
		return locations;
	}
}




