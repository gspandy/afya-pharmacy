
package com.ndz.controller;

import com.ndz.component.party.EmployeePositionTypeRenderer;
import com.ndz.zkoss.EmployeeBox;
import com.ndz.zkoss.HrmsUtil;
import com.ndz.zkoss.ManagerBox;
import com.ndz.zkoss.util.HrmsInfrastructure;
import com.ndz.zkoss.util.MailUtil;
import com.smebiz.common.UtilDateTimeSME;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.IOUtils;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.zkforge.fckez.FCKeditor;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.*;

import java.io.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EmployeeRequisitionController extends GenericForwardComposer {

    private static final long serialVersionUID = 1L;

    @Override
    public void doAfterCompose(Component employeeRequisition) throws Exception {
        super.doAfterCompose(employeeRequisition);

        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        Set<String> employeePositionTypeToDisplay = new HashSet<String>();
        List<GenericValue> employeePositionType = delegator.findList("EmplPositionType", null, employeePositionTypeToDisplay, null, null, false);
        employeePositionType.add(0, null);
        Object employeePositionTypeArray = employeePositionType.toArray(new GenericValue[employeePositionType.size()]);
        SimpleListModel employeePositionTypeList = new SimpleListModel(employeePositionType);
        Listbox employeePositionTypeId = (Listbox) employeeRequisition.getFellow("requisitionPositionType");
        employeePositionTypeId.setModel(employeePositionTypeList);
        employeePositionTypeId.setItemRenderer(new EmployeePositionTypeRenderer());
    }

    /* Create the Requisition As Per the requisition Type(New/Replacement) */
    public static void createRequisition(Event event) throws GenericEntityException, InterruptedException {

        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        String logedInUserId = userLogin.getString("partyId");
        GenericValue emplPosGv = HumanResUtil.getEmplPositionForParty(logedInUserId, delegator);
        String deptId = null;
        if (emplPosGv != null) {
            deptId = emplPosGv.getString("partyId");
        }
        final Component employeeRequisition = event.getTarget().getParent().getParent().getParent();
        Radio requisitionType = ((Radiogroup) employeeRequisition.getFellow("requisitionType")).getSelectedItem();
        String reqType = requisitionType.getValue();
        String isPosIdFulfilled = null;
        String emplPosId = null;
        if (!("new".equals(reqType))) {
            emplPosId = ((Bandbox) employeeRequisition.getFellow("searchPanel")).getValue();
            GenericValue gv = delegator.findByPrimaryKey("EmplPosition", UtilMisc.toMap("emplPositionId", emplPosId));
            String replacedPosDept = null;
            if (gv != null) {
                replacedPosDept = gv.getString("partyId");
            }
            if (!(deptId.equals(replacedPosDept))) {
                Messagebox.show("You are not authorized to replace Requisition for this Position Id as it belongs to another department", "Error", 1, null);
                return;
            }
            isPosIdFulfilled = checkForPositionFullfillment(emplPosId);
            if (isPosIdFulfilled.equals("3")) {
                Messagebox.show("You Cannot Create Requsition For this position Id:" + reqType + ";as it is created through Requisition still not fullfilled",
                        "Error", 1, null);
                return;
            } else if (isPosIdFulfilled.equals("2")) {
                Messagebox.show("This Position Id is fulfilled by another Employee. Do you want to create Requisition for the Position Id?", "Warning",
                        Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
                    public void onEvent(Event evt) {
                        if ("onYes".equals(evt.getName())) {
                            try {
                                getDataToCreateRequistion((Window) employeeRequisition);
                            } catch (GenericEntityException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            return;
                        }
                    }
                });

            } else {

                getDataToCreateRequistion((Window) employeeRequisition);

            }
        } else {

            getDataToCreateRequistion((Window) employeeRequisition);

        }

    }

    public static void getDataToCreateRequistion(Window cmp) throws GenericEntityException, InterruptedException {

        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
        String employeeId = userLogin.getString("partyId");

        GenericValue emplPosGV = HumanResUtil.getEmplPositionForParty(employeeId, delegator);
        String departmentId = emplPosGV.getString("partyId");

        String hodPosId = HumanResUtil.getHODPositionId(departmentId, delegator);
        String positionType = null;
        Long noOfPosition = null;
        String setReqType = null;
        String replacementpositionId = null;
        GenericValue posTypes = null;
        Radio requisitionType = ((Radiogroup) cmp.getFellow("requisitionType")).getSelectedItem();
        String reqType = requisitionType.getValue();
        if (reqType.equals("new")) {
            Listitem positionTypeInput = ((Listbox) cmp.getFellow("requisitionPositionType")).getSelectedItem();
            positionType = (String) positionTypeInput.getValue();
            Integer noOfPositionType = ((Spinner) cmp.getFellow("noOfPosition")).getValue();
            noOfPosition = new Long(noOfPositionType);
            setReqType = "new";
        } else {
            replacementpositionId = ((Bandbox) cmp.getFellow("searchPanel")).getValue();
            posTypes = delegator.findByPrimaryKey("EmplPosition", UtilMisc.toMap("emplPositionId", replacementpositionId));
            positionType = posTypes.getString("emplPositionTypeId");
            noOfPosition = new Long(1);
            setReqType = "replacement";

        }

        Integer minExprienceType = ((Spinner) cmp.getFellow("minExprience")).getValue();
        Long minExprience = new Long(minExprienceType);
        Integer maxExprienceType = ((Spinner) cmp.getFellow("maxExprience")).getValue();
        Long maxExprience = new Long(maxExprienceType);
        String jobTitle = ((Textbox) cmp.getFellow("jobTitle")).getValue();
        String qualification = ((FCKeditor) cmp.getFellow("qualification")).getValue();
        String jobDetails = ((FCKeditor) cmp.getFellow("jobDetails")).getValue();
        String roleDetails = ((FCKeditor) cmp.getFellow("roleDetails")).getValue();
        String justifyNote = ((FCKeditor) cmp.getFellow("justifyHire")).getValue();
        String softSkills = ((FCKeditor) cmp.getFellow("softSkills")).getValue();
        Integer minSalaryType = ((Spinner) cmp.getFellow("minCTC")).getValue();

        java.math.BigDecimal minSalary = new java.math.BigDecimal(minSalaryType);
        Integer maxSalaryType = ((Spinner) cmp.getFellow("maxCTC")).getValue();
        java.math.BigDecimal maxSalary = new java.math.BigDecimal(maxSalaryType);
        java.sql.Date fromDate = null;
        java.sql.Date thruDate = null;
        Date fromDateType = ((Datebox) cmp.getFellow("fromDate")).getValue();
        if (fromDateType != null) {
            fromDate = new java.sql.Date(fromDateType.getTime());
        }
        Date thruDateType = ((Datebox) cmp.getFellow("thruDate")).getValue();
        if (thruDateType != null) {
            thruDate = new java.sql.Date(thruDateType.getTime());
        }
        Listitem currencyItem = ((Listbox) cmp.getFellow("currencyId")).getSelectedItem();
        String uomId = (String) currencyItem.getValue();
        Listitem minBaslineItem = ((Listbox) cmp.getFellow("minBaslineId")).getSelectedItem();
        String minBaslineId = (String) minBaslineItem.getValue();
        Listitem maxBaslineItem = ((Listbox) cmp.getFellow("maxBaslineId")).getSelectedItem();
        String maxBaslineId = (String) maxBaslineItem.getValue();
        Listitem requirementTypeItem = ((Listbox) cmp.getFellow("requirementType")).getSelectedItem();
        String requirementType = (String) requirementTypeItem.getValue();
        if (minBaslineId != maxBaslineId) {
            Messagebox.show("The BaseLine For Minimum and Maximum CTC Should be Same", "Error", 1, null);
            return;
        }
        final String requisitionId = delegator.getNextSeqId("EmployeeRequisition");
        Map<String, Comparable> employeeRequisitionMap = new HashMap<String, Comparable>();
        employeeRequisitionMap.put("requisitionId", requisitionId);
        employeeRequisitionMap.put("positionType", positionType);
        employeeRequisitionMap.put("numberOfPosition", noOfPosition);
        employeeRequisitionMap.put("approverPositionId", hodPosId);
        employeeRequisitionMap.put("partyId", employeeId);
        employeeRequisitionMap.put("qualification", qualification);
        employeeRequisitionMap.put("minExprience", minExprience);
        employeeRequisitionMap.put("maxExprience", maxExprience);
        employeeRequisitionMap.put("justificationForHiring", justifyNote);
        employeeRequisitionMap.put("jobTitle", jobTitle);
        employeeRequisitionMap.put("jobDescription", jobDetails);
        employeeRequisitionMap.put("roleDetails", roleDetails);
        employeeRequisitionMap.put("softSkills", softSkills);
        employeeRequisitionMap.put("minimumSalary", minSalary);
        employeeRequisitionMap.put("maximumSalary", maxSalary);
        employeeRequisitionMap.put("fromDate", fromDate);
        employeeRequisitionMap.put("reqRaisedByDept", departmentId);
        employeeRequisitionMap.put("requisitionType", setReqType);
        employeeRequisitionMap.put("replacementpositionId", replacementpositionId);
        employeeRequisitionMap.put("uomId", uomId);
        employeeRequisitionMap.put("enumId", maxBaslineId);
        employeeRequisitionMap.put("requirementType", requirementType);
        employeeRequisitionMap.put("thruDate", thruDate);
        employeeRequisitionMap.put("balancePositionToFulfill", noOfPosition);
        employeeRequisitionMap.put("statusId", "Saved");
        GenericValue employeedetails = delegator.create("EmployeeRequisition", employeeRequisitionMap);
        String resultId = (String) employeedetails.get("requisitionId");
        System.out.println(resultId);
        // Messagebox.show(Labels.getLabel("HRMS_REQUISITION_SUCCESSMESSAGE"
        // + resultId),
        // "Success", Messagebox.OK, "", 1, new EventListener() {
        Messagebox.show("Requisition " + (resultId) + " created successfully", "Success", Messagebox.OK, "", 1, new EventListener() {
            public void onEvent(Event evt) throws GenericEntityException {
                Executions.getCurrent().sendRedirect("/control/editRequisition?requisitionId=" + requisitionId);
            }
        });

    }

    public static void submitRequisition(Event event) throws GenericEntityException, InterruptedException {

        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        String employeeId = userLogin.getString("partyId");

        Component editRequisitionWindow = event.getTarget();
        String lReqId = ((Textbox) editRequisitionWindow.getFellow("requisitionId")).getValue();
        GenericValue reqGV = delegator.findOne("EmployeeRequisition", false, "requisitionId", lReqId);
        String presentStatus = reqGV.getString("statusId");
        String reqRaisedDept = reqGV.getString("partyId");

        if (!employeeId.equals(reqRaisedDept)) { // swetalina
            Messagebox.show("You do not have Permission to Submit the Requisition", "Warning", 1, null);
            return;

        } // swetalina

        GenericValue requisitionGV = delegator.findByPrimaryKey("EmployeeRequisition", UtilMisc.toMap("requisitionId", lReqId));
        String reqInitiatorId = ((Textbox) editRequisitionWindow.getFellow("employeeId")).getValue();
        String statusId = ((Textbox) editRequisitionWindow.getFellow("statusId")).getValue();
        Listitem positionTypeInput = ((Listbox) editRequisitionWindow.getFellow("requisitionPositionType")).getSelectedItem();
        String positionType = (String) positionTypeInput.getValue();
        Integer noOfPositionType = ((Spinner) editRequisitionWindow.getFellow("noOfPosition")).getValue();
        Long noOfPosition = new Long(noOfPositionType);
        Integer minExprienceType = ((Spinner) editRequisitionWindow.getFellow("minExprience")).getValue();
        Long minExprience = new Long(minExprienceType);
        Integer maxExprienceType = ((Spinner) editRequisitionWindow.getFellow("maxExprience")).getValue();
        Long maxExprience = new Long(maxExprienceType);
        String jobTitle = ((Textbox) editRequisitionWindow.getFellow("jobTitle")).getValue();
        String qualification = ((FCKeditor) editRequisitionWindow.getFellow("qualification")).getValue();
        String jobDetails = ((FCKeditor) editRequisitionWindow.getFellow("jobDetails")).getValue();
        String roleDetails = ((FCKeditor) editRequisitionWindow.getFellow("roleDetails")).getValue();
        String justifyNote = ((FCKeditor) editRequisitionWindow.getFellow("justifyHire")).getValue();
        String softSkills = ((FCKeditor) editRequisitionWindow.getFellow("softSkills")).getValue();
        Integer minSalaryType = ((Spinner) editRequisitionWindow.getFellow("minCTC")).getValue();
        java.math.BigDecimal minSalary = new java.math.BigDecimal(minSalaryType);
        Integer maxSalaryType = ((Spinner) editRequisitionWindow.getFellow("maxCTC")).getValue();
        java.math.BigDecimal maxSalary = new java.math.BigDecimal(maxSalaryType);

        Date fromDateType = ((Datebox) editRequisitionWindow.getFellow("fromDate")).getValue();
        java.sql.Date fromDate = new java.sql.Date(fromDateType.getTime());
        Date thruDateType = ((Datebox) editRequisitionWindow.getFellow("thruDate")).getValue();
        java.sql.Date thruDate = new java.sql.Date(thruDateType.getTime());
        Listitem currencyItem = ((Listbox) editRequisitionWindow.getFellow("currencyId")).getSelectedItem();
        String uomId = (String) currencyItem.getValue();
        Listitem minBaslineItem = ((Listbox) editRequisitionWindow.getFellow("minBaslineId")).getSelectedItem();
        String minBaslineId = (String) minBaslineItem.getValue();
        Listitem maxBaslineItem = ((Listbox) editRequisitionWindow.getFellow("maxBaslineId")).getSelectedItem();
        String maxBaslineId = (String) maxBaslineItem.getValue();
        if (minBaslineId != maxBaslineId) {
            Messagebox.show("The BaseLine For Minimum and Maximum CTC Should be Same", "Warning", 1, null);
            return;
        }

        Map<String, Object> reqs = null;
        reqs = UtilMisc.toMap("requisitionId", lReqId, "partyId", reqInitiatorId, "statusId", statusId, "numberOfPosition", noOfPosition, "qualification",
                qualification, "minExprience", minExprience, "maxExprience", maxExprience, "jobTitle", jobTitle, "jobDescription", jobDetails, "roleDetails",
                roleDetails, "softSkills", softSkills, "justificationForHiring", justifyNote, "minimumSalary", minSalary, "maximumSalary", maxSalary, "uomId",
                uomId, "enumId", maxBaslineId);
        requisitionGV.putAll(reqs);
        requisitionGV.store();
        Messagebox.show("Requisition  " + (lReqId) + "  Submitted Successfully", "Success", Messagebox.OK, "", 1, new EventListener() {
            public void onEvent(Event evt) throws GenericEntityException {
                Executions.getCurrent().sendRedirect("/control/requisitionManagement");
            }
        });

    }

    public static void showRequisitionWindow(Event event, GenericValue gv) throws SuspendNotAllowedException, InterruptedException, GenericEntityException {

        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
        String requisitionId = gv.getString("requisitionId");
        String approverPositionId = gv.getString("approverPositionId");
        String requisitionInitiator = gv.getString("partyId");

        GenericValue person = null;
        person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", requisitionInitiator));
        String initatorName = person.getString("firstName") + " " + person.getString("lastName");
        initatorName = org.apache.commons.lang.StringUtils.capitalize(initatorName);
        String status = gv.getString("statusId");
        String posType = gv.getString("positionType");
        String numberOfPosition = gv.getString("numberOfPosition");
        String savedJobDescription = gv.getString("jobDescription");
        String savedRoleDetails = gv.getString("roleDetails");
        String savedSoftSkills = gv.getString("softSkills");
        String qualificationType = gv.getString("qualification");
        String savedMinExprience = gv.getString("minExprience");
        String savedMaxExprience = gv.getString("maxExprience");
        String justificationForHiring = gv.getString("justificationForHiring");
        String minimumSalary = gv.getString("minimumSalary");
        String maximumSalary = gv.getString("maximumSalary");
        String reqRaisedDate = gv.getString("fromDate");
        GenericValue partyGroupGv = null;
        partyGroupGv = delegator.findByPrimaryKey("PartyGroup", UtilMisc.toMap("partyId", gv.getString("reqRaisedByDept")));
        String reqInitiatorDeptName = partyGroupGv.getString("groupName");
        Map reqIdMap = new HashMap();
        reqIdMap.put("requisitionId", requisitionId);
        final Window win = (Window) Executions.createComponents("/zul/employeeRequisition/processRequisition.zul", null, reqIdMap);
        win.setTitle("Approve Requisition");

        String partyId = userLogin.getString("partyId");
        String setValuePartyId = null;
        List<GenericValue> roleTypes = delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", partyId));
        for (GenericValue roleTypeGv : roleTypes) {
            String roleTypeId = roleTypeGv.getString("roleTypeId");
            String userLoginId = roleTypeGv.getString("partyId");
            if ((roleTypeId.equals("Requisition_Admin")) && (userLoginId.equals(partyId))) {
                setValuePartyId = userLoginId;
            }
        }

        Label setPartyIdValue = (Label) win.getFellow("reqAdminPartyId");
        setPartyIdValue.setValue(setValuePartyId);

        Label reqInitiatorDept = (Label) win.getFellow("deptId");
        reqInitiatorDept.setValue(reqInitiatorDeptName);
        Label employeeId = (Label) win.getFellow("employeeId");
        employeeId.setValue(requisitionInitiator);
        Label employeeName = (Label) win.getFellow("employeeName");
        employeeName.setValue(initatorName);
        Label fromDate = (Label) win.getFellow("fromDate");
        fromDate.setValue(reqRaisedDate);
        Label savedRequisitionId = (Label) win.getFellow("requisitionId");
        savedRequisitionId.setValue(requisitionId);
        final Label requisitionStatus = (Label) win.getFellow("requisitionStatus");
        requisitionStatus.setValue(status);
        Label jobTitle = (Label) win.getFellow("jobTitle");
        jobTitle.setValue(posType);
        Label noOfPosition = (Label) win.getFellow("noOfPosition");
        noOfPosition.setValue(numberOfPosition);

        Label jobDescription = (Label) win.getFellow("jobDescription");
        jobDescription.setValue(savedJobDescription);
        Label roleDetails = (Label) win.getFellow("roleDetails");
        roleDetails.setValue(savedRoleDetails);
        Label qualification = (Label) win.getFellow("qualification");
        qualification.setValue(qualificationType);
        Label softSkills = (Label) win.getFellow("softSkills");
        softSkills.setValue(savedSoftSkills);
        Label minExprience = (Label) win.getFellow("minExprience");
        minExprience.setValue(savedMinExprience);
        Label maxExprience = (Label) win.getFellow("maxExprience");
        maxExprience.setValue(savedMaxExprience);
        Label justify = (Label) win.getFellow("justify");
        justify.setValue(justificationForHiring);
        Label minSalary = (Label) win.getFellow("minSalary");
        minSalary.setValue(minimumSalary);
        Label maxSalary = (Label) win.getFellow("maxSalary");
        maxSalary.setValue(maximumSalary);

		/* Check for the User Permission */
        Boolean isHOD = false;
        String hasPermission = ((Label) win.getFellow("permissionId")).getValue();
        if (hasPermission.equals("HOD"))
            isHOD = true;
        String reqAdminPartyId = ((Label) win.getFellow("reqAdminPartyId")).getValue();
        String loggedInPartyId = userLogin.getString("partyId");
        Boolean isReqAdmin = false;
        if (loggedInPartyId.equals(reqAdminPartyId))
            isReqAdmin = true;
        System.out.println("\n\n\n\n\n\n\n\n\nisReqAdminPartyId" + reqAdminPartyId);
        System.out.println("\n\n\n\n\n\n\n\n\nisReqAdmin" + isReqAdmin);
        System.out.println("\n\n\n\n\n\n\n\n\n Two Strings are equal" + isReqAdmin);
        System.out.println("\n\n\n\n\n\n\n\n\n Has Permission" + hasPermission);
        if (isHOD) {
            Button approveRequisitionButton = (Button) win.getFellow("approveRequisition");
            Hbox enableForAdmin = (Hbox) win.getFellow("enableForAdmin");
            enableForAdmin.setVisible(false);
            approveRequisitionButton.addEventListener("onClick", new EventListener() {
                public void onEvent(Event event) throws Exception {
                    requisitionStatus.setValue("REQ_HOD_APPROVED");
                    EmployeeRequisitionController.processRequisition(event);
                    win.detach();
                }
            });

            Button rejectRequisitionButton = (Button) win.getFellow("rejectRequisition");
            rejectRequisitionButton.addEventListener("onClick", new EventListener() {
                public void onEvent(Event event) throws Exception {
                    requisitionStatus.setValue("REQ_HOD_REJECTED");
                    EmployeeRequisitionController.processRequisition(event);
                    win.detach();
                }
            });
        }
        if (isReqAdmin) {

            System.out.println("\n\n\n*************Event Callled");
            Button acceptRequisitionButton = (Button) win.getFellow("adminAcceptRequisition");
            Hbox enableForHOD = (Hbox) win.getFellow("enableForHOD");
            enableForHOD.setVisible(false);
            final String reqId = ((Textbox) win.getFellow("reqId")).getValue();
            acceptRequisitionButton.addEventListener("onClick", new EventListener() {
                public void onEvent(Event event) throws Exception {
                    requisitionStatus.setValue("PROCESSING");
                    EmployeeRequisitionController.processRequisition(event);
                    Executions.getCurrent().sendRedirect("/control/viewRequisition?requisitionId=" + reqId);
                    win.detach();
                }
            });

        }
        win.doModal();

    }

    public static void processRequisition(Event event) throws GenericEntityException, InterruptedException {

        Component processRequisitionWindow = event.getTarget();
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
        String employeeId = userLogin.getString("partyId");
        GenericValue employeePosIdGv = HumanResUtil.getEmplPositionForParty(employeeId, delegator);
        String employeePosId = employeePosIdGv.getString("emplPositionId");
        String employeePosTypeId = HumanResUtil.getPositionTypeId(delegator, employeePosId);

        List<GenericValue> roleTypes = delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", employeeId));
        boolean isReqAdmin = false;
        for (GenericValue roleTypeGv : roleTypes) {
            String roleTypeId = roleTypeGv.getString("roleTypeId");
            if (roleTypeId.equals("Requisition_Admin")) {
                isReqAdmin = true;
                break;
            }
        }

        String lReqId = ((Label) processRequisitionWindow.getFellow("requisitionId")).getValue();
        GenericValue reqGV = delegator.findOne("EmployeeRequisition", false, "requisitionId", lReqId);
        String presentStatus = reqGV.getString("statusId");
        String raisedDept = reqGV.getString("reqRaisedByDept");
        List empDept = delegator.findByAnd("Employment", UtilMisc.toMap("partyIdTo", employeeId));
        GenericValue empDeptGv = EntityUtil.getFirst(empDept);
        String partyIdFrom = empDeptGv.getString("partyIdFrom");
        boolean isBelongsToDept = false;
        if (raisedDept.equals(partyIdFrom)) {
            isBelongsToDept = true;
        }
        /*
		 * Checks if Loggedin user belongs to HOD then only can approve/reject
		 */
        if ((employeePosTypeId.equals("HOD")) && isBelongsToDept) {

            if (presentStatus.equals("NEW") || presentStatus.equals("REQ_HOD_APPROVED") || presentStatus.equals("REQ_ADMIN_REJECTED")) {
                Map<String, String> d_reqId = UtilMisc.toMap("requisitionId", lReqId);
                GenericValue requisitionGV = delegator.findByPrimaryKey("EmployeeRequisition", d_reqId);
                String commnent = ((Textbox) processRequisitionWindow.getFellow("hodComment")).getValue();
                String budgetId = ((Bandbox) processRequisitionWindow.getFellow("budgetId")).getValue();
                String budgetItemSeqId = ((Bandbox) processRequisitionWindow.getFellow("budgetItemSeqId")).getValue();
                Map reqs = null;
                reqs = UtilMisc.toMap("requisitionId", lReqId, "approverPositionId", employeePosId, "hodComment", commnent, "statusId", "REQ_HOD_APPROVED",
                        "budgetItemSeqId", budgetItemSeqId, "budgetId", budgetId, "isAdminApproved", "1");
                requisitionGV.putAll(reqs);
                requisitionGV.store();
                Messagebox.show(Labels.getLabel("HRMS_REQUISITION_APPROVALSUCCESSMESSAGE"), "Success", Messagebox.OK, "", 1, new EventListener() {
                    public void onEvent(Event evt) throws GenericEntityException {
                        Executions.getCurrent().sendRedirect("/control/requisitionManagement");
                    }
                });
            } else {
                Messagebox.show(Labels.getLabel("HRMS_REQUISITION_PROCESSERRORMESSAGE"), "Error", 1, null);
                return;
            }

        } else {

            Messagebox.show(Labels.getLabel("HRMS_REQUISITION_APPROVALERRORMESSAGE"), "Success", 1, null);
            return;
        }
    }

    public static void hodRejectRequisition(Event event) throws GenericEntityException, InterruptedException {

        Component processRequisitionWindow = event.getTarget();
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
        String employeeId = userLogin.getString("partyId");
        GenericValue employeePosIdGv = HumanResUtil.getEmplPositionForParty(employeeId, delegator);
        String employeePosId = employeePosIdGv.getString("emplPositionId");
        String employeePosTypeId = HumanResUtil.getPositionTypeId(delegator, employeePosId);

        List<GenericValue> roleTypes = delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", employeeId));
        boolean isReqAdmin = false;
        for (GenericValue roleTypeGv : roleTypes) {
            String roleTypeId = roleTypeGv.getString("roleTypeId");
            if (roleTypeId.equals("Requisition_Admin")) {
                isReqAdmin = true;
                break;
            }
        }

        String lReqId = ((Label) processRequisitionWindow.getFellow("requisitionId")).getValue();
        GenericValue reqGV = delegator.findOne("EmployeeRequisition", false, "requisitionId", lReqId);
        String presentStatus = reqGV.getString("statusId");

		/*
		 * Checks if Loggedin user belongs to HOD then only can approve/reject
		 */
        if ((employeePosTypeId.equals("HOD")) || isReqAdmin) {

            if ((presentStatus.equals("NEW")) || (presentStatus.equals("REQ_HOD_APPROVED") || ((presentStatus.equals("REQ_HOD_APPROVED")) && isReqAdmin))) {
                Map<String, String> d_reqId = UtilMisc.toMap("requisitionId", lReqId);
                GenericValue requisitionGV = delegator.findByPrimaryKey("EmployeeRequisition", d_reqId);
                String commnent = ((Textbox) processRequisitionWindow.getFellow("hodComment")).getValue();
                Map reqs = null;
                reqs = UtilMisc.toMap("requisitionId", lReqId, "approverPositionId", employeePosId, "hodComment", commnent, "statusId", "REQ_HOD_REJECTED");
                requisitionGV.putAll(reqs);
                requisitionGV.store();
                Messagebox.show("Requisition Rejected", "Success", Messagebox.OK, null, 1, new EventListener() {
                    public void onEvent(Event evt) throws GenericEntityException {
                        Executions.getCurrent().sendRedirect("/control/requisitionManagement");
                    }
                });

            } else {
                Messagebox.show("Requisition already Approved/Rejected", "Warning", 1, null);
                return;
            }

        } else {
            processRequisitionWindow.detach();
            Messagebox.show("You do not have Permission to Process the Requisition", "Warning", 1, null);
            return;
        }
    }

    public static void adminRejectRequisition(Event event) throws GenericEntityException, InterruptedException {
        Component processRequisitionWindow = event.getTarget();
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
        String lReqId = ((Label) processRequisitionWindow.getFellow("requisitionId")).getValue();
        GenericValue reqGV = delegator.findOne("EmployeeRequisition", false, "requisitionId", lReqId);
        String presentStatus = reqGV.getString("statusId");
        String employeeId = userLogin.getString("partyId");
        GenericValue employeePosIdGv = HumanResUtil.getEmplPositionForParty(employeeId, delegator);
        String employeePosId = employeePosIdGv.getString("emplPositionId");
        String employeePosTypeId = HumanResUtil.getPositionTypeId(delegator, employeePosId);
        if (presentStatus.equals("REQ_HOD_APPROVED")) {
            Map<String, String> d_reqId = UtilMisc.toMap("requisitionId", lReqId);
            GenericValue requisitionGV = delegator.findByPrimaryKey("EmployeeRequisition", d_reqId);
            String commnent = ((Textbox) processRequisitionWindow.getFellow("hodComment1")).getValue();
            Map reqs = null;
            reqs = UtilMisc.toMap("requisitionId", lReqId, "approverPositionId", employeePosId, "adminComment", commnent, "statusId", "REQ_ADMIN_REJECTED",
                    "isAdminApproved", "1");
            requisitionGV.putAll(reqs);
            requisitionGV.store();
            final String reqId = ((Textbox) processRequisitionWindow.getFellow("reqId")).getValue();
            System.out.println("Receiving Requisition Id" + reqId);
            Messagebox.show("Requisition Rejected", "Success", Messagebox.OK, null, 1, new EventListener() {
                public void onEvent(Event evt) throws GenericEntityException {
                    Executions.getCurrent().sendRedirect("/control/requisitionManagement");
                }
            });

        } else if (presentStatus.equals("REQ_HOD_REJECTED")) {

            Messagebox.show(Labels.getLabel("HRMS_REQUISITION_REJECTMESSAGE"), "Success", 1, null);
            return;

        } else {
            Messagebox.show("HOD Needs To Approve This Requisition Before Processing", "Error", 1, null);
            return;
        }

    }

    public static void adminProcessRequisition(Event event) throws GenericEntityException, InterruptedException {
        System.out.println("Event Called To Process the Requisition");
        Component processRequisitionWindow = event.getTarget();
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
        String employeeId = userLogin.getString("partyId");
        GenericValue employeePosIdGv = HumanResUtil.getEmplPositionForParty(employeeId, delegator);
        String employeePosId = employeePosIdGv.getString("emplPositionId");
        String employeePosTypeId = HumanResUtil.getPositionTypeId(delegator, employeePosId);

        List<GenericValue> roleTypes = delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", employeeId));
        boolean isReqAdmin = false;
        for (GenericValue roleTypeGv : roleTypes) {
            String roleTypeId = roleTypeGv.getString("roleTypeId");
            if (roleTypeId.equals("Requisition_Admin")) {
                isReqAdmin = true;
                break;
            }
        }

        String lReqId = ((Label) processRequisitionWindow.getFellow("requisitionId")).getValue();
        GenericValue reqGV = delegator.findOne("EmployeeRequisition", false, "requisitionId", lReqId);
        String presentStatus = reqGV.getString("statusId");

		/*
		 * Checks if Loggedin user belongs to HOD then only can approve/reject
		 */
        if (isReqAdmin) {

            if (presentStatus.equals("REQ_HOD_APPROVED")) {
                Map<String, String> d_reqId = UtilMisc.toMap("requisitionId", lReqId);
                GenericValue requisitionGV = delegator.findByPrimaryKey("EmployeeRequisition", d_reqId);
                String commnent = ((Textbox) processRequisitionWindow.getFellow("hodComment1")).getValue();

                Map reqs = null;
                reqs = UtilMisc.toMap("requisitionId", lReqId, "approverPositionId", employeePosId, "adminComment", commnent, "statusId", "REQ_ADMIN_APPROVED",
                        "isAdminApproved", "1");
                requisitionGV.putAll(reqs);
                requisitionGV.store();
                final String reqId = ((Textbox) processRequisitionWindow.getFellow("reqId")).getValue();
                Messagebox.show("Requisition Accepted Sucessfully", "Success", Messagebox.OK, "", 1, new EventListener() {
                    public void onEvent(Event evt) throws GenericEntityException {
                        Executions.getCurrent().sendRedirect("/control/viewRequisition?requisitionId=" + reqId);
                    }
                });

            } else if (presentStatus.equals("REQ_HOD_REJECTED")) {

                Messagebox.show(Labels.getLabel("HRMS_REQUISITION_REJECTMESSAGE"), "Error", 1, null);
                return;

            } else {
                Messagebox.show("Requisition already Processed", "Error", 1, null);
                return;
            }

        } else {
            Messagebox.show("You do not have Permission to Process the Requisition", "Error", 1, null);
            return;
        }
    }

    private  Media uploadedFile;

    private  String attachmentName;

    private static final String UPLOADPATH = "./hot-deploy/hrms/webapp/hrms/images/";

    public void setUploadedFile(UploadEvent uploadEvent, Component component) throws IOException {
        uploadedFile = uploadEvent.getMedia();
        attachmentName = uploadedFile.getName();
        if (attachmentName.endsWith(".pdf") || attachmentName.endsWith(".doc") || attachmentName.endsWith(".docx")) {
            try {
                Messagebox.show("Resume " + attachmentName + " Uploaded Successfully", "Success", 1, null);
                ((Label) component).setValue(attachmentName);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else {

            try {
                Messagebox.show("Please attach the resume with .pdf,.doc or .docx format", "Error", 1, null);
                uploadedFile = null;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    public void submitApplication(Event event) throws InterruptedException, IOException, GenericServiceException, GenericEntityException {
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
        Component submitApplicationWindow = event.getTarget();

        String requisitionId = ((Textbox) submitApplicationWindow.getFellow("reqId")).getValue();
        String applyingPartyId = ((Textbox) submitApplicationWindow.getFellow("applyingPartyId")).getValue();
        String emplPositionId = ((Textbox) submitApplicationWindow.getFellow("searchPanel")).getValue();

        String fileName = uploadedFile == null ? null : (UPLOADPATH + uploadedFile.getName());
        java.util.Date now = new java.util.Date();
        java.sql.Date currDate = new java.sql.Date(now.getTime());
        LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
        Map submitApp = UtilMisc.toMap("userLogin", userLogin, "requisitionId", requisitionId, "fileName", fileName, "applyingPartyId", applyingPartyId,
                "emplPositionId", emplPositionId, "applicationDate", currDate, "statusId", "EMPL_POS_ACTIVE");
        Map result = null;
        result = dispatcher.runSync("createEmploymentApp", submitApp);
        String applicationId = (String) result.get("applicationId");
        String extention = fileName.substring(fileName.lastIndexOf('.'));
        if (extention.equals(".doc") || extention.equals(".pdf")) {
            String actualFileName = UPLOADPATH + applicationId + extention;
            File diskFile = new File(actualFileName);
            FileOutputStream outputStream = new FileOutputStream(diskFile);
            outputStream.write(uploadedFile.getByteData());
        } else {
            Messagebox.show("Upload a doc/pdf data File", "Error", 1, null);
            return;
        }
        Messagebox.show("Application Successfully Submitted", "Success", 1, null);
    }

    public void downloadApplication(Event event) throws GenericEntityException, FileNotFoundException {
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
        Component viewApplliedApplicationWindow = event.getTarget();

        GenericValue emplApplication = null;
        String appId = ((Textbox) viewApplliedApplicationWindow.getFellow("appId")).getValue();
        emplApplication = delegator.findByPrimaryKey("EmploymentApp", UtilMisc.toMap("applicationId", appId));

        String downloadFileNameWithPath = emplApplication.getString("fileName");
        String applicationId = emplApplication.getString("applicationId");
        String actualFileName = UPLOADPATH + applicationId + downloadFileNameWithPath.substring(downloadFileNameWithPath.lastIndexOf('.'));
        InputStream inputStream = new FileInputStream(actualFileName);
        if (inputStream != null)
            Filedownload.save(inputStream, null, actualFileName);
        else
            alert(downloadFileNameWithPath + "not found");

    }

    public static void downloadResume(Event event, GenericValue gv) throws GenericEntityException, FileNotFoundException, InterruptedException {

        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
        String partyId = gv.getString("partyId");
        EntityCondition condition1 = EntityCondition.makeCondition("partyId", EntityComparisonOperator.EQUALS, partyId);
        List orderBy = new ArrayList();
        orderBy.add("-lastUpdatedStamp");
        List<GenericValue> partyResumeList = delegator.findList("PartyResume", condition1, null, orderBy, null, false);
        String downloadFileNameWithPath = EntityUtil.getFirst(partyResumeList).getString("resumeText");
        String actualFileName = UPLOADPATH + downloadFileNameWithPath;
        InputStream inputStream = new FileInputStream(actualFileName);
        if (inputStream != null)
            Filedownload.save(inputStream, null, actualFileName);
        else
            Messagebox.show(downloadFileNameWithPath + "not found", "Error", 1, null);

    }

    public static void showApplicationWindow(Event event, GenericValue gv) throws GenericEntityException, SuspendNotAllowedException, InterruptedException {

        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

        String applicationId = gv.getString("applicationId");
        String emplPositionId = gv.getString("emplPositionId");
        String requisitionId = gv.getString("requisitionId");
        String applyingPartyId = gv.getString("applyingPartyId");
        String statusId = gv.getString("statusId");

        GenericValue person = null;
        person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", applyingPartyId));
        String applyingParty = person.getString("firstName") + " " + person.getString("lastName");
        applyingParty = org.apache.commons.lang.StringUtils.capitalize(applyingParty);
        Map arg = new HashMap();
        arg.put("applicationId", applicationId);
        Window win = (Window) Executions.createComponents("/zul/employmentApplication/viewAppliedApplication.zul", null, arg);
        Label application = (Label) win.getFellow("applicationId");
        application.setValue(applicationId);
        Label positionId = (Label) win.getFellow("positionId");
        positionId.setValue(emplPositionId);
        Label appliedBy = (Label) win.getFellow("appliedBy");
        appliedBy.setValue(applyingParty);
        win.doModal();

    }

    @SuppressWarnings("unchecked")
    public void createProspect(Event event, Window Comp) throws GenericServiceException {
        Boolean beganTransaction = false;

        try {
            GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
            GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

            String userLoginId = userLogin.getString("partyId");
            Component createProspectWindow = event.getTarget();
            beganTransaction = TransactionUtil.begin();
            LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
            String firstName = ((Textbox) createProspectWindow.getFellow("firstName")).getValue();
            String lastName = ((Textbox) createProspectWindow.getFellow("lastName")).getValue();
            String emailAddress = ((Textbox) createProspectWindow.getFellow("emailAddress")).getValue();
            String phoneNumber = ((Textbox) createProspectWindow.getFellow("phoneNumber")).getValue();
            String prospectRole = ((Textbox) createProspectWindow.getFellow("prospectRole")).getValue();

            String requisitionId = ((Textbox) createProspectWindow.getFellow("reqId")).getValue();
            String positionCategory = ((Textbox) createProspectWindow.getFellow("positionCategory")).getValue();
            String prospectId = com.ndz.zkoss.HrmsUtil.getProspectId(delegator,positionCategory);
            Date beforeConvinientTimeType = ((Timebox) createProspectWindow.getFellow("beforeConvinientTime")).getValue();
            Timestamp beforeConvinientTime = new Timestamp(beforeConvinientTimeType.getTime());
            Date afterConvinientTimeType = ((Timebox) createProspectWindow.getFellow("afterConvinientTime")).getValue();
            Timestamp afterConvinientTime = new Timestamp(afterConvinientTimeType.getTime());

            java.util.Date now = new java.util.Date();
            java.sql.Date currDate = new java.sql.Date(now.getTime());

            // creates Prospect as Person
            Map prospectDetails = UtilMisc.toMap("userLogin", userLogin, "firstName", firstName, "lastName", lastName, "partyId", prospectId);
            Map result = dispatcher.runSync("createPerson", prospectDetails);

            String partyId = (String) result.get("partyId");
            // creates Role Of Prospect as Role Type Prospect
            Map prospectRoleDetails = UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "roleTypeId", prospectRole);
            dispatcher.runSync("createPartyRole", prospectRoleDetails);
            // creates EmailAddress of Prospect

            Map prospectEmailAddress = UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "fromDate", UtilDateTime.nowTimestamp(), "roleTypeId",
                    prospectRole, "emailAddress", emailAddress);
            Map employeeEmailContactAddressResult = null;
            employeeEmailContactAddressResult = dispatcher.runSync("createPartyEmailAddress", prospectEmailAddress);
            String emailContactMecId = (String) employeeEmailContactAddressResult.get("contactMechId");
            if (UtilValidate.isNotEmpty(emailContactMecId)) {
                GenericValue emailGv = delegator.makeValidValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId",
                        emailContactMecId, "contactMechPurposeTypeId", "PRIMARY_EMAIL", "fromDate", UtilDateTime.nowTimestamp()));
                delegator.create(emailGv);
            }

            // creates PhoneNumber of Prospect
            Map prospectContactNumberDetails = UtilMisc.toMap("userLogin", userLogin, "contactNumber", phoneNumber, "partyId", partyId);
            Map employeeTelecomNumberAddressResult = null;
            employeeTelecomNumberAddressResult = dispatcher.runSync("createPartyTelecomNumber", prospectContactNumberDetails);
            String telContactMecId = (String) employeeTelecomNumberAddressResult.get("contactMechId");
            if (UtilValidate.isNotEmpty(telContactMecId)) {
                GenericValue telGv = delegator.makeValidValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId", telContactMecId,
                        "contactMechPurposeTypeId", "PRIMARY_PHONE", "fromDate", UtilDateTime.nowTimestamp()));
                delegator.create(telGv);
            }

            // Gets The File Name

            String fileName = uploadedFile == null ? null : (uploadedFile.getName());
            String actualFileName = null;
            String fileNameStoredInDb = null;
            if (fileName != null) {
                String extention = fileName.substring(fileName.lastIndexOf('.'));
                actualFileName = UPLOADPATH + fileName;
                File diskFile = new File(actualFileName);
                FileOutputStream outputStream = new FileOutputStream(diskFile);
                IOUtils.copyLarge(uploadedFile.getStreamData(), outputStream);
            } else {
                Messagebox.show("Profile/Resume Cannot create Prospect;Upload candidate's profile", "Error", 1, null);
                return;
            }
            // creates Prospect'sResume
            Map prospectResume = UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "resumeText", fileName, "resumeStatus", "1");
            dispatcher.runSync("createPartyResume", prospectResume);

            updatePreviousYearLeaveWithZeroBalance(delegator,requisitionId,prospectId);

            // creates Prospect's Application
            Timestamp applicationDateInTimeStamp = new Timestamp(currDate.getTime());
            Map submitApp = UtilMisc.toMap("userLogin", userLogin, "requisitionId", requisitionId, "applyingPartyId", partyId, "referredByPartyId",
                    userLoginId, "applicationDate", applicationDateInTimeStamp, "statusId", "EMPL_POS_ACTIVE", "fileName", fileName, "beforeConvinientTime",
                    beforeConvinientTime, "afterConvinientTime", afterConvinientTime);
            dispatcher.runSync("createEmploymentApp", submitApp);
            Comp.detach();
            Messagebox.show("Prospect with ID " + " " + partyId + " " + "created successfully for" + " " + HumanResUtil.getFullName(partyId), "Success", 1, null);

            if (beganTransaction)
                TransactionUtil.commit();

        } catch (Exception e) {

            if (beganTransaction)
                try {
                    TransactionUtil.rollback();
                } catch (GenericTransactionException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            e.printStackTrace();

        }

    }

    public static void showInterviewScheduleWindow(Event event, GenericValue gv) throws SuspendNotAllowedException, InterruptedException,
            GenericEntityException {

        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        Map arg = new HashMap();
        arg.put("partyId", gv.getString("partyId"));
        Window win = (Window) Executions.createComponents("/zul/employmentApplication/scheduleInterview.zul", null, arg);
        GenericValue person = null;
        person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", gv.getString("partyId")));
        String candidateName = person.getString("firstName") + " " + person.getString("lastName");
        String resumeId = gv.getString("resumeId");
        Label candidateId = (Label) win.getFellow("partyId");
        candidateId.setValue(candidateName);
        win.doModal();
    }

    public static void scheduleInterview(Event event) throws GenericEntityException, GenericServiceException, InterruptedException {

        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        Component scheduleInterviewWindow = event.getTarget();

        String prospectRole = ((Textbox) scheduleInterviewWindow.getFellow("prospectRole")).getValue();
        String candidateId = ((Textbox) scheduleInterviewWindow.getFellow("candidateId")).getValue();
        List<GenericValue> empApplication = delegator.findByAnd("EmploymentApp", UtilMisc.toMap("applyingPartyId", candidateId));
        String applicationId = null;
        for (GenericValue appGv : empApplication) {
            applicationId = appGv.getString("applicationId");
            break;
        }

        String roleTypeId = ((Label) scheduleInterviewWindow.getFellow("roleTypeId")).getValue();

        Date fromDateType = ((Datebox) scheduleInterviewWindow.getFellow("fromDate")).getValue();
        java.sql.Date fromDate = new java.sql.Date(fromDateType.getTime());

        String statusId = "PER_IN_PROGRESS";

        String interviewerId = ((EmployeeBox) scheduleInterviewWindow.getFellow("searchPanel")).getValue();

        Map scheduleInterview = UtilMisc.toMap("userLogin", userLogin, "applicationId", applicationId, "roleTypeId", prospectRole, "fromDate", fromDate,
                "interviewerId", interviewerId, "partyId", candidateId, "statusId", statusId, "scheduleTime", null, "thruDate", UtilDateTime.nowTimestamp());
        LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
        dispatcher.runSync("createPerformanceNote", scheduleInterview);

        Messagebox.show("Interview Scheduled For The Candidate", "Success", 1, null);
    }

    public static void deleteScheduleInterview(Event event, GenericValue gv) throws GenericEntityException, InterruptedException {

        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
        String partyId = gv.getString("partyId");
        delegator.removeByAnd("PerformanceNote", UtilMisc.toMap("partyId", partyId));
        Messagebox.show("Interview Scheduled  For The Candidate Deleted", "Success", 1, null);
    }

    @SuppressWarnings("unchecked")
    public static void createConsultant(Event event) throws GenericServiceException, InterruptedException, GenericEntityException {

        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        Component createConsultantWindow = event.getTarget();

        String consultantName = ((Textbox) createConsultantWindow.getFellow("consultantName")).getValue();
        String emailAddress = ((Textbox) createConsultantWindow.getFellow("emailId")).getValue();
        Listitem locationItem = ((Listbox) createConsultantWindow.getFellow("locationId")).getSelectedItem();
        String locationId = (String) locationItem.getValue();
        String consultantId = ((Textbox) createConsultantWindow.getFellow("consultantId")).getValue();
        String contactNumber = ((Textbox) createConsultantWindow.getFellow("contactNumber")).getValue();
        java.util.Date now = new java.util.Date();
        java.sql.Date currDate = new java.sql.Date(now.getTime());
        Map consultantDetails = UtilMisc.toMap("userLogin", userLogin, "partyId", consultantId, "partyTypeId", "PARTY_GROUP", "groupName", consultantName,
                "statusId", "PARTY_ENABLED");
        LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
        Map result = dispatcher.runSync("createPartyGroup", consultantDetails);
        String partyId = (String) result.get("partyId");
        Map employmentContext = UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "roleTypeId", "RECRUITMENT_AGENCY");
        Map partyRoleResult = null;
        partyRoleResult = dispatcher.runSync("createPartyRole", employmentContext);

		/*
		 * 
		 * createPartyRelationship service getting called
		 */
        Map partyRelationship = UtilMisc.toMap("userLogin", userLogin, "partyIdFrom", "Company", "partyIdTo", partyId, "roleTypeIdFrom", "RECRUITMENT_AGENCY",
                "roleTypeIdTo", "_NA_", "partyRelationshipTypeId", "GROUP_ROLLUP", "fromDate", UtilDateTime.nowTimestamp());
        dispatcher.runSync("createPartyRelationship", partyRelationship);
		/* Adds Location To Newly Created Department */

        // Map locationMap = UtilMisc.toMap("partyIdTo", partyId, "locationId",
        // locationId);
        // delegator.create("DepartmentLocation", locationMap); (deleted
        // departmentlocation entity)

        Map employeeEmailContactAddress = UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "fromDate", UtilDateTime.nowTimestamp(), "emailAddress",
                emailAddress);
        Map employeeEmailContactAddressResult = null;
        employeeEmailContactAddressResult = dispatcher.runSync("createPartyEmailAddress", employeeEmailContactAddress);
        String emailContactMechId = (String) employeeEmailContactAddressResult.get("contactMechId");
        Map employeeTelecomNumberAddress = UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "fromDate", UtilDateTime.nowTimestamp(), "contactNumber",
                contactNumber);
        String userLoginId = ((Textbox) createConsultantWindow.getFellow("userLoginId")).getValue();
        userLoginId = userLoginId.toLowerCase();
        String password = ((Textbox) createConsultantWindow.getFellow("password")).getValue();
        String verifyPassword = ((Textbox) createConsultantWindow.getFellow("verifyPassword")).getValue();

        Map createUserLogin = UtilMisc.toMap("userLogin", userLogin, "userLoginId", userLoginId, "currentPassword", password, "currentPasswordVerify",
                verifyPassword, "partyId", partyId);
        dispatcher.runSync("createUserLogin", createUserLogin);
        Map assignSecurityGroup = UtilMisc.toMap("userLogin", userLogin, "userLoginId", userLoginId, "groupId", "HRMS_RECRUIT", "fromDate",
                UtilDateTime.nowTimestamp());
        Map assignSecurityGroupResult = null;
        assignSecurityGroupResult = dispatcher.runSync("addUserLoginToSecurityGroup", assignSecurityGroup);
        Map employeeTelecomNumberAddressResult = null;
        employeeTelecomNumberAddressResult = dispatcher.runSync("createPartyTelecomNumber", employeeTelecomNumberAddress);
        String phoneNumberContactMechId = (String) employeeTelecomNumberAddressResult.get("contactMechId");
        Map recruitmentAgency = UtilMisc.toMap("agencyId", partyId, "agencyName", consultantName, "emailAddress", emailAddress, "contactNumber", contactNumber,
                "locationId", locationId, "userLoginId", userLoginId, "password", password, "emailContactMechId", emailContactMechId,
                "contactNumberContactMechId", phoneNumberContactMechId);

        Map mailContentMap = new HashMap();
        mailContentMap.put("sendTo", emailAddress);
        mailContentMap.put("subject", "Recruitment Agency Account Activation");
        mailContentMap.put("templateName", "reqAgencyCreation.ftl");

        Properties mailProp = UtilProperties.getProperties("easyHrmsMail.properties");
        mailContentMap.put("sendFrom", mailProp.get("sendFrom"));
        mailContentMap.put("templateData", UtilMisc.toMap("userName", userLoginId, "password", password, "firstName", consultantName, "teamFrom",
                mailProp.get("teamFrom"), "website", mailProp.get("website"), "companyWebsite", mailProp.get("companyWebsite")));

        ((Map) mailContentMap.get("templateData")).putAll(mailProp);

        dispatcher.runSync("sendGenericNotificationEmail", mailContentMap);

        delegator.create("RecruitmentAgency", recruitmentAgency);
        Messagebox.show("Recruitment Agency Created", "Success", 1, null);

    }

    public static void showAddPerformanceNoteWindow(Event event, GenericValue gv) throws GenericEntityException, SuspendNotAllowedException,
            InterruptedException {

        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        Map arg = new HashedMap();
        String statusId = null;
        if (gv != null) {
            statusId = gv.getString("statusId");
        }

        arg.put("statusId", statusId);
        arg.put("thruDate", gv.getTimestamp("thruDate"));
        arg.put("interviewerId", gv.getString("interviewerId"));
        arg.put("comments", gv.getString("comments"));
        arg.put("interviewPanelDetails",gv.getString("interviewPanelDetails"));
        arg.put("performanceRating", gv.getString("performanceRating"));
        arg.put("fromDate",gv.getTimestamp("fromDate"));
        arg.put("gv", gv);
        Window win = (Window) Executions.createComponents("/zul/employeeRequisition/addPerformanceNote.zul", null, arg);

        Textbox partyIdBox = (Textbox) win.getFellow("candidateId");
        partyIdBox.setValue(gv.getString("partyId"));
        Textbox applicationIdBox = (Textbox) win.getFellow("applicationId");
        applicationIdBox.setValue(gv.getString("applicationId"));
        GenericValue person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", gv.getString("partyId")));
        String interviewerName = person.getString("firstName") + " " + person.getString("lastName");
        Label candidateNameLable = (Label) win.getFellow("candidateName");
        candidateNameLable.setValue(interviewerName);
        Label scheduledDateLabel = (Label) win.getFellow("scheduledDate");
        String fromDate = new SimpleDateFormat("dd-MM-yyyy h:mm a").format(gv.getTimestamp("fromDate").getTime());
        scheduledDateLabel.setValue(fromDate);
        Label interviewerIdLabel = (Label) win.getFellow("interviewerId");
        interviewerIdLabel.setValue(gv.getString("interviewerId"));
        Label statusIdLabel = (Label) win.getFellow("statusId");
        statusIdLabel.setValue(gv.getString("statusId"));
        win.doModal();
    }

    public static void showInformationWindow(Event event, GenericValue gv) throws GenericEntityException, SuspendNotAllowedException, InterruptedException {

        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        Map arg = new HashedMap();
        arg.put("partyId", gv.getString("interviewerId"));
        Window win = (Window) Executions.createComponents("/zul/employmentApplication/information.zul", null, arg);
        win.doModal();
    }

    public static void addPerformanceNote(final Event event, final Timestamp thruDate, final Component component) throws GenericServiceException, InterruptedException, GenericEntityException {
        Messagebox.show("Performance Note cannot be modified. Do you want to continue?", "Warning", Messagebox.YES | Messagebox.NO,
                Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener() {
            public void onEvent(Event evt) throws Exception {
                if ("onYes".equals(evt.getName())) {
                    try {
                        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
                        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
                        Component addPerformanceWindow = event.getTarget();
                        String candidateId = ((Textbox) addPerformanceWindow.getFellow("candidateId")).getValue();
                        String applicationId = ((Textbox) addPerformanceWindow.getFellow("applicationId")).getValue();
                        Integer ratingType = ((Spinner) addPerformanceWindow.getFellow("rating")).getValue();
                        ratingType = ratingType == null ? 0 : ratingType;
                        java.math.BigDecimal rating = new java.math.BigDecimal(ratingType);
                        Listitem statusIdItem = ((Listbox) addPerformanceWindow.getFellowIfAny("finalstatusId", true)).getSelectedItem();
                        String statusId = statusIdItem != null ? (String) statusIdItem.getValue() : "PER_IN_PROGRESS";
                        String comment = ((Textbox) addPerformanceWindow.getFellow("comment")).getValue();
                        String interviewPanelDetail = ((Textbox) addPerformanceWindow.getFellow("interviewPanelDetail")).getValue();
                        String prospectRole = ((Textbox) addPerformanceWindow.getFellow("prospectRole")).getValue();
                        Date communicationDateType = ((Datebox) addPerformanceWindow.getFellow("communicationDate")).getValue();
                        java.sql.Date communicationDate = new java.sql.Date(communicationDateType.getTime());
                        Date fromDateType = ((Datebox) addPerformanceWindow.getFellow("fromDate")).getValue();
                        java.sql.Timestamp fromDate = new java.sql.Timestamp(fromDateType.getTime());
                        Map addPerformanceNoteDetails = UtilMisc.toMap("userLogin", userLogin, "performanceRating", rating, "partyId", candidateId, "applicationId",
                                applicationId, "communicationDate", communicationDate, "roleTypeId", prospectRole, "fromDate", fromDate, "comments", comment, "statusId", statusId,
                                "communicationTime", null, "thruDate", thruDate,"interviewPanelDetails",interviewPanelDetail);
                        LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
                        dispatcher.runSync("updatePerformanceNote", addPerformanceNoteDetails);
                        Events.postEvent("onClick$searchPerCompany", addPerformanceWindow.getPage().getFellow("searchPanel"), null);
                        Messagebox.show("Interview Performance Note Added", "Success", 1, null);
                        component.detach();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    return;
                }
            }
        });
    }


    public static void addReportingStructure(Event event, Component comp) throws GenericServiceException, InterruptedException, GenericEntityException, ParseException {

        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        Component addReportingStructureWindow = event.getTarget();

        String employeeId = ((EmployeeBox) addReportingStructureWindow.getFellow("reportBandbox")).getValue();

        String emplPositionId = null;
        if (employeeId != "") {
            GenericValue emplPosReportingToGV = HumanResUtil.getEmplPositionForParty(employeeId, delegator);
            emplPositionId = emplPosReportingToGV.getString("emplPositionId");
            System.out.println("\n\n\nEmployee Position Id********" + emplPositionId);
        }
        String emplPosReportingParty = ((ManagerBox) addReportingStructureWindow.getFellow("positionIdReportingTo")).getValue();
        if (employeeId.equals(emplPosReportingParty)) {
            Messagebox.show("Employee Id and Manager Id Cannot Be Same", "Error", 1, null);
            return;
        }
        String managerPosId = null;
        if (emplPosReportingParty != "") {
            GenericValue emplPosReportingToGV = HumanResUtil.getEmplPositionForParty(emplPosReportingParty, delegator);
            managerPosId = emplPosReportingToGV.getString("emplPositionId");
            System.out.println("\n\n\nManager Position Id********" + managerPosId);
        }
        Date fromDateType = new Date();
        Timestamp fromDate = new Timestamp(fromDateType.getTime());
		/*
		 * Date thruDateType = (Date) ((Datebox) addReportingStructureWindow
		 * .getFellow("thruDate")).getValue(); Timestamp thruDate = null; if
		 * (thruDateType != null) { thruDate = new
		 * Timestamp(thruDateType.getTime()); }
		 */
        List checksameReportingStructList = delegator.findByAnd("EmplPositionReportingStruct",
                UtilMisc.toMap("emplPositionIdManagedBy", emplPositionId, "emplPositionIdReportingTo", managerPosId));
        if (checksameReportingStructList.size() > 0) {
            Messagebox.show("Cannot Add Reporting Structure;Reporting Manager Id is same with Current Reporting Manager", "Error", 1, null);
            return;
        }
        List<GenericValue> checkEmploymentList = delegator.findByAnd("Employment", UtilMisc.toMap("partyIdTo", employeeId));
        GenericValue checkEmploymentGv = EntityUtil.getFirst(checkEmploymentList);
        Timestamp employmentDate = checkEmploymentGv.getTimestamp("fromDate");
        Date employmentDateType = new Date(employmentDate.getTime());
        if (UtilDateTimeSME.daysBetween(employmentDateType, fromDateType) >= 0) {
            Listitem primaryFlagItem = ((Listbox) addReportingStructureWindow.getFellow("primaryFlag")).getSelectedItem();
            String primaryFlag = (String) primaryFlagItem.getValue();
            List<GenericValue> existingReportingStructureList = delegator.findByAnd("EmplPositionReportingStruct",
                    UtilMisc.toMap("emplPositionIdManagedBy", emplPositionId, "thruDate", null));
            if (existingReportingStructureList.size() > 0) {
                GenericValue existingReportingStructGv = EntityUtil.getFirst(existingReportingStructureList);
                String emplPositionIdReportingTo = existingReportingStructGv.getString("emplPositionIdReportingTo");
                Timestamp existingfromDate = existingReportingStructGv.getTimestamp("fromDate");

                Timestamp d_fromDate = null;
                Timestamp d_thruDate = null;
                Date currDateType = new Date();
                Timestamp currDate = new Timestamp(currDateType.getTime());
                Date cuurDate = UtilDateTime.DEFAULT_DATE_FORMATTER.parse(UtilDateTime.formatDate(new java.util.Date()));
                if (existingReportingStructGv != null) {
                    d_fromDate = existingReportingStructGv.getTimestamp("fromDate");
                    if (cuurDate.compareTo(UtilDateTime.DEFAULT_DATE_FORMATTER.parse(UtilDateTime.formatDate(d_fromDate))) == 0)
                        d_thruDate = UtilDateTime.getTimestamp(cuurDate.getTime());
                    else
                        d_thruDate = UtilDateTimeSME.substractDaysToTimestamp(currDate, 1);
                }

                existingReportingStructGv.putAll(UtilMisc.toMap("emplPositionIdReportingTo", emplPositionIdReportingTo, "emplPositionIdManagedBy",
                        emplPositionId, "primaryFlag", "Y", "fromDate", existingfromDate, "thruDate", d_thruDate));
                delegator.store(existingReportingStructGv);
            }
            if (managerPosId != null) {
                Map reportingStructureDetails = UtilMisc.toMap("userLogin", userLogin, "emplPositionIdReportingTo", managerPosId, "emplPositionIdManagedBy",
                        emplPositionId, "primaryFlag", primaryFlag, "fromDate", fromDate, "thruDate", null);
                LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);

                List currentReportingStructList = delegator.findByAnd("EmplPositionReportingStruct",
                        UtilMisc.toMap("emplPositionIdManagedBy", emplPositionId, "fromDate", fromDate, "thruDate", null));

                dispatcher.runSync("createEmplPositionReportingStruct", reportingStructureDetails);
            }
            Messagebox.show("Reporting Structure Added", "Success", 1, null);
            Events.postEvent("onClick", Path.getComponent("/searchPanel//searchPerCompany"), null);
            comp.detach();
        } else {
            Messagebox.show("From Date Must Be After Employee's Joining Date\nJoining Date is:" + new Date(employmentDate.getTime()), "Error", 1, null);
        }

    }

    public static void editReportingStructure(Event event, Component comp) throws InterruptedException, GenericEntityException, GenericServiceException, ParseException {

        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        Component editReportingStructureWindow = event.getTarget();

        String employeeId = ((Label) editReportingStructureWindow.getFellow("employeeId")).getValue();
        String mgrPosId = ((Label) editReportingStructureWindow.getFellow("magePosId")).getValue();
        String emplPositionId = null;
        if (employeeId != "") {
            GenericValue emplPosReportingToGV = HumanResUtil.getEmplPositionForParty(employeeId, delegator);
            emplPositionId = emplPosReportingToGV.getString("emplPositionId");
            System.out.println("\n\n\nEmployee Position Id********" + emplPositionId);
        }
        String emplPosReportingParty = ((ManagerBox) editReportingStructureWindow.getFellow("positionIdReportingTo")).getValue();
        if (employeeId.equals(emplPosReportingParty)) {
            Messagebox.show("Employee Id and Manager Id Cannot Be Same", "Error", 1, null);
            return;
        }
        String managerPosId = null;
        if (emplPosReportingParty != "") {
            GenericValue emplPosReportingToGV = HumanResUtil.getEmplPositionForParty(emplPosReportingParty, delegator);
            managerPosId = emplPosReportingToGV.getString("emplPositionId");
            System.out.println("\n\n\nManager Position Id********" + managerPosId);
        }
        List checksameReportingStructList = delegator.findByAnd("EmplPositionReportingStruct",
                UtilMisc.toMap("emplPositionIdManagedBy", emplPositionId, "emplPositionIdReportingTo", managerPosId));
        if (checksameReportingStructList.size() > 0) {
            Messagebox.show("Cannot Update Reporting Structure;Reporting Manager Id is same with Current Reporting Manager", "Error", 1, null);
        } else {
            List existingReportingStructureList = delegator.findByAnd("EmplPositionReportingStruct",
                    UtilMisc.toMap("emplPositionIdManagedBy", emplPositionId, "emplPositionIdReportingTo", mgrPosId));
            GenericValue existingReportingStructGv = null;
            existingReportingStructGv = EntityUtil.getFirst(existingReportingStructureList);
            Timestamp d_fromDate = null;
            Timestamp d_thruDate = null;
            Date currDateType = new Date();
            Timestamp currDate = new Timestamp(currDateType.getTime());
            Date cuurDate = UtilDateTime.DEFAULT_DATE_FORMATTER.parse(UtilDateTime.formatDate(new java.util.Date()));
            if (existingReportingStructGv != null) {
                d_fromDate = existingReportingStructGv.getTimestamp("fromDate");
                d_thruDate = existingReportingStructGv.getTimestamp("thruDate");
				/*if(cuurDate.compareTo(UtilDateTime.DEFAULT_DATE_FORMATTER.parse(UtilDateTime.formatDate(d_fromDate))) == 0)
					d_thruDate = UtilDateTime.getTimestamp(cuurDate.getTime());
				else
					d_thruDate = UtilDateTimeSME.substractDaysToTimestamp(currDate, 1);*/
            }
            Listitem primaryFlagItem = ((Listbox) editReportingStructureWindow.getFellow("primaryFlag")).getSelectedItem();
            String primaryFlag = (String) primaryFlagItem.getValue();
            if (managerPosId != null) {
                Map reportingStructureDetails = UtilMisc.toMap("emplPositionIdReportingTo", mgrPosId, "emplPositionIdManagedBy", emplPositionId, "primaryFlag",
                        primaryFlag, "fromDate", d_fromDate, "thruDate", d_thruDate);
                GenericValue reportingStructGV = null;
				/*
				 * List<GenericValue> reportingStructListGV =
				 * delegator.findByAnd("EmplPositionReportingStruct",
				 * UtilMisc.toMap( "emplPositionIdManagedBy", emplPositionId));
				 * reportingStructGV =
				 * EntityUtil.getFirst(reportingStructListGV);
				 */
                existingReportingStructGv.putAll(reportingStructureDetails);
                delegator.store(existingReportingStructGv);

                Map createNewStruct = UtilMisc.toMap("userLogin", userLogin, "emplPositionIdReportingTo", managerPosId, "emplPositionIdManagedBy",
                        emplPositionId, "fromDate", d_fromDate, "thruDate", null);

                delegator.removeByAnd("EmplPositionReportingStruct", UtilMisc.toMap("emplPositionIdReportingTo", existingReportingStructGv.getString("emplPositionIdReportingTo"),
                        "emplPositionIdManagedBy", existingReportingStructGv.getString("emplPositionIdManagedBy"), "fromDate", d_fromDate));
                LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
                dispatcher.runSync("createEmplPositionReportingStruct", createNewStruct);
            }
            Messagebox.show("Reporting Structure Updated", "Success", 1, null);
            Events.postEvent("onClick", Path.getComponent("/searchPanel//searchPerCompany"), null);
            comp.detach();
        }

    }

    public static void deleteReportingStruct(Event event, GenericValue gv) throws GenericServiceException, InterruptedException {

        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
        String emplPositionIdReportingTo = gv.getString("emplPositionIdReportingTo");
        String emplPositionIdManagedBy = gv.getString("emplPositionIdManagedBy");
        Map reportingStructureDetails = UtilMisc.toMap("userLogin", userLogin, "emplPositionIdReportingTo", emplPositionIdReportingTo,
                "emplPositionIdManagedBy", emplPositionIdManagedBy, "fromDate", gv.getTimestamp("fromDate"));
        dispatcher.runSync("deleteEmplPositionReportingStruct", reportingStructureDetails);

        Messagebox.show("Reporting Structure Deleted", "Success", 1, null);
    }

    public static String checkForPositionFullfillment(String emplPositionId) throws GenericEntityException {
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");

        List positionFullfillmentList = null;
        GenericValue positionFulfillmentGv = null;
        String partyId = null;
        List<GenericValue> findEmployeeTerminationApp = null;
        String terminationStatus = null;
        List positionRelatedToRequisitionList = null;
        GenericValue positionRelatedToRequisitionGv = null;
        String requisitionId = null;
        GenericValue findEmployeeTerminationAppGv = null;
        positionFullfillmentList = delegator.findByAnd("EmplPositionFulfillment", UtilMisc.toMap("emplPositionId", emplPositionId));
        positionRelatedToRequisitionList = delegator.findByAnd("EmplPosition", UtilMisc.toMap("emplPositionId", emplPositionId));
        positionRelatedToRequisitionGv = EntityUtil.getFirst(positionRelatedToRequisitionList);
        requisitionId = positionRelatedToRequisitionGv.getString("requisitionId");

        if (positionFullfillmentList.size() > 0) {
            positionFulfillmentGv = EntityUtil.getFirst(positionFullfillmentList);
            partyId = positionFulfillmentGv.getString("partyId");
            findEmployeeTerminationApp = delegator.findByAnd("MaxTerminationDetail", UtilMisc.toMap("partyId", partyId));
            findEmployeeTerminationAppGv = EntityUtil.getFirst(findEmployeeTerminationApp);
            if (findEmployeeTerminationAppGv != null) {
                terminationStatus = findEmployeeTerminationAppGv.getString("statusId");
            }
        }
        if (findEmployeeTerminationAppGv != null) {
            if (positionFullfillmentList.size() > 0 && (terminationStatus.equals("ET_ADM_APPROVED"))) {
                return "1";
            } else if (positionFullfillmentList.size() > 0 && (!(terminationStatus.equals("ET_ADM_APPROVED")))) {
                return "2";
            }
        } else {
            if (positionFullfillmentList.size() <= 0 && ((requisitionId != null))) {
                return "3";
            } else {
                return "2";
            }
        }
        return "";
    }

    public static void sendEmail(Event event) throws GenericEntityException, GenericServiceException, InterruptedException {

        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
        Component assignWindow = event.getTarget();
        String requisitionId = ((Textbox) assignWindow.getFellow("requisitionId")).getValue();
        Set lstitem = new HashSet();
        lstitem = ((Listbox) assignWindow.getFellow("dataGrid")).getSelectedItems();
        Iterator it = lstitem.iterator();
        Object item = null;
        String agencyId = null;
        String emailAddress = null;
        GenericValue recruitmentAgencyGV = null;
        //Boolean selected;
        //selected = false;
        while (it.hasNext()) {
            //selected = true;
            item = it.next();
            Listitem selectedItem = (Listitem) item;
            agencyId = selectedItem.getLabel();
            List agencyList = delegator.findByAnd("RecruitmentAgency", UtilMisc.toMap("agencyId", agencyId));
            recruitmentAgencyGV = EntityUtil.getFirst(agencyList);
            emailAddress = recruitmentAgencyGV.getString("emailAddress");
            String firstName = recruitmentAgencyGV.getString("agencyName");

            Map mailContentMap = new HashMap();
            mailContentMap.put("sendTo", emailAddress);
            mailContentMap.put("templateName", "reqAgencyAssignment.ftl");

            Properties mailProp = UtilProperties.getProperties("easyHrmsMail.properties");
            mailContentMap.put("sendFrom", mailProp.get("sendFrom"));
            mailContentMap.put("subject", "Requisition Assigned");
            mailContentMap.put(
                    "templateData",
                    UtilMisc.toMap("requisitionId", requisitionId, "firstName", firstName, "teamFrom", mailProp.get("teamFrom"), "website",
                            mailProp.get("website"), "companyName", mailProp.get("companyName"), "requisitionURL", mailProp.get("requisitionURL")));

            ((Map) mailContentMap.get("templateData")).putAll(mailProp);
            try {
                delegator.create(
                        "RecruitmentRequisition",
                        UtilMisc.toMap("requisitionId", requisitionId, "agencyId", agencyId, "emailContactMechId",
                                recruitmentAgencyGV.getString("emailContactMechId"), "contactNumberContactMechId",
                                recruitmentAgencyGV.getString("contactNumberContactMechId")));
                dispatcher.runSync("sendGenericNotificationEmail", mailContentMap);
            } catch (Exception e) {
                dispatcher.runSync("sendGenericNotificationEmail", mailContentMap);

            }

        }
        if (lstitem.size() > 0)
            Messagebox.show("Mail sent successfully to the selected Recruitment Agencies", "Success", 1, null);
        else
            Messagebox.show("No Recruitment Agency selected", "Error", 1, null);

    }

    public static void createPositionAndReportingStructure(Event event) throws GenericServiceException, GenericEntityException, InterruptedException {

        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        Component adminViewRequisitionWindow = event.getTarget();
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

        String requisitionType = ((Textbox) adminViewRequisitionWindow.getFellow("requisitionType")).getValue();
        Integer noOfPosition = ((Intbox) adminViewRequisitionWindow.getFellow("nodOfPosToCreate")).getValue();
        String requisitionPosTypeId = ((Textbox) adminViewRequisitionWindow.getFellow("requisitionPosType")).getValue();
        String departmentId = ((Textbox) adminViewRequisitionWindow.getFellow("reqRaisedByDept")).getValue();
        String managedEmpId = ((ManagerBox) adminViewRequisitionWindow // swetalina
                .getFellow("mgrbox")).getValue();
        String managedPosId = "";
        List positionList = delegator.findByAnd("EmplPositionFulfillment", "partyId", managedEmpId);
        GenericValue mgrpositionGv = org.ofbiz.entity.util.EntityUtil.getFirst(positionList);
        if (mgrpositionGv != null)
            managedPosId = mgrpositionGv.getString("emplPositionId"); // swetalina
        String reqInitatorId = ((Textbox) adminViewRequisitionWindow.getFellow("reqInitatorId")).getValue();
        String requisitionId = ((Label) adminViewRequisitionWindow.getFellow("requisitionId")).getValue();
        Date estimatedFromDateType = ((Datebox) adminViewRequisitionWindow.getFellow("estimatedFromDate")).getValue();
        Timestamp estimatedFromDate = new Timestamp(estimatedFromDateType.getTime());
        Timestamp estimatedThruDate = null;
        Date estimatedThruDateType = ((Datebox) adminViewRequisitionWindow.getFellow("estimatedThruDate")).getValue();
        if (estimatedThruDateType != null) {
            estimatedThruDate = new Timestamp(estimatedThruDateType.getTime());
        }
        Listitem salaryFlagItem = ((Listbox) adminViewRequisitionWindow.getFellow("salaryFlag")).getSelectedItem();
        String salaryFlag = (String) salaryFlagItem.getValue();
        Listitem exemptFlagItem = ((Listbox) adminViewRequisitionWindow.getFellow("exemptFlag")).getSelectedItem();
        String exemptFlag = (String) exemptFlagItem.getValue();
        Listitem temporaryFlagItem = ((Listbox) adminViewRequisitionWindow.getFellow("temporaryFlag")).getSelectedItem();
        String temporaryFlag = (String) temporaryFlagItem.getValue();
        Listitem fulltimeFlagItem = ((Listbox) adminViewRequisitionWindow.getFellow("fulltimeFlag")).getSelectedItem();
        String fulltimeFlag = (String) fulltimeFlagItem.getValue();
        Listitem locIdItem = ((Listbox) adminViewRequisitionWindow.getFellow("locId")).getSelectedItem();
        String locationId = (String) locIdItem.getValue();
        String budgetId = ((Label) adminViewRequisitionWindow.getFellow("budgetId")).getValue();
        String budgetItemSeqId = ((Label) adminViewRequisitionWindow.getFellow("budgetItemSeqId")).getValue();
        GenericValue emplPosGV = HumanResUtil.getEmplPositionForParty(reqInitatorId, delegator);
        String reqInitatorPosId = emplPosGV.getString("emplPositionId");
        if (managedPosId.equals("")) {
            managedPosId = reqInitatorPosId;
        }
        EntityCondition condition = EntityCondition.makeCondition("emplPositionId", EntityComparisonOperator.EQUALS, managedPosId);
        Set fieldToSelect = new HashSet();
        fieldToSelect.add("partyId");
        fieldToSelect.add("emplPositionId");
        List<GenericValue> partyOfPos = delegator.findList("EmplPositionFulfillment", condition, fieldToSelect, null, null, false);
        String managerName = null;
        if (partyOfPos.size() >= 0) {
            String employeeId = null;
            for (GenericValue partyId : partyOfPos) {
                employeeId = partyId.getString("partyId");
                break;
            }
            GenericValue person = null;
            person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", employeeId));
            managerName = person.getString("firstName") + " " + person.getString("lastName");
            managerName = org.apache.commons.lang.StringUtils.capitalize(managerName);
        } else {
            Messagebox.show("This Position Has Not Fullfilled as Manager", "Error", 1, null);
            return;
        }
        Map createPosition = UtilMisc.toMap("userLogin", userLogin, "emplPositionTypeId", requisitionPosTypeId, "partyId", departmentId, "salaryFlag",
                salaryFlag, "statusId", "EMPL_POS_ACTIVE", "requisitionId", requisitionId, "estimatedFromDate", estimatedFromDate, "estimatedThruDate",
                estimatedThruDate, "exemptFlag", exemptFlag, "temporaryFlag", temporaryFlag, "fulltimeFlag", fulltimeFlag, "budgetId", budgetId,
                "budgetItemSeqId", budgetItemSeqId);
        Map result = null;

        LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
        java.util.Date now = new java.util.Date();
        Timestamp currDate = new Timestamp(now.getTime());
        Map reportingStruct = new HashMap();
        List openedPositions = new LinkedList();
        openedPositions = new ArrayList();
        List managerNames = new LinkedList();
        managerNames = new ArrayList();
        if (requisitionType.equals("new")) {
            for (int i = 0; i < noOfPosition; i++) {
                result = dispatcher.runSync("createEmplPosition", createPosition);
                String PositionIdReportingTo = (String) result.get("emplPositionId");
                openedPositions.add(i, PositionIdReportingTo);
                managerNames.add(i, managerName);
                reportingStruct.put("emplPositionIdReportingTo", managedPosId);
                reportingStruct.put("emplPositionIdManagedBy", PositionIdReportingTo);
                reportingStruct.put("fromDate", currDate);
                reportingStruct.put("primaryFlag", "Y");
                reportingStruct.put("userLogin", userLogin);
                result = dispatcher.runSync("createEmplPositionReportingStruct", reportingStruct);
				/* Update the Requisition Status To Processing */
                GenericValue replacementPosIdGv = null;
                replacementPosIdGv = delegator.findByPrimaryKey("EmployeeRequisition", UtilMisc.toMap("requisitionId", requisitionId));
                Map updateRequisition = UtilMisc.toMap("requisitionId", requisitionId, "statusId", "PROCESSING", "locationId", locationId);
                replacementPosIdGv.putAll(updateRequisition);
                replacementPosIdGv.store();
            }
            Object posArray = openedPositions.toArray(new String[openedPositions.size()]);
            SimpleListModel model = new SimpleListModel(openedPositions);
            Listbox posList = (Listbox) adminViewRequisitionWindow.getFellow("posList");
            posList.setModel(model);
            Object nameArray = openedPositions.toArray(new String[managerNames.size()]);
            SimpleListModel nameModel = new SimpleListModel(managerNames);
            Listbox nameList = (Listbox) adminViewRequisitionWindow.getFellow("managerNameList");
            nameList.setModel(nameModel);
            Div div = (Div) adminViewRequisitionWindow.getFellow("openedPos");
            div.setVisible(true);
			/*
			 * Div showMenu = (Div) adminViewRequisitionWindow
			 * .getFellow("showMenu"); showMenu.setVisible(true);
			 */
            Div reportsToDiv = (Div) adminViewRequisitionWindow.getFellow("reportsTo");
            reportsToDiv.setVisible(false);
            Div hideButtonsDiv = (Div) adminViewRequisitionWindow.getFellow("hideButtons");
            hideButtonsDiv.setVisible(false);
			/*
			 * Div showButtonDiv = (Div) adminViewRequisitionWindow
			 * .getFellow("showButton"); showButtonDiv.setVisible(true);
			 */
        } else {
            GenericValue replacementPosIdGv = null;
            replacementPosIdGv = delegator.findByPrimaryKey("EmployeeRequisition", UtilMisc.toMap("requisitionId", requisitionId));
            String replcaementPosId = (String) replacementPosIdGv.get("replacementpositionId");
			/* Update the Requisition Status To Processing */
            Map updateRequisition = UtilMisc.toMap("requisitionId", requisitionId, "statusId", "PROCESSING", "locationId", locationId);
            replacementPosIdGv.putAll(updateRequisition);
            replacementPosIdGv.store();
			/* Update the existing PositionId with the RequisitionId */
            GenericValue emplPosGv = delegator.findByPrimaryKey("EmplPosition", UtilMisc.toMap("emplPositionId", replcaementPosId));
            String posTypeId = emplPosGV.getString("emplPositionTypeId");
			/*
			 * Map updateEmplPos = UtilMisc.toMap("emplPositionId",
			 * replcaementPosId, "requisitionId", requisitionId);
			 * emplPosGv.putAll(updateEmplPos); emplPosGv.store();
			 */
            Map createReplacementPosition = UtilMisc.toMap("userLogin", userLogin, "emplPositionTypeId", posTypeId, "partyId", departmentId, "salaryFlag",
                    salaryFlag, "statusId", "EMPL_POS_ACTIVE", "requisitionId", requisitionId, "estimatedFromDate", estimatedFromDate, "estimatedThruDate",
                    estimatedThruDate, "exemptFlag", exemptFlag, "temporaryFlag", temporaryFlag, "fulltimeFlag", fulltimeFlag, "budgetId", budgetId,
                    "budgetItemSeqId", budgetItemSeqId);
            result = dispatcher.runSync("createEmplPosition", createPosition);
            String PositionIdReportingTo = (String) result.get("emplPositionId");
            reportingStruct.put("emplPositionIdReportingTo", managedPosId);
            reportingStruct.put("emplPositionIdManagedBy", PositionIdReportingTo);
            reportingStruct.put("fromDate", currDate);
            reportingStruct.put("primaryFlag", "Y");
            reportingStruct.put("userLogin", userLogin);
            result = dispatcher.runSync("createEmplPositionReportingStruct", reportingStruct);
            openedPositions.add(0, PositionIdReportingTo);
            managerNames.add(0, managerName);
            Object posArray = openedPositions.toArray(new String[openedPositions.size()]);
            SimpleListModel model = new SimpleListModel(openedPositions);
            Listbox posList = (Listbox) adminViewRequisitionWindow.getFellow("posList");
            posList.setModel(model);
            Object nameArray = openedPositions.toArray(new String[managerNames.size()]);
            SimpleListModel nameModel = new SimpleListModel(managerNames);
            Listbox nameList = (Listbox) adminViewRequisitionWindow.getFellow("managerNameList");
            nameList.setModel(nameModel);
            // Div div = (Div)
            // adminViewRequisitionWindow.getFellow("openedPos");
            // div.setVisible(true);

            // Div hideButtonsDiv = (Div) adminViewRequisitionWindow
            // .getFellow("hideButtons");
            // hideButtonsDiv.setVisible(false);

            // Div reportsToDiv = (Div) adminViewRequisitionWindow
            // .getFellow("reportsTo");
            // reportsToDiv.setVisible(false);
        }

        Messagebox.show("Position Requested Opened for Further Process", "Success", 1, null);

        Executions.sendRedirect("/control/viewProcessingRequisition?requisitionId=" + requisitionId);
    }

    public static void updateConsultant(Event event) throws GenericServiceException, GenericEntityException, InterruptedException {
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        Component editConsultantWindow = event.getTarget();

        String consultantName = ((Textbox) editConsultantWindow.getFellow("consultantName")).getValue();
        String emailAddress = ((Textbox) editConsultantWindow.getFellow("emailId")).getValue();
        Listitem locationItem = ((Listbox) editConsultantWindow.getFellow("locationId")).getSelectedItem();
        String locationId = (String) locationItem.getValue();
        String consultantId = ((Textbox) editConsultantWindow.getFellow("consultantId")).getValue();
        String contactNumber = ((Textbox) editConsultantWindow.getFellow("contactNumber")).getValue();
        java.util.Date now = new java.util.Date();
        java.sql.Date currDate = new java.sql.Date(now.getTime());
        Map consultantDetails = UtilMisc.toMap("userLogin", userLogin, "partyId", consultantId, "partyTypeId", "PARTY_GROUP", "groupName", consultantName,
                "statusId", "PARTY_ENABLED");
        LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
        Map result = dispatcher.runSync("updatePartyGroup", consultantDetails);

		/*
		 * 
		 * createPartyRelationship service getting called
		 */
		/* Adds Location To Newly Created Department */
		
		/*List departmentLocationList = delegator.findByAnd("DepartmentLocation", UtilMisc.toMap("partyIdTo", consultantId));
		GenericValue departmentLocationGv = EntityUtil.getFirst(departmentLocationList);
		Map locationMap = UtilMisc.toMap("partyIdTo", consultantId, "locationId", locationId);
		departmentLocationGv.putAll(locationMap);
		departmentLocationGv.store();*/


        String emailContactMechId = ((Textbox) editConsultantWindow.getFellow("emailContactMechId")).getValue();
        Map employeeEmailContactAddress = UtilMisc.toMap("userLogin", userLogin, "contactMechId", emailContactMechId, "emailAddress", emailAddress, "partyId",
                consultantId);
        Map employeeEmailContactAddressResult = null;
        employeeEmailContactAddressResult = dispatcher.runSync("updatePartyEmailAddress", employeeEmailContactAddress);
        String phoneContactMechId = ((Textbox) editConsultantWindow.getFellow("phoneContactMechId")).getValue();
        Map employeeTelecomNumberAddress = UtilMisc.toMap("userLogin", userLogin, "contactMechId", phoneContactMechId, "contactNumber", contactNumber,
                "partyId", consultantId);
        dispatcher.runSync("updatePartyTelecomNumber", employeeTelecomNumberAddress);
        String currentUserLoginId = ((Label) editConsultantWindow.getFellow("userLoginIdLabel")).getValue();
        String currentPassword = ((Label) editConsultantWindow.getFellow("currentPassword")).getValue();
        String password = ((Textbox) editConsultantWindow.getFellow("password")).getValue();
        String verifyPassword = ((Textbox) editConsultantWindow.getFellow("verifyPassword")).getValue();
        if (UtilValidate.isEmpty(password)) {
            password = currentPassword;
            verifyPassword = currentPassword;
        }
        Map oldUserLoginMap = UtilMisc.toMap("userLoginId", currentUserLoginId, "currentPassword", password, "partyId", consultantId, "enabled", "N",
                "disabledDateTime", UtilDateTime.nowTimestamp());
        Map newUserLoginMap = UtilMisc.toMap("userLogin", userLogin, "userLoginId", currentUserLoginId, "currentPassword", password, "currentPasswordVerify",
                verifyPassword, "partyId", consultantId, "enabled", "Y", "password", currentPassword);
        if (currentUserLoginId != "" && password != "" && verifyPassword != "") {
            updateUseLogin(currentUserLoginId, oldUserLoginMap, newUserLoginMap, delegator, consultantId);
            Map assignSecurityGroup = UtilMisc.toMap("userLogin", userLogin, "userLoginId", currentUserLoginId, "groupId", "HRMS_RECRUIT", "fromDate",
                    UtilDateTime.nowTimestamp());
            Map assignSecurityGroupResult = null;
            assignSecurityGroupResult = dispatcher.runSync("addUserLoginToSecurityGroup", assignSecurityGroup);
            Map recruitmentAgencyMap = UtilMisc.toMap("agencyId", consultantId, "agencyName", consultantName, "emailAddress", emailAddress, "contactNumber",
                    contactNumber, "locationId", locationId, "userLoginId", currentUserLoginId, "password", password);
            updateRecruitmentAgency(consultantId, recruitmentAgencyMap, delegator);
        } else if (currentUserLoginId != "" && password == "" && verifyPassword == "") {
            Map assignSecurityGroup = UtilMisc.toMap("userLogin", userLogin, "userLoginId", currentUserLoginId, "groupId", "HRMS_RECRUIT", "fromDate",
                    UtilDateTime.nowTimestamp());
            Map assignSecurityGroupResult = null;

            Map updateOldUserLoginMap = UtilMisc.toMap("userLoginId", currentUserLoginId, "partyId", consultantId, "enabled", "N", "disabledDateTime",
                    UtilDateTime.nowTimestamp());
            Map updateNewUserLoginMap = UtilMisc.toMap("userLogin", userLogin, "userLoginId", currentUserLoginId, "password", currentPassword, "currentPassword", currentPassword,
                    "currentPasswordVerify", currentPassword, "partyId", consultantId, "enabled", "Y");
            Map recruitmentAgencyMap = UtilMisc.toMap("agencyId", consultantId, "agencyName", consultantName, "emailAddress", emailAddress, "contactNumber",
                    contactNumber, "locationId", locationId, "userLoginId", currentUserLoginId, "password", currentPassword);
            updateUseLogin(currentUserLoginId, updateOldUserLoginMap, updateNewUserLoginMap, delegator, consultantId);
            assignSecurityGroupResult = dispatcher.runSync("addUserLoginToSecurityGroup", assignSecurityGroup);
            updateRecruitmentAgency(consultantId, recruitmentAgencyMap, delegator);
        } else if (currentUserLoginId == "" && password != "" && verifyPassword != "") {
            Map updatePasswordMap = UtilMisc.toMap("userLogin", userLogin, "userLoginId", currentUserLoginId, "currentPassword", currentPassword,
                    "newPassword", password, "newPasswordVerify", verifyPassword);
            Map recruitmentAgencyMap = UtilMisc.toMap("agencyId", consultantId, "agencyName", consultantName, "emailAddress", emailAddress, "contactNumber",
                    contactNumber, "locationId", locationId, "userLoginId", currentUserLoginId, "password", password);
            dispatcher.runSync("updatePassword", updatePasswordMap);
            updateRecruitmentAgency(consultantId, recruitmentAgencyMap, delegator);
        } else {
            Map recruitmentAgencyMap = UtilMisc.toMap("agencyId", consultantId, "agencyName", consultantName, "emailAddress", emailAddress, "contactNumber",
                    contactNumber, "locationId", locationId);
            updateRecruitmentAgency(consultantId, recruitmentAgencyMap, delegator);
        }
        Messagebox.show("Recruitment Agency Updated", "Success", 1, null);
    }

    public static void updateUseLogin(String currentUserLogin, Map oldLoginMap, Map newLoginMap, GenericDelegator delegator, String consultantId)
            throws GenericEntityException, GenericServiceException {
        LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
        //List existingUserLoginlist = delegator.findByAnd("UserLogin", UtilMisc.toMap("userLoginId", currentUserLogin, "partyId", consultantId));
        //GenericValue existinguserLoginGv = EntityUtil.getFirst(existingUserLoginlist);
        //existinguserLoginGv.putAll(oldLoginMap);
        //existinguserLoginGv.remove();
        dispatcher.runSync("updatePassword", UtilMisc.toMap("userLogin", newLoginMap.get("userLogin"), "userLoginId", newLoginMap.get("userLoginId"), "newPassword", newLoginMap.get("currentPassword"), "newPasswordVerify", newLoginMap.get("currentPassword"), "currentPassword", newLoginMap.get("password")));
    }

    public static void updateRecruitmentAgency(String agencyId, Map agencyDetailMap, GenericDelegator delegator) throws GenericEntityException {
        List recruitmentAgencyList = delegator.findByAnd("RecruitmentAgency", UtilMisc.toMap("agencyId", agencyId));
        GenericValue recruitmentAgencyGv = EntityUtil.getFirst(recruitmentAgencyList);
        recruitmentAgencyGv.putAll(agencyDetailMap);
        recruitmentAgencyGv.store();
    }

    public static void deleteConsultant(Event event, GenericValue gv, final Button btn) throws GenericEntityException {
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        final GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        final String agencyId = gv.getString("agencyId");
        final String userLoginId = gv.getString("userLoginId");

        try {
            Messagebox.show("Do You Want To Delete this Record?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
                public void onEvent(Event evt) {
                    if ("onYes".equals(evt.getName())) {

                        try {
                            List existingUserLoginlist = delegator.findByAnd("UserLogin", UtilMisc.toMap("userLoginId", userLoginId, "partyId", agencyId));
                            GenericValue existinguserLoginGv = EntityUtil.getFirst(existingUserLoginlist);
                            Map userLoginMap = UtilMisc.toMap("userLoginId", userLoginId, "enabled", "N");
                            existinguserLoginGv.putAll(userLoginMap);
                            existinguserLoginGv.store();
                            delegator.removeByAnd("RecruitmentAgency", UtilMisc.toMap("agencyId", agencyId));
                            Events.postEvent("onClick", btn, null);

                        } catch (GenericEntityException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        try {
                            Messagebox.show("Agency  Deleted Successfully", Labels.getLabel("HRMS_SUCCESS"), 1, null);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                        return;
                    }
                }
            });
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void showApplyWindow(String requisitionId) throws SuspendNotAllowedException, InterruptedException, GenericEntityException {

        Map arg = new HashMap();
        arg.put("requisitionId", requisitionId);
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();
        GenericValue requisition =  delegator.findOne("EmployeeRequisition", UtilMisc.toMap("requisitionId", requisitionId), false);
        arg.put("positionCategory", requisition.getString("positionCategory"));
        Window win = (Window) Executions.createComponents("/zul/employmentApplication/createProspect.zul", null, arg);
        win.doModal();
    }

    public  void updateProspectDetails(Event event, Window win, GenericValue gv)
            throws GenericServiceException, InterruptedException, IOException,GenericEntityException {
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        Component editProspectWindow = event.getTarget();

        String firstName = ((Textbox) editProspectWindow.getFellow("firstName")).getValue();
        String lastName = ((Textbox) editProspectWindow.getFellow("lastName")).getValue();
        String contactNumber = ((Textbox) editProspectWindow.getFellow("contactNumber")).getValue();
        String emailAddress = ((Textbox) editProspectWindow.getFellow("emailAddress")).getValue();
        String partyId = ((Textbox) editProspectWindow.getFellow("partyId")).getValue();
        String emailContactMechId = ((Textbox) editProspectWindow.getFellow("emailContactMechId")).getValue();
        String phoneContactMechId = null;
        phoneContactMechId = ((Textbox) editProspectWindow.getFellow("phoneContactMechId")).getValue();

        LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
        
        GenericValue partyResumeGv = null;
        String resumeId=null;
        String storedFileName=null;
		List<GenericValue> partyResumeList = delegator.findByAnd("PartyResume",UtilMisc.toMap("partyId",partyId));
		if(partyResumeList.size() > 0) {
			partyResumeGv = partyResumeList.get(0);
			resumeId = (String) partyResumeGv.getString("resumeId");
			storedFileName=(String) partyResumeGv.getString("resumeText");
		}
		
        String fileName = uploadedFile == null ? storedFileName : (uploadedFile.getName());
        
        String actualFileName = null;
        String fileNameStoredInDb = null;
        if (uploadedFile != null) {
            String extention = fileName.substring(fileName.lastIndexOf('.'));
            actualFileName = UPLOADPATH + fileName;
            File diskFile = new File(actualFileName);
            FileOutputStream outputStream = new FileOutputStream(diskFile);
            IOUtils.copyLarge(uploadedFile.getStreamData(), outputStream);
        } 
        // creates Prospect'sResume
        
        Map prospectResume = UtilMisc.toMap("userLogin", userLogin, "resumeId",resumeId,"partyId", partyId, "resumeText", fileName, "resumeStatus", "1");
        dispatcher.runSync("updatePartyResume", prospectResume);

        dispatcher.runSync("updatePartyEmailAddress", UtilMisc.toMap(
                "userLogin", userLogin, "partyId", partyId, "contactMechId",
                emailContactMechId, "emailAddress", emailAddress));
        dispatcher.runSync("updatePartyTelecomNumber", UtilMisc.toMap(
                "userLogin", userLogin, "partyId", partyId, "contactMechId",
                phoneContactMechId, "contactNumber", contactNumber));
        dispatcher.runSync("updatePerson", UtilMisc.toMap("userLogin",
                userLogin, "partyId", partyId, "firstName", firstName,
                "lastName", lastName));
        dispatcher.runSync("updatePartyEmailAddress",
                UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "contactMechId", emailContactMechId, "emailAddress", emailAddress));
        dispatcher.runSync("updatePartyTelecomNumber",
                UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "contactMechId", phoneContactMechId, "contactNumber", contactNumber));
        dispatcher.runSync("updatePerson", UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "firstName", firstName, "lastName", lastName));
        Messagebox.show("Prospect Details Updated", Labels.getLabel("HRMS_SUCCESS"), 1, null);
        win.detach();
    }

         
    public static void sendRequisitionMailToCEO(String requisitionId, Delegator delegator) throws GenericEntityException, GenericServiceException, InterruptedException  {
		GenericValue requisitionGv = delegator.findOne("EmployeeRequisition", UtilMisc.toMap("requisitionId", requisitionId), false);
		String ceoMailId = HrmsUtil.getCeoEmailId(delegator);
		if(UtilValidate.isEmpty(ceoMailId))
       	return;
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		Delegator delegator2 = userLogin.getDelegator();
   		LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();
   		GenericValue personGv = delegator.findOne("Person", UtilMisc.toMap("partyId", requisitionGv.getString("partyId")), false);
        boolean result = MailUtil.sendRequisitionMailToCEO(userLogin.getString("partyId"), delegator2, "requisitionNotificationEmail.ftl", 
        		ceoMailId, UtilMisc.toMap("requisitionGv", requisitionGv, "personGv", personGv), dispatcher,"Requisition Approval");
   
        if (result) {
           Messagebox.show("Mail Sent Successfully", "Success", 1, null);
       }
}

    public static void updatePreviousYearLeaveWithZeroBalance(Delegator delegator,String requisitionId,String partyId) throws GenericEntityException {
        Map previousYearAndCurrentYearStartAndEndDate = HrmsUtil.getCurrentAndPreviousYearStartAndEndDate();
        GenericValue requisitionGv = delegator.findOne("EmployeeRequisition", UtilMisc.toMap("requisitionId", requisitionId), false);
        String employeeType = requisitionGv.getString("employeeType");
        String positionCategory = requisitionGv.getString("positionCategory");
        String employeePositionTypeId = requisitionGv.getString("positionType");
        List<GenericValue> leaveLimits = delegator.findByAnd("EmplLeaveLimit", UtilMisc.toMap("beginYear", previousYearAndCurrentYearStartAndEndDate.get("previousYearStartDate"),
                "endYear", previousYearAndCurrentYearStartAndEndDate.get("previousYearEndDate"), "employeeType", employeeType, "positionCategory", positionCategory), null, false);
        for(GenericValue leaveLimit:leaveLimits){
            GenericValue employeeLeaveInfo = delegator.makeValue("EmployeeLeaveInfo",
                    UtilMisc.toMap("partyId", partyId, "leaveType", leaveLimit.getString("leaveTypeId"), "leaveLimit", 0.0, "availedLeave", 0.0,
                            "balanceLeave", 0.0, "positionType", employeePositionTypeId, "beginYear", previousYearAndCurrentYearStartAndEndDate.get("previousYearStartDate"),
                            "endYear", previousYearAndCurrentYearStartAndEndDate.get("previousYearEndDate")));
            employeeLeaveInfo.create();
        }
    }
    
}
