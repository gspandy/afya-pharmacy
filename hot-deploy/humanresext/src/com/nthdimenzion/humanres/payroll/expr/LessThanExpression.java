package com.nthdimenzion.humanres.payroll.expr;

import com.nthdimenzion.humanres.payroll.calc.ResultContext;


/**
 * @author sandeep
 *
 */
public class LessThanExpression extends Expression {
	public LessThanExpression(Operand left, Operand right) {
		super(left, right);
	}

	public void evaluate(ResultContext context) {
		left.evaluate(context);
		Number num1 = context.numberResult();
		right.evaluate(context);
		Number num2 = context.numberResult();
		context.setResult(num1.doubleValue() < num2.doubleValue());
	}
}
