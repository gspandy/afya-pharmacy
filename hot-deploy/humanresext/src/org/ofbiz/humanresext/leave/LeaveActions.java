package org.ofbiz.humanresext.leave;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.humanresext.util.HumanResUtil;

/**
 * @author sandeep
 */
public class LeaveActions {
	
	private static final String resourse = "HumanResExtUiLabels"; 
	
	public static String addNewEmplLeave(HttpServletRequest request, HttpServletResponse response){
		GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
		GenericValue leave = delegator.makeValue("EmplLeave");
		Timestamp fromDate = HumanResUtil.toSqlTimestampNoTime(request.getParameter("fromDate"));
		Timestamp thruDate = HumanResUtil.toSqlTimestampNoTime(request.getParameter("thruDate"));
		if(thruDate == null)
			thruDate = fromDate;

		if(!isValidLeaveData(fromDate, thruDate, request))
			return "error";
		Double lpaidDays = HumanResUtil.getWorkingDaysBetween(fromDate, thruDate, delegator).doubleValue();
		System.out.println("lpaidDays " + lpaidDays);
		leave.set("leaveTypeId", request.getParameter("leaveTypeId"));
		leave.set("fromDate", fromDate);
		leave.set("thruDate", thruDate);
		//leave.set("addsWhileOnLeave", request.getParameter("addsWhileOnLeave"));
		leave.set("description", request.getParameter("description"));
		leave.set("leaveStatusId", "LT_SAVED"); //First leave is just saved
		leave.set("paidDays", lpaidDays); //By default all leave days are paid days
		
		//GenericValue user = (GenericValue)request.getSession().getAttribute("userLogin");
		
		String emplId = (String)request.getParameter("partyId");
		System.out.println("************* \n\n empl party id: " + emplId);
		String leaveId = delegator.getNextSeqId("EmplLeave");
		leave.set("partyId", emplId);
		leave.set("leaveId", leaveId);
		
		try {
			leave.set("mgrPositionId", HumanResUtil.getManagerPositionIdForParty(emplId, delegator));
			delegator.create(leave);
		} catch (GenericEntityException e) {
			request.setAttribute("_ERROR_MESSAGE_", message(e.getMessage(), request));
			return "error";
		}
		request.setAttribute("_EVENT_MESSAGE_", message("LeaveAppliedSuccessfully", request));
		return "success";
	}
	
	private static boolean isValidLeaveData(Timestamp from, Timestamp to, HttpServletRequest request){
		boolean valid = true;
		if(to.before(from)){
			request.setAttribute("_ERROR_MESSAGE_", message("ToBeforeFromError", request));
			valid = false;
		}
		return valid;
	}

	public static String cancelLeave(HttpServletRequest request, HttpServletResponse response){
		String[] primKey = request.getQueryString().replaceAll("%", " ").split("&");
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		try {
			GenericValue leave = delegator.findOne("EmplLeave", false, "partyId", primKey[0], "fromDate", HumanResUtil.toSqlTimestampNoTime(primKey[1]));
			leave.set("leaveStatusId", "CANCELLED");
			leave.store();
		} catch (GenericEntityException e) {
			request.setAttribute("_ERROR_MESSAGE_", message("LeaveCancelError", request));
			return "error";
		}
		request.setAttribute("_EVENT_MESSAGE_", message("LeaveCancelSuccessful", request));
		return "success";		
	}
	
	public static String approveLeave(HttpServletRequest request, HttpServletResponse response){
		if(storeMgrComment(request, "APPROVED"))
			request.setAttribute("_EVENT_MESSAGE_", message("LeaveApprovedSuccessfully", request));
		else
			request.setAttribute("_ERROR_MESSAGE_", message("LeaveUnknownError", request));
		return "success";
	}

	public static String rejectLeave(HttpServletRequest request, HttpServletResponse response){
		if(storeMgrComment(request, "REJECTED"))
			request.setAttribute("_EVENT_MESSAGE_", message("LeaveRejectedSuccessfully", request));
		else
			request.setAttribute("_ERROR_MESSAGE_", message("LeaveUnknownError", request));
		return "success";
	}
	
	private static boolean storeMgrComment(HttpServletRequest request, String leaveStatusId){
		String partyId = request.getParameter("partyId");
		String fromDate = request.getParameter("fromDate");
		String mgrComment = request.getParameter("mgrComment");
		GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
		try {
			GenericValue leave = delegator.findOne("EmplLeave", false, "partyId", partyId, "fromDate", fromDate);
			leave.set("leaveStatusId", leaveStatusId);
			leave.set("mgrComment", mgrComment);
			leave.store();
		} catch (GenericEntityException e) {
			return false;
		}
		return true;		
	}
	
	private static String message(String messageKey, HttpServletRequest request){
		return UtilProperties.getMessage(resourse, messageKey, request.getLocale());
	}
}