package com.nthdimenzion.humanres.payroll.calc;

public class InitializationException extends RuntimeException {

	public InitializationException() {
		super();
	}

	public InitializationException(String msg) {
		super(msg);
	}

	public InitializationException(Throwable t) {
		super(t);
	}
}
