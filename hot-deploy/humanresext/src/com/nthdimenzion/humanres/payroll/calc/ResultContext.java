package com.nthdimenzion.humanres.payroll.calc;

import java.util.Map;
import javolution.util.FastMap;

/**
 * @author sandeep
 * 
 */
public class ResultContext {

	private final Map<String, Object> context = FastMap.newInstance();

	public static final String RESULTKEY = "result";

	public void setResult(Object value) {
		context.put(RESULTKEY, value);
	}

	public Object getResult() {
		return context.get(RESULTKEY);
	}

	public String stringResult() {
		return (String) get(RESULTKEY);
	}

	public Boolean booleanResult() {
		return (Boolean) get(RESULTKEY);
	}

	public Double doubleResult() {
		return numberResult().doubleValue();
	}

	public Number numberResult() {
		return (Number) get(RESULTKEY);
	}

	public Object get(String key) {
		return context.get(key);
	}

	public void set(String key, Object value) {
		context.put(key, value);
	}
}
