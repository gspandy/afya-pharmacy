package com.ndz.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Messagebox;

import com.ndz.zkoss.HrmsUtil;

public final class EmployeeInsuranceDetailComposer extends GenericForwardComposer{
	
	private GenericDelegator delegator;
	
	private List<GenericValue> partyRelationshipTypeGvs;
	
	private Map<String, Object> insuranceDetail;
	
	private String employeeId;
	
	private final String NULL_VALUE = null;
	
	private GenericValue activeInsuranceDetail;
	
	@Override
	public ComponentInfo doBeforeCompose(Page page, Component parent, ComponentInfo compInfo) {
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		delegator = (GenericDelegator) userLogin.getDelegator();
		try {
			partyRelationshipTypeGvs = delegator.findByAnd("PartyRelationshipType", UtilMisc.toMap("parentTypeId","FAMILY"));
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return super.doBeforeCompose(page, parent, compInfo);
		
	}
	
	@Override
	public void doAfterCompose(Component component) throws Exception {
		super.doAfterCompose(component);
		employeeId = (String) Executions.getCurrent().getArg().get("partyId");
		insuranceDetail = buildInitialMap(insuranceDetail);
	}
	
	private Map<String, Object> buildInitialMap(Map<String, Object> employeeInsuranceDetail) throws GenericEntityException{
		if(employeeInsuranceDetail!=null){
			return employeeInsuranceDetail;
		}
		employeeInsuranceDetail = new HashMap<String, Object>();
		GenericValue partyGroupGv = delegator.findByPrimaryKey("PartyGroup", org.ofbiz.base.util.UtilMisc.toMap("partyId", "Company"));
		String companyName = partyGroupGv.getString("groupName");
		employeeInsuranceDetail.put("employeeId", employeeId);
		employeeInsuranceDetail.put("memberId", NULL_VALUE);
		employeeInsuranceDetail.put("insuranceId", NULL_VALUE);
		employeeInsuranceDetail.put("insuranceCompany", NULL_VALUE);
		employeeInsuranceDetail.put("memberName", NULL_VALUE);
		employeeInsuranceDetail.put("age", NULL_VALUE);
		employeeInsuranceDetail.put("gender", NULL_VALUE);
		employeeInsuranceDetail.put("policyNumber", NULL_VALUE);
		employeeInsuranceDetail.put("policyHolder", companyName);
		employeeInsuranceDetail.put("relationshipType", NULL_VALUE);
		employeeInsuranceDetail.put("primaryBenificiary", HrmsUtil.getFullName(delegator, employeeId));
		employeeInsuranceDetail.put("validFrom", NULL_VALUE);
		return employeeInsuranceDetail;
	}
	
	public void createOrUpdateInsuranceDetail(final Component createMedicalInsuranceBtn,final Component createMedicalInsuranceWindow) throws Exception{
		boolean beganTransaction = TransactionUtil.begin();
		try{
		List<GenericValue> insuranceDetailList = delegator.findByAnd("EmployeeInsuranceDetail", UtilMisc.toMap("memberId",insuranceDetail.get("memberId"),"employeeId",employeeId));
		if(UtilValidate.isNotEmpty(insuranceDetailList)){
			activeInsuranceDetail = insuranceDetailList.get(0);
		}
		if(activeInsuranceDetail!=null && (activeInsuranceDetail.getString("employeeId").equals((String)insuranceDetail.get("employeeId")) && activeInsuranceDetail.getString("memberId").equals((String)insuranceDetail.get("memberId")))){
			activeInsuranceDetail.put("validThru", new java.util.Date());	
			delegator.store(activeInsuranceDetail);
		}
		try{
		delegator.create("EmployeeInsuranceDetail", insuranceDetail);
		}catch (GenericEntityException e) {
			if(!beganTransaction){
				TransactionUtil.rollback();
			}
			Messagebox.show("Insurance detail for member with same date already exists", "Success", Messagebox.OK, Messagebox.NONE);
			return;
		}
		Messagebox.show("Insurance detail created Successfully", "Success", Messagebox.OK, Messagebox.NONE, new EventListener() {
			public void onEvent(Event evt) {
				if ("onOK".equals(evt.getName())) {
					Events.postEvent(Events.ON_CLICK,createMedicalInsuranceBtn,null);
					createMedicalInsuranceWindow.detach();
				
				}
			}
		});
		if(beganTransaction){
			TransactionUtil.commit();
		}
		}catch (Exception e) {
			if(!beganTransaction){
				TransactionUtil.rollback();
			}	
		}
	}


	public List<GenericValue> getPartyRelationshipTypeGvs() {
		return partyRelationshipTypeGvs;
		
	}
	
	public Map<String, Object> getInsuranceDetail() {
		return insuranceDetail;
	}

	public void setInsuranceDetail(Map<String, Object> insuranceDetail) {
		this.insuranceDetail = insuranceDetail;
	}

	
}
