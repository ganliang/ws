package com.kingbase.wsdl.parser;

import java.io.InputStream;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.kingbase.wsdl.bean.ServiceBean;
import com.kingbase.wsdl.exceptions.WSDLServiceException;
import com.kingbase.wsdl.utils.DocumentUtil;
import com.kingbase.wsdl.utils.ElementUtil;
import com.kingbase.wsdl.utils.HttpClientUtil;

/**
 * 解析wsdl types
 * @author ganliang
 */
public class TypesParse {

	private static final String TYPES="types";
	private static final String IMPORT="import";
	private static final String SCHEMA="schema";
	/**
	 * 解析types
	 * @param element 根元素节点
	 * @return
	 */
	public static void parse(Element element,ServiceBean serviceBean){
		if(element==null){
			throw new WSDLServiceException("节点不能为空");
		}
		if(!element.isRootElement()){
			throw new WSDLServiceException("types节点存在于根节点");
		}
		
		List<Element> typesElements = ElementUtil.findElements(element, TYPES);
		if(typesElements.size()==0){
			throw new IllegalArgumentException("types节点不存在");
		}
		List<Element> schemaElements = ElementUtil.findElements(typesElements.get(0), SCHEMA);
		if(schemaElements.size()==0){
			throw new IllegalArgumentException("types节点下不存在schema子节点");
		}
		//查看 schema节点下 是否存在 import节点 
		Element schemaElement = schemaElements.get(0);
		
		List<Element> importElements = ElementUtil.findElements(schemaElement,IMPORT);
		
		//schema节点 是否存在 element节点
		List<Element> elements = ElementUtil.findElements(schemaElement,"element");
		
		//soap 方式解析schema
		if(importElements.size()==0||(importElements.size()>0&&elements.size()>0)){
			parseSchemaFromSOAP(schemaElement,serviceBean);
		}
		//xsd方式解析schema
		else{
			parseSchemaFromXSD(importElements.get(0),serviceBean);
		}
	}
	
	/**
	 * 从本地文件中 获取types节点
	 * @param schemaElement schema节点
	 * @param serviceBean
	 */
	public static void parseSchemaFromSOAP(Element schemaElement, ServiceBean serviceBean) {
		serviceBean.setWsdlType("soap");
		OperationParse.parseOperationsFromSOAP(schemaElement, serviceBean);
	}
	
	/**
	 * wsdl文件 将types存放在另一个wsdl文件中
	 * @param importElement
	 * @param serviceBean
	 */
	private static void parseSchemaFromXSD(Element importElement, ServiceBean serviceBean) {
		String schemaLocation = importElement.attributeValue("schemaLocation");
		if(!schemaLocation.startsWith("http://")&&!schemaLocation.startsWith("https://")){
			schemaLocation=serviceBean.getHostURL()+schemaLocation;
		}
		serviceBean.getImportWSDL().add(schemaLocation);
		
		//下载import文件
		InputStream inputStream = HttpClientUtil.send(schemaLocation);
		Document document = DocumentUtil.getDocument(inputStream);
		//获取根节点(schema节点)
		Element rootElement = ElementUtil.getRootElement(document);
		
		serviceBean.setWsdlType("xsd");
		
		OperationParse.parseOperationsFromXSD(rootElement, serviceBean);
	}
}
