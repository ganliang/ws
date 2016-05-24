package com.kingbase.ws.caller;

import java.util.HashMap;
import java.util.Map;

import org.apache.axis2.AxisFault;

import junit.framework.TestCase;

public class AxisCallerTest extends TestCase{

	public void test(){
		AxisCaller caller=new AxisCaller();
		Map<String, Object> parameterMap=new HashMap<String, Object>();
		parameterMap.put("name", "甘亮");
		parameterMap.put("lover", "陈伟生");
		
		String cal="";
		try {
			//cal=caller.caller("http://www.webxml.com.cn/WebServices/WeatherWS.asmx?wsdl", "http://WebXml.com.cn/","soap", "getRegionDataset", parameterMap);
			//cal = caller.caller("http://192.168.8.144:9999/services/helloWord?wsdl", "http://service.cytoscape.com/","xsd", "say", parameterMap);
			cal = caller.caller("http://192.168.1.102:9000/serices/HelloWorld?wsdl", "http://helloWord.services.kingbase.com/","xsd", "love", parameterMap);
		} catch (AxisFault e) {
			System.out.println("eeeeeeeeeeeeeeeeeeeeeeee");
		}
		System.out.println(cal);
	}
}
