package com.kingbase.wsdl.parser;

import java.io.InputStream;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.kingbase.ws.utils.DocumentUtil;
import com.kingbase.ws.utils.HttpClientUtil;
import com.kingbase.wsdl.bean.ServiceBean;
import com.kingbase.wsdl.utils.ElementUtil;

/**
 * 解析wsdl
 * @author ganliang
 */
public class SOAPParser {
	
	private static final String IMPORT="import";
	private static final String SCHEMA="schema";

	/**
	 * 解析Types、Message、PortType
	 * @param element根节点
	 * @param serviceBean
	 */
	public void parserTMPT(Element element,ServiceBean serviceBean){
		List<Element> importElements = ElementUtil.findElements(element,IMPORT);
		//wsdl将 wsdl:types wsdl:message wsdl:portType 节点存放在本文件中
		if(importElements.size()==0){
			parserTMPTFromLocal(element,serviceBean);
		}
		//wsdl文件 将wsdl:types wsdl:message wsdl:portType 存放在另一个wsdl文件中
		else{
			parserTMPTFromImport(importElements.get(0),serviceBean);
		}		
	}

	/**
	 * 从本地wsdl中 解析Types、Message、PortType
	 * @param element 根元素节点
	 * @param serviceBean
	 */
	private void parserTMPTFromLocal(Element element, ServiceBean serviceBean) {
		TypesParse.parse(element,serviceBean);
		
		PortTypeParse.parse(element,serviceBean);
	}
	
	/**
	 * 从导出文件wsdl中 解析Types、Message、PortType
	 * @param importElement import元素
	 * @param serviceBean
	 */
	private void parserTMPTFromImport(Element importElement, ServiceBean serviceBean) {
		String location = importElement.attributeValue("location");
		
		if(!location.startsWith("http://")&&!location.startsWith("https://")){
			location=serviceBean.getHostURL()+location;
		}
		//发送请求 
		InputStream inputStream = HttpClientUtil.send(location);
		
		Document document = DocumentUtil.getDocument(inputStream);
		
		Element rootElement = ElementUtil.getRootElement(document);
		
		parserTMPTFromLocal(rootElement, serviceBean);
	}
}
