package com.kingbase.services.helloWord;

import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(serviceName="HellowordService")
public class HellowordService {

	public void print(@WebParam(name="name") String name){
		System.out.println("hello "+name);
	}
	
	public String sayHi(@WebParam(name="name") String name){
		return "hello "+name;
	}
	
	public String love(@WebParam(name="name") String name,@WebParam(name="lover") String lover){
		return name+"  love  "+lover;
	}
	
}
