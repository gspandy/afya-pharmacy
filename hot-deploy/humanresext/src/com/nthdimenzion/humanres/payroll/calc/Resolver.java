package com.nthdimenzion.humanres.payroll.calc;

import java.util.Map;

import org.ofbiz.entity.GenericDelegator;

/*
 * 
 * The implementation of the Resolve should register themselves
 * to the RuleResolverManager
 * 
 */
public interface Resolver {
	
	public Object resolve(String key);
	
	public Object resolve(String key,GenericDelegator delegator);

	public String getComponentName();

	public void setLookupContext(Map ctx);
}
