package com.ibm.demo.day2.exception;

public class NoAgeEligibilityException extends RuntimeException {

	private static final long serialVersionUID = 628355502492179165L;

	public NoAgeEligibilityException() {
		super();
	}

	public NoAgeEligibilityException(String message) {
		super(message);
	}
}
