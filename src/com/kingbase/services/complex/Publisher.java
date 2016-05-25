package com.kingbase.services.complex;

import javax.xml.ws.Endpoint;

public class Publisher {

	public static void main(String[] args) {
		Endpoint.publish("http://192.168.1.102:9001/serices/complex", new ComplexModelService());
	}
}
