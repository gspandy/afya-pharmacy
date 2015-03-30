package com.smebiz.dynamicform;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.digester.Digester;
import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.component.ComponentConfig.WebappInfo;
import org.ofbiz.base.location.FlexibleLocation;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.xml.sax.SAXException;

import com.smebiz.dynamicform.input.FormInput;
import com.smebiz.dynamicform.input.renderer.FormInputRenderer;

public class DynaFormUtil {

	static FormCategory rootCategory = null;

	public static void addSubComponents(String formId, GenericValue compEntity,
			FormComponent comp) {

		GenericDelegator delegator = GenericDelegator
				.getGenericDelegator("default");

		EntityCondition condition = EntityCondition.makeCondition(UtilMisc
				.toMap("formId", formId, "parentCompId", compEntity
						.getString("compId")));
		List<GenericValue> children = null;
		try {
			children = delegator.findList("CustomFormComponents", condition,
					null, null, null, false);
		} catch (GenericEntityException ge) {
		}

		for (GenericValue child : children) {
			if ("2".equals(child.getString("compType"))) {
				FormRow row = new FormRow();
				if (child.getString("dynamicRuleId") != null) {
					String ruleId = child.getString("dynamicRuleId");
					FormInput input = DynaFormUtil.getFormInput(ruleId);
					row.setFormInput(input);
				}
				comp.addComponent(row);
				addSubComponents(formId, child, row);
			} else if ("3".equals(child.getString("compType"))) {
				FormCell cell = new FormCell();
				if (child.getString("dynamicRuleId") != null) {
					String ruleId = child.getString("dynamicRuleId");
					FormInput input = DynaFormUtil.getFormInput(ruleId);
					cell.setFormInput(input);
				} else if (child.getString("description") != null) {
					cell.setDescription(child.getString("description"));
				}
				comp.addComponent(cell);
				addSubComponents(formId, child, cell);
			}
		}
	}

	public static FormInput getFormInput(String input_id) {
		FormCategory rootCategory = null;
		try {
			rootCategory = getFormRootCategory();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (rootCategory != null) {
			for (FormCategory cat : rootCategory.getCategories()) {

				for (FormInput input : cat.getInputs()) {
					if (input_id.equals(input.getId()))
						return input;
				}
			}
		}
		return null;
	}

	public static FormCategory getFormRootCategory() throws IOException {
		if (rootCategory != null) {
			return rootCategory;
		}

		URL locationUrl = null;
		try {
			locationUrl = FlexibleLocation
					.resolveLocation(DynaFormConstants.TEMPLATE_LOCATION);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		if (locationUrl == null) {
			throw new IllegalArgumentException(
					"FreeMarker file not found at location: "
							+ DynaFormConstants.TEMPLATE_LOCATION);
		}
		InputStream is = locationUrl.openStream();
		Digester digester = new Digester();
		digester.setClassLoader(Thread.currentThread().getContextClassLoader());
		digester.addObjectCreate("categories/category", FormCategory.class);
		digester.addObjectCreate("categories/category/input", FormInput.class);
		digester.addObjectCreate("categories/category/input/renderer",
				FormInputRenderer.class);
		digester.addSetNext("categories/category/input", "addFormInput");
		digester.addSetProperties("categories/category/", "hrName", "hrName");

		digester.addSetNext("categories/category/input/renderer",
				"setFormInputRenderer");

		digester.addSetProperties("categories/category/input", "id", "id");
		digester.addSetProperties("categories/category/input", "display",
				"display");
		digester.addSetProperties("categories/category/input", "columns",
				"columns");
		digester.addSetProperties("categories/category/input", "outputType",
				"outputType");

		digester.addSetProperties("categories/category/input/renderer",
				"invoke", "invoke");
		digester.addSetProperties("categories/category/input/renderer",
				"classname", "classname");
		digester.addSetProperties("categories/category/input/renderer", "args",
				"args");
		digester.addSetProperties("categories/category/input/renderer",
				"templateName", "templateName");
		digester.addSetProperties("categories/category/input/renderer",
				"scriptFileName", "scriptFileName");

		digester.addObjectCreate("categories", FormCategory.class);
		digester.addSetNext("categories/category", "addCategory");

		try {
			rootCategory = (FormCategory) digester.parse(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rootCategory;
	}

	public static Form getForm(String formId, GenericDelegator delegator) {
		GenericValue customForm = null;
		Form form = new Form();

		try {
			customForm = delegator.findOne("CustomForms", UtilMisc.toMap(
					"formId", formId), false);

			EntityCondition condition = EntityCondition.makeCondition(UtilMisc
					.toMap("formId", formId, "compType", "1"));
			List<GenericValue> formSections = delegator.findList(
					"CustomFormComponents", condition, null, null, null, false);
			form.setName(customForm.getString("formName"));
			form.setDescription(customForm.getString("description"));
			for (GenericValue section : formSections) {
				String desc = section.getString("description");
				FormSection secObj = new FormSection(desc);
				addSubComponents(formId, section, secObj);
				form.addComponent(secObj);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return form;
	}
}