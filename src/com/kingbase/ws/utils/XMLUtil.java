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
import org.dom4j.Namespace;

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
		/*OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace(nameSpace, "");
		OMElement method = fac.createOMElement(methodName, omNs);
        
		for (Entry<String, Object> entry : parameterMap.entrySet()) {
			OMElement param = fac.createOMElement(entry.getKey(), omNs);
			param.addChild(fac.createOMText(param, String.valueOf(entry.getValue())));
			method.addChild(param);
		}
		method.build();*/
		
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace(nameSpace, "");
		OMElement method = fac.createOMElement(methodName, omNs);
		
		OMElement org = fac.createOMElement("org", omNs);
		
		OMElement orgid = fac.createOMElement("orgid", omNs);
		orgid.addChild(fac.createOMText(orgid, "56"));
		
		OMElement parentorgid = fac.createOMElement("parentorgid", omNs);
		parentorgid.addChild(fac.createOMText(parentorgid, "1"));
		
		OMElement orgname = fac.createOMElement("orgname", omNs);
		orgname.addChild(fac.createOMText(orgname, "1"));
		
		OMElement orgcode = fac.createOMElement("orgcode", omNs);
		orgcode.addChild(fac.createOMText(orgcode, "1"));
		
		OMElement orgaddress = fac.createOMElement("orgaddress", omNs);
		orgaddress.addChild(fac.createOMText(orgaddress, "1"));
		
		org.addChild(orgid);
		org.addChild(parentorgid);
		org.addChild(orgname);
		org.addChild(orgcode);
		org.addChild(orgaddress);
		
		method.addChild(org);
		
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
	
	/**
	 * 打印一个字符串的xml
	 * @param xml
	 * @throws DocumentException
	 */
	public static String printXML(String xml) throws DocumentException{
		Document document = DocumentHelper.parseText(xml);
		//添加根几点
		Element rootElement = document.getRootElement();
		StringBuilder builder=new StringBuilder();
		
		recursionPrintXML(rootElement,builder,1);
		return builder.toString();
	}
	
	/**
	 * 递归打印xml
	 * @param element 元素节点
	 * @param builder
	 * @param deep 节点深度
	 */
	@SuppressWarnings("unchecked")
	private static void recursionPrintXML(Element element,StringBuilder builder,int deep) {
		//获取空格数
		StringBuilder deepBuilder=new StringBuilder();
		for (int i = 1; i < deep; i++) {
			deepBuilder.append("&nbsp");
		}
		
		boolean textOnly = element.isTextOnly();
		if(textOnly){
			builder.append(deepBuilder.toString()+"<"+element.getQualifiedName()+">"+element.getText()+"</"+element.getQualifiedName()+">\r\n");
		}else{
			builder.append(deepBuilder.toString()+"<"+element.getQualifiedName());
			//命名空间
			
			Namespace namespace = element.getNamespace();
			if(namespace!=null){
				String prefix = namespace.getPrefix();
				if(prefix==null||"".equals(prefix)){
					builder.append(" xmlns=\""+namespace.getStringValue()+"\"");
				}else{
					builder.append(" xmlns:"+namespace.getPrefix()+"=\""+namespace.getStringValue()+"\"");
				}
			}
			builder.append(">\r\n");
			
			List<Element> elements = element.elements();
			for (Element ele : elements) {
				recursionPrintXML(ele, builder,deep+1);
			}
			builder.append(deepBuilder.toString()+"</"+element.getQualifiedName()+">\r\n");
		}
	}
}
