package com.nthdimenzion.humanres.payroll.expr;

import com.nthdimenzion.humanres.payroll.calc.ResultContext;

public class AndExpression extends Expression {

	public AndExpression(Operand left, Operand right) {
		super(left, right);
	}

	public void evaluate(ResultContext context) {
		left.evaluate(context);
		Boolean b1 = context.booleanResult();
		right.evaluate(context);
		Boolean b2 = context.booleanResult();
		context.setResult(b1 && b2);
	}

	@Override
	public String toString() {
		return left.toString() + " && " + right.toString();
	}
}
