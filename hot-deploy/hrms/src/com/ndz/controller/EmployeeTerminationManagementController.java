package com.ndz.controller;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.api.Intbox;
import org.zkoss.zul.api.Comboitem;
import org.zkoss.zul.Window;

import com.ibm.icu.util.Calendar;
import com.ndz.zkoss.HrmsUtil;
import com.ndz.zkoss.UtilMessagesAndPopups;
import com.ndz.zkoss.util.HrmsInfrastructure;
import com.ndz.zkoss.util.MailUtil;

public class EmployeeTerminationManagementController extends GenericForwardComposer {

	public static void applyTerminationApplication(Event event) throws GenericEntityException, InterruptedException {
		Component applyTerminationWindow = event.getTarget();
		applyTerminationWindow.getDesktop().getExecution().getNativeRequest();
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute(
				"userLogin");
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
		
		String employeeId = null;
        Boolean employeeBoxVisible = ((Combobox) applyTerminationWindow.getFellow("employeeBox")).isVisible();
        if (employeeBoxVisible) {
            Comboitem requestingEmployeeId = ((Combobox) applyTerminationWindow.getFellow("employeeBox")).getSelectedItem();
            employeeId = requestingEmployeeId != null ? (String) requestingEmployeeId.getValue() : userLogin.getString("partyId");
        } else
            employeeId = userLogin.getString("partyId");
		
		GenericValue managerGv = HumanResUtil.getManagerPositionForParty(employeeId, delegator);
		
		Security security = (Security) Executions.getCurrent().getAttributes().get("security");
        boolean isManager = security.hasPermission("HUMANRES_MGR", userLogin);
    
		if (managerGv == null && !isManager) {
			Messagebox.show("Cannot be Saved;No Manager For Approval", "Error", 1, null);
			return;
		}
		List<GenericValue> terminationList = delegator.findByAnd("EmplTermination", UtilMisc.toMap("partyId",
				employeeId));

		String submitStatus = null;
		String statusType = null;
		List listOrdered = org.ofbiz.entity.util.EntityUtil.orderBy(terminationList, UtilMisc
				.toList("lastUpdatedStamp DESC"));
		if (listOrdered.size() > 0) {
			GenericValue terminationGv = EntityUtil.getFirst(listOrdered);
			submitStatus = (String) terminationGv.getString("submitStatus");
			statusType = terminationGv.getString("statusType");

			if (submitStatus == null && statusType == null) {
				Messagebox.show("Cannot be Saved Again ;Resignation Application Already Saved", "Error", 1, null);
				return;
			}
			if (submitStatus != null) {
				if (submitStatus.equals("YES")) {
					Messagebox.show("Cannot be Saved;Resignation Application Already Submitted", "Error", 1, null);
					return;
				}
			}

		}
		String mgrPosId = HumanResUtil.getManagerPositionIdForParty(employeeId, delegator);
		
		String mgrPositionId=mgrPosId;
    	if(mgrPosId == null){
    		GenericValue empPos=HumanResUtil.getEmplPositionForParty(employeeId, delegator);
    		mgrPositionId=empPos==null?null:empPos.getString("emplPositionId");
    	}
		
		String terminationReason = (String) ((Textbox) applyTerminationWindow.getFellow("reasonForTermination"))
				.getValue();
		Integer noticePeriodType = (Integer) ((Intbox) applyTerminationWindow.getFellow("noticePeriod")).getValue();
		Long noticePeriod = new Long(noticePeriodType);
		Date applicationDate = (Date) ((Datebox) applyTerminationWindow.getFellow("applicationDate")).getValue();

		Map employeeTerminationDetails = UtilMisc.toMap("userLogin", userLogin, "partyId", employeeId, "reason",
				terminationReason, "mgrPositionId", mgrPositionId, "applicationDate", new java.sql.Date(applicationDate
						.getTime()), "terminationDate", new java.sql.Date(HrmsUtil.addDaysToTimestamp(applicationDate,
						noticePeriod).getTime()), "noticePeriod", noticePeriod, "exitType", "Resignation",
				"lastUpdatedDate", UtilDateTime.nowTimestamp());

		LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
		try {
			dispatcher.runSync("createEmplTerminationService", employeeTerminationDetails);

			Messagebox.show("Resignation Application Saved;Need To Submit", "Success", 1, null);
		} catch (GenericServiceException e) {
			Messagebox.show("Resignation Application Not Saved Due To Data Error", "Error", 1, null);
			e.printStackTrace();
		}
	}

	public static void applyTerminationApplicationFor(Event event, Component comp) throws GenericEntityException,
			InterruptedException {
		Component applyTerminationWindow = event.getTarget();
		applyTerminationWindow.getDesktop().getExecution().getNativeRequest();
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute(
				"userLogin");
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
		String employeeId = userLogin.getString("partyId");
		String partyId = (String) ((Combobox) applyTerminationWindow.getFellow("searchPanel")).getSelectedItem().getValue();
		if(employeeId.equals(partyId)){
			Messagebox.show("Cannot Teminate self. Please change Employee id.", "Error", 1, null);
			return;
		}
		String mgrPosId = HumanResUtil.getManagerPositionIdForParty(partyId, delegator);
		GenericValue loginPosIdGv = HumanResUtil.getEmplPositionForParty(employeeId, delegator);
		String loginPosId = null;
		if (loginPosIdGv != null) {
			loginPosId = (String) loginPosIdGv.getString("emplPositionId");
		}
		if (loginPosId.equals(mgrPosId)) {
			List<GenericValue> terminationList = delegator.findByAnd("EmplTermination", UtilMisc.toMap("partyId",
					partyId));
			String submitStatus = null;
			List listOrdered = org.ofbiz.entity.util.EntityUtil.orderBy(terminationList, UtilMisc
					.toList("lastUpdatedStamp DESC"));
			if (listOrdered.size() > 0) {
				GenericValue terminationGv = EntityUtil.getFirst(listOrdered);
				submitStatus = (String) terminationGv.getString("submitStatus");
				if (submitStatus != null) {
					if (submitStatus.equals("YES")) {
						Messagebox.show("Cannot be Submitted;Termination Application Already Submitted", "Error", 1,
								null);
						return;
					}
				}

			}

			String terminationReason = (String) ((Textbox) applyTerminationWindow.getFellow("reasonForTermination"))
					.getValue();
			Integer noticePeriodType = (Integer) ((Intbox) applyTerminationWindow.getFellow("noticePeriod")).getValue();
			Long noticePeriod = new Long(noticePeriodType);
			Date applicationDateType = (Date) ((Datebox) applyTerminationWindow.getFellow("applicationDate"))
					.getValue();
			java.sql.Date applicationDate = null;
			java.sql.Date latestDate = null;
			java.util.Date now = new java.util.Date();
			java.sql.Date curr = new java.sql.Date(now.getTime());
			if (applicationDateType != null) {
				applicationDate = new java.sql.Date(applicationDateType.getTime());
			} else {
				applicationDate = curr;
			}
			Date terminationDateType = (Date) ((Datebox) applyTerminationWindow.getFellow("terminationDate"))
					.getValue();
			java.sql.Date terminationDate = new java.sql.Date(terminationDateType.getTime());
			Map employeeTerminationDetails = UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "reason",
					terminationReason, "mgrPositionId", mgrPosId, "applicationDate", applicationDate,
					"terminationDate", terminationDate, "noticePeriod", noticePeriod, "exitType", "Termination",
					"lastUpdatedDate", UtilDateTime.nowTimestamp());
			LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
			try {
				Map result = null;
				result = dispatcher.runSync("createEmplTerminationService", employeeTerminationDetails);
				String terminationId = (String) result.get("terminationId");
				Map terminationDetails = UtilMisc.toMap("userLogin", userLogin, "terminationId", terminationId,
						"partyId", partyId, "submitStatus", "YES", "managerStatusType", "2","mgrPositionId", mgrPosId);
				dispatcher.runSync("submitEmplTerminationService", terminationDetails);
				Messagebox.show("Termination Application Submitted;Need To Approve For Further Process", "Success", 1,
						null);
				comp.detach();
			} catch (GenericServiceException e) {
				Messagebox.show("Termination Application Not Saved Due To Data Error", "Error", 1, null);
				e.printStackTrace();
			}
		} else {
			Messagebox.show("You are not the authorized Manager of this Employee", "Error", 1, null);
		}
	}

	public static void updateTerminationApplication(Event event) throws GenericEntityException, InterruptedException {
		Component editTerminationApplicationWindow = event.getTarget();
		editTerminationApplicationWindow.getDesktop().getExecution().getNativeRequest();
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute(
				"userLogin");
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
		String employeeId = userLogin.getString("partyId");
		String mgrPosId = HumanResUtil.getManagerPositionIdForParty(employeeId, delegator);
		String terminationId = (String) ((Label) editTerminationApplicationWindow.getFellow("terminationId"))
				.getValue();
		String terminationReason = (String) ((Textbox) editTerminationApplicationWindow
				.getFellow("reasonForTermination")).getValue();
		Integer noticePeriodType = (Integer) ((Intbox) editTerminationApplicationWindow.getFellow("noticePeriod"))
				.getValue();
		Long noticePeriod = new Long(noticePeriodType);
		Date applicationDateType = (Date) ((Datebox) editTerminationApplicationWindow.getFellow("applicationDate"))
				.getValue();
		java.sql.Date applicationDate = new java.sql.Date(applicationDateType.getTime());

		Map employeeTerminationDetails = UtilMisc.toMap("userLogin", userLogin, "partyId", employeeId, "reason",
				terminationReason, "mgrPositionId", mgrPosId, "applicationDate", applicationDate, "terminationDate",
				new java.sql.Date(HrmsUtil.addDaysToTimestamp(applicationDate, noticePeriod).getTime()),
				"terminationId", terminationId, "noticePeriod", noticePeriod, "lastUpdatedDate", UtilDateTime
						.nowTimestamp());
		LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
		try {
			dispatcher.runSync("updateEmplTerminationService", employeeTerminationDetails);
			Messagebox.show("Termination Application Updated;Need To Submit", "Success", 1, null);
		} catch (GenericServiceException e) {
			Messagebox.show("Resignation Application Not Updated Due To Data Error", "Error", 1, null);
			e.printStackTrace();
		}
	}

	public static void submitTerminationApplication(Event event) throws InterruptedException {

		Component editTerminationApplicationWindow = event.getTarget();
		editTerminationApplicationWindow.getDesktop().getExecution().getNativeRequest();
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute(
				"userLogin");
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

		String terminationId = (String) ((Label) editTerminationApplicationWindow.getFellow("terminationId"))
				.getValue();
		String partyId = (String) ((Label) editTerminationApplicationWindow.getFellow("partyId")).getValue();
		
		 String mgrPositionId = ((Label) editTerminationApplicationWindow.getFellow("mgrPositionId")).getValue();
		 
		Map terminationDetails = UtilMisc.toMap("userLogin", userLogin, "terminationId", terminationId, "partyId",
				partyId, "submitStatus", "YES", "managerStatusType", "2","mgrPositionId",mgrPositionId);
		LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
		try {
			dispatcher.runSync("submitEmplTerminationService", terminationDetails);
			Messagebox.show("Resignation Application Submitted", "Success", 1, null);
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void approveTerminationApplication(Event event, final Component comp) throws InterruptedException,
			GenericServiceException, GenericEntityException {

		final Component processTerminationApplicationWindow = event.getTarget();
		processTerminationApplicationWindow.getDesktop().getExecution().getNativeRequest();
		final GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute(
				"userLogin");
		final GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

		Messagebox.show("Resignation Approved cannot be modified. Do you want to continue?", "Warning", Messagebox.YES
				| Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			public void onEvent(Event evt) throws GenericEntityException {
				if ("onYes".equals(evt.getName())) {

					String terminationId = (String) ((Label) processTerminationApplicationWindow
							.getFellow("terminationId")).getValue();
					String partyId = (String) ((Label) processTerminationApplicationWindow.getFellow("partyId"))
							.getValue();
					String terminationDateType = (String) ((Label) processTerminationApplicationWindow
							.getFellow("terminationDate")).getValue();
					SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
					java.util.Date dt = null;
					try {
						dt = formatter.parse(terminationDateType);
					} catch (ParseException e3) {
						// TODO Auto-generated catch block
						e3.printStackTrace();
					}
					java.sql.Date terminationDate = new java.sql.Date(dt.getTime());

					// java.sql.Date terminationDate = new
					// java.sql.Date(formatter.parse(terminationDateType));
					// java.sql.Date terminationDate = new
					// java.sql.Date(terminationDateType);
					Timestamp disableDateTime = null;
					disableDateTime = new Timestamp(terminationDate.getTime());
					String statusId = null;
					String approverType = (String) ((Textbox) processTerminationApplicationWindow
							.getFellow("approverType")).getValue();
					String noticePeriodType = (String) ((Textbox) processTerminationApplicationWindow
							.getFellow("noticePeriod")).getValue();
					Long noticePeriod = new Long(noticePeriodType);

					String terminationStatus = (String) ((Label) processTerminationApplicationWindow
							.getFellow("terminationStatus")).getValue();
					//String comment = (String) ((Textbox) processTerminationApplicationWindow.getFellow("comment")).getValue();
					String comment=null;
					boolean hasManager=true;
					GenericValue managerGv = HumanResUtil.getManagerPositionForParty(partyId, delegator);
					if(managerGv==null)
						hasManager=false;
					if(hasManager)
						comment = (String) ((Textbox) processTerminationApplicationWindow.getFellow("comment")).getValue();
					
					List userLoginDetails = new ArrayList();
					try {
						userLoginDetails = delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", partyId));
					} catch (GenericEntityException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					GenericValue userLoginGv = EntityUtil.getFirst(userLoginDetails);
					String userLoginId = userLoginGv != null ? (String) userLoginGv.getString("userLoginId"):null;
					Double remainingLeaves = 0.0;
					Double encashLeaves = null;
					String settlementType = null;
					String adminComment = null;
					String statusType = null;
					String managerStatusType = null;
				//	if (approverType.equals("true")) {
					if("ET_SUBMITTED".equals(terminationStatus) && approverType.equals("true")){
						statusId = "ET_MGR_APPROVED";
						String remainingLeavesType = (String) ((Textbox) processTerminationApplicationWindow
								.getFellow("remainingLeaves")).getValue();

						remainingLeaves = new Double(remainingLeavesType);
						String encashLeavesType = (String) ((Textbox) processTerminationApplicationWindow
								.getFellow("encashLeaves")).getValue();
						encashLeaves = new Double(encashLeavesType);
						Radio settlementTypeItem = ((Radiogroup) processTerminationApplicationWindow
								.getFellow("settlementType")).getSelectedItem();
						settlementType = (String) settlementTypeItem.getValue();
						statusType = "1";
						managerStatusType = "2";
					//} else {
					}else if("ET_MGR_APPROVED".equals(terminationStatus)){
						Integer remainingLeavesType = (Integer) ((Intbox) processTerminationApplicationWindow
								.getFellow("unusedLeaves")).getValue();
						remainingLeaves = new Double(remainingLeavesType);
						Integer encashLeavesType = (Integer) ((Intbox) processTerminationApplicationWindow
								.getFellow("settlementLeaves")).getValue();
						encashLeaves = new Double(encashLeavesType);
						Radio settlementTypeItem = ((Radiogroup) processTerminationApplicationWindow
								.getFellow("settlementType")).getSelectedItem();
						settlementType = (String) settlementTypeItem.getValue();
						// settlementType = (String) ((Label)
						// processTerminationApplicationWindow.getFellow("settlementTypeId")).getValue();
						statusId = "ET_ADM_APPROVED";
						statusType = "1";
						managerStatusType = "2";
						adminComment = (String) ((Textbox) processTerminationApplicationWindow
								.getFellow("adminComment")).getValue();
						Map passwordMap = UtilMisc.toMap("userLogin", userLogin, "userLoginId", userLoginId, "enabled",
								"Y", "disabledDateTime", disableDateTime);
						LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
						if(userLoginId != null){
							try {
								dispatcher.runSync("updateUserLoginSecurity", passwordMap);
							} catch (GenericServiceException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
						List employmentList = new ArrayList();
						try {
							employmentList = delegator.findByAnd("Employment", UtilMisc.toMap("partyIdTo", partyId));
						} catch (GenericEntityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						GenericValue employmentGv = EntityUtil.getFirst(employmentList);
						employmentGv.putAll(UtilMisc.toMap("thruDate", disableDateTime));
						try {
							employmentGv.store();
						} catch (GenericEntityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					Map terminationDetails = UtilMisc.toMap("userLogin", userLogin, "terminationId", terminationId,
							"partyId", partyId, "statusId", statusId, "encashLeaves", encashLeaves, "unusedLeaves",
							remainingLeaves, "hr_comment", comment, "terminationDate", terminationDate, "noticePeriod",
							new Long(noticePeriod), "settlementType", settlementType, "statusType", statusType,
							"adminComment", adminComment, "submitStatus", "YES", "managerComment", comment,
							"managerStatusType", managerStatusType, "updatedBy", userLogin.getString("partyId"));
					LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
					try {
						dispatcher.runSync("processEmplTerminationService", terminationDetails);
						try {
							Messagebox.show("Resignation Approved/Rejected successfully", "Success", 1, null);
							Events.postEvent("onClick", Path.getComponent("/searchPanel//searchPerCompany"), null);
							comp.detach();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (GenericServiceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					return;
				}
			}
		});

	}

	public static void rejectTerminationApplication(Event event, final Component comp) throws InterruptedException {

		final Component processTerminationApplicationWindow = event.getTarget();
		processTerminationApplicationWindow.getDesktop().getExecution().getNativeRequest();
		final GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute(
				"userLogin");
		final GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
		Messagebox.show("Resignation Rejected cannot be modified. Do you want to continue?", "Warning", Messagebox.YES
				| Messagebox.NO, Messagebox.QUESTION, new EventListener() {
			public void onEvent(Event evt) throws GenericEntityException {
				if ("onYes".equals(evt.getName())) {
					String terminationId = (String) ((Label) processTerminationApplicationWindow
							.getFellow("terminationId")).getValue();
					String partyId = (String) ((Label) processTerminationApplicationWindow.getFellow("partyId"))
							.getValue();
					String approverType = (String) ((Textbox) processTerminationApplicationWindow
							.getFellow("approverType")).getValue();
					String noticePeriodType = (String) ((Textbox) processTerminationApplicationWindow
							.getFellow("noticePeriod")).getValue();
					Long noticePeriod = new Long(noticePeriodType);
					Double remainingLeaves = 0D;
					Double encashLeaves = 0D;
					
					String terminationStatus = (String) ((Label) processTerminationApplicationWindow
							.getFellow("terminationStatus")).getValue();
					
					//String comment = (String) ((Textbox) processTerminationApplicationWindow.getFellow("comment")).getValue();
					String comment=null;
					boolean hasManager=true;
					GenericValue managerGv = HumanResUtil.getManagerPositionForParty(partyId, delegator);
					if(managerGv==null)
						hasManager=false;
					if(hasManager)
						comment = (String) ((Textbox) processTerminationApplicationWindow.getFellow("comment")).getValue();
					
					
					String terminationDateType = (String) ((Label) processTerminationApplicationWindow
							.getFellow("terminationDate")).getValue();
					SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
					java.util.Date dt = null;
					try {
						dt = formatter.parse(terminationDateType);
					} catch (ParseException e3) {
						// TODO Auto-generated catch block
						e3.printStackTrace();
					}
					java.sql.Date terminationDate = new java.sql.Date(dt.getTime());

					String statusId = null;
					String statusType = null;
					String managerStatusType = null;
					//if (approverType.equals("true")) {
					if("ET_SUBMITTED".equals(terminationStatus) && approverType.equals("true")){
						statusId = "ET_MGR_REJECTED";
						statusType = "2";
						managerStatusType = "2";
					//} else {
					}else if("ET_MGR_APPROVED".equals(terminationStatus)){	
						statusId = "ET_ADM_REJECTED";
						statusType = "1";
						managerStatusType = "2";
					}
					Map terminationDetails = new HashMap();
					if (statusId.equals("ET_ADM_REJECTED")) {
						String adminComment = (String) ((Textbox) processTerminationApplicationWindow
								.getFellow("adminComment")).getValue();
						terminationDetails = UtilMisc.toMap("userLogin", userLogin, "terminationId", terminationId,
								"partyId", partyId, "statusId", statusId, "encashLeaves", encashLeaves, "unusedLeaves",
								remainingLeaves, "hr_comment", comment, "terminationDate", terminationDate,
								"noticePeriod", new Long(noticePeriod), "statusType", statusType, "managerComment",
								comment, "adminComment", adminComment, "managerStatusType", managerStatusType,
								"updatedBy", userLogin.getString("partyId"));
					} else {
						terminationDetails = UtilMisc.toMap("userLogin", userLogin, "terminationId", terminationId,
								"partyId", partyId, "statusId", statusId, "encashLeaves", encashLeaves, "unusedLeaves",
								remainingLeaves, "hr_comment", comment, "terminationDate", terminationDate,
								"noticePeriod", noticePeriod, "statusType", statusType, "managerComment", comment,
								"managerStatusType", managerStatusType, "updatedBy", userLogin.getString("partyId"));
					}
					LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
					try {
						dispatcher.runSync("processEmplTerminationService", terminationDetails);
						try {
							Messagebox.show("Resignation Approved/Rejected successfully", "Success", 1, null);
							Events.postEvent("onClick", Path.getComponent("/searchPanel//searchPerCompany"), null);
							comp.detach();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (GenericServiceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					return;
				}
			}
		});

	}

	public static void deleteResignation(Event event, GenericValue gv, final Button button) throws InterruptedException {
		Component searchPanel = event.getTarget();
		final String terminationId = gv.getString("terminationId");
		final GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		Events.postEvent("onClick$searchButton", searchPanel.getPage().getFellow("searchPanel"), null);

		Messagebox.show("Do You Want To Delete this Record?", "Warning", Messagebox.YES | Messagebox.NO,
				Messagebox.QUESTION, new EventListener() {
					public void onEvent(Event evt) {
						if ("onYes".equals(evt.getName())) {

							try {

								List claims = delegator.findByAnd("EmplTerminationStatus", UtilMisc.toMap(
										"terminationId", terminationId));
								if (claims != null && claims.size() > 0) {
									delegator.removeByAnd("EmplTerminationStatus", UtilMisc.toMap("terminationId",
											terminationId));
									delegator.removeByAnd("EmplTermination", UtilMisc.toMap("terminationId",
											terminationId));
									Events.postEvent("onClick", button, null);
								}

							} catch (GenericEntityException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							try {
								Messagebox.show("Resignation Application Deleted", "Success", 1, null);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else {
							return;
						}
					}
				});

	}
	
	public static void sendResignationNotificationMailToCFO(String terminationId, Delegator delegator) throws GenericEntityException, GenericServiceException, InterruptedException  {
			GenericValue terminationGv = delegator.findOne("EmplTermination", UtilMisc.toMap("terminationId", terminationId), false);
			String ceoMailId = HrmsUtil.getCeoEmailId(delegator);
			if(UtilValidate.isEmpty(ceoMailId))
	       	return;
			GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
			Delegator delegator2 = userLogin.getDelegator();
	   		LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();
	   		GenericValue personGv = delegator.findOne("Person", UtilMisc.toMap("partyId", terminationGv.getString("partyId")), false);
	        boolean result = MailUtil.sendResignationMailToCEO(userLogin.getString("partyId"), delegator2, "cfoResignationNotificationEmail.ftl", 
	        		ceoMailId, UtilMisc.toMap("terminationGv", terminationGv, "personGv", personGv), dispatcher);
	   
	        if (result) {
	           Messagebox.show("Mail Sent Successfully", "Success", 1, null);
	       }
	}
}
