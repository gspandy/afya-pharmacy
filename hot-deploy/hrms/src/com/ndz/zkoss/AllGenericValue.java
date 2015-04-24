package com.ndz.zkoss;

import org.ofbiz.entity.GenericValue;

public class AllGenericValue extends GenericValue {
	private static final long serialVersionUID = 1L;
	@Override
	public String getString(String name) {
		return "All";
	}
}
