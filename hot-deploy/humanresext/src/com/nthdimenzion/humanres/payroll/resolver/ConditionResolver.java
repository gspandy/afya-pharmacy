package com.nthdimenzion.humanres.payroll.resolver;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import com.nthdimenzion.humanres.payroll.calc.PayrollContext;
import com.nthdimenzion.humanres.payroll.calc.ResolverManager;
import com.nthdimenzion.humanres.payroll.expr.Expression;
import com.nthdimenzion.humanres.payroll.expr.ExpressionBuilder;
import com.nthdimenzion.humanres.payroll.expr.Operand;

public class ConditionResolver extends AbstractResolver {

    private static final String COMP_NAME = "CONDITION";

    public String getComponentName() {
        // TODO Auto-generated method stub
        return COMP_NAME;
    }

    public Expression resolve(String conditionId) {
        PayrollContext workingMemory = PayrollContext.getInstance();
        GenericDelegator delegator = (GenericDelegator) workingMemory.get("delegator");
        Expression condition = null;
        try {
            GenericValue conditionGv = delegator.findOne("PayrollCondition", false, "conditionId", conditionId);
            ResolverManager mgr = PayrollContext.getResolver();
            Operand operandOne = (Operand) mgr.resolve(conditionGv.getString("operandOne"));
            Operand operandTwo = (Operand) mgr.resolve(conditionGv.getString("operandTwo"));
            String operatorName = conditionGv.getString("operatorId");
            if (operandOne != null && operandTwo != null) {
                condition = ExpressionBuilder.buildExpression(operatorName, operandOne, operandTwo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Debug.logInfo("Method resolve: Operand " + conditionId + " evaluated to value " + condition, "ConditionResolver");

        return condition;
    }

    @Override
    public Object resolve(String key, GenericDelegator delegator) {
        // TODO Auto-generated method stub
        return null;
    }
}
