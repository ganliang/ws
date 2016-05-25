package com.kingbase.wsdl.exceptions;

public class WSDLServiceException extends IllegalArgumentException{

	private static final long serialVersionUID = 4028548326916467944L;
	
	public WSDLServiceException() {
		super();
	}

	public WSDLServiceException(String string) {
		super(string);
	}

	@Override
	public String getMessage() {
		return super.getMessage();
	}

	@Override
	public void printStackTrace() {
		super.printStackTrace();
	}

	
}
