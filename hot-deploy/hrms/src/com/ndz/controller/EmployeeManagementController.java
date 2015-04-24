package com.ndz.controller;

/*
 * @author Samir
 * */

import com.ndz.zkoss.HrmsUtil;
import com.ndz.zkoss.util.EmployeePositionService;
import com.ndz.zkoss.util.HrmsInfrastructure;
import com.ndz.zkoss.util.MailUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
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
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.*;
import org.zkoss.zul.api.Comboitem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

public class EmployeeManagementController extends GenericForwardComposer {

    private static List<GenericValue> userLoginList;
    public List divisionsForADepartment  ;

    public List getDivisionsForADepartment() {
        return divisionsForADepartment;
    }

    public void setDivisionsForADepartment(List divisionsForADepartment) {
        this.divisionsForADepartment = divisionsForADepartment;
    }

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
    }

    public void setDivisionsForADepartment(org.zkoss.zul.Comboitem item) {
        String departmentId = (String)item.getValue();
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();
        List divisionId = HrmsUtil.getListOfDivisionIdOfADepartment(departmentId);
        List conditions = new ArrayList();
        conditions.add(EntityCondition.makeCondition("divisionId",EntityOperator.IN,divisionId));
        try {
            divisionsForADepartment = delegator.findList("Division", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void createEmployee(Event event) {
        Boolean beganTransaction = false;
        String securityGroup = null;
        try {
            beganTransaction = TransactionUtil.begin();
            System.out.println("*******Event Called********");
            GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
            GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
            Component newEmployeeWindow = event.getTarget();


            //String employeeUniqueId = ((Textbox) newEmployeeWindow.getFellow("employeeUniqueId")).getValue();
            //String employeeAccWithAgent = ((Textbox) newEmployeeWindow.getFellow("employeeAccWithAgent")).getValue();
            String firstName = ((Textbox) newEmployeeWindow.getFellow("addEmployee_firstName")).getValue();
            String middleName = ((Textbox) newEmployeeWindow.getFellow("addEmployee_middleName")).getValue();
            String lastName = ((Textbox) newEmployeeWindow.getFellow("addEmployee_lastName")).getValue();
            String userName = ((Textbox) newEmployeeWindow.getFellow("addEmployee_userLoginId")).getValue();
            Listitem securityGroupType = ((Listbox) newEmployeeWindow.getFellow("securityGroup")).getSelectedItem();
            securityGroup = (String) securityGroupType.getValue();
            Comboitem disabledEmployeeIdType = ((Combobox) newEmployeeWindow.getFellow("disabledEmplId")).getSelectedItem();
            String disabledEmployeeId = null;
            if(null!=disabledEmployeeIdType){
                disabledEmployeeId = (String) disabledEmployeeIdType.getValue();
            }
            Comboitem positionCatg = ((Combobox) newEmployeeWindow.getFellow("positionCategories")).getSelectedItem();
            String positionCategory = positionCatg != null ? (String) positionCatg.getValue() : null;

            String employeeId = com.ndz.zkoss.HrmsUtil.getEmployeeId(delegator,positionCategory);
            userName = userName.toLowerCase();// converting username to
            // lowercase due to the
            // case-sensitive userlogin
            // implementation
            // changed.swetalina
            String password = ((Textbox) newEmployeeWindow.getFellow("addEmployee_password")).getValue();
            String verifyPassword = ((Textbox) newEmployeeWindow.getFellow("addEmployee_verifyPassword")).getValue();
            Comboitem employeeDepartmentType = ((Combobox) newEmployeeWindow.getFellow("employeeDepartment")).getSelectedItem();
            String employeeDepartment = (String) employeeDepartmentType.getValue();
            String emailAddress = ((Textbox) newEmployeeWindow.getFellow("addEmployee_emailAddress")).getValue();
            Listitem genderType = ((Listbox) newEmployeeWindow.getFellow("addEmployee_gender")).getSelectedItem();
            String gender = (String) genderType.getValue();
            Date birthDateInput = (Date) ((Datebox) newEmployeeWindow.getFellow("addEmployee_birthDate")).getValue();
            java.sql.Date birthDate = new java.sql.Date(birthDateInput.getTime());

            String telecomNumber = ((Textbox) newEmployeeWindow.getFellow("addEmployee_contactNumber")).getValue();
            String emergencyContactNumber = ((Textbox) newEmployeeWindow.getFellow("addEmployee_emergencyContactNumber")).getValue();
            String positionId = ((Textbox) newEmployeeWindow.getFellow("emplPositionId")).getValue();

            String location = ((Label) newEmployeeWindow.getFellow("locId")).getValue();
            String socialSecurityNumber = ((Textbox) newEmployeeWindow.getFellow("addEmployee_socialSecurityNumber")).getValue();
            Comboitem gradeComboItem = ((Combobox) newEmployeeWindow.getFellow("addEmployee_grades")).getSelectedItem();
            String grade = gradeComboItem != null ? (String) gradeComboItem.getValue() : null;
            String nrcNo = ((Textbox) newEmployeeWindow.getFellow("addEmployee_NRCNo")).getValue();
            Radio empType = ((Radiogroup) newEmployeeWindow.getFellow("emp_administration")).getSelectedItem();
            String employeeType = empType != null ? empType.getValue() : null;
            String napsaNo = ((Textbox) newEmployeeWindow.getFellow("addEmployee_napsaNo")).getValue();

            Listitem cfoOrCeoType = ((Listbox) newEmployeeWindow.getFellow("cfoOrCeo")).getSelectedItem();
            String cfoOrCeo = cfoOrCeoType != null ? (String) cfoOrCeoType.getValue() : null;

            String isCfo = "N";
            String isCeo = "N";
            if (cfoOrCeo != null && "CFO".equals(cfoOrCeo)) {
                List personList = delegator.findByAnd("Person", UtilMisc.toMap("isCfo", "Y"));
                if (UtilValidate.isNotEmpty(personList)) {
                    Messagebox.show("One CFO is already there for this department", "Error", 1, null);
                    return;
                }
                isCfo = "Y";
            }
            if (cfoOrCeo != null && "CEO".equals(cfoOrCeo)) {
                List personList = delegator.findByAnd("Person", UtilMisc.toMap("isCeo", "Y"));
                if (UtilValidate.isNotEmpty(personList)) {
                    Messagebox.show("One CEO is already there for this department", "Error", 1, null);
                    return;
                }
                isCeo = "Y";
            }


            System.out.println("**********Security Group**************:" + securityGroup);
            System.out.println("*****************PositionId is :::::" + positionId);

            LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
            /*
             * d
			 * createPersonAndUserLogin service getting called
			 */
            boolean isValid = checkValidPositionId(positionId, delegator);
            if (isValid) {
                List emplPositionList = delegator.findByAnd("EmplPosition", UtilMisc.toMap("emplPositionId", positionId));
                GenericValue emplPositionGv = EntityUtil.getFirst(emplPositionList);
                String deptId = (String) emplPositionGv.getString("partyId");
                if (!(employeeDepartment.equals(deptId))) {
                    Messagebox.show("Position Id's Department and Department Selected are not same;select valid position and department" + positionId, "Error",
                            1, null);
                    return;
                }

                Map person = UtilMisc.toMap("userLogin", userLogin, "firstName", firstName, "lastName", lastName, "middleName", middleName, "gender", gender, "birthDate", birthDate, "locationId",
                        location, "partyId", employeeId, "nrcNo", nrcNo, "socialSecurityNumber", socialSecurityNumber, "grades", grade, "employeeType", employeeType,
                        "positionCategory", positionCategory, "isCfo", isCfo, "isCeo", isCeo,"napsaNo",napsaNo,"previousEmployeeId",disabledEmployeeId);

                Map context = UtilMisc.toMap("userLogin", userLogin, "firstName", firstName, "lastName", lastName, "middleName", middleName, "userLoginId",
                        userName, "currentPassword", password, "currentPasswordVerify", verifyPassword, "gender", gender, "birthDate", birthDate, "locationId",
                        location, "partyId", employeeId, "nrcNo", nrcNo, "socialSecurityNumber", socialSecurityNumber, "grades", grade, "employeeType", employeeType,
                        "positionCategory", positionCategory, "isCfo", isCfo, "isCeo", isCeo,"napsaNo",napsaNo,"previousEmployeeId",disabledEmployeeId);

                Map result = null;
                GenericValue userLoginGv = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userName), false);

                if(UtilValidate.isNotEmpty(disabledEmployeeId) && UtilValidate.isNotEmpty(userLoginGv)){
                    result = dispatcher.runSync("createPersonWithoutUserLogin", person);
                    userLoginGv.put("disabledDateTime",null);
                    userLoginGv.put("enabled","Y");
                    userLoginGv.put("partyId",employeeId);
                    userLoginGv.store();
                    GenericValue disabledPersonGv = delegator.findOne("Person",UtilMisc.toMap("partyId",disabledEmployeeId),false);
                    disabledPersonGv.put("isDisabled","N");
                    disabledPersonGv.store();
                }else{
                    result = dispatcher.runSync("createPersonAndUserLogin", context);
                }


                System.out.println("\n\n\n\n\n\nCreate Person And User Login Service Result Message\n\n" + result);

                String partyId = (String) result.get("partyId");
                Object employeeIdObject = result.get("partyId");
                System.out.println("***********PartyID***********===================:" + partyId);
                /*
				 * 
				 * createPartyRole service getting called
				 */
                String partyRoleTo = ((Textbox) newEmployeeWindow.getFellow("employeeRoleTo")).getValue();
                String partyRoleFrom = ((Textbox) newEmployeeWindow.getFellow("employeeRoleFrom")).getValue();
                Map partyRoleResult = null;
                if (securityGroup.equals("HUMANRES_MGR")) {
                    partyRoleTo = "MANAGER";
                }
                Map employmentContext = UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "roleTypeId", partyRoleTo);
                partyRoleResult = dispatcher.runSync("createPartyRole", employmentContext);
                System.out.println("\n\n\n\n\n\ncreatePartyRole Result Message\n\n" + partyRoleResult);
                Date joiningDateType = (Date) ((Datebox) newEmployeeWindow.getFellow("joiningDate")).getValue();
                Timestamp joiningDate = new Timestamp(joiningDateType.getTime());
                List<GenericValue> partyRoleGvs = delegator.findList("PartyRole", EntityCondition.makeCondition(EntityCondition.makeCondition("roleTypeId", EntityOperator.NOT_EQUAL, "_NA_"), EntityComparisonOperator.AND, EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, employeeDepartment)), null, null, null, false);
                String roleTypeIdFrom = "_NA_";
                if (UtilValidate.isNotEmpty(partyRoleGvs)) {
                    GenericValue partyRoleGv = partyRoleGvs.get(0);
                    roleTypeIdFrom = partyRoleGv.getString("roleTypeId");
                }
                Map<String, Object> employeeRelationship = UtilMisc.toMap("userLogin", userLogin, "partyIdFrom", employeeDepartment, "partyIdTo", partyId, "roleTypeIdFrom",
                        roleTypeIdFrom, "roleTypeIdTo", partyRoleTo, "partyRelationshipTypeId", "EMPLOYMENT", "fromDate", UtilDateTime.nowTimestamp());
                Map relationshipResult = dispatcher.runSync("createPartyRelationship", employeeRelationship);

                Map employmentDetails = UtilMisc.toMap("userLogin", userLogin, "roleTypeIdFrom", roleTypeIdFrom, "roleTypeIdTo", partyRoleTo,
                        "partyIdFrom", employeeDepartment, "partyIdTo", partyId, "fromDate", joiningDate);
                Map employmentResult = null;
                employmentResult = dispatcher.runSync("createEmployment", employmentDetails);
                Map<String, String> emplStatus = UtilMisc.toMap("partyId", partyId, "createdBy", userLogin.getString("partyId"), "statusId", "ESI000");

                delegator.create("EmplStatus", emplStatus);
                Map employmentAssoc = UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "emplPositionId", positionId, "fromDate", UtilDateTime.nowTimestamp());
                Map employmentAssocResult = null;
                employmentAssocResult = dispatcher.runSync("createEmplPositionFulfillment", employmentAssoc);
                EmployeePositionService.updateEmployeePositionStatus(positionId);
                Map employeeEmailContactAddress = UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "fromDate", UtilDateTime.nowTimestamp(),
                        "roleTypeId", partyRoleTo, "emailAddress", emailAddress);
                Map employeeEmailContactAddressResult = null;
                employeeEmailContactAddressResult = dispatcher.runSync("createPartyEmailAddress", employeeEmailContactAddress);
                System.out.println("\n\n\n\n\n\ncreatePartyEmailAddress Result Message\n\n" + employeeEmailContactAddressResult);

                String emailContactMecId = (String) employeeEmailContactAddressResult.get("contactMechId");
                if (UtilValidate.isNotEmpty(emailContactMecId)) {
                    GenericValue emailGv = delegator.makeValidValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId",
                            emailContactMecId, "contactMechPurposeTypeId", "PRIMARY_EMAIL", "fromDate", UtilDateTime.nowTimestamp()));
                    delegator.create(emailGv);
                }

                Map employeeTelecomNumber = UtilMisc.toMap("userLogin", userLogin, "contactNumber", telecomNumber);
                Map employeeTelecomNumberResult = null;
                employeeTelecomNumberResult = dispatcher.runSync("createTelecomNumber", employeeTelecomNumber);
                String telecomContactMechId = (String) employmentAssocResult.get("contactMechId");
				/*
				 * 
				 * createPartyTelecomNumber service getting called
				 */
                Map employeeTelecomNumberAddress = UtilMisc.toMap("userLogin", userLogin, "contactMechId", telecomContactMechId, "partyId", partyId,
                        "fromDate", UtilDateTime.nowTimestamp(), "roleTypeId", partyRoleTo, "contactNumber", telecomNumber);
                Map employeeTelecomNumberAddressResult = null;
                employeeTelecomNumberAddressResult = dispatcher.runSync("createPartyTelecomNumber", employeeTelecomNumberAddress);
                String telContactMecId = (String) employeeTelecomNumberAddressResult.get("contactMechId");
                if (UtilValidate.isNotEmpty(telContactMecId)) {
                    GenericValue telGv = delegator.makeValidValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId",
                            telContactMecId, "contactMechPurposeTypeId", "PRIMARY_PHONE", "fromDate", UtilDateTime.nowTimestamp()));
                    delegator.create(telGv);
                }

                if (UtilValidate.isNotEmpty(emergencyContactNumber)) {

                    Map employeeEmergencyContactNumberAddress = UtilMisc.toMap("userLogin", userLogin, "partyId", partyId,
                            "fromDate", UtilDateTime.nowTimestamp(), "roleTypeId", partyRoleTo, "contactNumber", emergencyContactNumber);
                    Map employeeEmergencyContactNumberAddressResult = null;
                    employeeEmergencyContactNumberAddressResult = dispatcher.runSync("createPartyTelecomNumber", employeeEmergencyContactNumberAddress);
                    String emergencyContactMecId = (String) employeeEmergencyContactNumberAddressResult.get("contactMechId");

                    if (UtilValidate.isNotEmpty(emergencyContactMecId)) {

                        GenericValue emergencyNumGv = delegator.makeValidValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId",
                                emergencyContactMecId, "contactMechPurposeTypeId", "EMERGENCY_PHONE_NUMBER", "fromDate", UtilDateTime.nowTimestamp()));
                        delegator.create(emergencyNumGv);
                    }
                }

				/*
				 * 
				 * addUserLoginToSecurityGroup service getting called
				 */
                String isRequisitionAdmin = "N";
                if (securityGroup.equals("Requisition_Admin")) {
                    isRequisitionAdmin = "Y";
                    securityGroup = "HUMANRES_MGR";
                    Map assignRole = UtilMisc.toMap("userLogin", userLogin, "roleTypeId", "Requisition_Admin", "partyId", partyId);
                    dispatcher.runSync("createPartyRole", assignRole);
                }
                Map assignSecurityGroup = UtilMisc.toMap("userLogin", userLogin, "userLoginId", userName, "groupId", securityGroup, "fromDate",
                        UtilDateTime.nowTimestamp(), "isRequisitionAdmin", isRequisitionAdmin);
                @SuppressWarnings("unused")
                Map assignSecurityGroupResult = null;
                assignSecurityGroupResult = dispatcher.runSync("addUserLoginToSecurityGroup", assignSecurityGroup);

                // TODO Remove this hard coding later
                assignSecurityGroup = UtilMisc.toMap("userLogin", userLogin, "userLoginId", userName, "groupId", "HRMS", "fromDate",
                        UtilDateTime.nowTimestamp());
                assignSecurityGroupResult = null;
                assignSecurityGroupResult = dispatcher.runSync("addUserLoginToSecurityGroup", assignSecurityGroup);
                Map preferenceMap = UtilMisc.toMap("partyId", partyId, "bankName", "_NA_", "bankAccountNumber", "_NA_", "panNumber", "_NA_", "pfAccountNumber",
                        "_NA_");
                delegator.create("Preferences", preferenceMap);
                closeRequisitionIfPosFulfilled(positionId, delegator);// Update
                // Requisition
                // Status
                updatePreviousYearLeaveWithZeroBalance(delegator,employeeId,employeeType,positionCategory,emplPositionGv.getString("emplPositionTypeId"));
                Messagebox.show("Created employee with employee id:" + partyId, "Success", 1, null);
                if (beganTransaction)
                    TransactionUtil.commit();
            } else {
                Messagebox.show("Employee position id provided not valid;Cannot create employee with position id" + positionId, "Success", 1, null);
                return;
            }
            if(UtilValidate.isNotEmpty(emailAddress)){
                Map<String, Object> resultMap = MailUtil.sendMail("createEmployeeEmail.ftl", emailAddress, UtilMisc.toMap("userName", userName, "password", password, "firstName", firstName, "lastName", lastName), dispatcher);
            }
			/*Map mailContentMap = new HashMap();
			mailContentMap.put("sendTo", emailAddress);
			mailContentMap.put("templateName", "createEmployeeEmail.ftl");

			Properties mailProp = UtilProperties.getProperties("easyHrmsMail.properties");
			mailContentMap.put("sendFrom", mailProp.get("sendFrom"));
			mailContentMap.put("subject", "Welcome To" + " " + mailProp.get("companyName"));
			mailContentMap.put("templateData", UtilMisc.toMap("userName", userName, "password", password, "firstName", firstName, "lastName", lastName,
					"teamFrom", mailProp.get("teamFrom"), "website", mailProp.get("website"), "companyName", mailProp.get("companyName"), "companyWebsite",
					mailProp.get("companyWebsite")));

			((Map) mailContentMap.get("templateData")).putAll(mailProp);

			dispatcher.runSync("sendGenericNotificationEmail", mailContentMap);*/
        } catch (Exception e) {
            try {
                Messagebox.show("Error creating Employee", "Error", 1, null);
            } catch (Throwable t) {

            }
            e.printStackTrace();
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

    @SuppressWarnings("unchecked")
    public static void createPosition(Event event) throws InterruptedException, GenericServiceException, GenericEntityException {

        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
        Component createPositionWindow = event.getTarget();
        String partyId = (String) ((Combobox) createPositionWindow.getFellow("partyId")).getSelectedItem().getValue();
        Listitem emplPositionTypeIdItem = (Listitem) ((Listbox) createPositionWindow.getFellow("emplPositionTypeId")).getSelectedItem();
        emplPositionTypeIdItem = emplPositionTypeIdItem == null ? null : emplPositionTypeIdItem;
        String emplPositionTypeId = null;
        if (emplPositionTypeIdItem != null) {
            emplPositionTypeId = (String) emplPositionTypeIdItem.getValue();
        }

        Listitem statusIdItem = (Listitem) ((Listbox) createPositionWindow.getFellow("statusId")).getSelectedItem();
        statusIdItem = statusIdItem == null ? null : statusIdItem;

        String statusId = null;
        if (statusIdItem != null) {
            statusId = (String) statusIdItem.getValue();
        }

        Timestamp estimatedFromDate = null;
        Date estimatedFromDateType = (Date) ((Datebox) createPositionWindow.getFellow("estimatedFromDate")).getValue();
        if (estimatedFromDateType != null) {
            estimatedFromDate = new Timestamp(estimatedFromDateType.getTime());
        }

        Timestamp estimatedThruDate = null;
        Date estimatedThruDateType = (Date) ((Datebox) createPositionWindow.getFellow("estimatedThruDate")).getValue();
        if (estimatedThruDateType != null) {
            estimatedThruDate = new Timestamp(estimatedThruDateType.getTime());
        }
        Timestamp actualFromDate = null;
        Date actualFromDateType = (Date) ((Datebox) createPositionWindow.getFellow("actualFromDate")).getValue();
        if (actualFromDateType != null) {
            actualFromDate = new Timestamp(actualFromDateType.getTime());
        }
        Timestamp actualThruDate = null;
        Date actualThruDateType = (Date) ((Datebox) createPositionWindow.getFellow("actualThruDate")).getValue();
        if (actualThruDateType != null) {
            actualThruDate = new Timestamp(actualThruDateType.getTime());
        }
        String budgetId = null;
        String budgetIdTemp = (String) ((Bandbox) createPositionWindow.getFellow("budgetId")).getValue();
        if (budgetIdTemp != " ") {
            budgetId = budgetIdTemp;
        }
        String budgetItemSeqId = null;
        String budgetItemSeqIdTemp = (String) ((Bandbox) createPositionWindow.getFellow("budgetItemSeqId")).getValue();
        if (budgetItemSeqIdTemp != " ") {
            budgetItemSeqId = budgetItemSeqIdTemp;
        }
        Listitem salaryFlagItem = (Listitem) ((Listbox) createPositionWindow.getFellow("salaryFlag")).getSelectedItem();
        String salaryFlag = (String) salaryFlagItem.getValue();
        Listitem exemptFlagItem = (Listitem) ((Listbox) createPositionWindow.getFellow("exemptFlag")).getSelectedItem();
        String exemptFlag = (String) exemptFlagItem.getValue();
        Listitem fullTimeFlagItem = (Listitem) ((Listbox) createPositionWindow.getFellow("fullTimeFlag")).getSelectedItem();
        String fullTimeFlag = (String) fullTimeFlagItem.getValue();
        Listitem temporaryFlagItem = (Listitem) ((Listbox) createPositionWindow.getFellow("temporaryFlag")).getSelectedItem();
        String temporaryFlag = (String) temporaryFlagItem.getValue();
        createPositionWindow.getFellow("responsibilitySubType");
        Textbox t1 = (Textbox) createPositionWindow.getFellow("responsibilitySubType");
        String additionalRes = t1.getText();
        String divisionId = (String) ((Combobox) createPositionWindow.getFellow("divisionId")).getSelectedItem().getValue();
        Map positionDetails = UtilMisc.toMap("userLogin", userLogin, "statusId", statusId, "partyId", partyId, "budgetId", budgetId, "budgetItemSeqId",
                budgetItemSeqId, "emplPositionTypeId", emplPositionTypeId, "estimatedFromDate", estimatedFromDate, "estimatedThruDate", estimatedThruDate,
                "actualFromDate", actualFromDate, "actualThruDate", actualThruDate, "salaryFlag", salaryFlag, "exemptFlag", exemptFlag, "fulltimeFlag",
                fullTimeFlag, "temporaryFlag", temporaryFlag, "additionalResponsibility", additionalRes,"divisionId",divisionId);
        LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
        Map result = null;
        result = dispatcher.runSync("createEmplPosition", positionDetails);
        String positionId = (String) result.get("emplPositionId");

        createPositionResponsibility(positionId, delegator, createPositionWindow, estimatedFromDate, estimatedThruDate);
        // Events.postEvent("onClick$searchPerCompany", createPositionWindow
        // .getPage().getFellow("searchPanel"), null);
        Messagebox.show("Employee Position " + positionId + " created successfully", "Success", 1, null);
        Component win = event.getTarget().getParent().getParent().getParent();
        // win.detach();
    }

    @SuppressWarnings("unchecked")
    public static void updatePosition(Event event) throws InterruptedException, GenericServiceException, GenericEntityException {

        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
        Component editPositionWindow = event.getTarget();
        String partyId = (String) ((Combobox) editPositionWindow.getFellow("partyId")).getSelectedItem().getValue();
        Listitem emplPositionTypeIdItem = (Listitem) ((Listbox) editPositionWindow.getFellow("emplPositionTypeId")).getSelectedItem();
        String emplPositionTypeId = (String) emplPositionTypeIdItem.getValue();
        Listitem statusIdItem = (Listitem) ((Listbox) editPositionWindow.getFellow("statusId")).getSelectedItem();
        String statusId = (String) statusIdItem.getValue();
        Timestamp estimatedFromDate = null;
        Date estimatedFromDateType = (Date) ((Datebox) editPositionWindow.getFellow("estimatedFromDate")).getValue();
        if (estimatedFromDateType != null) {
            estimatedFromDate = new Timestamp(estimatedFromDateType.getTime());
        }
        Timestamp estimatedThruDate = null;
        Date estimatedThruDateType = (Date) ((Datebox) editPositionWindow.getFellow("estimatedThruDate")).getValue();
        if (estimatedThruDateType != null) {
            estimatedThruDate = new Timestamp(estimatedThruDateType.getTime());
        }
        Timestamp actualFromDate = null;
        Date actualFromDateType = (Date) ((Datebox) editPositionWindow.getFellow("actualFromDate")).getValue();
        if (actualFromDateType != null) {
            actualFromDate = new Timestamp(actualFromDateType.getTime());
        }
        Timestamp actualThruDate = null;
        Date actualThruDateType = (Date) ((Datebox) editPositionWindow.getFellow("actualThruDate")).getValue();
        if (actualThruDateType != null) {
            actualThruDate = new Timestamp(actualThruDateType.getTime());
        }
        String budgetId = null;
        String budgetIdTemp = (String) ((Bandbox) editPositionWindow.getFellow("budgetId")).getValue();
        if (budgetIdTemp != " ") {
            budgetId = budgetIdTemp;
        }
        String budgetItemSeqId = null;
        String budgetItemSeqIdTemp = (String) ((Bandbox) editPositionWindow.getFellow("budgetItemSeqId")).getValue();
        if (budgetItemSeqIdTemp != " ") {
            budgetItemSeqId = budgetItemSeqIdTemp;
        }
        Listitem salaryFlagItem = (Listitem) ((Listbox) editPositionWindow.getFellow("salaryFlag")).getSelectedItem();
        String salaryFlag = (String) salaryFlagItem.getValue();
        Listitem exemptFlagItem = (Listitem) ((Listbox) editPositionWindow.getFellow("exemptFlag")).getSelectedItem();
        String exemptFlag = (String) exemptFlagItem.getValue();
        Listitem fullTimeFlagItem = (Listitem) ((Listbox) editPositionWindow.getFellow("fulltimeFlag")).getSelectedItem();
        String fulltimeFlag = (String) fullTimeFlagItem.getValue();
        Listitem temporaryFlagItem = (Listitem) ((Listbox) editPositionWindow.getFellow("temporaryFlag")).getSelectedItem();
        String temporaryFlag = (String) temporaryFlagItem.getValue();
        String emplPositionId = (String) ((Label) editPositionWindow.getFellow("emplPosId")).getValue();
        String responsibilitySubType = (String) ((Textbox) editPositionWindow.getFellow("responsibilitySubType")).getValue();
        Map positionDetails = UtilMisc.toMap("userLogin", userLogin, "statusId", statusId, "partyId", partyId, "budgetId", budgetId, "budgetItemSeqId",
                budgetItemSeqId, "emplPositionTypeId", emplPositionTypeId, "estimatedFromDate", estimatedFromDate, "estimatedThruDate", estimatedThruDate,
                "actualFromDate", actualFromDate, "actualThruDate", actualThruDate, "salaryFlag", salaryFlag, "exemptFlag", exemptFlag, "fulltimeFlag",
                fulltimeFlag, "temporaryFlag", temporaryFlag, "emplPositionId", emplPositionId, "additionalResponsibility", responsibilitySubType);
        LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
        dispatcher.runSync("updateEmplPosition", positionDetails);
        updatePositionResponsibility(emplPositionId, delegator, editPositionWindow, estimatedFromDate, estimatedThruDate);

        // delegator.create("EmplPositionResponsibilitySubType",createResponsibilitySubTypes);
        Messagebox.show("Position Updated", "Success", 1, null);
    }

    @SuppressWarnings("unchecked")
    public static boolean checkValidPositionId(String emplPositionId, GenericDelegator delegator) throws GenericEntityException {

        List emplPositionIdList = delegator.findByAnd("EmplPosition", UtilMisc.toMap("emplPositionId", emplPositionId));
        List emplPositionFullfillmentList = null;
        emplPositionFullfillmentList = delegator.findByAnd("EmplPositionFulfillment", UtilMisc.toMap("emplPositionId", emplPositionId));
        if (emplPositionIdList.size() > 0 && emplPositionFullfillmentList.size() <= 0) {
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static void getUserLoginId(Event event) throws GenericEntityException, GenericServiceException, InterruptedException {

        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
        Component frogotPasswordWindow = event.getTarget();
        String emailAddress = (String) ((Textbox) frogotPasswordWindow.getFellow("emailAddress")).getValue();
        List emailAddressList = delegator.findByAnd("ContactMech", UtilMisc.toMap("infoString", emailAddress));
        if (emailAddressList.size() > 0) {
            GenericValue emailAddressGv = EntityUtil.getFirst(emailAddressList);
            String contactMechId = (String) emailAddressGv.getString("contactMechId");
            List partyConatctMechList = delegator.findByAnd("PartyContactMech", UtilMisc.toMap("contactMechId", contactMechId));
            GenericValue partyConatctMechGv = EntityUtil.getFirst(partyConatctMechList);
            String partyId = (String) partyConatctMechGv.getString("partyId");

            List userLoginList = delegator.findByAnd("UserLogin", UtilMisc.toMap("partyId", partyId));

            if (UtilValidate.isEmpty(userLoginList)) {
                Messagebox.show("User Login does not exist for the given Email Address", "Error", 1, null);
                return;
            }


            GenericValue person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId));
            String firstName = person.getString("firstName");
            String lastName = person.getString("lastName");

            GenericValue userLoginGv = EntityUtil.getFirst(userLoginList);
            String userLoginId = (String) userLoginGv.getString("userLoginId");
            String password = (String) userLoginGv.getString("currentPassword");
            double randNum = Math.random();

            // multiply by 100,000 to usually make a 5 digit number
            String passwordToSend = userLoginId + ((long) (randNum * 100000));
            Map passwordMap = UtilMisc.toMap("userLoginId", userLoginId, "currentPassword", password, "newPassword", passwordToSend, "newPasswordVerify",
                    passwordToSend);
            dispatcher.runSync("updatePassword", passwordMap);

            Map mailContentMap = new HashMap();
            mailContentMap.put("sendTo", emailAddress);
            mailContentMap.put("subject", "Request For Password");
            mailContentMap.put("templateName", "forgotPassword.ftl");

            Properties mailProp = UtilProperties.getProperties("easyHrmsMail.properties");
            mailContentMap.put("sendFrom", mailProp.get("sendFrom"));
            mailContentMap.put(
                    "templateData",
                    UtilMisc.toMap("userName", userLoginId, "password", passwordToSend, "firstName", firstName, "lastName", lastName, "teamFrom",
                            mailProp.get("teamFrom"), "website", mailProp.get("website")));

            ((Map) mailContentMap.get("templateData")).putAll(mailProp);

            dispatcher.runSync("sendGenericNotificationEmail", mailContentMap);
            Messagebox.show("Password Has been Sent To Your Mail", "Success", Messagebox.OK, "", 1, new EventListener() {
                public void onEvent(Event evt) throws GenericEntityException {
                    Executions.getCurrent().sendRedirect("/control/main");
                }
            });
        } else {
            Messagebox.show("Email Address does not exist", "Error", 1, null);
            return;
        }
    }

    @SuppressWarnings({"unchecked"})
    public static void selectLocation(Comboitem ci, Event event) throws GenericEntityException {
        Component newEmployeeWindow = event.getTarget();
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        String deptId = (String) ci.getValue();
        GenericValue departmentPositionList = delegator.findByPrimaryKey("DepartmentPosition", UtilMisc.toMap("departmentId", deptId));
        List locationNameList = null;
        List<String> locationName = new ArrayList<String>();
        locationName.add(0, null);
        String locationId = (String) departmentPositionList.getString("locationId");
        locationNameList = delegator.findByAnd("Location", UtilMisc.toMap("locationId", locationId));
        GenericValue locationNameGv = EntityUtil.getFirst(locationNameList);
        String locationNames = (String) locationNameGv.get("locationName");

        Label locId = (Label) newEmployeeWindow.getFellow("locId");
        locId.setValue(locationId);

        Textbox locName = (Textbox) newEmployeeWindow.getFellow("locName");
        locName.setValue(locationNames);

        EntityFindOptions options = new EntityFindOptions();
        options.setDistinct(true);
        List positionTypeList = delegator.findList("EmplPosition",
                EntityCondition.makeCondition(UtilMisc.toMap("partyId", deptId, "statusId", "EMPL_POS_ACTIVE")),
                UtilMisc.toSet("emplPositionTypeId", "partyId"), null, options, false);
        Combobox positionTypeBox = (Combobox) newEmployeeWindow.getFellow("emplPositionType");
        positionTypeBox.setModel(new SimpleListModel(positionTypeList));
    }

    @SuppressWarnings("unchecked")
    public static void closeRequisitionIfPosFulfilled(String emplPositionId, GenericDelegator delegator) throws GenericEntityException {

        List getRequisitionIdList = delegator.findByAnd("EmplPosition", UtilMisc.toMap("emplPositionId", emplPositionId));
        GenericValue getRequisitionIdGv = null;
        getRequisitionIdGv = EntityUtil.getFirst(getRequisitionIdList);
        String requisitionId = null;
        if (getRequisitionIdGv != null) {
            requisitionId = (String) getRequisitionIdGv.getString("requisitionId");
        }
        List requisitionDetailsList = null;
        if (requisitionId != null) {
            requisitionDetailsList = delegator.findByAnd("EmployeeRequisition", UtilMisc.toMap("requisitionId", requisitionId));
            GenericValue requisitionDetailsGv = EntityUtil.getFirst(requisitionDetailsList);
            @SuppressWarnings("unused")
            Long numberOfPosition = (Long) requisitionDetailsGv.getLong("numberOfPosition");
            Long balancePositionToFulfill = (Long) requisitionDetailsGv.getLong("balancePositionToFulfill");
            balancePositionToFulfill = balancePositionToFulfill - 1;
            Map updateRequistionMap = UtilMisc.toMap("requisitionId", requisitionId, "balancePositionToFulfill", balancePositionToFulfill);
            requisitionDetailsGv.putAll(updateRequistionMap);
            requisitionDetailsGv.store();
            if (balancePositionToFulfill == 0) {
                Map updateRequistionStatus = UtilMisc.toMap("requisitionId", requisitionId, "statusId", "Closed");
                requisitionDetailsGv.putAll(updateRequistionStatus);
                requisitionDetailsGv.store();
            }
        }
    }

    public static void setActivePosition(Comboitem ci, Event event) {
        Component newEmployeeWindow = event.getTarget();
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        GenericValue gv = (GenericValue) ci.getValue();
        String deptId = gv.getString("partyId");
        String emplPositionTypeId = gv.getString("emplPositionTypeId");

        GenericValue positionGV = null;
        try {
            positionGV = EntityUtil.getFirst(delegator.findList("EmplPosition",
                    EntityCondition.makeCondition(UtilMisc.toMap("partyId", deptId, "emplPositionTypeId", emplPositionTypeId, "statusId", "EMPL_POS_ACTIVE")),
                    null, null, null, false));
        } catch (GenericEntityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Textbox positionId = (Textbox) newEmployeeWindow.getFellow("emplPositionId");
        if (positionGV != null) {
            positionId.setValue(positionGV.getString("emplPositionId"));
        }

    }

    public static void updatePositionResponsibility(String emplPositionId, GenericDelegator delegator, Component comp, Timestamp fromDate, Timestamp thruDate)
            throws GenericEntityException, GenericServiceException {
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
        if (UtilValidate.isNotEmpty(emplPositionId))
            delegator.removeByAnd("EmplPositionResponsibility", UtilMisc.toMap("emplPositionId", emplPositionId));
        String d_positionResponsibility = null;
        Set lstitem = new HashSet();
        lstitem = (Set) ((Listbox) comp.getFellow("responsibilityTypeId")).getSelectedItems();
        Iterator it = lstitem.iterator();
        Object item = null;

        while (it.hasNext()) {
            item = it.next();
            Listitem selectedItem = (Listitem) item;
            String responsibilityType = (String) selectedItem.getValue();
            String responsibilitySubType = (String) ((Textbox) comp.getFellow("responsibilitySubType")).getValue();
            Map positionResponsibility = UtilMisc.toMap("emplPositionId", emplPositionId, "responsibilityTypeId", responsibilityType, "fromDate", fromDate,
                    "thruDate", thruDate);
            Map positionaddResponsibility = UtilMisc.toMap("emplPositionId", emplPositionId, "additionalResponsibility", responsibilitySubType);

            GenericValue reponsibilityGv = delegator.makeValue("EmplPositionResponsibility", positionResponsibility);
            GenericValue addresponsibilityGv = delegator.makeValue("EmplPosition", positionaddResponsibility);
            try {
                delegator.createOrStore(reponsibilityGv);
                delegator.createOrStore(addresponsibilityGv);

            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        }

    }

    public static void createPositionResponsibility(String emplPositionId, GenericDelegator delegator, Component comp, Timestamp fromDate, Timestamp thruDate) {
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
        Set lstitem = new HashSet();
        lstitem = (Set) ((Listbox) comp.getFellow("responsibilityTypeId")).getSelectedItems();
        Iterator it = lstitem.iterator();
        Object item = null;

        while (it.hasNext()) {
            item = it.next();
            Listitem selectedItem = (Listitem) item;
            String responsibilityType = (String) selectedItem.getValue();
            Map positionResponsibility = UtilMisc.toMap("emplPositionId", emplPositionId, "responsibilityTypeId", responsibilityType, "fromDate", fromDate,
                    "thruDate", thruDate);
            GenericValue reponsibilityGv = delegator.makeValue("EmplPositionResponsibility", positionResponsibility);
            try {
                delegator.createOrStore(reponsibilityGv);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            String responsibilitySubType = null;
            responsibilitySubType = (String) ((Textbox) comp.getFellow("responsibilitySubType")).getValue();
            if (responsibilitySubType != null) {

				/*
				 * delegator.create("EmplPositionResponsibilitySubType",
				 * UtilMisc.toMap("emplPositionId", emplPositionId,
				 * "responsibilityTypeId", responsibilityType,
				 * "responsibilitySubType", responsibilitySubType));
				 */

            }
        }

    }

    static Properties pathProperties = UtilProperties.getProperties("easyHrmsPath.properties");
    private static final String UPLOADPATH = (String) pathProperties.get("companyLogoImagePath");

    @SuppressWarnings("unused")
    public static void employeeUploadResume(UploadEvent uploadEvent, String partyId) throws GenericEntityException, IOException, InterruptedException {
        Component comp = uploadEvent.getTarget();
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        Media uploadedFile = uploadEvent.getMedia();
        String fileName = uploadedFile == null ? null : (uploadedFile.getName());
        if (fileName != null) {
            String actualFileName = null;
            String extention = fileName.substring(fileName.lastIndexOf('.'));
            if (fileName != null) {
                if (extention.equalsIgnoreCase(".doc") || extention.equalsIgnoreCase(".docx") || extention.equalsIgnoreCase(".pdf")) {
                    if (StringUtils.isNotEmpty(System.getProperty("ofbiz.home"))) {
                        actualFileName = ((String) System.getProperty("ofbiz.home")).concat("/hot-deploy/hrms/webapp/hrms/images/").concat(
                                uploadedFile.getName());
                    } else {
                        actualFileName = UPLOADPATH + uploadedFile.getName();
                    }
                    File diskFile = new File(actualFileName);
                    if (!diskFile.exists())
                        diskFile.createNewFile();
                    FileOutputStream outputStream = new FileOutputStream(diskFile);
                    IOUtils.copyLarge(uploadedFile.getStreamData(), outputStream);
                    delegator.createOrStore(delegator.makeValue("PartyResume",
                            UtilMisc.toMap("resumeId", delegator.getNextSeqId("PartyResume"), "partyId", partyId, "resumeText", fileName)));
                    Messagebox.show("Resume Uploaded Successfully", "Success", 1, null);
                } else {
                    Messagebox.show("Please Provide A Pdf or Doc File", "Error", 1, null);
                }
            }
        }
        Events.postEvent("onClick", uploadEvent.getTarget().getFellow("resumeWin").getParent().getFellow("appendWindow").getParent().getFellow("profileWindow")
                .getFellow("resume"), null);
    }

    public static void deleteResume(final String fileName, final String partyId, final String resumeId, final Event event) throws GenericEntityException,
            InterruptedException {
        Messagebox.show("Do you want to delete this Record?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {

            public void onEvent(Event event1) throws Exception {
                if ("onYes".equals(event1.getName())) {
                    String actualFileName = null;
                    if (fileName != null) {
                        if (StringUtils.isNotEmpty(System.getProperty("ofbiz.home"))) {
                            actualFileName = ((String) System.getProperty("ofbiz.home")).concat("/hot-deploy/hrms/webapp/hrms/images/").concat(fileName);
                        } else {
                            actualFileName = UPLOADPATH + fileName;
                        }
                        File diskFile = new File(actualFileName);
                        if (diskFile.exists()) {
                            diskFile.delete();
                        }
                        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
                        int affect = delegator.removeByAnd("PartyResume", UtilMisc.toMap("resumeId", resumeId, "partyId", partyId, "resumeText", fileName));
                        if (affect > 0) {
                            Messagebox.show("Resume Deleted Successfully", "Success", 1, null);
                        }

                    }
                }
                Events.postEvent(
                        "onClick",
                        event.getTarget().getFellow("resumeWin").getParent().getFellow("appendWindow").getParent().getFellow("profileWindow")
                                .getFellow("resume"), null);
            }
        });
    }
	/*
	 * public static String getResponsibilityTypeId(Component comp) { Set
	 * lstitem = new HashSet(); lstitem = (Set) ((Listbox)
	 * comp.getFellow("responsibilityTypeId")) .getSelectedItems(); Iterator it
	 * = lstitem.iterator(); Object item = null; String responsibilityType =
	 * null; while (it.hasNext()) { item = it.next(); Listitem selectedItem =
	 * (Listitem) item; responsibilityType = (String) selectedItem.getValue(); }
	 * return responsibilityType;
	 * 
	 * }
	 */


    /*
    * pre populate the details of the employee, when user selects the employee from
    * disabled employee list. This method should pre populate all the details except
    * department details.
    *
    */
    public static void prePopulateEmployeeDetails(Event event) throws GenericEntityException {
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();
        Component newEmployeeWindow = event.getTarget();
            //((Textbox) newEmployeeWindow.getFellow("addEmployee_socialSecurityNumber")).setValue(null);
            ((Textbox) newEmployeeWindow.getFellow("addEmployee_middleName")).setValue(null);
            //((Textbox) newEmployeeWindow.getFellow("addEmployee_lastName")).setValue(null);
            //((Datebox) newEmployeeWindow.getFellow("joiningDate")).setValue(null);
            //((Datebox) newEmployeeWindow.getFellow("addEmployee_birthDate")).setValue(null);
            //((Listbox) newEmployeeWindow.getFellow("addEmployee_gender")).setSelectedIndex(0);
            //((Textbox) newEmployeeWindow.getFellow("addEmployee_NRCNo")).setValue(null);
            ((Textbox) newEmployeeWindow.getFellow("addEmployee_napsaNo")).setValue(null);
            //((Combobox) newEmployeeWindow.getFellow("addEmployee_grades")).setValue(null);
            ((Textbox) newEmployeeWindow.getFellow("addEmployee_emailAddress")).setValue(null);
            ((Textbox) newEmployeeWindow.getFellow("addEmployee_contactNumber")).setValue(null);
            ((Textbox) newEmployeeWindow.getFellow("addEmployee_emergencyContactNumber")).setValue(null);

        Comboitem disabledEmployeeIdType = ((Combobox) newEmployeeWindow.getFellow("disabledEmplId")).getSelectedItem();
        String disabledEmployeeId = null;
        if(null!=disabledEmployeeIdType){
            disabledEmployeeId = (String) disabledEmployeeIdType.getValue();
            GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", disabledEmployeeId), false);
            List employmentList = delegator.findByAnd("Employment",UtilMisc.toMap("partyIdTo",disabledEmployeeId),null,false);
            GenericValue employment = EntityUtil.getFirst(employmentList);

            ((Textbox) newEmployeeWindow.getFellow("addEmployee_firstName")).setValue(person.getString("firstName"));
            if(UtilValidate.isNotEmpty(person.getString("middleName"))){
                ((Textbox) newEmployeeWindow.getFellow("addEmployee_middleName")).setValue(person.getString("middleName"));
            }
            if(UtilValidate.isNotEmpty(person.getString("lastName"))){
                ((Textbox) newEmployeeWindow.getFellow("addEmployee_lastName")).setValue(person.getString("lastName"));
            }
            if(UtilValidate.isNotEmpty(employment.getTimestamp("fromDate"))){
                ((Datebox) newEmployeeWindow.getFellow("joiningDate")).setValue(employment.getTimestamp("fromDate"));
            }
            if(UtilValidate.isNotEmpty(person.getDate("birthDate"))){
                ((Datebox) newEmployeeWindow.getFellow("addEmployee_birthDate")).setValue(person.getDate("birthDate"));
            }
            String gender = person.getString("gender");
            if(gender.equals("M")){
                ((Listbox) newEmployeeWindow.getFellow("addEmployee_gender")).setSelectedIndex(1);
            }else{
                ((Listbox) newEmployeeWindow.getFellow("addEmployee_gender")).setSelectedIndex(2);
            }
            if(UtilValidate.isNotEmpty(person.getString("nrcNo"))){
                ((Textbox) newEmployeeWindow.getFellow("addEmployee_NRCNo")).setValue(person.getString("nrcNo"));
            }
            if(UtilValidate.isNotEmpty(person.getString("socialSecurityNumber")))
            {
                ((Textbox) newEmployeeWindow.getFellow("addEmployee_socialSecurityNumber")).setValue(person.getString("socialSecurityNumber"));
            }
            String napsa = person.getString("napsaNo");
            if(UtilValidate.isNotEmpty(napsa) && !napsa.equals("N")){
                ((Textbox) newEmployeeWindow.getFellow("addEmployee_napsaNo")).setValue(napsa);
            }
            if(UtilValidate.isNotEmpty(person.getString("grades"))){
                org.zkoss.zul.Comboitem comboitem = new org.zkoss.zul.Comboitem();
                comboitem.setValue(person.getString("grades"));
                ((Combobox) newEmployeeWindow.getFellow("addEmployee_grades")).setValue(person.getString("grades"));
            }
            Map<String,String> contactMechMap = HrmsUtil.getPartyContactMechValueMaps(delegator, disabledEmployeeId);
            String email = contactMechMap.get("email");
            String emergencyPhoneNumber = contactMechMap.get("emergencyPhoneNumber");
            String telecomNumber = contactMechMap.get("phoneNumber");
            if(UtilValidate.isNotEmpty(email)){
                ((Textbox) newEmployeeWindow.getFellow("addEmployee_emailAddress")).setValue(email);
            }
            if(UtilValidate.isNotEmpty(telecomNumber)){
                ((Textbox) newEmployeeWindow.getFellow("addEmployee_contactNumber")).setValue(telecomNumber);
            }
            if (UtilValidate.isNotEmpty(emergencyPhoneNumber)){
                ((Textbox) newEmployeeWindow.getFellow("addEmployee_emergencyContactNumber")).setValue(emergencyPhoneNumber);
            }
        }else{
            Events.postEvent("onClick", event.getTarget(), null);
        }
    }

    public static void updatePreviousYearLeaveWithZeroBalance(Delegator delegator,String partyId,String employeeType,String positionCategory,String employeePositionTypeId) throws GenericEntityException {
        Map previousYearAndCurrentYearStartAndEndDate = HrmsUtil.getCurrentAndPreviousYearStartAndEndDate();
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
