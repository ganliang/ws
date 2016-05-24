package com.kingbase.ws.caller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class HttpCallerTest extends TestCase{
	
	public void testSmallImage(){
		HttpCaller caller=new HttpCaller();
		try {
			Map<String, String> parameterMap=new HashMap<String, String>();
			parameterMap.put("theStockCode", "sh000001");
			parameterMap.put("theImageType", "1");
			
			String responseBody = caller.caller("http://www.webxml.com.cn/webservices/ChinaStockSmallImageWS.asmx?wsdl", "http://WebXml.com.cn/", "getSmallImage","soap", parameterMap);
			System.out.println(responseBody);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void testSmallImageByte(){
		HttpCaller caller=new HttpCaller();
		try {
			Map<String, String> parameterMap=new HashMap<String, String>();
			parameterMap.put("theStockCode", "sh000001");
			parameterMap.put("theImageType", "1");
			
			String responseBody = caller.caller("http://www.webxml.com.cn/webservices/ChinaStockSmallImageWS.asmx?wsdl", "http://WebXml.com.cn/", "getSmallImageByte","soap", parameterMap);
			System.out.println(responseBody);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void testPrint(){
		HttpCaller caller=new HttpCaller();
		try {
			Map<String, String> parameterMap=new HashMap<String, String>();
			parameterMap.put("name", "甘亮");
			
			String responseBody = caller.caller("http://192.168.1.102:9000/serices/HelloWorld?wsdl", "http://helloWord.services.kingbase.com/","print", "xsd", parameterMap);
			System.out.println(responseBody);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void testLove(){
		HttpCaller caller=new HttpCaller();
		try {
			Map<String, String> parameterMap=new HashMap<String, String>();
			parameterMap.put("name", "甘亮");
			parameterMap.put("lover", "陈伟生");
			
			String responseBody = caller.caller("http://192.168.1.102:9000/serices/HelloWorld?wsdl", "http://helloWord.services.kingbase.com/","love", "xsd", parameterMap);
			System.out.println(responseBody);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
