package com.ndz.controller;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Messagebox;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * @author: NthDimenzion
 * @since 1.0
 */
public class EmployeeDependantComposer extends GenericForwardComposer {

    private Delegator delegator;

    private List<GenericValue> partyRelationshipTypeGvs;

    private GenericValue employeeDependantDetail;

    private String employeeId;

    private Component familyDependantLink;

    private String dependantId;

    private String successMessage = "Member added Successfully";

    private String windowTitle = "Add Member";

    private boolean newRecord = true;

    @Override
    public ComponentInfo doBeforeCompose(Page page, Component parent, ComponentInfo compInfo) {
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        this.delegator = userLogin.getDelegator();
        try {
            partyRelationshipTypeGvs = delegator.findByAnd("PartyRelationshipType", UtilMisc.toMap("parentTypeId", "FAMILY"));
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return super.doBeforeCompose(page, parent, compInfo);

    }

    @Override
    public void doAfterCompose(Component component) throws Exception {
        super.doAfterCompose(component);
        employeeId = (String) Executions.getCurrent().getArg().get("partyId");
        familyDependantLink = (Component) Executions.getCurrent().getArg().get("familyDependantBtn");
        String dependantId = (String) Executions.getCurrent().getArg().get("dependantId");
        if (UtilValidate.isNotEmpty(dependantId)) {
            this.dependantId = dependantId;
            newRecord = false;
            employeeDependantDetail = delegator.findByPrimaryKey("EmployeeDependant", UtilMisc.toMap("dependantId", dependantId));
            successMessage = "Member detail updated successfully";
            windowTitle = "Member Dependant";
            return;
        }
        this.dependantId = delegator.getNextSeqId("EmployeeDependant");
        employeeDependantDetail = buildNewFamilyDependantGenericValue();
    }

    private GenericValue buildNewFamilyDependantGenericValue() {
        GenericValue familyDependantGv = GenericValue.create(delegator.getModelEntity("EmployeeDependant"));
        familyDependantGv.put("employeeId", employeeId);
        familyDependantGv.put("fromDate", new java.sql.Date(new Date().getTime()));
        familyDependantGv.put("dependantId", dependantId);
        return familyDependantGv;
    }

    public void saveOrUpdateFamilyDependant(final Component parentComponent) throws GenericEntityException, InterruptedException {
    	employeeDependantDetail.put("dateOfBirth", new java.sql.Date(((Date)employeeDependantDetail.get("dateOfBirth")).getTime()));
    	employeeDependantDetail.put("fromDate", new java.sql.Date(((Date)employeeDependantDetail.get("fromDate")).getTime()));
    	if(employeeDependantDetail.get("thruDate") != null)
    		employeeDependantDetail.put("thruDate", new java.sql.Date(((Date)employeeDependantDetail.get("thruDate")).getTime()));
    	if(employeeDependantDetail.get("timeOfBirth") != null)
    		employeeDependantDetail.put("timeOfBirth", new java.sql.Timestamp(((Date)employeeDependantDetail.get("timeOfBirth")).getTime()));
    	    	   	
    	delegator.createOrStore(employeeDependantDetail);
        if (employeeDependantDetail.get("thruDate") != null && !newRecord) {
            updateDependantsPolicy();
        }
        Messagebox.show(successMessage, "Success", Messagebox.OK, Messagebox.NONE, new EventListener() {
            public void onEvent(Event evt) {
                if ("onOK".equals(evt.getName())) {
                    Events.postEvent(Events.ON_CLICK, familyDependantLink, null);
                    parentComponent.detach();

                }
            }
        });
    }

    private void updateDependantsPolicy() throws GenericEntityException {
        java.sql.Date nowDate = new java.sql.Date(UtilDateTime.getDayEnd(new Timestamp(new Date().getTime())).getTime());
        EntityCondition dateCondition = EntityCondition.makeCondition("validThru", EntityComparisonOperator.GREATER_THAN_EQUAL_TO, nowDate);
        EntityCondition dependantCondition = EntityCondition.makeCondition("dependantId", EntityComparisonOperator.EQUALS, dependantId);
        EntityCondition ec = EntityCondition.makeCondition(dateCondition, EntityComparisonOperator.AND, dependantCondition);
        List<GenericValue> insuranceDetailGvs = delegator.findList("EmployeePolicyDetail", ec, null, null, null, false);
        if (UtilValidate.isNotEmpty(insuranceDetailGvs)) {
            GenericValue policyGv = insuranceDetailGvs.get(0);
            policyGv.put("validThru", employeeDependantDetail.get("thruDate"));
            if (nowDate.after((Date)employeeDependantDetail.get("thruDate"))) {
                policyGv.put("active", "N");
            }  else{
                policyGv.put("active", null);
            }
            delegator.store(policyGv);
        }
    }

    public List<GenericValue> getPartyRelationshipTypeGvs() {
        return partyRelationshipTypeGvs;
    }

    public GenericValue getEmployeeDependantDetail() {
        return employeeDependantDetail;
    }

    public void setEmployeeDependantDetail(GenericValue employeeDependantDetail) {
        this.employeeDependantDetail = employeeDependantDetail;
    }

    public String getWindowTitle() {
        return windowTitle;
    }
}
