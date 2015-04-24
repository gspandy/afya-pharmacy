package com.ndz.controller;

import com.ndz.vo.EmployeePolicyDetailVo;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Window;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public final class EmployeePolicyDetailViewComposer extends GenericForwardComposer {

    private Delegator delegator;

    private List<EmployeePolicyDetailVo> employeePolicyDetailVos;

    private String employeeId;

    private Component newPolicyBtn;

    private GenericValue activePrimaryPolicyGv;

    @Override
    public ComponentInfo doBeforeCompose(Page page, Component parent, ComponentInfo compInfo) {
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        this.delegator = userLogin.getDelegator();
        employeeId = (String) Executions.getCurrent().getArg().get("partyId");
        List<GenericValue> insuranceDetailGvs = getAllActiveEmployeePolicyDetail();
        newPolicyBtn = (Component) Executions.getCurrent().getArg().get("newPolicyBtn");
        activePrimaryPolicyGv = getPrimaryPolicy();
        if (newPolicyBtn != null) {
            java.sql.Date nowDate = new java.sql.Date(UtilDateTime.getDayEnd(new Timestamp(new java.util.Date().getTime())).getTime());
            newPolicyBtn.setVisible((activePrimaryPolicyGv == null || activePrimaryPolicyGv.getDate("validThru").before(nowDate)));
        }
        if (UtilValidate.isEmpty(insuranceDetailGvs) && activePrimaryPolicyGv != null) {
            insuranceDetailGvs = new ArrayList<GenericValue>();
            insuranceDetailGvs.add(activePrimaryPolicyGv);
        }
        employeePolicyDetailVos = buildEmployeePolicyVo(insuranceDetailGvs);
        return super.doBeforeCompose(page, parent, compInfo);
    }

    private GenericValue getPrimaryPolicy() {
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

    private List<EmployeePolicyDetailVo> buildEmployeePolicyVo(List<GenericValue> insuranceDetailGvs) {
        List<EmployeePolicyDetailVo> employeePolicyDetailVoList = new ArrayList<EmployeePolicyDetailVo>();
        for (GenericValue insuranceDetailGv : insuranceDetailGvs) {
            try {
                EmployeePolicyDetailVo policyVo = EmployeePolicyDetailVo.createEmployeePolicyDetailVo(insuranceDetailGv, delegator, activePrimaryPolicyGv);
                employeePolicyDetailVoList.add(policyVo);
            } catch (Exception e) {
            }
        }
        return employeePolicyDetailVoList;
    }

    public void openUpdateWindow(String policyId, String employeeId, Component medicalInsuranceLink) throws Exception {
        String editPage = (activePrimaryPolicyGv != null && activePrimaryPolicyGv.getString("policyId").equals(policyId)) ? "/zul/employeeProfile/editEmployeeMedicalInsuranceDetail.zul" : "/zul/employeeProfile/addEditDependantInsuranceDetail.zul";
        Window editInsuranceWindow = (Window) Executions.createComponents(editPage, null, UtilMisc.toMap("partyId", employeeId, "policyId", policyId, "createMedicalInsuranceButton", medicalInsuranceLink));
        editInsuranceWindow.doModal();
    }

    public void openDependantWindow(Component createMedicalInsuranceButton) throws InterruptedException {
        Window dependantInsuranceWindow = (Window) Executions.createComponents("/zul/employeeProfile/addEditDependantInsuranceDetail.zul", null, UtilMisc.toMap("partyId", employeeId, "createMedicalInsuranceButton", createMedicalInsuranceButton));
        dependantInsuranceWindow.doModal();

    }

    public List<EmployeePolicyDetailVo> getEmployeePolicyDetailVos() {
        return employeePolicyDetailVos;
    }

    public GenericValue getActivePrimaryPolicyGv() {
        return activePrimaryPolicyGv;
    }
}
