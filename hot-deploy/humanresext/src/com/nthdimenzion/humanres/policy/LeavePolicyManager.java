package com.nthdimenzion.humanres.policy;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityDateFilterCondition;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityListIterator;

import com.nthdimenzion.humanres.policy.LeavePolicy.LeavePolicyAttribute;
import com.smebiz.common.UtilDateTimeSME;

public class LeavePolicyManager {

	final static String LEAVEPOLICY = "LEAVE_POLICY";
	LeavePolicy policy = null;
	Calendar calendar = GregorianCalendar.getInstance();

	public void initialize(GenericDelegator delegator) {
		policy = new LeavePolicy();
		EntityCondition whereEntityCondition = EntityCondition.makeCondition(UtilMisc.toMap("policyTypeName", LEAVEPOLICY));
		Set<String> fieldsToSelect = new HashSet<String>();
		fieldsToSelect.add("attrName");
		fieldsToSelect.add("attrNumValue");

		EntityListIterator iter = null;
		try {
			iter = delegator.find("PolicyDetailView", whereEntityCondition, null, fieldsToSelect, null, null);

			GenericValue rec = iter.next();
			while (rec != null) {
				String attrName = rec.getString("attrName");
				Number num = rec.getDouble("attrNumValue");
				LeavePolicyAttribute attribute = LeavePolicyAttribute.valueOf(attrName);
				attribute.setValue(policy, num);
				rec = iter.next();
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				iter.close();
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		System.out.println(" ******* LEAVE POLICY *********** " + policy);
	}

	public void executeMonthEndBatch(List<GenericValue> employees,GenericDelegator delegator) {

		Iterator<GenericValue> iter = employees.iterator();
		for (GenericValue earnedLeaveRec = null; iter.hasNext();) {
			earnedLeaveRec = iter.next();
			boolean beganTransaction = false;
			try {
				beganTransaction = TransactionUtil.begin(7200);
				String partyId = earnedLeaveRec.getString("partyId");

				Double earnedLeaves = earnedLeaveRec.getDouble("earnedLeaves");
				java.sql.Date fromDate = earnedLeaveRec.getDate("fromDate");
				java.sql.Date thruDate = earnedLeaveRec.getDate("thruDate");

				thruDate = getNextMonthEndDate(fromDate);
				fromDate = getNextMonthBeginDate(fromDate);

				earnedLeaves = earnedLeaves + policy.getEarnedLeavesPerMonth();

				Map<String, Object> fields = UtilMisc.toMap("partyId", partyId, "fromDate", fromDate, "thruDate", thruDate, "earnedLeaves",
						earnedLeaves);

				delegator.create("EmplEarnedLeaveMonthly", fields);

				TransactionUtil.commit(beganTransaction);

			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					TransactionUtil.rollback();
				} catch (GenericTransactionException gte) {
					gte.printStackTrace();
				}
			}
		}

	}

	private double getPaidLeavesForParty(String partyId, Date fromDate, Date thruDate,GenericDelegator delegator) {

		double paidDays = 0d;

		EntityCondition leave_cond1 = EntityDateFilterCondition.makeCondition("fromDate", EntityComparisonOperator.GREATER_THAN_EQUAL_TO,
				new Timestamp(fromDate.getTime()));
		EntityCondition leave_cond2 = EntityDateFilterCondition.makeCondition("thruDate", EntityComparisonOperator.LESS_THAN_EQUAL_TO,
				new Timestamp(thruDate.getTime()));

		EntityCondition leave_cond3 = EntityCondition.makeCondition("partyId", EntityComparisonOperator.EQUALS, partyId);

		EntityCondition leave_cond = EntityCondition.makeCondition(UtilMisc.toList(leave_cond3, leave_cond1, leave_cond2));

		List<GenericValue> leaves = null;
		try {
			leaves = delegator.findList("EmplLeave", leave_cond, null, null, null, false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (leaves != null) {
			for (GenericValue leave : leaves) {
				paidDays += leave.getDouble("paidDays");
			}
		}
		return paidDays;
	}

	private GenericValue getPrevCustomTimePeriodHalfYearly(Date fromDate,GenericDelegator delegator) {
		System.out.println("*** getPreviousHalfYearPeriod() for " + fromDate);
		GenericValue customPeriod = null;
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(fromDate);
		calendar.add(Calendar.DATE, -1);
		Date prevHalfYearEndDate = new Date(calendar.getTime().getTime());
		List<GenericValue> periods = null;
		try {
			periods = delegator.findByAnd("CustomTimePeriod", UtilMisc.toMap("thruDate", prevHalfYearEndDate, "periodTypeId",
					"HALF_YEAR"));
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		if (periods != null) {
			customPeriod = periods.get(0);
			System.out.println("*** RETURNING PREVIOUS HALF_YEAR CUSTOM TIME PERIOD " + customPeriod);
		}
		return customPeriod;
	}

	public Date getNextMonthBeginDate(Date date) {
		calendar.setTime(date);
		Date fromDate = null;
		if (calendar.get(Calendar.MONTH) < 11) {
			calendar.roll(Calendar.MONTH, true);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			fromDate = new Date(calendar.getTime().getTime());
		} else {
			calendar.roll(Calendar.YEAR, true);
			calendar.set(Calendar.DAY_OF_MONTH, 01);
			calendar.set(Calendar.MONTH, 0);
			fromDate = new Date(calendar.getTime().getTime());
		}
		return fromDate;
	}

	public Date getNextMonthEndDate(Date date) {
		calendar.setTime(date);
		Date thruDate = null;
		if (calendar.get(Calendar.MONTH) < 11) {
			calendar.roll(Calendar.MONTH, true);
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			thruDate = new Date(calendar.getTime().getTime());
		} else {
			calendar.roll(Calendar.YEAR, true);
			calendar.set(Calendar.MONTH, 0);
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			thruDate = new Date(calendar.getTime().getTime());
		}
		return thruDate;
	}

	/**
	 * It calculates the pro rate Earned Leaves.
	 * 
	 * @param partyId
	 * @throws GenericEntityException
	 */
	public void allocateEarnedLeave(String partyId,GenericDelegator delegator) throws GenericEntityException {
		double leavesPerMonth = policy.getEarnedLeavesPerMonth();

		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 01);

		Date fromDate = new Date(calendar.getTime().getTime());
		System.out.println("Before " + calendar.getTime());

		calendar.set(Calendar.DAY_OF_MONTH, calendar.getMaximum(Calendar.DAY_OF_MONTH));

		Date thruDate = new Date(calendar.getTime().getTime());

		double daysRemaining = UtilDateTimeSME.getMonthInterval(UtilDateTime.nowDate(), thruDate);

		System.out.println("After " + calendar.getTime());

		Map<String, Object> fields = new HashMap<String, Object>();
		fields.put("partyId", partyId);
		fields.put("fromDate", fromDate);
		fields.put("thruDate", thruDate);
		fields.put("earnedLeaves", leavesPerMonth * daysRemaining);

		delegator.create("EmplEarnedLeaveMonthly", fields);
	}

	@SuppressWarnings("deprecation")
	public void executeRollup(GenericDelegator delegator) {

		Calendar calendar = GregorianCalendar.getInstance();
		int year = calendar.get(Calendar.YEAR);

		Date thruDate = new Date(calendar.getTime().getTime());

		EntityCondition entityCondition = EntityCondition.makeCondition(UtilMisc.toMap("periodTypeId", "HALF_YEAR", "thruDate", thruDate));

		Set<String> fieldsToSelect = new HashSet<String>();
		fieldsToSelect.add("customTimePeriodId");
		fieldsToSelect.add("fromDate");
		fieldsToSelect.add("thruDate");

		List<GenericValue> periods = null;
		try {
			periods = delegator.findList("CustomTimePeriod", entityCondition, fieldsToSelect, null, null, true);
		} catch (GenericEntityException ge) {

		}

		GenericValue halfYearRec = periods.get(0);
		String customTimePeriodId = halfYearRec.getString("customTimePeriodId");

		Date halfYearFromDate = halfYearRec.getDate("fromDate");
		Date halfYearThruDate = halfYearRec.getDate("thruDate");

		List<GenericValue> records = null;

		EntityCondition condition = EntityCondition.makeCondition("thruDate", EntityComparisonOperator.EQUALS, halfYearThruDate);

		try {
			records = delegator.findList("EmplEarnedLeaveMonthly", condition, null, null, null, false);

		} catch (GenericEntityException ge) {
			ge.printStackTrace();
		}

		boolean rolledUp = false;
		for (GenericValue rec : records) {
			try {
				String partyId = rec.getString("partyId");
				/*
				 * double leavesForThisHalf = getPaidLeavesForParty(partyId,
				 * halfYearFromDate, halfYearThruDate);
				 */
				double earnedLeaves = rec.getDouble("earnedLeaves");

				Map<String, Object> fields = new HashMap<String, Object>();
				fields.put("partyId", partyId);
				fields.put("customTimePeriodId", customTimePeriodId);
				fields.put("earnedLeaves", earnedLeaves);
				delegator.create("EmplEarnedLeaveRollup", fields);
				rolledUp = true;
			} catch (GenericEntityException ge) {
				ge.printStackTrace();
			}
			if (rolledUp) {
				try {
					EntityCondition condition3 = EntityCondition.makeCondition("thruDate", EntityComparisonOperator.LESS_THAN,
							halfYearThruDate);
					delegator.removeByCondition("EmplEarnedLeaveMonthly", condition3, false);
				} catch (GenericEntityException ge) {
					ge.printStackTrace();
				}
			}
		}
	}
}
