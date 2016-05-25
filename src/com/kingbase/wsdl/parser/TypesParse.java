package com.kingbase.wsdl.parser;

import java.util.List;

import org.dom4j.Element;

import com.kingbase.wsdl.bean.ServiceBean;
import com.kingbase.wsdl.exceptions.WSDLServiceException;
import com.kingbase.wsdl.utils.ElementUtil;

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
		
		List<Element> importElements = ElementUtil.findElements(element,IMPORT);
		//wsdl将 wsdl:types wsdl:message wsdl:portType 节点存放在本文件中
		if(importElements.size()==0){
			parseTypesFromLocal(element,serviceBean);
		}
		//wsdl文件 将wsdl:types wsdl:message wsdl:portType 存放在另一个wsdl文件中
		else{
			parseTypesFromImport(importElements.get(0),serviceBean);
		}
	}
	
	/**
	 * 从本地文件中 获取types节点
	 * @param element 根节点
	 * @param serviceBean
	 */
	public static void parseTypesFromLocal(Element element, ServiceBean serviceBean) {
		List<Element> typesElements = ElementUtil.findElements(element,TYPES);
		if(typesElements.size()==0){
			throw new IllegalArgumentException("WSDL格式不正确,缺少types节点");
		}
		Element typesElement = typesElements.get(0);
		
		List<Element> schemaelements = ElementUtil.findElements(typesElement, SCHEMA);
		if(schemaelements.size()>0){
			Element schemaelement = schemaelements.get(0);
			
			List<Element> importElements = ElementUtil.findElements(schemaelement, IMPORT);
			//将方法 参数存放在本地wsdl中
			if(importElements.size()==0){
				OperationParse.parseOperationsFromLocal(schemaelement,serviceBean);
			}
			//将方法 参数存放在另一个wsdl中
			else{
				OperationParse.parseOperationsFromImport(importElements.get(0),serviceBean);
			}
		}
	}
	
	/**
	 * wsdl文件 将types存放在另一个wsdl文件中
	 * @param importElement
	 * @param serviceBean
	 */
	private static void parseTypesFromImport(Element importElement, ServiceBean serviceBean) {
		
		
	}
}
