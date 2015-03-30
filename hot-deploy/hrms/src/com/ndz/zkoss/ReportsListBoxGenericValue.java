package com.ndz.zkoss;

import java.util.List;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.SimpleListModel;

import com.ndz.zkoss.util.HrmsInfrastructure;

public class ReportsListBoxGenericValue extends
GenericForwardComposer {
	private static final long serialVersionUID = 1L;
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		if ("applyClaimType".equals(comp.getId())){
			ClaimType(comp);
		}if("loanType".equals(comp.getId())){
			loanType(comp);
		}if("positionType".equals(comp.getId())){
			positionType(comp);
			employeeStatus(comp);
		}if("requisitionReports".equals(comp.getId())){
			requisitionPositionType(comp);
			requisitionDepartment(comp);
		}
	   if("leaveReportWindow".equals(comp.getId())){
		leaveReport(comp);
	   }
	}

	private void leaveReport(Component root) throws GenericEntityException {
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		EntityCondition condition = EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS, "LEAVE_TYPE");
		List<GenericValue> Type = delegator.findList("Enumeration", condition, null, null, null, false);
		Type.add(0,null);
		SimpleListModel Name = new SimpleListModel(Type);
		Listbox listBoxName = (Listbox) root.getFellow("leaveType");
		listBoxName.setModel(Name);
		listBoxName.setItemRenderer(new DropDownGenericValueAdapter("description","enumId"));
		
		EntityCondition condition2 = EntityCondition.makeCondition("statusTypeId",EntityOperator.EQUALS,"LEAVE_STATUS");
		List<GenericValue> statusItemList = delegator.findList("StatusItem", condition2, null, null, null, false);
		statusItemList.add(0,null);
		SimpleListModel listModel = new SimpleListModel(statusItemList);
		Listbox statusType = (Listbox) root.getFellow("statusType");
		statusType.setModel(listModel);
		statusType.setItemRenderer(new DropDownGenericValueAdapter("description","statusId"));
	}
	@SuppressWarnings("deprecation")
	private void ClaimType(Component root) throws GenericEntityException {
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		EntityCondition condition = EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS, "CLAIM_TYPE");
		List<GenericValue> Type = delegator.findList(
				"Enumeration", condition, null, null, null, false);
		Type.add(0,null);
		SimpleListModel Name = new SimpleListModel(Type);
		Listbox listBoxName = (Listbox) root.getFellow("applyClaimType");
		listBoxName.setModel(Name);
		listBoxName.setItemRenderer(new DropDownGenericValueAdapter("description","enumId"));
		
		EntityCondition condition2 = EntityCondition.makeCondition("statusTypeId",EntityOperator.EQUALS,"CLAIM_STATUS");
		List<GenericValue> statusItemList = delegator.findList("StatusItem", condition2, null, null, null, false);
		statusItemList.add(0,null);
		SimpleListModel listModel = new SimpleListModel(statusItemList);
		Listbox statusType = (Listbox) root.getFellow("statusType");
		statusType.setModel(listModel);
		statusType.setItemRenderer(new DropDownGenericValueAdapter("description","statusId"));
	}
	@SuppressWarnings("deprecation")
	private void loanType(Component root) throws GenericEntityException {
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		EntityCondition condition = EntityCondition.makeCondition("enumTypeId",EntityOperator.EQUALS, "ELOAN_TYPE");
		List<GenericValue> Type = delegator.findList(
				"Enumeration", condition, null, null, null, false);
		Type.add(0,null);
		SimpleListModel Name = new SimpleListModel(Type);
		Listbox listBoxName = (Listbox) root.getFellow("loanType");
		listBoxName.setModel(Name);
		listBoxName.setItemRenderer(new DropDownGenericValueAdapter("description","description"));
		
		EntityCondition condition2 = EntityCondition.makeCondition("statusTypeId",EntityOperator.EQUALS,"ELOAN_STATUS");
		List<GenericValue> statusItemList = delegator.findList("StatusItem", condition2, null, null, null, false);
		statusItemList.add(0,null);
		SimpleListModel listModel = new SimpleListModel(statusItemList);
		Listbox statusType = (Listbox) root.getFellow("statusType");
		statusType.setModel(listModel);
		statusType.setItemRenderer(new DropDownGenericValueAdapter("description","statusId"));
		

	}
	
	@SuppressWarnings("deprecation")
	private void positionType(Component root) throws GenericEntityException {
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		
		List<GenericValue> Type = delegator.findList(
				"EmplPositionType", null, null, null, null, false);
		Type.add(0,null);
		SimpleListModel Name = new SimpleListModel(Type);
		Listbox listBoxName = (Listbox) root.getFellow("positionType");
		listBoxName.setModel(Name);
		listBoxName.setItemRenderer(new DropDownGenericValueAdapter("emplPositionTypeId","emplPositionTypeId"));
	}
	
	@SuppressWarnings("deprecation")
	private void employeeStatus(Component root) throws GenericEntityException {
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		EntityCondition condition = EntityCondition.makeCondition("statusTypeId",EntityOperator.EQUALS, "EMPL_POSITION_STATUS");
		List<GenericValue> Type = delegator.findList(
				"StatusItem", condition, null, null, null, false);
		Type.add(0,null);
		SimpleListModel Name = new SimpleListModel(Type);
		Listbox listBoxName = (Listbox) root.getFellow("employeeStatusDescription");
		listBoxName.setModel(Name);
		listBoxName.setItemRenderer(new DropDownGenericValueAdapter("description","description"));
	}
	@SuppressWarnings("deprecation")
	private void requisitionPositionType(Component root) throws GenericEntityException {
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		
		List<GenericValue> Type = delegator.findList(
				"EmplPositionType", null, null, null, null, false);
		Type.add(0,null);
		SimpleListModel Name = new SimpleListModel(Type);
		Listbox listBoxName = (Listbox) root.getFellow("requisitionPositionType");
		listBoxName.setModel(Name);
		listBoxName.setItemRenderer(new DropDownGenericValueAdapter("emplPositionTypeId","emplPositionTypeId"));
	}
	@SuppressWarnings("deprecation")
	private void requisitionDepartment(Component root) throws GenericEntityException {
		GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
		
		List<GenericValue> Type = delegator.findList(
				"EmplPosition", null, null, null, null, false);
		SimpleListModel Name = new SimpleListModel(Type);
		Listbox listBoxName = (Listbox) root.getFellow("dataGrid");
		listBoxName.setModel(Name);
		listBoxName.setItemRenderer(new DropDownGenericValueAdapter("emplPositionId","emplPositionId"));
	}

}
