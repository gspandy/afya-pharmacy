package com.nthdimenzion.humanres.payroll.expr;

/**
 * @author sandeep
 * 
 */
public abstract class Expression implements Operand {

	protected Operand left;
	protected Operand right;
	protected final String module = this.getClass().getName();

	public Expression(Operand left, Operand right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public String toString() {
		return " Left Operand " + left + " operator " + "Right Operand " + right;
	}
}