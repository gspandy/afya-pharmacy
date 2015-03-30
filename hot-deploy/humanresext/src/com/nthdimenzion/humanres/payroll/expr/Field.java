package com.nthdimenzion.humanres.payroll.expr;

import com.nthdimenzion.humanres.payroll.calc.ResultContext;

public class Field<T> implements Operand {

	private final T value;

	public Field(T value) {
		this.value = value;
	}

	public void evaluate(ResultContext context) {
		context.setResult(value);
	}

	public T getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value.toString();
	}

}