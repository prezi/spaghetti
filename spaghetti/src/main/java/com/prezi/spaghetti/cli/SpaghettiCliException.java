package com.prezi.spaghetti.cli;

public class SpaghettiCliException extends RuntimeException {
	public SpaghettiCliException() {
	}

	public SpaghettiCliException(String s) {
		super(s);
	}

	public SpaghettiCliException(String s, Throwable throwable) {
		super(s, throwable);
	}

	public SpaghettiCliException(Throwable throwable) {
		super(throwable);
	}
}
