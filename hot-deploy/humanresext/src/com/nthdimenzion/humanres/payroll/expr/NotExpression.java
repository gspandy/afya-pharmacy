package com.nthdimenzion.humanres.payroll.expr;

import com.nthdimenzion.humanres.payroll.calc.ResultContext;

public class NotExpression extends Expression {

	public NotExpression(Operand left, Operand right) {
		super(left, right);
	}

	public void evaluate(ResultContext context) {
		left.evaluate(context);
		boolean bool1 = context.booleanResult();
		right.evaluate(context);
		boolean bool2 = context.booleanResult();
		context.setResult(!(bool1 && bool2));
	}

	@Override
	public String toString() {
		return "!( " + left.toString() + " && " + right.toString() + " )";
	}
}
