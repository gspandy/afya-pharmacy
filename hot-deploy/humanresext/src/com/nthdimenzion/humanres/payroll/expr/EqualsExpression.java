package com.nthdimenzion.humanres.payroll.expr;

import com.nthdimenzion.humanres.payroll.calc.ResultContext;

public class EqualsExpression extends Expression {

	public EqualsExpression(Operand left, Operand right) {
		super(left, right);
		System.out.println("Created a EqualsExpression " + left + "==" + right);
	}

	public void evaluate(ResultContext context) {
		left.evaluate(context);
		Object obj1 = context.getResult();
		right.evaluate(context);
		Object obj2 = context.getResult();
		if (obj1 instanceof String && obj2 instanceof String) {
			context.setResult(obj1.toString().toUpperCase().equals(obj2.toString().toUpperCase()));
		} else
			context.setResult(obj1.equals(obj2));
	}

	@Override
	public String toString() {
		return left.toString() + " == " + right.toString();
	}
}
