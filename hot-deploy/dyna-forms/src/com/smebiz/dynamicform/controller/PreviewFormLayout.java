package com.smebiz.dynamicform.controller;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

import com.smebiz.dynamicform.DynaFormUtil;
import com.smebiz.dynamicform.Form;
import com.smebiz.dynamicform.FormCategory;
import com.smebiz.dynamicform.FormCell;
import com.smebiz.dynamicform.FormComponent;
import com.smebiz.dynamicform.FormRow;
import com.smebiz.dynamicform.input.FormInput;
import com.smebiz.dynamicform.renderer.RenderUtils;
import com.smebiz.dynamicform.renderer.RendererContext;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.servlet.FreemarkerServlet;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.ext.servlet.HttpSessionHashModel;
import freemarker.ext.servlet.ServletContextHashModel;

public class PreviewFormLayout extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3830566001689069208L;

	private static final String FORM_INPUT_FILE = "formInputFile";
	DynaFormUtil data = new DynaFormUtil();

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		FormCategory rootCategory=null;
		try {
			rootCategory = DynaFormUtil.getFormRootCategory();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getServletContext().setAttribute("dynaFormCategories", rootCategory);
		RenderUtils.setCategories(rootCategory);
	}
	
	
	public String previewLayout(HttpServletRequest req, HttpServletResponse resp) {

		String formId = req.getParameter("formId");
		String renderfull = req.getParameter("renderFull");
		GenericDelegator delegator = GenericDelegator.getGenericDelegator("default");
		
		Form form = DynaFormUtil.getForm(formId,delegator);
			RendererContext context = new RendererContext();
			try {
				context.setWriter(resp.getWriter());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for(Enumeration<String> enumeration = req.getParameterNames();enumeration.hasMoreElements();){
				
				String key = enumeration.nextElement();
				String value = req.getParameter(key);
				context.put(key, value);
			}

			for(Enumeration<String> enumeration = req.getAttributeNames();enumeration.hasMoreElements();){
				
				String key = enumeration.nextElement();
				Object value = req.getAttribute(key);
				context.put(key, value);
			}
			RendererContext.setRendererContext(context);
			try {
				form.setRenderBodyOnly(!Boolean.parseBoolean(renderfull));
				form.render();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		return "success";
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		previewLayout(req, resp);
	}

	private void addSubComponents(HttpServletRequest req,HttpServletResponse resp, String formId,
			GenericValue compEntity, FormComponent comp) {

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
				addSubComponents(req,resp, formId, child, row);
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
				addSubComponents(req,resp, formId, child, cell);
			}
		}
	}

}
