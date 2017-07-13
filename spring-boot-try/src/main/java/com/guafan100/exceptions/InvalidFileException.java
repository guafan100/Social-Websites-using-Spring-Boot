package com.guafan100.exceptions;

public class InvalidFileException extends Exception {

	//avoid warning
	private static final long serialVersionUID = 1L;

	public InvalidFileException(String message) {
		super(message);
	}
}
