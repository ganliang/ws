package com.kingbase.ws.caller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.ParseException;
import org.dom4j.DocumentException;

import junit.framework.TestCase;

public class HttpCallerTest extends TestCase{
	
	public void testSmallImage() throws ParseException, DocumentException{
		HttpCaller caller=new HttpCaller();
		try {
			Map<String, Object> parameterMap=new HashMap<String, Object>();
			parameterMap.put("theStockCode", "sh000001");
			parameterMap.put("theImageType", "1");
			
			String responseBody = caller.caller("http://www.webxml.com.cn/webservices/ChinaStockSmallImageWS.asmx?wsdl", "http://WebXml.com.cn/","soap", "getSmallImage", parameterMap);
			System.out.println(responseBody);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void testSmallImageByte() throws ParseException, DocumentException{
		HttpCaller caller=new HttpCaller();
		try {
			Map<String, Object> parameterMap=new HashMap<String, Object>();
			parameterMap.put("theStockCode", "sh000001");
			parameterMap.put("theImageType", "1");
			
			String responseBody = caller.caller("http://www.webxml.com.cn/webservices/ChinaStockSmallImageWS.asmx?wsdl", "http://WebXml.com.cn/","soap", "getSmallImageByte", parameterMap);
			System.out.println(responseBody);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void testPrint() throws ParseException, DocumentException{
		HttpCaller caller=new HttpCaller();
		try {
			Map<String, Object> parameterMap=new HashMap<String, Object>();
			parameterMap.put("name", "甘亮");
			
			String responseBody = caller.caller("http://192.168.8.144:9000/serices/HelloWorld?wsdl", "http://helloWord.services.kingbase.com/","xsd","print",  parameterMap);
			System.out.println(responseBody);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void testLove() throws ParseException, DocumentException{
		HttpCaller caller=new HttpCaller();
		try {
			Map<String, Object> parameterMap=new HashMap<String, Object>();
			parameterMap.put("name", "甘亮");
			parameterMap.put("lover", "陈伟生");
			
			String responseBody = caller.caller("http://192.168.8.144:9000/serices/HelloWorld?wsdl", "http://helloWord.services.kingbase.com/","xsd", "love", parameterMap);
			System.out.println(responseBody);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
