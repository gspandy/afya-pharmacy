package com.ndz.controller;

import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

public final class EmployeeInsuranceDetailViewComposer extends GenericForwardComposer {
	
	private GenericDelegator delegator;
	
	private List<GenericValue> insuranceDetailGvs;
	
	private String employeeId;
	
	private GenericValue insuranceDetailGv;
	
	private List<GenericValue> partyRelationshipTypeGvs;
	
	
	@Override
	public ComponentInfo doBeforeCompose(Page page, Component parent, ComponentInfo compInfo) {
		GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
		delegator = (GenericDelegator) userLogin.getDelegator();
		employeeId = (String) Executions.getCurrent().getArg().get("partyId");
		try {
			insuranceDetailGvs = delegator.findByAnd("InsuranceDetailPartyRelationshipTypeView", UtilMisc.toMap("employeeId",employeeId,"validThru",null));
			partyRelationshipTypeGvs = delegator.findByAnd("PartyRelationshipType", UtilMisc.toMap("parentTypeId","FAMILY"));
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		String memberId = (String)Executions.getCurrent().getArg().get("memberId");
		if(UtilValidate.isNotEmpty(memberId)){
			List<GenericValue> insuranceDetails;
			try {
				insuranceDetails = delegator.findByAnd("EmployeeInsuranceDetail", UtilMisc.toMap("employeeId",employeeId,"memberId",memberId,"validThru",null));
				insuranceDetailGv = EntityUtil.getFirst(insuranceDetails);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		}
		return super.doBeforeCompose(page, parent, compInfo);
	}
	
	
	public void openUpdateWindow(String memberId, String employeeId,Component medicalInsuranceLink) throws Exception {
		Window editInsuranceWindow = (Window) Executions.createComponents("/zul/employeeProfile/editMedicalInsuranceDetail.zul", null, UtilMisc.toMap("partyId",employeeId,"memberId",memberId,"medicalInsuranceLink",medicalInsuranceLink));
		editInsuranceWindow.doModal();
	}
	
	public void updateInsuranceDetail(final Component root,final Component medicalInsuranceLink) throws Exception {
		delegator.store(insuranceDetailGv);
		Messagebox.show("Insurance detail updated Successfully", "Success", Messagebox.OK, Messagebox.NONE, new EventListener() {
			public void onEvent(Event evt) {
				if ("onOK".equals(evt.getName())) {
					Events.postEvent(Events.ON_CLICK,medicalInsuranceLink,null);
					root.detach();
				}
			}
		});
	}
	public List<GenericValue> getInsuranceDetailGvs() {
		return insuranceDetailGvs;
	}


	public GenericValue getInsuranceDetailGv() {
		return insuranceDetailGv;
	}


	public void setInsuranceDetailGv(GenericValue insuranceDetailGv) {
		this.insuranceDetailGv = insuranceDetailGv;
	}


	public List<GenericValue> getPartyRelationshipTypeGvs() {
		return partyRelationshipTypeGvs;
	}

}
