package com.ndz.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Div;

public class SearchPanel extends GenericForwardComposer {

	String entityName;
	private List<String> includeFields = new ArrayList<String>(0);

	public SearchPanel(String entityName, String[] includeFields) {
		this.entityName = entityName;
		this.includeFields = Arrays.asList(includeFields);
	}

	@Override
	public void doAfterCompose(Component panel) throws Exception {
		super.doAfterCompose(panel);
		System.out.println(" Entity Name " + entityName);

		GenericValue userLogin = (GenericValue) Executions.getCurrent()
				.getDesktop().getSession().getAttribute("userLogin");

		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
		ModelEntity entityModel = delegator.getModelEntity(entityName);

		Iterator<ModelField> iter = entityModel.getFieldsIterator();

		Panelchildren panelChildren = new Panelchildren();
		panelChildren.setParent(panel);

		for (; iter.hasNext();) {
			ModelField modelField = iter.next();
			if (!includeFields.contains(modelField.getName()))
				continue;

			Component comp = null;
			if ("id".equals(modelField.getType())
					|| "id-ne".equals(modelField.getType())) {
				Textbox textField = new Textbox();
				textField.setCols(20);
				comp = textField;
			} else if ("id-long".equals(modelField.getType())
					|| "id-long-ne".equals(modelField.getType())) {
				Textbox textField = new Textbox();
				textField.setCols(40);
				comp = textField;
			} else if ("id-vlong".equals(modelField.getType())
					|| "id-vlong-ne".equals(modelField.getType())) {
				Textbox textField = new Textbox();
				textField.setCols(60);
				comp = textField;
			} else if ("very-short".equals(modelField.getType())) {
				Textbox textField = new Textbox();
				textField.setCols(6);
				comp = textField;
			} else if ("name".equals(modelField.getType())
					|| "short-varchar".equals(modelField.getType())) {
				Textbox textField = new Textbox();
				textField.setCols(40);
				comp = textField;
			} else if ("value".equals(modelField.getType())
					|| "comment".equals(modelField.getType())
					|| "description".equals(modelField.getType())
					|| "long-varchar".equals(modelField.getType())
					|| "url".equals(modelField.getType())
					|| "email".equals(modelField.getType())) {
				Textbox textField = new Textbox();
				textField.setCols(60);
				comp = textField;
			} else if ("floating-point".equals(modelField.getType())
					|| "currency-amount".equals(modelField.getType())
					|| "numeric".equals(modelField.getType())) {
				comp = new Doublebox();
			} else if ("date-time".equals(modelField.getType())
					|| "date".equals(modelField.getType())
					|| "time".equals(modelField.getType())) {
				comp = new Datebox();
			} else {
				Textbox textField = new Textbox();
				textField.setCols(10);
				comp = textField;
			}

			Div div = new Div();
			div.setWidth("300px");
			div.setAlign("right");
			div.setStyle("margin:5px 5px 5px 5px");
			
			Label label = new Label();
			label.setValue(org.apache.commons.lang.StringUtils
					.capitalize(modelField.getName()));
			label.setStyle("margin:15px 5px 5px 5px;font-weight:bold");
			
			div.appendChild(label);
			
			comp.setId(modelField.getName());
			Listbox operators = new Listbox();
			operators.setId(modelField.getName() + "_op");
			operators.setMold("select");
			
			operators.appendItem("Begins With", "like");
			operators.appendItem("contains", "Contains");
			operators.appendItem("Is Empty", "empty");
			operators.appendItem("Not Equal", "notEqual");
			Hbox hbox = new Hbox(new Component[] { div, operators, comp });
			panelChildren.appendChild(hbox);
			
		}
		panel.appendChild(panelChildren);
	}
}
