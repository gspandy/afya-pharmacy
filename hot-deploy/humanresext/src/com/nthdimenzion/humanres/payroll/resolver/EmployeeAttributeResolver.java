package com.nthdimenzion.humanres.payroll.resolver;

import java.util.Map;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericDelegator;

import com.nthdimenzion.humanres.payroll.calc.Resolver;

public class EmployeeAttributeResolver implements Resolver {

	private static final String COMP_NAME = "EMPL";
	private static final String module = EmployeeAttributeResolver.class.getName();

	public String getComponentName() {
		return COMP_NAME;
	}

	public Object resolve(String key) {
		Debug.logInfo(" Resolving " + key + " against the Employee ", module);
		FieldResolver resolver = new FieldResolver();
		return resolver.resolve(key);
	}

	public void setLookupContext(Map map) {}

	@Override
	public Object resolve(String key, GenericDelegator delegator) {
		return null;
	}
}
