package com.kingbase.services.helloWord;

import javax.xml.ws.Endpoint;

public class Publisher {

	public static void main(String[] args) {
	    Endpoint.publish("http://192.168.1.102:9000/serices/HelloWorld", new HellowordService());
	}
}
