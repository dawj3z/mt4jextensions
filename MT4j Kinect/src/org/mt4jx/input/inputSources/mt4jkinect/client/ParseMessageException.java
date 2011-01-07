package org.mt4jx.input.inputSources.mt4jkinect.client;

public class ParseMessageException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ParseMessageException(Exception e){
		super(e);
	}
	public ParseMessageException(String msg){
		super(msg);
	}
}
