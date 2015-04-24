package com.nthdimenzion.humanres.payroll.expr;

import java.util.Arrays;
import com.nthdimenzion.humanres.payroll.calc.ResultContext;

public class INExpression extends Expression {

	public INExpression(Operand left, Operand right) {
		super(left, right);
	}

	public void evaluate(ResultContext context) {
		left.evaluate(context);
		StringBuilder arrayDummy = new StringBuilder(context.stringResult());
		arrayDummy.deleteCharAt(0);
		arrayDummy.deleteCharAt(arrayDummy.length()-1);
		String[] array = arrayDummy.toString().split(",");
		context.setResult(Arrays.asList(array).contains(context.stringResult()));
	}

	@Override
	public String toString() {
		return "( " + left.toString() + " IN " + right.toString() + " )";
	}
}
