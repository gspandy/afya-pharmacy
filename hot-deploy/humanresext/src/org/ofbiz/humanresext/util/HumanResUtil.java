package org.ofbiz.humanresext.util;

import javolution.util.FastList;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.humanresext.leave.LeaveTypeCountWrapper;
import org.ofbiz.party.contact.ContactHelper;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.util.*;



/**
 * 
 * @author sandeep
 * 
 */
public class HumanResUtil {


	public static GenericValue getReportingMangerForParty(String partyId, GenericDelegator delegator) throws GenericEntityException {
		String mgrPositionId = getManagerPositionIdForParty(partyId, delegator);
		if (mgrPositionId == null)
			return null;
		GenericValue mgrPositionFilledPosition = getLatestFulfillmentForPosition(mgrPositionId, delegator);
		if (mgrPositionFilledPosition == null)
			return null;
		return mgrPositionFilledPosition.getRelatedOne("Party");
	}

	/* Nafis */
	@SuppressWarnings("null")
	public static String[] getAllReportingMangerForParty(String partyId, GenericDelegator delegator) throws GenericEntityException {

		GenericValue emplPosition = getEmplPositionForParty(partyId, delegator);
		if (emplPosition == null)
			return null;
		EntityCondition condition = EntityCondition.makeCondition("emplPositionIdManagedBy", emplPosition.get("emplPositionId"));
		List<GenericValue> allStructs = delegator.findList("EmplPositionReportingStruct", condition, null, null, null, false);
		if (allStructs == null || allStructs.size() == 0)
			return null;
		String[] mgrPositionFilledPosition = new String[allStructs.size()];
		int i = 0;
		for (GenericValue gv : allStructs) {
			String emplPositionId = gv.getString("emplPositionIdReportingTo");
			EntityCondition findPartyIdCondition = EntityCondition.makeCondition("emplPositionId", emplPositionId);
			List<GenericValue> findPartyIdList = delegator.findList("EmplPositionFulfillment", findPartyIdCondition, null, null, null, false);
			String mgrPartyId = (String) findPartyIdList.get(0).get("partyId");
			mgrPositionFilledPosition[i] = mgrPartyId;
			i++;
		}
		return mgrPositionFilledPosition;
	}

	public static List<String> getAllReportingMangerForParty(GenericValue userLogin, Timestamp fromDate, Timestamp thrudate) throws GenericEntityException {
		String partyId = (String) userLogin.get("partyId");
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
		GenericValue emplPosition = getEmplPositionForParty(partyId, delegator);
		if (emplPosition == null)
			return null;

		EntityCondition condition = EntityCondition.makeCondition("emplPositionIdManagedBy", emplPosition.get("emplPositionId"));
		List<GenericValue> allStructs = delegator.findList("EmplPositionReportingStruct", condition, null, null, null, false);
		if (allStructs == null || allStructs.size() == 0)
			return null;
		List<String> mgrPositionFilledPosition = new ArrayList<String>();
		int i = 0;
		for (GenericValue gv : allStructs) {
			Timestamp actualFromDate = gv.getTimestamp("fromDate");
			Timestamp actualThruDate = gv.getTimestamp("thruDate");

			if (actualThruDate == null) {
				if (actualFromDate.compareTo(thrudate) <= 0) {
					String emplPositionId = gv.getString("emplPositionIdReportingTo");
					EntityCondition findPartyIdCondition = EntityCondition.makeCondition("emplPositionId", emplPositionId);
					List<GenericValue> findPartyIdList = delegator.findList("EmplPositionFulfillment", findPartyIdCondition, null, null, null, false);
					String mgrPartyId = (String) findPartyIdList.get(0).get("partyId");
					mgrPositionFilledPosition.add(mgrPartyId);
				}
			}
			if (actualThruDate != null) {
				if (actualThruDate.compareTo(fromDate) >= 0 && actualFromDate.compareTo(thrudate) <= 0) {
					String emplPositionId = gv.getString("emplPositionIdReportingTo");
					EntityCondition findPartyIdCondition = EntityCondition.makeCondition("emplPositionId", emplPositionId);
					List<GenericValue> findPartyIdList = delegator.findList("EmplPositionFulfillment", findPartyIdCondition, null, null, null, false);
					String mgrPartyId = (String) findPartyIdList.get(0).get("partyId");
					mgrPositionFilledPosition.add(mgrPartyId);
				}
			}
			i++;
		}
		return mgrPositionFilledPosition;
	}

	public static List<String> getAllActiveReportingMangerForParty(String partyId, Timestamp fromDate, Timestamp thrudate,GenericDelegator delegator) throws GenericEntityException {
		GenericValue emplPosition = getEmplPositionForParty(partyId, delegator);
		if (emplPosition == null)
			return null;

		EntityCondition condition = EntityCondition.makeCondition("emplPositionIdManagedBy", emplPosition.get("emplPositionId"));
		List<GenericValue> allStructs = delegator.findList("EmplPositionReportingStruct", condition, null, null, null, false);
		if (allStructs == null || allStructs.size() == 0)
			return null;
		List<String> mgrPositionFilledPosition = new ArrayList<String>();
		int i = 0;
		for (GenericValue gv : allStructs) {
			Timestamp actualFromDate = gv.getTimestamp("fromDate");
			Timestamp actualThruDate = gv.getTimestamp("thruDate");

			if (actualThruDate == null) {
				if (actualFromDate.compareTo(thrudate) <= 0) {
					String emplPositionId = gv.getString("emplPositionIdReportingTo");
					EntityCondition findPartyIdCondition = EntityCondition.makeCondition("emplPositionId", emplPositionId);
					List<GenericValue> findPartyIdList = delegator.findList("EmplPositionFulfillment", findPartyIdCondition, null, null, null, false);
					String mgrPartyId = (String) findPartyIdList.get(0).get("partyId");
					GenericValue emplPositionGv = getEmplPositionForParty(mgrPartyId, delegator);
					if (!("EMPL_POS_INACTIVE".equals(emplPositionGv.getString("statusId"))))
						mgrPositionFilledPosition.add(mgrPartyId);
				}
			}
			if (actualThruDate != null) {
				if (actualThruDate.compareTo(fromDate) >= 0 && actualFromDate.compareTo(thrudate) <= 0) {
					String emplPositionId = gv.getString("emplPositionIdReportingTo");
					EntityCondition findPartyIdCondition = EntityCondition.makeCondition("emplPositionId", emplPositionId);
					List<GenericValue> findPartyIdList = delegator.findList("EmplPositionFulfillment", findPartyIdCondition, null, null, null, false);
					String mgrPartyId = (String) findPartyIdList.get(0).get("partyId");
					GenericValue emplPositionGv = getEmplPositionForParty(mgrPartyId, delegator);
					if (!("EMPL_POS_INACTIVE".equals(emplPositionGv.getString("statusId"))))
						mgrPositionFilledPosition.add(mgrPartyId);
				}
			}
			i++;
		}
		return mgrPositionFilledPosition;
	}

	public static List<GenericValue> getSubordinatesForParty(String partyId, GenericDelegator delegator) throws GenericEntityException {
		GenericValue emplPosition = getEmplPositionForParty(partyId, delegator);
		if (emplPosition == null)
			return null;
		EntityCondition condition = EntityCondition.makeCondition("emplPositionIdReportingTo", emplPosition.get("emplPositionId"));
		List<GenericValue> allStructs = delegator.findList("EmplPositionReportingStruct", condition, null, UtilMisc.toList("-fromDate"), null, false);
		if (allStructs == null || allStructs.size() == 0)
			return null;
		return allStructs;
	}

	public static GenericValue getManagerPositionForParty(String partyId, GenericDelegator delegator) throws GenericEntityException {
		String managerPositionId = getManagerPositionIdForParty(partyId, delegator);
		if (managerPositionId == null)
			return null;
		return delegator.findOne("EmplPosition", false, "emplPositionId", managerPositionId);
	}

	public static String getManagerPositionIdForParty(String partyId, GenericDelegator delegator) throws GenericEntityException {
		GenericValue emplPosition = getEmplPositionForParty(partyId, delegator);
		if (emplPosition == null)
			return null;
		EntityCondition condition = EntityCondition.makeCondition("emplPositionIdManagedBy", emplPosition.get("emplPositionId"));
		List<GenericValue> allStructs = delegator.findList("EmplPositionReportingStruct", condition, null, UtilMisc.toList("-fromDate"), null, false);
		if (allStructs == null || allStructs.size() == 0)
			return null;
		GenericValue reportingStruct = allStructs.get(0);
		if (reportingStruct != null) {
			return (String) reportingStruct.get("emplPositionIdReportingTo");
		}
		return null;
	}

	public static GenericValue getEmplPositionForParty(GenericValue party, GenericDelegator delegator) throws GenericEntityException {
		return getEmplPositionForParty((String) party.get("partyId"), delegator);
	}

	public static GenericValue getEmplPositionForParty(String partyId, GenericDelegator delegator) throws GenericEntityException {
		GenericValue filledPosition = getLatestEmplPositionFulfillmentForParty(partyId, delegator);
		if (filledPosition == null)
			return null;
		return filledPosition.getRelatedOne("EmplPosition");
	}

	public static GenericValue getLatestEmplPositionFulfillmentForParty(String partyId, GenericDelegator delegator) throws GenericEntityException {
		EntityCondition condition = EntityCondition.makeCondition("partyId", partyId);
		List<String> orederByList = FastList.newInstance();
		orederByList.add("-fromDate");
		List<GenericValue> allFilledpositions = delegator.findList("EmplPositionFulfillment", condition, null, orederByList, null, false);
		if (allFilledpositions == null || allFilledpositions.size() == 0)
			return null;
		return allFilledpositions.get(0);
	}

	public static GenericValue getLatestFulfillmentForPosition(String positionId, GenericDelegator delegator) throws GenericEntityException {
		EntityCondition condition = EntityCondition.makeCondition("emplPositionId", positionId);
		List<String> orederByList = FastList.newInstance();
		orederByList.add("-fromDate");
		List<GenericValue> allFilledpositions = delegator.findList("EmplPositionFulfillment", condition, null, orederByList, null, false);
		if (allFilledpositions == null || allFilledpositions.size() == 0)
			return null;
		return allFilledpositions.get(0);
	}

	public static java.sql.Timestamp toSqlTimestampNoTime(String dateTime) {
		return new Timestamp(toSqlDate(dateTime).getTime());
	}

	public static java.sql.Date toSqlDate(String dateTime) {
		if (dateTime == null)
			return null;
		String[] dateNTime = splitDateTime(dateTime);
		return Date.valueOf(dateNTime[0]);
	}

	public static String[] splitDateTime(String dateTime) {
		return splitDateTime(dateTime, " ");
	}

	public static String[] splitDateTime(String dateTime, String delm) {
		return dateTime.split(delm);
	}

	public static boolean checkPrefReviewAccess(GenericDelegator delegator, String perfReviewId, String partyId) {
		GenericValue value = null;
		try {
			value = delegator.findOne("EmplPerfReview", UtilMisc.toMap("perfReviewId", perfReviewId, "partyId", partyId), true);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return false;
		}
		if (value != null)
			return true;

		return false;
	}

	public static String getEmailAddress(GenericValue party) {
		String email = null;
		if (party != null) {
			GenericValue partyContact = EntityUtil
					.getFirst((List<GenericValue>) (ContactHelper.getContactMech(party, "PRIMARY_EMAIL", "EMAIL_ADDRESS", false)));
			if (partyContact != null)
				email = (String) partyContact.get("infoString");
		}
		return email;
	}

	public static List<LeaveTypeCountWrapper> findLeaveDetailsByType(GenericDelegator delegator, EntityCondition condition) {
		Map<String, LeaveTypeCountWrapper> leaveBalanceByType = new HashMap<String, LeaveTypeCountWrapper>();

		List<GenericValue> availableLeaveTypes = FastList.newInstance();
		List<GenericValue> leaveCountViews = FastList.newInstance();
		try {
			availableLeaveTypes = delegator.findList("EmplLeaveType", null, null, null, null, true);
			leaveCountViews = delegator.findList("LeaveTypeCountView", condition, null, null, null, false);
		} catch (GenericEntityException e) {
			throw new RuntimeException(e);
		}

		for (GenericValue leaveType : availableLeaveTypes) {
			String leaveTypeId = (String) leaveType.get("leaveTypeId");
			leaveBalanceByType.put(leaveTypeId, new LeaveTypeCountWrapper(leaveTypeId, (Number) leaveType.get("entitled")));
		}
		for (GenericValue leaveTypeUsed : leaveCountViews) {
			Number used = (Number) leaveTypeUsed.get("sum");
			leaveBalanceByType.get((String) leaveTypeUsed.get("leaveTypeId")).setUsed(used);
		}

		return new ArrayList<LeaveTypeCountWrapper>(leaveBalanceByType.values());
	}

	public static Set<Timestamp> getAllPublicHolidays(GenericDelegator delegator) {
		Set<Timestamp> holidays = new HashSet<Timestamp>();
		List<GenericValue> holidayRecords = null;
		try {
			holidayRecords = delegator.findList("PublicHoliday", null, null, null, null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		for (GenericValue holiday : holidayRecords)
			holidays.add((Timestamp) holiday.get("onDate"));
		return holidays;
	}

	public static Set<String> getAllNonWorkingDays(GenericDelegator delegator) {
		List<GenericValue> nonWorkingDaysRecords = null;
		EntityCondition condtn = EntityCondition.makeCondition("isWorking", "N");
		Set<String> fieldsToSelectFromEntity = new HashSet<String>();
		fieldsToSelectFromEntity.add("hr_day");
		try {
			nonWorkingDaysRecords = delegator.findList("hr_Weekday", condtn, fieldsToSelectFromEntity, null, null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}

		Set<String> nonWorkingDays = new HashSet<String>();
		for (GenericValue hr_weekday : nonWorkingDaysRecords) {
			String strWeekday = (String) hr_weekday.get("hr_day");
			nonWorkingDays.add(strWeekday);
		}
		return nonWorkingDays;
	}

	public static Integer getWorkingDaysBetween(Timestamp fromDate, Timestamp thruDate, GenericDelegator delegator) {
		System.out.println("getWorkingDaysBetween ");
		int workingDays = 0;
		Set<Timestamp> holidays = getAllPublicHolidays(delegator);
		Set<String> nonWorkingDays = getAllNonWorkingDays(delegator);
		for (Timestamp currentDay = fromDate; !currentDay.after(thruDate); currentDay = UtilDateTime.addDaysToTimestamp(currentDay, 1)) {

			java.util.Calendar calendar = (new GregorianCalendar());
			calendar.setTime(currentDay);
			String strWeekdayLiteral = convertToDayOfWeekLiteral(currentDay.getTime());

			/*
			 * System.out.println(calendar.get(Calendar.DATE) + " " +
			 * calendar.get(Calendar.MONTH) + " " + calendar.get(Calendar.YEAR)
			 * + " " + calendar.get(Calendar.DAY_OF_WEEK) + " " +
			 * strWeekdayLiteral);
			 */

			Iterator<Timestamp> it = holidays.iterator();
			while (it.hasNext()) {
				Timestamp timestamp = (Timestamp) it.next();
				Calendar tempCal = new GregorianCalendar();
				tempCal.setTime(timestamp);
				/*
				 * System.out.println(tempCal.get(Calendar.DATE) + " " +
				 * tempCal.get(Calendar.MONTH) + " " +
				 * tempCal.get(Calendar.YEAR) + " " +
				 * tempCal.get(Calendar.DAY_OF_WEEK));
				 */
			}

			Iterator<String> it1 = nonWorkingDays.iterator();
			while (it1.hasNext()) {
				String day = (String) it1.next();
				System.out.println(day);
			}

			if (!holidays.contains(currentDay) && !(nonWorkingDays.contains(strWeekdayLiteral))) {
				/*
				 * System.out.println(" !holidays.contains(currentDay) " +
				 * !holidays.contains(currentDay));System.out.println(
				 * " !(nonWorkingDays.contains(strWeekdayLiteral)) " +
				 * !(nonWorkingDays.contains(strWeekdayLiteral)));
				 */
				++workingDays;
			}

		}
		System.out.println("workingDays " + workingDays);
		return workingDays;
	}

	public static String getPositionDescription(GenericDelegator delegator, String emplPositionTypeId) {
		GenericValue value = null;
		try {
			value = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), true);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return "_NA_";
		}
		return value.getString("description");
	}

	public static String getPositionTypeId(GenericDelegator delegator, String emplPositionId) {
		GenericValue value = null;
		try {
			value = delegator.findOne("EmplPosition", UtilMisc.toMap("emplPositionId", emplPositionId), true);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return "_NA_";
		}
		return value.getString("emplPositionTypeId");
	}

	public static String getFullName(String partyId) throws GenericEntityException {
		return getFullName(GenericDelegator.getGenericDelegator("default"), partyId, " ");
	}

	public static String getFullName(GenericDelegator delegator, String partyId, String separator) throws GenericEntityException {

		Map primaryKey = UtilMisc.toMap("partyId", partyId);
		GenericValue entity = delegator.findOne("PartyAndPerson", primaryKey, false);
		String firstName = "N";
		String lastName = "A";
		StringBuilder buffer = new StringBuilder();
		if (entity != null) {
			firstName = entity.getString("firstName");
			lastName = entity.getString("lastName");
			if (separator == null) {
				separator = ",";
			}
		}
		buffer.append(firstName).append(separator == null ? " " : separator).append(lastName);
		return buffer.toString();
	}

	public static List<GenericValue> getAllEmployeesLeaveDetails(GenericDelegator delegator) {

		EntityCondition entityCondition = EntityCondition.makeCondition("thruDate", null);

		Set<String> fieldsToSelect = new HashSet<String>();
		fieldsToSelect.add("partyIdTo");
		fieldsToSelect.add("fromDate");

		List<String> orderBy = new ArrayList<String>(1);
		orderBy.add("fromDate");

		List<GenericValue> entities = null;
		try {
			entities = delegator.findList("EmploymentEarnedLeaveView", entityCondition, fieldsToSelect, orderBy, null, false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return entities;
	}

	private static String convertToDayOfWeekLiteral(long timeInMillisecs) {
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL);
		StringBuffer strBuff = new StringBuffer();
		FieldPosition fieldPos = new FieldPosition(DateFormat.DAY_OF_WEEK_FIELD);

		StringBuffer strOut = dateFormat.format(new Date(timeInMillisecs), strBuff, fieldPos);
		return strOut.substring(fieldPos.getBeginIndex(), fieldPos.getEndIndex());
	}

	/* Added By s@mir */
	public static String getHODPositionId(String departmentId, GenericDelegator delegator) throws GenericEntityException {
		List<GenericValue> positions = delegator.findByAnd("DepartmentPosition", UtilMisc.toMap("departmentId", departmentId));
		positions = EntityUtil.filterByDate(positions);
		GenericValue position = EntityUtil.getFirst(positions);
		System.out.println("*******getHODPositionId DepartmentId:    " + departmentId + "      position" + position);
		if (position == null)
			return null;
		return position.getString("departmentPositionId");
	}

	/* Added By s@mir */
	public static String getPartyIdForPositionId(String positionId, GenericDelegator delegator) throws GenericEntityException {
		List<GenericValue> partyList = delegator.findByAnd("EmplPositionFulfillment", UtilMisc.toMap("emplPositionId", positionId));
		GenericValue party = EntityUtil.getFirst(partyList);
		if (party == null)
			return null;
		return party.getString("partyId");

	}

	/* Added By s@mir */
	public static String getResponsibilityForPositionId(String positionId, GenericDelegator delegator) throws GenericEntityException {
		String responsibilityTypeId = null;
		GenericValue positionResponsibilityTypeGv = null;
		List<GenericValue> responsibilityList = null;
		GenericValue responsibilityTypeGv = null;
		String responsibilityDescription = null;
		List<GenericValue> positionList = delegator.findByAnd("EmplPositionResponsibility", UtilMisc.toMap("emplPositionId", positionId));
		if (positionList.size() > 0) {
			positionResponsibilityTypeGv = EntityUtil.getFirst(positionList);
			responsibilityTypeId = (String) positionResponsibilityTypeGv.getString("responsibilityTypeId");
			responsibilityList = delegator.findByAnd("ResponsibilityType", UtilMisc.toMap("responsibilityTypeId", responsibilityTypeId));
			responsibilityTypeGv = EntityUtil.getFirst(responsibilityList);
			responsibilityDescription = (String) responsibilityTypeGv.getString("description");
		}
		if (responsibilityDescription == null)
			return null;
		return responsibilityDescription;

	}

	/* Added By s@mir */

	public static String getAdditionalResponsibility(String positionId, GenericDelegator delegator) throws GenericEntityException {
		GenericValue positionResponsibilitySubTypeGv = null;
		String responsibilitySubType = null;
		List<GenericValue> positionList = delegator.findByAnd("PosResponsibilitySubType", UtilMisc.toMap("emplPositionId", positionId));
		if (positionList.size() > 0) {
			positionResponsibilitySubTypeGv = EntityUtil.getFirst(positionList);
			responsibilitySubType = (String) positionResponsibilitySubTypeGv.getString("responsibilitySubType");
		}
		if (responsibilitySubType == null)
			return null;
		return responsibilitySubType;
	}

	public static String getDefaultGeoIdForLoggedInUser(GenericValue userLogin) throws Exception {
		String partyId = userLogin.getString("partyId");
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
		GenericValue personLocation = delegator.findOne("PersonLocation", UtilMisc.toMap("partyId", partyId), false);
		if (personLocation == null)
			return null;
		return personLocation.getRelatedOne("PostalAddress").getString("countryGeoId");
	}

	public static GenericValue getPositionIdForPositionType(String departmentId, String emplPositionTypeId,GenericDelegator delegator) {
		GenericValue positionGV = null;
		try {
			positionGV = EntityUtil.getFirst(delegator.findList("EmplPosition", EntityCondition.makeCondition(UtilMisc.toMap("partyId", departmentId,
					"emplPositionTypeId", emplPositionTypeId, "statusId", "EMPL_POS_ACTIVE")), null, null, null, false));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return positionGV;
	}

	public static boolean checkPermission(String partyId, String permissionGroup,GenericDelegator delegator) {
		EntityCondition condition1 = EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "thruDate", null));
		EntityCondition condition2 = EntityCondition.makeCondition("groupId", permissionGroup);
		EntityCondition condition = EntityCondition.makeCondition(condition1, condition2);
		List<GenericValue> checkUserLoginSecurityGroup = null;
		try {
			checkUserLoginSecurityGroup = delegator.findList("PartySecurityAndPartyDetail", condition, null, null, null, true);
		} catch (GenericEntityException e) {
		}
		return checkUserLoginSecurityGroup.size() > 0;
	}
	
	public static boolean checkEmployeeTermination(String partyId, GenericDelegator delegator) {
		EntityCondition condition1 = EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,partyId);
		EntityCondition condition2= EntityCondition.makeCondition("statusId",EntityOperator.EQUALS,"ET_ADM_APPROVED");
		EntityCondition condition3 = EntityCondition.makeCondition(condition1,EntityOperator.AND,condition2);
		EntityCondition condition4=EntityCondition.makeCondition("terminationDate",EntityOperator.LESS_THAN_EQUAL_TO,new java.sql.Date(System.currentTimeMillis()));
		EntityCondition condition5 = EntityCondition.makeCondition(condition3,EntityOperator.AND,condition4);
   
		List<GenericValue> checkEmployeeTerminationList = null;
		try {
			checkEmployeeTerminationList = delegator.findList("MaxTerminationDetail", condition5, null, null, null, true);
		} catch (GenericEntityException e) {
		}
		return checkEmployeeTerminationList.size() > 0;
	}

	public static String getGradeOfEmployeeForOfferLetter(GenericDelegator delegator, String requisitionId)throws GenericEntityException {
		Map primaryKey = UtilMisc.toMap("requisitionId", requisitionId);
		GenericValue entity = delegator.findOne("EmployeeRequisition", primaryKey, false);
		String grade=entity.getString("grade");
		return grade;
	}
	
	public static String getPositionTypeOfEmployeeForOfferLetter(GenericDelegator delegator, String requisitionId)throws GenericEntityException {
		Map primaryKey = UtilMisc.toMap("requisitionId", requisitionId);
		GenericValue entity = delegator.findOne("EmployeeRequisition", primaryKey, false);
		String positionType=entity.getString("positionType");
		return positionType;
	}
	
	public static String getDepartmentNameForOfferLetter(GenericDelegator delegator, String requisitionId)throws GenericEntityException{
		Map primaryKey = UtilMisc.toMap("requisitionId", requisitionId);
		GenericValue entity = delegator.findOne("EmployeeRequisition", primaryKey, false);
		String departmentId=entity.getString("reqRaisedByDept");
		List<GenericValue> departmentGvs = delegator.findByAnd("DepartmentPosition", UtilMisc.toMap("departmentId",departmentId));
	    GenericValue departmentGv = UtilValidate.isNotEmpty(departmentGvs) ? departmentGvs.get(0) : null;
	    if (departmentGv != null) return departmentGv.getString("departmentName");
	    return "";
	}

	public static GenericValue getCurrentFiscalYear(GenericDelegator delegator) throws GenericEntityException {
		java.sql.Date currentDate = new java.sql.Date(new java.util.Date().getTime());
		EntityCondition cn1 = EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, currentDate);
		EntityCondition cn2 = EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, currentDate);
		EntityCondition con = EntityCondition.makeCondition(cn1, EntityOperator.AND, cn2);
		List<GenericValue> customTimePeriod = delegator.findList("CustomTimePeriod", con, null, null, null, false);
		return EntityUtil.getFirst(customTimePeriod);
	}
	
}