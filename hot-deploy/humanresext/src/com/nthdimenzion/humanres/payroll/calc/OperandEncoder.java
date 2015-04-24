package com.nthdimenzion.humanres.payroll.calc;

/**
 * The implementation should decode and return a String[] with index = 0 as the
 * COMPONENT NAME and index = 1 as the key;
 * 
 * @author pradyumna
 * 
 */
public interface OperandEncoder {

	public String[] decode(String operandName);

	public String encode(String[] parts);
}
