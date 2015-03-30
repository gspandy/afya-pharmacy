package com.nthdimenzion.humanres.payroll.calc;

import java.util.LinkedList;
import java.util.List;
import javolution.util.FastList;
import org.ofbiz.base.util.Debug;
import com.nthdimenzion.humanres.payroll.expr.AggregateFunction;
import com.nthdimenzion.humanres.payroll.expr.Expression;

public class Rule {

	private final List<Action> actions = new LinkedList<Action>();

	private static final Number DEFAULTRESULT = 0;

	private static final String module = Rule.class.getName();

	private String aggregateFunc;

	private String id;

	private String name;

	private String description;

	private boolean ignoreZero;

	private Number defaultValue;

	public Rule() {}

	public Rule(String id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public boolean isIgnoreZero() {
		return ignoreZero;
	}

	public void setIgnoreZero(boolean ignoreZero) {
		this.ignoreZero = ignoreZero;
	}

	public Number getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Number defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAggregateFunc() {
		return aggregateFunc;
	}

	public void setAggregateFunc(String aggregateFunc) {
		this.aggregateFunc = aggregateFunc;
	}

	public Number evaluate() {
		if (aggregateFunc == null) {
			for (Action action : actions) {
				ResultContext context = new ResultContext();
				action.condition.evaluate(context);
				Debug.logInfo("Method evaluate  Result --> " + context.booleanResult(), module);
				if (context.booleanResult()) {
					action.expression.evaluate(context);
					Debug.logInfo("Method evaluate Result --> " + context.numberResult(), module);
					return context.numberResult();
				}
			}
		} else {
			List<Number> results = FastList.newInstance();
			for (Action action : actions) {
				ResultContext context = new ResultContext();
				action.condition.evaluate(context);
				Debug.logInfo("Method evaluate  Result --> " + context.booleanResult(), module);
				if (context.booleanResult()) {
					Debug.logInfo("Method evaluating Expression " + action.expression, module);
					action.expression.evaluate(context);
					Debug.logInfo("Method evaluate Result --> " + context.numberResult(), module);
					Number result = context.numberResult();
					if (result.intValue() > 0 || !isIgnoreZero()) {
						results.add(result);
					}
				}
			}
			System.out.println(" RESULT FOR AGGREGATION " + results);
			Debug.logInfo(" RESULT FOR AGGREGATION " + results, "Rule");
			return AggregateFunction.evaluate(aggregateFunc, results.toArray(new Number[results.size()]));
		}

		return defaultValue != null ? defaultValue : DEFAULTRESULT;
	}

	public void put(Expression condition, Expression expression) {
		actions.add(new Action(condition, expression));
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder("Rule");
		buffer.append(" Action ").append(actions.toString());
		return buffer.toString();
	}

	class Action {

		Expression condition;
		Expression expression;

		public Action(Expression cond, Expression expression) {
			this.condition = cond;
			this.expression = expression;
		}

		@Override
		public String toString() {
			StringBuilder buffer = new StringBuilder("Action = \n\t{ ");
			buffer.append(" \t\tCondition = [ " + condition.toString() + " ]\n");
			buffer.append(" \t\tExpression = [ " + expression.toString() + " ] \n\t}");
			return buffer.toString();
		}
	}
}