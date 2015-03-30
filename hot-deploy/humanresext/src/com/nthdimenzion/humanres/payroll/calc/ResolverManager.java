package com.nthdimenzion.humanres.payroll.calc;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilProperties;
import com.nthdimenzion.humanres.payroll.expr.Operand;
import com.nthdimenzion.humanres.payroll.resolver.AbstractResolver;

public class ResolverManager {

	private static final Map<String, Constructor<?>> componentClasses = FastMap.newInstance();
	private static final String module = ResolverManager.class.getName();
	private OperandEncoder encoder = null;

	private static final Map<String, Resolver> resolvers = FastMap.newInstance();

	public ResolverManager() {

		for (String key : componentClasses.keySet()) {
			Constructor constructor = componentClasses.get(key);
			Resolver resolver = null;
			try {
				resolver = (Resolver) constructor.newInstance(new Object[0]);
			} catch (Throwable t) {
				t.printStackTrace();
			}
			resolvers.put(key, resolver);
		}
	}

	public ResolverManager(OperandEncoder encoder) {
		this();
		this.encoder = encoder;
	}

	public Resolver getResolver(String componentName) {
		return resolvers.get(componentName);
	}

	public OperandEncoder getEncoder() {
		if (this.encoder == null) {
			encoder = new OperandEncoderImpl();
		}
		return this.encoder;
	}

	public void register(String componentName, Resolver resolver) {
		resolvers.put(componentName, resolver);
	}

	public Object resolve(String operandName) {
		Debug.logInfo("resolve Key = " + operandName, module);
		String component, key = "";
		String[] parts = getEncoder().decode(operandName);
		Operand operand = null;
		//Dynamic Based on Formula or Expression
		if (parts != null) {
			component = parts[0];
			key = parts[1];
			Resolver resolver = resolvers.get(component);
			if (resolver != null && key!=null) {
				return resolver.resolve(key);
			}
		}
		//Constants
		operand = AbstractResolver.newField(operandName);
		return operand;
	}

	public String encode(String[] parts) {
		return getEncoder().encode(parts);
	}

	static {
		Properties properties = UtilProperties.getProperties("resolver");
		for (Entry<Object, Object> entry : properties.entrySet()) {
			String key = entry.getKey().toString();
			String resolverName = entry.getValue().toString();
			Constructor<?> constructor = null;
			try {
				constructor = Class.forName(resolverName, true, Thread.currentThread().getContextClassLoader()).getConstructor(new Class[0]);
				componentClasses.put(key, constructor);
			} catch (Throwable t) {
				// TODO Auto-generated catch block
				t.printStackTrace();
			}
			Debug.logInfo("*** Resolver " + resolverName + " registered with key " + key, module);
		}
	}

	public String getComponentName() {
		// TODO Auto-generated method stub
		return null;
	}
}
