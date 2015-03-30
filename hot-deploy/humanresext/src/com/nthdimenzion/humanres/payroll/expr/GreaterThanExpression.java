package com.nthdimenzion.humanres.payroll.expr;

import com.nthdimenzion.humanres.payroll.calc.ResultContext;

public class GreaterThanExpression extends Expression {

	public GreaterThanExpression(Operand left, Operand right) {
		super(left, right);
	}

	public void evaluate(ResultContext context) {
		left.evaluate(context);
		Number num1 = context.numberResult();
		right.evaluate(context);
		Number num2 = context.numberResult();
		context.setResult(num1.doubleValue() > num2.doubleValue());
	}

	@Override
	public String toString() {
		return "Greater Than Expression " + left.toString() + " >= " + right.toString();
	}
}