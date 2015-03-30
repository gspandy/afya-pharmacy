package com.nthdimenzion.humanres.payroll.resolver;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

public class FieldResolver extends AbstractResolver {

	private final static String module = FieldResolver.class.getName();
	private final static String COMP_NAME = "CUSTOM-FIELD";

	public Object resolve(String operandName,GenericDelegator delegator) {
		Debug.logInfo("Method resolve " + operandName, module);
		Object obj = null;
		//First try it in the Map (RuleContext)
		Debug.logInfo("Method resolve::Not found in the RuleContext Map. Trying to evaluate against Employee.", module);

		EntityCondition entityCondition = EntityCondition.makeCondition(UtilMisc.toMap("description", operandName));
		Set<String> fieldsToSelect = new HashSet<String>();
		fieldsToSelect.add("className");

		String className = null;
		try {
			List<GenericValue> genericValues = delegator.findList("PayrollEmplAttr", entityCondition, fieldsToSelect, null, null, false);
			if (genericValues.size() == 1) {
				className = genericValues.get(0).getString("className");
			}

		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Debug.logInfo("Method resolve::Delegating to class " + className + " for evaluating " + operandName, module);

		Class klass = null;
		try {
			klass = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		java.lang.reflect.Method m = null;
		try {
			m = klass.getMethod("execute", new Class[0]);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			obj = m.invoke(new Object[0]);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Debug.logInfo("Method resolve: Operand " + operandName + " evaluated to value " + obj, module);
		Object ret = newField(obj.toString());
		return ret;
	}

	public String getComponentName() {
		return COMP_NAME;
	}

	@Override
	public Object resolve(String key){
		System.out.println("\n\n\n\n\n\n\n\n\n\n\n   .................. Please check this code. \n\n\n\n\n\n\n\n\n\n\n");
		return null;
	}
}
