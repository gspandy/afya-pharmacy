package com.nthdimenzion.humanres.payroll.resolver;

import java.util.Map;
import com.nthdimenzion.humanres.payroll.calc.Resolver;
import com.nthdimenzion.humanres.payroll.expr.Field;
import com.nthdimenzion.humanres.payroll.expr.Operand;

public abstract class AbstractResolver implements Resolver {

	public static Operand newField(String operandString) {
		com.nthdimenzion.humanres.payroll.expr.Operand operand = null;
		Number numOperand = null;
		try {
			numOperand = Double.parseDouble(operandString);
		} catch (NumberFormatException nfe) {}

		Boolean boolOperand = null;
		if ("true".equalsIgnoreCase(operandString) || "false".equalsIgnoreCase(operandString)) {
			operand = new Field<Boolean>(Boolean.parseBoolean(operandString));
		} else if (numOperand != null && boolOperand == null) {
			operand = new Field<Number>(numOperand);
		} else if (numOperand == null && boolOperand == null) {
			operand = new Field<String>(operandString);
		}
		return operand;
	}

	public void setLookupContext(Map ctx) {}

}
