package com.nthdimenzion.humanres.payroll.calc;

import java.util.HashMap;
import java.util.Map;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;

public class PayrollContext {

	private static final ThreadLocal<PayrollContext> tl = new ThreadLocal<PayrollContext>();
	private Map<String, Object> workingMemory = null;
	public static final String DELEGATOR = "delegator";
	public static final String USER_LOGIN = "userLogin";
	public static final String RESOLVER_MGR = "ResolverManager";

	private PayrollContext() {
		workingMemory = new HashMap<String, Object>();
	}

	public static PayrollContext getInstance() {
		return tl.get();
	}

	public static PayrollContext getInstance(GenericValue userLogin) {
		GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();//GenericDelegator.getGenericDelegator("default");
		PayrollContext ctx = tl.get();
		if (ctx == null) {
			ctx = new PayrollContext();
			tl.set(ctx);
		}
		if (ctx.workingMemory.get(RESOLVER_MGR) == null) {
			ResolverManager mgr = new ResolverManager(new OperandEncoderImpl());
			ctx.workingMemory.put(RESOLVER_MGR, mgr);
		}
		ctx.workingMemory.put(PayrollContext.USER_LOGIN, userLogin);
		ctx.workingMemory.put(PayrollContext.DELEGATOR, delegator);
		return ctx;
	}

	public static PayrollContext getInstance(GenericValue userLogin, boolean createNew) {
		PayrollContext ctx = null;
		if (createNew) {
			ctx = new PayrollContext();
			tl.set(ctx);
		}
		ctx = getInstance(userLogin);
		return ctx;
	}

	public static ResolverManager getResolver() {
		PayrollContext ctx = tl.get();
		return (ResolverManager) ctx.workingMemory.get(RESOLVER_MGR);
	}

	public void putAll(Map<String, Object> map) {
		workingMemory.putAll(map);
	}

	public static void clear() {
		tl.remove();
	}

	public void put(String key, Object value) {
		workingMemory.put(key, value);
	}

	public Object get(String key) {
		return workingMemory.get(key);
	}

}
