package com.smebiz.common;

public class UploadException extends Exception {

	public UploadException(String msg, Throwable t) {
		super(msg, t);
	}

	public UploadException(String msg) {
		super(msg);
	}
}
