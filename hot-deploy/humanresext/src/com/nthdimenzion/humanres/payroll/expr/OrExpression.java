package com.nthdimenzion.humanres.payroll.expr;

import com.nthdimenzion.humanres.payroll.calc.ResultContext;

/**
 * @author sandeep
 * 
 */
public class OrExpression extends Expression {

	public OrExpression(Operand left, Operand right) {
		super(left, right);
	}

	public void evaluate(ResultContext context) {
		left.evaluate(context);
		boolean b1 = context.booleanResult();
		right.evaluate(context);
		boolean b2 = context.booleanResult();
		context.setResult(b1 || b2);
	}

	@Override
	public String toString() {
		return "( " + left.toString() + " || " + right.toString() + " )";
	}
}
