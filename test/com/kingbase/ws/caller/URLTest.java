package com.kingbase.ws.caller;

public class URLTest {

	public static void main(String[] args) {
		String wsdllocation="http://192.168.1.36:8080/default/orgbizService?wsdl";
		int indexOf = wsdllocation.indexOf("/", "http://".length());
		
		String substring = wsdllocation.substring(0, indexOf);
		System.out.println(substring);
	}
}
