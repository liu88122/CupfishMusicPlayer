package com.cupfish.musicplayer.exception;

public class NetTimeoutException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1042362072074977097L;

	public NetTimeoutException() {
		super();
	}

	public NetTimeoutException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public NetTimeoutException(String detailMessage) {
		super(detailMessage);
	}

	public NetTimeoutException(Throwable throwable) {
		super(throwable);
	}

	
	
}
