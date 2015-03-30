package com.nthdimenzion.humanres.payroll.resolver;

import java.util.List;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import com.nthdimenzion.humanres.payroll.calc.PayrollContext;
import com.nthdimenzion.humanres.payroll.calc.ResolverManager;
import com.nthdimenzion.humanres.payroll.calc.Rule;
import com.nthdimenzion.humanres.payroll.expr.Expression;

public final class RuleResolver extends AbstractResolver {

    public static final String COMP_NAME = "RULE";
    private static final String module = RuleResolver.class.getName();

    public String getComponentName() {
        return COMP_NAME;
    }

    public Object resolve(String ruleId) {

        Debug.logInfo(" Resolving RuleId = " + ruleId, module);
        PayrollContext workingMemory = PayrollContext.getInstance();
        GenericDelegator delegator = (GenericDelegator) workingMemory.get("delegator");
        GenericValue userLogin = (GenericValue) workingMemory.get(PayrollContext.USER_LOGIN);
        GenericValue payrollRule = null;
        try {
            payrollRule = delegator.findOne("PayrollRule", false, "ruleId", ruleId);
        } catch (GenericEntityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Rule rule = new Rule(payrollRule.getString("ruleId"), payrollRule.getString("ruleName"), payrollRule.getString("ruleDescription"));
        rule.setAggregateFunc(payrollRule.getString("aggregateFunc"));
        rule.setDefaultValue(payrollRule.getDouble("defaultValue"));
        rule.setIgnoreZero("N".equals(payrollRule.getString("ignoreZero")) ? false : true);
        setConditionsAndActions(rule, userLogin);
        Debug.logInfo(" Rule = " + rule, module);
        return rule;
    }

    private void setConditionsAndActions(Rule rule, GenericValue userLogin) {

        try {
            GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
            EntityCondition ruleIdCondition = EntityCondition.makeCondition("ruleId", rule.getId());
            List<GenericValue> conditionsAndActions = delegator.findList("RuleConditionAction", ruleIdCondition, null, null, null, false);
            ResolverManager mgr = PayrollContext.getResolver();
            for (GenericValue conditionAndAction : conditionsAndActions) {
                String conditionId = conditionAndAction.getString("conditionId");
                String operandName = mgr.encode(new String[]{"CONDITION", conditionId});
                Expression conditionExpression = (Expression) mgr.resolve(operandName);
                String actionId = conditionAndAction.getString("actionId");
                operandName = mgr.encode(new String[]{"ACTION", actionId});
                Expression action = (Expression) mgr.resolve(operandName);
                if (conditionExpression != null) {
                    rule.put(conditionExpression, action);
                }
            }
        } catch (GenericEntityException gee) {

        }
    }

    @Override
    public Object resolve(String key, GenericDelegator delegator) {
        // TODO Auto-generated method stub
        return null;
    }
}
