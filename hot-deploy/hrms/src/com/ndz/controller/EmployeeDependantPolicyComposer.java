package com.ndz.controller;

import com.ndz.zkoss.HrmsUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Messagebox;

import java.lang.String;
import java.sql.Timestamp;
import java.util.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: NthDimenzion
 * @since 1.0
 */
public class EmployeeDependantPolicyComposer extends GenericForwardComposer {

    private Delegator delegator;

    private List<GenericValue> employeeDependants;

    private String employeeId;

    private Map<String, Object> insuranceDetail;

    private String successMessage = "Insurance created for dependent successfully";

    private String policyId;

    private boolean newRecord;

    private String windowTitle = "Add Insurance Detail";

    private GenericValue dependantPolicyGv;

    @Override
    public ComponentInfo doBeforeCompose(Page page, Component parent, ComponentInfo compInfo) {
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        this.delegator = userLogin.getDelegator();
        employeeId = (String) Executions.getCurrent().getArg().get("partyId");
        java.sql.Date nowDate = new java.sql.Date(UtilDateTime.getDayEnd(new Timestamp(new Date().getTime())).getTime());
        List<GenericValue> allEmployeeDependants = getAllActiveDependant();
        List<GenericValue> allActiveEmployeePolicy = getAllActiveEmployeePolicyDetail();
        employeeDependants = filterEmployeeDependantByEmployeePolicy(allEmployeeDependants, allActiveEmployeePolicy);
        return super.doBeforeCompose(page, parent, compInfo);

    }

    @Override
    public void doAfterCompose(Component component) throws Exception {
        String policyId = (String) Executions.getCurrent().getArg().get("policyId");
        if (UtilValidate.isEmpty(policyId)) {
            this.policyId = delegator.getNextSeqId("EmployeePolicyDetail");
            newRecord = true;
            insuranceDetail = buildNewInsuranceGv();
            return;
        }
        successMessage = "Insurance detail updated for dependent successfully";
        this.policyId = policyId;
        windowTitle = "Edit Insurance Detail";
        dependantPolicyGv = delegator.findByPrimaryKey("EmployeePolicyDetail", UtilMisc.toMap("policyId", policyId));
        insuranceDetail = populatePolicyDetail(dependantPolicyGv);
    }

    public void saveOrUpdateDependantInsurance(final Component medicalInsuranceLink, final Component dependantInsuranceWindow) throws GenericEntityException, InterruptedException {
        if (newRecord) {
            insuranceDetail.remove("memberName");
            insuranceDetail.remove("age");
            insuranceDetail.remove("gender");
            insuranceDetail.remove("validThruInString");
            insuranceDetail.put("validFrom", new java.sql.Date(((Date)insuranceDetail.get("validFrom")).getTime()));
            delegator.create("EmployeePolicyDetail", insuranceDetail);
        } else {
            dependantPolicyGv.put("memberId", insuranceDetail.get("memberId"));
            dependantPolicyGv.put("insuranceId", insuranceDetail.get("insuranceId"));
            dependantPolicyGv.put("insuranceCompany", insuranceDetail.get("insuranceCompany"));
            dependantPolicyGv.put("policyNumber", insuranceDetail.get("policyNumber"));
            dependantPolicyGv.put("validFrom", new java.sql.Date(((Date)insuranceDetail.get("validFrom")).getTime()));
            delegator.store(dependantPolicyGv);
        }
        Messagebox.show(successMessage, "Success", Messagebox.OK, Messagebox.NONE, new EventListener() {
            public void onEvent(Event evt) {
                if ("onOK".equals(evt.getName())) {
                    Events.postEvent(Events.ON_CLICK, medicalInsuranceLink, null);
                    dependantInsuranceWindow.detach();

                }
            }
        });
    }

    public void populateMemberDetail(String dependantId) throws GenericEntityException {
        GenericValue dependantGv = delegator.findByPrimaryKey("EmployeeDependant", UtilMisc.toMap("dependantId", dependantId));
        insuranceDetail.put("memberName", dependantGv.getString("dependantName"));
        insuranceDetail.put("age", HrmsUtil.getAge(dependantGv.getDate("dateOfBirth")));
        insuranceDetail.put("gender", dependantGv.getString("gender"));
        insuranceDetail.put("dependantId", dependantId);
    }

    private Map<String, Object> buildNewInsuranceGv() throws GenericEntityException {
        GenericValue partyGroupGv = delegator.findByPrimaryKey("PartyGroup", UtilMisc.toMap("partyId", "Company"));
        String companyName = partyGroupGv.getString("groupName");
        Map<String, Object> newInsurance = new HashMap<String, Object>();
        GenericValue primaryPolicy = getActivePrimaryPolicy();
        newInsurance.put("policyId", policyId);
        newInsurance.put("employeeId", employeeId);
        newInsurance.put("insuranceCompany", primaryPolicy.getString("insuranceCompany"));
        newInsurance.put("memberName", "");
        newInsurance.put("age", "");
        newInsurance.put("gender", "");
        newInsurance.put("policyNumber", primaryPolicy.getString("policyNumber"));
        newInsurance.put("policyHolder", companyName);
        newInsurance.put("primaryBenificiary", HrmsUtil.getFullName(delegator, employeeId));
        newInsurance.put("validFrom", new java.sql.Date(((Date)primaryPolicy.getDate("validFrom")).getTime()));
        newInsurance.put("validThru", primaryPolicy.getDate("validThru"));
        newInsurance.put("validThruInString", UtilDateTime.formatDate(primaryPolicy.getDate("validThru")));
        return newInsurance;
    }

    public Map<String, Object> populatePolicyDetail(GenericValue dependantPolicyGv) throws GenericEntityException {
        Map<String, Object> policyDetail = new HashMap<String, Object>();
        String dependantId = dependantPolicyGv.getString("dependantId");
        GenericValue dependantGv = delegator.findByPrimaryKey("EmployeeDependant", UtilMisc.toMap("dependantId", dependantId));
        GenericValue partyGroupGv = delegator.findByPrimaryKey("PartyGroup", UtilMisc.toMap("partyId", "Company"));
        String companyName = partyGroupGv.getString("groupName");

        policyDetail.put("policyId", policyId);
        policyDetail.put("employeeId", employeeId);
        policyDetail.put("insuranceCompany", dependantPolicyGv.getString("insuranceCompany"));
        policyDetail.put("memberName", dependantGv.getString("dependantName"));
        policyDetail.put("age", HrmsUtil.getAge(dependantGv.getDate("dateOfBirth")));
        policyDetail.put("gender", dependantGv.getString("gender"));
        policyDetail.put("policyNumber", dependantPolicyGv.getString("policyNumber"));
        policyDetail.put("policyHolder", dependantPolicyGv.getString("policyHolder"));
        policyDetail.put("memberId", dependantPolicyGv.getString("memberId"));
        policyDetail.put("insuranceId", dependantPolicyGv.getString("insuranceId"));
        policyDetail.put("primaryBenificiary", HrmsUtil.getFullName(delegator, employeeId));
        policyDetail.put("validFrom", dependantPolicyGv.getDate("validFrom"));
        policyDetail.put("validThru", dependantPolicyGv.getDate("validThru"));
        policyDetail.put("validThruInString", UtilDateTime.formatDate(dependantPolicyGv.getDate("validThru")));

        String relationshipTypeId = dependantGv.getString("relationshipType");
        GenericValue relationshipTypeGv = delegator.findByPrimaryKey("PartyRelationshipType", UtilMisc.toMap("partyRelationshipTypeId", relationshipTypeId));
        policyDetail.put("relationship", relationshipTypeGv.getString("partyRelationshipName"));
        return policyDetail;
    }


    private GenericValue getActivePrimaryPolicy() {
        java.sql.Date nowDate = new java.sql.Date(UtilDateTime.getDayEnd(new Timestamp(new Date().getTime())).getTime());
        EntityCondition dateCondition = EntityCondition.makeCondition("validThru", EntityComparisonOperator.GREATER_THAN_EQUAL_TO, nowDate);
        EntityCondition statusCondition = EntityCondition.makeCondition("active", EntityComparisonOperator.EQUALS, null);
        EntityCondition employeeCondition = EntityCondition.makeCondition("employeeId", EntityComparisonOperator.EQUALS, employeeId);
        EntityCondition dependantCondition = EntityCondition.makeCondition("dependantId", EntityComparisonOperator.EQUALS, null);
        List<EntityCondition> conditionList = java.util.Arrays.asList(statusCondition, employeeCondition, dependantCondition);
        EntityCondition ec = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
        List<GenericValue> policyDetailGvs = null;
        try {
            policyDetailGvs = delegator.findList("EmployeePolicyDetail", ec, null, null, null, false);
        } catch (GenericEntityException e) {

        }
        return UtilValidate.isNotEmpty(policyDetailGvs) ? policyDetailGvs.get(0) : null;
    }


    private List<GenericValue> filterEmployeeDependantByEmployeePolicy(List<GenericValue> employeeDependants, List<GenericValue> employeePolicyList) {
        if (UtilValidate.isEmpty(employeePolicyList) || employeePolicyList.size() == 1) {
            return employeeDependants;
        }
        List<String> dependantIdList = new ArrayList<String>();
        for (GenericValue gv : employeePolicyList) {
            if (UtilValidate.isNotEmpty(gv.getString("dependantId"))) {
                dependantIdList.add(gv.getString("dependantId"));
            }
        }
        java.sql.Date nowDate = new java.sql.Date(UtilDateTime.getDayEnd(new Timestamp(new Date().getTime())).getTime());
        List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
        EntityCondition dateCondition1 = EntityCondition.makeCondition("thruDate", EntityComparisonOperator.GREATER_THAN_EQUAL_TO, nowDate);
        EntityCondition dateCondition2 = EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null);
        EntityCondition dateCondition = EntityCondition.makeCondition(dateCondition1, EntityOperator.OR, dateCondition2);
        EntityCondition employeeCondition = EntityCondition.makeCondition("employeeId", EntityComparisonOperator.EQUALS, employeeId);
        EntityCondition relationshipCondition = EntityCondition.makeCondition("parentTypeId", EntityComparisonOperator.EQUALS, "FAMILY");
        EntityCondition dependantCondition = EntityCondition.makeCondition("dependantId", EntityComparisonOperator.NOT_IN, dependantIdList);
        conditionList.add(dateCondition);
        conditionList.add(employeeCondition);
        conditionList.add(relationshipCondition);
        conditionList.add(dependantCondition);
        EntityCondition ec = EntityCondition.makeCondition(conditionList, EntityComparisonOperator.AND);
        List<GenericValue> dependantGvs = null;
        try {
            dependantGvs = delegator.findList("EmployeeDependantPartyRelationshipTypeView", ec, null, null, null, false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return dependantGvs;

       /* if (UtilValidate.isEmpty(employeePolicyList) || employeePolicyList.size() == 1) {
            return employeeDependants;
        }
        List<GenericValue> filteredEmployeeDependants = new ArrayList<GenericValue>();
        for (GenericValue employeeDependant : employeeDependants) {
            for (GenericValue employeePolicyGv : employeePolicyList) {
                String dependantId = employeePolicyGv.getString("dependantId");
                if (!employeeDependant.getString("dependantId").equals(dependantId) && UtilValidate.isNotEmpty(dependantId)) {
                    filteredEmployeeDependants.add(employeeDependant);
                    break;
                } else if (UtilValidate.isNotEmpty(dependantId) && dependantId.equals(employeeDependant.getString("dependantId"))) {
                    break;
                }
            }
        }
        return filteredEmployeeDependants;*/
    }

    private List<GenericValue> getAllActiveEmployeePolicyDetail() {
        EntityCondition statusCondition = EntityCondition.makeCondition("active", EntityComparisonOperator.EQUALS, null);
        java.sql.Date nowDate = new java.sql.Date(UtilDateTime.getDayEnd(new Timestamp(new Date().getTime())).getTime());
        EntityCondition dateCondition = EntityCondition.makeCondition("validThru", EntityComparisonOperator.GREATER_THAN_EQUAL_TO, nowDate);
        EntityCondition employeeCondition = EntityCondition.makeCondition("employeeId", EntityComparisonOperator.EQUALS, employeeId);
        EntityCondition ec = EntityCondition.makeCondition(statusCondition, EntityComparisonOperator.AND, employeeCondition);
        List<GenericValue> insuranceDetailGvs = null;
        try {
            insuranceDetailGvs = delegator.findList("EmployeePolicyDetail", ec, null, null, null, false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return insuranceDetailGvs;
    }

    private List<GenericValue> getAllActiveDependant() {
        java.sql.Date nowDate = new java.sql.Date(UtilDateTime.getDayEnd(new Timestamp(new Date().getTime())).getTime());
        List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
        EntityCondition dateCondition1 = EntityCondition.makeCondition("thruDate", EntityComparisonOperator.GREATER_THAN_EQUAL_TO, nowDate);
        EntityCondition dateCondition2 = EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null);
        EntityCondition dateCondition = EntityCondition.makeCondition(dateCondition1, EntityOperator.OR, dateCondition2);
        EntityCondition employeeCondition = EntityCondition.makeCondition("employeeId", EntityComparisonOperator.EQUALS, employeeId);
        EntityCondition relationshipCondition = EntityCondition.makeCondition("parentTypeId", EntityComparisonOperator.EQUALS, "FAMILY");
        conditionList.add(dateCondition);
        conditionList.add(employeeCondition);
        conditionList.add(relationshipCondition);
        EntityCondition ec = EntityCondition.makeCondition(conditionList, EntityComparisonOperator.AND);
        List<GenericValue> dependantGvs = null;
        try {
            dependantGvs = delegator.findList("EmployeeDependantPartyRelationshipTypeView", ec, null, null, null, false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return dependantGvs;
    }

    public List<GenericValue> getEmployeeDependants() {
        return employeeDependants;
    }

    public Map<String, Object> getInsuranceDetail() {
        return insuranceDetail;
    }


    public void setInsuranceDetail(Map<String, Object> insuranceDetail) {
        this.insuranceDetail = insuranceDetail;
    }

    public boolean isNewRecord() {
        return newRecord;
    }

    public String getWindowTitle() {
        return windowTitle;
    }
}
