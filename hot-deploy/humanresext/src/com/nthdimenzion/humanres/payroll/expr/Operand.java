package com.nthdimenzion.humanres.payroll.expr;

import com.nthdimenzion.humanres.payroll.calc.ResultContext;

public interface Operand {

	public void evaluate(ResultContext resultContext);
}
