package com.nthdimenzion.humanres.payroll.calc;

import com.nthdimenzion.humanres.payroll.expr.Expression;
import com.nthdimenzion.humanres.payroll.expr.ExpressionBuilder;
import com.nthdimenzion.humanres.payroll.expr.Field;


public class TestCondition {

	public static void main(String[] args) {

		Field<Number> field1 = new Field<Number>(3);
		Field<Number> field2 = new Field<Number>(5);
		Field<Boolean> field5 = new Field<Boolean>(true);
		Field<Boolean> field6 = new Field<Boolean>(true);
		ResultContext context = new ResultContext();

		Expression exp1 = ExpressionBuilder.buildExpression("add", field1, field2);
		exp1.evaluate(context);
		System.out.println(context.numberResult());

		Expression exp2 = ExpressionBuilder.buildExpression("and", field5, field6);// true
		exp2.evaluate(context);
		System.out.println(context.booleanResult());

		Expression exp3 = ExpressionBuilder.buildExpression("greaterthan", field1, field2);// false
		exp3.evaluate(context);
		System.out.println(context.booleanResult());
	}
}