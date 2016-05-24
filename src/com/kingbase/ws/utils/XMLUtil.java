package com.kingbase.ws.utils;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class XMLUtil {

	/**
	 * 将xml转化为document
	 * @param xml
	 * @return
	 */
	public static Element getRootElement(String xml){
		Element rootElement=null;
		try {
			Document document = DocumentHelper.parseText(xml);
			//添加根几点
			rootElement = document.getRootElement();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return rootElement;
	}
	/**
	 * 将xml字符串添加换行 空格等
	 * @param json
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String encodeParameterXML(String xml) {
		StringBuilder builder=new StringBuilder();
		
		Element rootElement = getRootElement(xml);
		
		String namespaceURI = rootElement.getNamespaceURI();
		builder.append("<"+rootElement.getName()+"  xmlns=\""+namespaceURI+"\">\r\n");
		//遍历子节点
		List<Element> elements = rootElement.elements();
		for (Element element : elements) {
			String name = element.getName();
			String text = element.getText();
			builder.append("&nbsp&nbsp<"+name+">"+text+"</"+name+">\r\n");
		}
		
		builder.append("</"+rootElement.getName()+">");	
		return builder.toString();
	}
	
	/**
	 * 将xml字符串添加换行 空格等
	 * @param json
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String encodeResultXML(String xml) {
		StringBuilder builder=new StringBuilder();
		
		Element rootElement = getRootElement(xml);
		
		String namespaceURI = rootElement.getNamespaceURI();
		builder.append("<"+rootElement.getName()+"  xmlns=\""+namespaceURI+"\">\r\n");
		//遍历子节点
		List<Element> elements = rootElement.elements();
		for (Element element : elements) {
			String name = element.getName();
			builder.append("&nbsp&nbsp<"+name+">\r\n");
			
			String text = element.getTextTrim();
			if(text!=null&&!"".equals(text)){
				builder.append("&nbsp&nbsp"+text+"\r\n");
			}else{
				List<Element> elements2 = element.elements();
				for (Element ele : elements2) {
					builder.append("&nbsp&nbsp&nbsp&nbsp<"+ele.getName()+">"+ele.getText()+"</"+ele.getName()+">\r\n");
				}
			}
			
			builder.append("&nbsp&nbsp</"+name+">\r\n");
		}
		
		builder.append("</"+rootElement.getName()+">");	
		return builder.toString();
	}
	
	/**
	 * 构建参数
	 * @param nameSpace
	 * @param methodName
	 * @param methodName2 
	 * @param parameterMap
	 * @return
	 */
	public static OMElement createParameterElement(String nameSpace,String wsdlType, String methodName, Map<String, Object> parameterMap) {
		if(wsdlType==null||"".equals(wsdlType)||"soap".equalsIgnoreCase(wsdlType)){
			return createSOAPParameterElement(nameSpace, methodName, parameterMap);
		}else{
			return createXSDParameterElement(nameSpace, methodName, parameterMap);
		}
		
	}
	
	/**
	 * 创建soap格式的参数
	 * @param nameSpace
	 * @param methodName
	 * @param parameterMap
	 * @return
	 */
	public static OMElement createSOAPParameterElement(String nameSpace,String methodName, Map<String, Object> parameterMap){
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace(nameSpace, "");
		OMElement method = fac.createOMElement(methodName, omNs);
        
		for (Entry<String, Object> entry : parameterMap.entrySet()) {
			OMElement param = fac.createOMElement(entry.getKey(), omNs);
			param.addChild(fac.createOMText(param, String.valueOf(entry.getValue())));
			method.addChild(param);
		}
		method.build();
		return method;
	}
	
	/**
	 * 创建xsd格式的参数
	 * @param nameSpace
	 * @param methodName
	 * @param parameterMap
	 * @return
	 */
	private static OMElement createXSDParameterElement(String nameSpace, String methodName,
			Map<String, Object> parameterMap) {
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace(nameSpace, "xsd");
		OMElement method = fac.createOMElement(methodName, omNs);
        
		for (Entry<String, Object> entry : parameterMap.entrySet()) {
			OMElement param = fac.createOMElement(entry.getKey(), null);
			param.addChild(fac.createOMText(param, String.valueOf(entry.getValue())));
			method.addChild(param);
		}
		method.build();
		return method;
	}
}
