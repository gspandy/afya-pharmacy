package com.nthdimenzion.humanres.payroll.expr;

import com.nthdimenzion.humanres.payroll.calc.ResultContext;

/**
 * @author sandeep
 * 
 */
public class PercentageExpression extends Expression {

	public PercentageExpression(Operand left, Operand right) {
		super(left, right);
	}

	public void evaluate(ResultContext context) {
		left.evaluate(context);
		double num1 = context.doubleResult();
		right.evaluate(context);
		double num2 = context.doubleResult();
		context.setResult(num1 * num2 / 100);
	}

	@Override
	public String toString() {
		return left.toString() + " * " + right.toString() + "%";
	}
}
