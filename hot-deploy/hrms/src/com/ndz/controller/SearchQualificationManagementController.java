package com.ndz.controller;
import java.util.List;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionFunction;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.SimpleListModel;

import com.ndz.zkoss.GenericValueRenderer;
import com.ndz.zkoss.util.HrmsInfrastructure;

public class SearchQualificationManagementController extends GenericForwardComposer {
	private static final long serialVersionUID = 1L;

	public void doAfterCompose(Component searchQualificationPanel) throws Exception {
		super.doAfterCompose(searchQualificationPanel);
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		
		List<GenericValue> QualType = delegator.findList("PartyQualType", null, null, null, null, false);
		SimpleListModel QualTypeList = new SimpleListModel(QualType);
		Listbox listBoxEmployeeQualTypeId = (Listbox) searchQualificationPanel.getFellow("listBoxEmployeeQualTypeId");
		listBoxEmployeeQualTypeId.setModel(QualTypeList);
		listBoxEmployeeQualTypeId.setItemRenderer(new GenericValueRenderer(new String[]{"partyQualTypeId","description"}));
		
		EntityCondition fromCond = EntityConditionFunction.makeCondition(
				"statusTypeId", EntityComparisonOperator.EQUALS, "QUAL_STATUS");
		EntityCondition conditions = EntityCondition.makeCondition(fromCond);
		List<GenericValue> StatusID = delegator.findList("StatusItem",conditions,null, null, null, false);
		SimpleListModel StatusIDList = new SimpleListModel(StatusID);
		Listbox listBoxStatusId = (Listbox) searchQualificationPanel.getFellow("searchEmployeeQualificationStatusID");
		listBoxStatusId.setModel(StatusIDList);
		listBoxStatusId.setItemRenderer(new GenericValueRenderer(new String[]{"statusId","description"}));
		
		EntityCondition thruCond = EntityConditionFunction.makeCondition(
				"statusTypeId", EntityComparisonOperator.EQUALS, "PARTYQUAL_VERIFY");
		EntityCondition conditions1 = EntityCondition.makeCondition(thruCond);
		List<GenericValue> StatusVerifyID = delegator.findList("StatusItem",conditions1,null, null, null, false);
		SimpleListModel StatusVerifyIDList = new SimpleListModel(StatusVerifyID);
		Listbox listBoxStatusVerifyId = (Listbox) searchQualificationPanel.getFellow("searchEmployeeQualificationVerifyStatusID");
		listBoxStatusVerifyId.setModel(StatusVerifyIDList);
		listBoxStatusVerifyId.setItemRenderer(new GenericValueRenderer(new String[]{"statusId","description"}));
	}

}
