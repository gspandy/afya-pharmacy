package com.nthdimenzion.humanres.payroll.resolver;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import com.nthdimenzion.humanres.payroll.calc.PayrollContext;
import com.nthdimenzion.humanres.payroll.calc.ResolverManager;
import com.nthdimenzion.humanres.payroll.expr.Expression;
import com.nthdimenzion.humanres.payroll.expr.ExpressionBuilder;
import com.nthdimenzion.humanres.payroll.expr.Operand;

public class ActionResolver extends AbstractResolver {

	private static final String COMP_NAME = "ACTION";

	public String getComponentName() {
		return COMP_NAME;
	}

	public Object resolve(String actionId) {
		PayrollContext workingMemory = PayrollContext.getInstance();
		GenericDelegator delegator = (GenericDelegator) workingMemory.get("delegator");
		Expression action = null;
		try {
			GenericValue actionGV = delegator.findOne("PayrollAction", false, "actionId", actionId);
			ResolverManager mgr = PayrollContext.getResolver();
			Operand operandOne = (Operand) mgr.resolve(actionGV.getString("operandOne"));
			Operand operandTwo = (Operand) mgr.resolve(actionGV.getString("operandTwo"));
			String operatorName = actionGV.getString("operatorId");
			action = ExpressionBuilder.buildExpression(operatorName, operandOne, operandTwo);
		} catch (GenericEntityException gee) {

		}
		Debug.logInfo("Method resolve: Operand " + actionId + " evaluated to value " + action, "ActionResolver");
		return action;
	}

	@Override
	public Object resolve(String key, GenericDelegator delegator) {
		// TODO Auto-generated method stub
		return null;
	}
}
