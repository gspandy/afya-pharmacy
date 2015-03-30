package com.hrms.payroll.composer;

import java.util.List;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkplus.databind.BindingListModel;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Listbox;

import com.hrms.composer.HrmsComposer;

public class SalaryTemplateComposer extends HrmsComposer {

	Listbox templatesListBox;
	BindingListModel model;

	GenericValue selectedTemplate;

	public BindingListModel getModel() {
		return model;
	}

	public void setModel(BindingListModel model) {
		this.model = model;
	}

	public GenericValue getSelectedTemplate() {
		return selectedTemplate;
	}

	public void setSelectedTemplate(GenericValue selectedTemplate) {
		this.selectedTemplate = selectedTemplate;
	}

	@Override
	protected void executeAfterCompose(Component comp) throws Exception {
		List salaryStructures = delegator.findList("SalaryStructure", null,
				null, null, null, true);
		model = new BindingListModelList(salaryStructures, false);
		binder.loadAttribute(templatesListBox, "model");
	}

	public void onClick$copyButton(Event event) {

	}

	public void onClick$expireButton(Event event) {
		System.out.println(" Selected " + selectedTemplate);
	}

}
