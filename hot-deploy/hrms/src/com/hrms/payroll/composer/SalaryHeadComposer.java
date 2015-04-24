package com.hrms.payroll.composer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.util.EntityListIterator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zkplus.databind.BindingListModel;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

import com.hrms.composer.HrmsComposer;

public class SalaryHeadComposer extends HrmsComposer {

	Listbox salaryHeadListbox;
	BindingListModel model;
	GenericValue selectedSalaryHead;

	int totalSize = 10;

	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	public BindingListModel getModel() {
		return model;
	}

	public void setModel(BindingListModel model) {
		this.model = model;
	}

	public GenericValue getSelectedSalaryHead() {
		return selectedSalaryHead;
	}

	public void setSelectedSalaryHead(GenericValue selectedSalaryHead) {
		this.selectedSalaryHead = selectedSalaryHead;
	}

	@Override
	protected void executeAfterCompose(Component comp) throws Exception {
		DynamicViewEntity dve = new DynamicViewEntity();
		dve.setEntityName("SalaryHead");
		EntityListIterator iter = delegator.findListIteratorByCondition(
				dve, null, null, UtilMisc.toList("salaryHeadTypeId"),null,null);
		Long count = delegator.findCountByCondition("SalaryHead", null, null,null);
		totalSize = count.intValue();
		System.out.println(" TOTAL SIZE " + totalSize);

		model = new BindingListModelList(iter.getPartialList(0, 10), false);
		binder.loadAll();
		iter.close();
		
	}
	
	public void onClick(Event event){
		System.out.println("**************** "+event.getData());
	}

	public void onPaging$paging(ForwardEvent event) throws Exception {
		PagingEvent pagingEvent = (PagingEvent) event.getOrigin();
		int pageIndex = pagingEvent.getActivePage();
		DynamicViewEntity dve = new DynamicViewEntity();
		dve.setEntityName("SalaryHead");
		EntityListIterator iter = delegator.findListIteratorByCondition(
				dve, null, null, UtilMisc.toList("salaryHeadTypeId"),null,null);
		model = new BindingListModelList(iter
				.getPartialList(pageIndex * 10, 10), false);
		iter.close();
		binder.loadAttribute(salaryHeadListbox, "model");
	}

	public void onClick$newButton(Event event) {
		setNewsh(delegator.makeValidValue("SalaryHead", Collections.EMPTY_MAP));
	}

	public void onClick$ruleLookupButton(Event event) {

	}

	public void onClick$updateButton(Event event) throws Exception {
		delegator.store(selectedSalaryHead);
	}

	public void onClick$saveButton(Event event) throws Exception {
		System.out.println(" New Salary Head " + newsh);
		delegator.createOrStore(newsh, true);
	}

	BindingListModel ruleModel = null;

	public void onSelect$salaryHeadListbox(Event event) throws Exception {
		List<GenericValue> associatedRules = delegator.findList(
				"SalaryHeadRule", EntityCondition.makeCondition("salaryHeadId",
						selectedSalaryHead.getString("salaryHeadId")), null,
				null, null, true);

		List<GenericValue> rules = new ArrayList(associatedRules.size());
		for (GenericValue assocRule : associatedRules) {
			rules.add(assocRule.getRelatedOne("PayrollRule"));
		}

		ruleModel = new BindingListModelList(rules, false);
		Window win = (Window) (event.getTarget().getFellowIfAny(
				"salaryHeadDetail", false));
		win.setVisible(true);
		win.setFocus(true);

		Groupbox grpBox = (Groupbox) win
				.getFellowIfAny("formulaGroupBox", true);
		if (selectedSalaryHead != null
				&& "FORMULA".equals(selectedSalaryHead
						.getString("salaryComputationTypeId"))) {
			grpBox.setVisible(true);
			Grid rulesGrid = (Grid) win.getFellowIfAny("rulesGrid", true);
			binder.loadAttribute(rulesGrid, "model");
		} else {
			grpBox.setVisible(false);
		}
	}

	public BindingListModel getRuleModel() {
		return ruleModel;
	}

	public void setRuleModel(BindingListModel ruleModel) {
		this.ruleModel = ruleModel;
	}

	public void setNewsh(GenericValue newsh) {
		this.newsh = newsh;
	}

	public GenericValue getNewsh() {
		return newsh;
	}

	private GenericValue newsh;

}
