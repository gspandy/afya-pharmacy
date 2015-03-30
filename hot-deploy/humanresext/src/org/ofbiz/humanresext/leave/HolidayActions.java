package org.ofbiz.humanresext.leave;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.humanresext.util.HumanResUtil;

/**
 * @author sandeep
 */

public class HolidayActions {
	
	public static String addPublicHoliday(HttpServletRequest request, HttpServletResponse response){
		GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
		GenericValue holiday = delegator.makeValue("PublicHoliday");
		holiday.set("onDate", HumanResUtil.toSqlTimestampNoTime(request.getParameter("onDate")));
		holiday.set("description", request.getParameter("description"));
		try {
			delegator.create(holiday);
		} catch (GenericEntityException e) {
			request.setAttribute("_ERROR_MESSAGE_", "Holiday could not be added for some unknown database reason");
			return "success";
		}
		request.setAttribute("_EVENT_MESSAGE_", "Public holiday Added successfully");
		return "success" ;
	}
	
	public static String editWeekdays(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException{
		GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
		String[] strSelectedWeekdays = request.getParameterValues("hr_Weekday");

		storeNonWorkingDays(delegator, strSelectedWeekdays);
		return "success";
//		return "error";
	}

	public static String storeNonWorkingDays(GenericDelegator delegator, String[] strSelectedWeekdays)
			throws GenericEntityException {
		List<GenericValue> weekday = delegator.findList("hr_Weekday", null, null, null, null, false);		

		boolean isNoWeekdaySelected = (null == strSelectedWeekdays)?true:false; // the flag indicates whether no html check boxes were selected
																				// if none are selected the strSelectedWeekdays variable is null

		storeNonWorkingDays(delegator, strSelectedWeekdays, isNoWeekdaySelected);

		return "success";
//		return "error";
	}

	public static void storeNonWorkingDays(GenericDelegator delegator,
			String[] strSelectedWeekdays, boolean isNoWeekdaySelected)
			throws GenericEntityException {
		List<GenericValue> weekday = delegator.findList("hr_Weekday", null, null, null, null, false);		

		for (Iterator<GenericValue> iterator = weekday.iterator(); iterator.hasNext();) {
			GenericValue genericValue = iterator.next();
			
			if(isNoWeekdaySelected) {						// if no weekday is selected set all isWorking fields to 'Y' and go to next value 
				genericValue.set("isWorking","Y");
				System.out.println("isNoWeekdaySelected " + isNoWeekdaySelected);
			} else { // executed if at least one check box was selected on the screen

				for (int i = 0; i < strSelectedWeekdays.length; i++) {
					System.out.println("strSelectedWeekdays " + strSelectedWeekdays[i]);
				}

				List<String> selectedWeekdaysList = Arrays.asList(strSelectedWeekdays);
				String strWeekday = (String)genericValue.get("hr_day");
				boolean isValuePresent = selectedWeekdaysList.contains(strWeekday);

				if(isValuePresent) { 												// if the value of the 'day' field
					genericValue.set("isWorking","N" );								//  matches any of the selected weekday(s) on the screen
					System.out.println("strWeekday " + strWeekday + "selectedWeekdaysList " + isValuePresent);

				} else { 															// the value of the 'day' field does not match
					System.out.println("strWeekday " + strWeekday);					
					genericValue.set("isWorking","Y" );								// any of the selected weekday(s)
				}				
			}
			delegator.store(genericValue);
			System.out.println("genericValue " + genericValue);
		}
	}
}