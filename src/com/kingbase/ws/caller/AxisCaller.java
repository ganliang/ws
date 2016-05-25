package com.kingbase.ws.caller;

import java.util.HashMap;
import java.util.Map;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.log4j.Logger;

import com.kingbase.ws.utils.XMLUtil;

/**
 * 使用axis2调用wsdl
 * @author ganliang
 */
public class AxisCaller {

	private static final Logger log=Logger.getLogger(AxisCaller.class);
	
	/**
	 * 调用wsdl方法
	 * @param urlwsdl url地址
	 * @param nameSpace 命名空间
	 * @param methodName 调用的方法名称
	 * @param wsdlType wsdl类型 soap xsd 
	 * @param parameterMap 参数map
	 * @return
	 * @throws AxisFault 
	 */
	public String caller(String url,String nameSpace,String wsdlType, String methodName, Map<String, Object> parameterMap) throws AxisFault {
		OMElement result = null;
		Options options = new Options();
		// 指定调用WebService的URL
		options.setTo(new EndpointReference(url));
		options.setAction(nameSpace+methodName);
		options.setExceptionToBeThrownOnSOAPFault(true);
		
		ServiceClient serviceClient = new ServiceClient();
		serviceClient.setOptions(options);

		
		OMElement parameterElement = XMLUtil.createParameterElement(nameSpace,wsdlType,methodName,parameterMap);
		System.out.println(parameterElement);
		
		result = serviceClient.sendReceive(parameterElement);
		log.debug("调用结果 "+result.toString());	
		return result.toString();
	}
	
	
	
	public static void main(String[] args){
		AxisCaller caller=new AxisCaller();
		Map<String, Object> parameterMap=new HashMap<String, Object>();
		parameterMap.put("theImageType", "1");
		parameterMap.put("theStockCode", "sh000001");
		
		String cal="";
		try {
			//cal=caller.caller("http://www.webxml.com.cn/WebServices/WeatherWS.asmx?wsdl", "http://WebXml.com.cn/","soap", "getRegionDataset", parameterMap);
			//cal = caller.caller("http://192.168.8.144:9999/services/helloWord?wsdl", "http://service.cytoscape.com/","xsd", "say", parameterMap);
			cal = caller.caller("http://192.168.1.36:8080/default/orgbizService?wsdl", "http://www.primeton.com/orgbizService","soap", "addOrg", parameterMap);
		} catch (AxisFault e) {
			log.error("",e);
		}
		System.out.println(cal);
	}
}
