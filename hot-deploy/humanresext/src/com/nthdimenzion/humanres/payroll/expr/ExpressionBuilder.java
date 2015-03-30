package com.nthdimenzion.humanres.payroll.expr;

import java.lang.reflect.Constructor;
import java.util.Map;
import javolution.util.FastMap;
import com.nthdimenzion.humanres.payroll.calc.UnknownOperationException;

/**
 * @author sandeep
 * 
 */
public class ExpressionBuilder {

	public static void resisterExpression(String operatorName, Class<? extends Expression> expressionType) {
		operatorExpressionMap.put(operatorName, expressionType);
	}

	public static Expression buildExpression(String operatorName, Operand operandOne, Operand operandTwo) {
		Class<? extends Expression> expressionType = operatorExpressionMap.get(operatorName);
		if (expressionType == null)
			throw new UnknownOperationException("\n\nOperation " + operatorName + " Not Found\n\n\n");
		try {
			Constructor<? extends Expression> constructor = expressionType.getConstructor(Operand.class, Operand.class);
			return constructor.newInstance(operandOne, operandTwo);
		} catch (Exception e) {
			throw new UnknownOperationException("\n\nOperator " + operatorName + " Could not be built \n\n\n", e);
		}
	}

	private static final Map<String, Class<? extends Expression>> operatorExpressionMap = FastMap.newInstance();

	static {
		resisterExpression("ADD", AddExpression.class);
		resisterExpression("SUBTRACT", SubtractExpression.class);
		resisterExpression("MULTIPLY", MultiplyExpression.class);
		resisterExpression("DIVIDE", DivideExpression.class);
		resisterExpression("PERCENT", PercentageExpression.class);
		
		resisterExpression("AND", AndExpression.class);
		resisterExpression("OR", OrExpression.class);
		resisterExpression("NOT", NotExpression.class);

		resisterExpression("EQUALS", EqualsExpression.class);
		resisterExpression("GREATER-THAN", GreaterThanExpression.class);
		resisterExpression("GREATER-THAN-EQUALS", GreaterThanEqualsExpression.class);
		resisterExpression("LESS-THAN", LessThanExpression.class);
		resisterExpression("LESS-THAN-EQUALS", LessThanExpression.class);
	}
}