package com.nthdimenzion.humanres.policy;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;


public class LeavePolicyDelegator {

	LeavePolicyManager manager;

	private static final String module = "LeavePolicyDelegator";

	public LeavePolicyManager getManager() {
		return manager;
	}

	public void setManager(LeavePolicyManager manager) {
		this.manager = manager;
	}

	
	public void runMonthEndBatch(GenericDelegator delegator){
		manager.executeMonthEndBatch(getAllEmployeesForMonthlyProcessing(delegator),delegator);
	}
	
	private List<GenericValue> getAllEmployeesForMonthlyProcessing(GenericDelegator delegator) {

		List<GenericValue> entities = null;
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date thruDate = new Date(calendar.getTime().getTime());
		
		EntityCondition whereCondition = EntityCondition.makeCondition(UtilMisc.toMap("thruDate",thruDate));
		System.out.println("Entitycondition **** "+whereCondition );
		try {
			entities = delegator.findList("EmplEarnedLeaveMonthly", whereCondition, null, null, null, false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return entities;
	}

	public void allocateEarnedLeave(String partyId,GenericDelegator delegator) throws GenericEntityException {
			manager.allocateEarnedLeave(partyId,delegator);
	}

	public void executeRollup(GenericDelegator delegator){
		manager.executeRollup(delegator);
	}
}
