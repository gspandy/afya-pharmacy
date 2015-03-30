package com.ndz.zkoss.util;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

public class EmployeePositionService {

	
	public static void updateEmployeePositionStatus(String positionId){
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		GenericValue positionGv= delegator.makeValue("EmplPosition", UtilMisc.toMap("emplPositionId", positionId,"statusId","EMPL_POS_FULFILLED"));
		try {
			delegator.store(positionGv);
		} catch (GenericEntityException e) {
			throw new RuntimeException("Position Id does not exist");
		}
	}
}
