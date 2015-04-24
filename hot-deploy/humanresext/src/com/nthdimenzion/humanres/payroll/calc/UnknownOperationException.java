/**
 * 
 */
package com.nthdimenzion.humanres.payroll.calc;


/**
 * @author sandeep
 *
 */
public class UnknownOperationException extends RuntimeException {

	public UnknownOperationException(String message, Throwable t){
		super(message, t);
	}

	public UnknownOperationException(String message){
		super(message);
	}
	private static final long serialVersionUID = 1L;
}
