package com.nthdimenzion.humanres.payroll.calc;

public class OperandEncoderImpl implements OperandEncoder {

	private static char encodeSign = '#';
	private static char encodeBegin = '(';
	private static char encodeEnd = ')';
	private static char encodeOp = '.';

	public String[] decode(String operandName) {
		if (operandName != null && !operandName.startsWith(String.valueOf(encodeSign))) {
			return null;
		}
		String s = operandName.substring(1, operandName.length() - 1);
		int idx = s.indexOf(encodeBegin);
		if (idx == -1)
			return null;

		int idx2 = s.indexOf(encodeOp);
		if (idx2 == -1)
			return null;

		String componentName = s.substring(0, idx2);
		String key = s.substring(idx + 1, s.length() - 1);
		return new String[] { componentName, key };
	}

	public String encode(String[] parts) {
		return new StringBuilder().append(encodeSign).append(parts[0]).append(encodeOp).append(encodeBegin).append(parts[1]).append(encodeEnd).append(
				encodeSign).toString();
	}

	public static void main(String[] args) {
		args = new OperandEncoderImpl().decode("#RULE.(10000)#");

		System.out.println(args[0] + " -------- " + args[1]);
	}
}
