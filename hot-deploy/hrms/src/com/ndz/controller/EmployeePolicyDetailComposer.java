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
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Messagebox;

import java.sql.Timestamp;
import java.util.*;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public final class EmployeePolicyDetailComposer extends GenericForwardComposer {

    private Delegator delegator;

    private Map<String, Object> insuranceDetail;

    private String employeeId;

    private final String NULL_VALUE = null;

    private GenericValue activeInsuranceDetail;

    private boolean editMode;

    private String successMessage = "Medical insurance detail created for employee successfully";

    @Override
    public void doAfterCompose(Component component) throws Exception {
        super.doAfterCompose(component);
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        this.delegator = userLogin.getDelegator();
        employeeId = (String) Executions.getCurrent().getArg().get("partyId");
        String policyId = (String) Executions.getCurrent().getArg().get("policyId");
        editMode = UtilValidate.isNotEmpty(policyId);
        GenericValue policyGv = null;
        if (UtilValidate.isNotEmpty(policyId)) {
            successMessage = "Medical insurance detail updated for employee successfully";
            policyGv = delegator.findByPrimaryKey("EmployeePolicyDetail", UtilMisc.toMap("policyId", policyId));
        }
        insuranceDetail = buildInitialMap(policyGv);
        activeInsuranceDetail = getActivePrimaryPolicy();
    }

    private GenericValue getActivePrimaryPolicy() {
        java.sql.Date nowDate = new java.sql.Date(UtilDateTime.getDayEnd(new Timestamp(new Date().getTime())).getTime());
        //EntityCondition dateCondition = EntityCondition.makeCondition("validThru", EntityComparisonOperator.GREATER_THAN_EQUAL_TO, nowDate);
        EntityCondition employeeCondition = EntityCondition.makeCondition("employeeId", EntityComparisonOperator.EQUALS, employeeId);
        EntityCondition dependantCondition = EntityCondition.makeCondition("dependantId", EntityComparisonOperator.EQUALS, null);
        List<EntityCondition> conditionList = Arrays.asList(employeeCondition, dependantCondition);
        EntityCondition ec = EntityCondition.makeCondition(conditionList, EntityOperator.AND);
        List<GenericValue> policyDetailGvs = null;
        try {
            policyDetailGvs = delegator.findList("EmployeePolicyDetail", ec, null, Arrays.asList("policyId"), null, false);
        } catch (GenericEntityException e) {

        }
        return UtilValidate.isNotEmpty(policyDetailGvs) ? policyDetailGvs.get((policyDetailGvs.size() - 1)) : null;
    }

    private Map<String, Object> buildInitialMap(GenericValue policyGv) throws GenericEntityException {
        java.util.Map<String, Object> employeeInsuranceDetail = new HashMap<String, Object>();
        GenericValue partyGroupGv = delegator.findByPrimaryKey("PartyGroup", UtilMisc.toMap("partyId", "Company"));
        GenericValue personGv = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", employeeId));
        String companyName = partyGroupGv.getString("groupName");
        employeeInsuranceDetail.put("policyId", policyGv == null ? delegator.getNextSeqId("EmployeePolicyDetail") : policyGv.getString("policyId"));
        employeeInsuranceDetail.put("employeeId", employeeId);
        employeeInsuranceDetail.put("memberId", policyGv == null ? NULL_VALUE : policyGv.getString("memberId"));
        employeeInsuranceDetail.put("insuranceId", policyGv == null ? NULL_VALUE : policyGv.getString("insuranceId"));
        employeeInsuranceDetail.put("insuranceCompany", policyGv == null ? NULL_VALUE : policyGv.getString("insuranceCompany"));
        employeeInsuranceDetail.put("memberName", HrmsUtil.getFullName(delegator, employeeId));
        employeeInsuranceDetail.put("age",personGv.getDate("birthDate")==null? NULL_VALUE: HrmsUtil.getAge(personGv.getDate("birthDate")));
        employeeInsuranceDetail.put("gender", personGv.getDate("birthDate")==null? NULL_VALUE:("M".equals(personGv.getString("gender")) ? "Male" : "Female"));
        employeeInsuranceDetail.put("policyNumber", policyGv == null ? NULL_VALUE : policyGv.getString("policyNumber"));
        employeeInsuranceDetail.put("policyHolder", companyName);
        employeeInsuranceDetail.put("primaryBenificiary", HrmsUtil.getFullName(delegator, employeeId));
        employeeInsuranceDetail.put("validFrom", policyGv == null ? NULL_VALUE : policyGv.getDate("validFrom"));
        employeeInsuranceDetail.put("validThru", policyGv == null ? NULL_VALUE : policyGv.getDate("validThru"));
        return employeeInsuranceDetail;
    }

    public void saveOrUpdatePolicyForEmployee(final Component createMedicalInsuranceBtn, final Component createMedicalInsuranceWindow) throws InterruptedException, GenericEntityException {
    	 if(org.ofbiz.base.util.UtilValidate.isEmpty(insuranceDetail.get("age"))|| org.ofbiz.base.util.UtilValidate.isEmpty(insuranceDetail.get("gender"))){
    		 Messagebox.show("Please configure employee birthdate and gender from Personal Information", "Error", 1, null);
    			return;
         }
      	insuranceDetail.remove("age");
        insuranceDetail.remove("memberName");
        insuranceDetail.remove("gender");

        if (activeInsuranceDetail != null && !editMode) {
            activeInsuranceDetail.put("validThru", new java.sql.Date(new Date().getTime()));
            activeInsuranceDetail.put("active","N");
            delegator.store(activeInsuranceDetail);
            updateStatusForDependant();
        }
        if (activeInsuranceDetail != null && editMode) {
            updatePolicyEndDateForDependant();
        }

        if (editMode) {
            activeInsuranceDetail.put("memberId", insuranceDetail.get("memberId"));
            activeInsuranceDetail.put("insuranceId", insuranceDetail.get("insuranceId"));
            activeInsuranceDetail.put("insuranceCompany", insuranceDetail.get("insuranceCompany"));
            activeInsuranceDetail.put("policyNumber", insuranceDetail.get("policyNumber"));
            activeInsuranceDetail.put("validFrom", new java.sql.Date(((Date)insuranceDetail.get("validFrom")).getTime()));
            activeInsuranceDetail.put("validThru", new java.sql.Date(((Date)insuranceDetail.get("validThru")).getTime()));           delegator.store(activeInsuranceDetail);
        } else {
            List<GenericValue> existingPolicyGvs = delegator.findByAnd("EmployeePolicyDetail", UtilMisc.toMap("employeeId", employeeId));
            deactivateTheExistingPolicy(existingPolicyGvs);
            insuranceDetail.put("validFrom",new java.sql.Date(((Date)insuranceDetail.get("validFrom")).getTime()));
            insuranceDetail.put("validThru",new java.sql.Date(((Date)insuranceDetail.get("validThru")).getTime()));
            delegator.create("EmployeePolicyDetail", insuranceDetail);
        }
        Messagebox.show(successMessage, "Success", Messagebox.OK, Messagebox.NONE, new EventListener() {
            public void onEvent(Event evt) {
                if ("onOK".equals(evt.getName())) {
                    Events.postEvent(Events.ON_CLICK, createMedicalInsuranceBtn, null);
                    createMedicalInsuranceWindow.detach();

                }
            }
        });
    }

    private void deactivateTheExistingPolicy(List<GenericValue> policyGvs) throws GenericEntityException {
        if (UtilValidate.isNotEmpty(policyGvs)) {
            for (GenericValue policyGv : policyGvs) {
                policyGv.put("active", "N");
            }
            delegator.storeAll(policyGvs);
        }
    }

    private void updatePolicyEndDateForDependant() throws GenericEntityException {
        List<GenericValue> dependantPolicyGvList = getAllActiveEmployeePolicyDetail();
        java.sql.Date nowDate = new java.sql.Date(UtilDateTime.getDayEnd(new Timestamp(new Date().getTime())).getTime());
        for (GenericValue dependantGv : dependantPolicyGvList) {
            dependantGv.put("validThru", new java.sql.Date(((Date)insuranceDetail.get("validThru")).getTime()));
            dependantGv.put("insuranceCompany", insuranceDetail.get("insuranceCompany"));
            dependantGv.put("policyNumber", insuranceDetail.get("policyNumber"));
          /*  if (nowDate.after((Date)insuranceDetail.get("thruDate"))) {
                dependantGv.put("active", "N");
            }  else{
                dependantGv.put("active", null);
            }*/
        }
        delegator.storeAll(dependantPolicyGvList);
    }

    private void updateStatusForDependant() throws GenericEntityException {
        List<GenericValue> dependantPolicyGvList = getAllActiveEmployeePolicyDetail();
        java.sql.Date nowDate = new java.sql.Date(UtilDateTime.getDayEnd(new Timestamp(new Date().getTime())).getTime());
        for (GenericValue dependantGv : dependantPolicyGvList) {
            dependantGv.put("active", "N");
        }
        delegator.storeAll(dependantPolicyGvList);
    }


    private List<GenericValue> getAllActiveEmployeePolicyDetail() {
        java.sql.Date nowDate = new java.sql.Date(UtilDateTime.getDayEnd(new Timestamp(new Date().getTime())).getTime());
        List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
        EntityCondition dateCondition = EntityCondition.makeCondition("active", EntityComparisonOperator.EQUALS, null);
        EntityCondition employeeCondition = EntityCondition.makeCondition("employeeId", EntityComparisonOperator.EQUALS, employeeId);
        EntityCondition dependantCondition = EntityCondition.makeCondition("dependantId", EntityComparisonOperator.NOT_EQUAL, null);
        conditionList.add(employeeCondition);
        conditionList.add(dateCondition);
        conditionList.add(dependantCondition);
        EntityCondition ec = EntityCondition.makeCondition(conditionList, EntityComparisonOperator.AND);
        List<GenericValue> insuranceDetailGvs = null;
        try {
            insuranceDetailGvs = delegator.findList("EmployeePolicyDetail", ec, null, null, null, false);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return insuranceDetailGvs;
    }


    public Map<String, Object> getInsuranceDetail() {
        return insuranceDetail;
    }

    public void setInsuranceDetail(Map<String, Object> insuranceDetail) {
        this.insuranceDetail = insuranceDetail;
    }


}
