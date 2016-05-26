package com.kingbase.wsdl.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.Document;
import org.dom4j.Element;

import com.kingbase.wsdl.bean.OperationBean;
import com.kingbase.wsdl.bean.ParameterBean;
import com.kingbase.wsdl.bean.ParameterTypeBean;
import com.kingbase.wsdl.bean.ParameterTypeBean.BasicTypeBean;
import com.kingbase.wsdl.bean.ServiceBean;
import com.kingbase.wsdl.utils.DocumentUtil;
import com.kingbase.wsdl.utils.ElementUtil;
import com.kingbase.wsdl.utils.HttpClientUtil;

public class OperationParse {

	/**
	 * 将方法 参数存放在本地wsdl中
	 * @param schemaelement
	 * @param serviceBean
	 */
	public static void parseOperationsFromSOAP(Element schemaelement, ServiceBean serviceBean) {
		Map<String,List<ParameterBean>> operationMap=new HashMap<String,List<ParameterBean>>();
		List<ParameterTypeBean> parameterTypeBeans=new ArrayList<ParameterTypeBean>();
		
		List<Element> elements = ElementUtil.findElements(schemaelement, "element");
		
		//构建输入输出参数
		for (Element element : elements) {
			String operationName=element.attributeValue("name");
			
			List<Element> complexTypeElements = ElementUtil.findElements(element, "complexType");
			if(complexTypeElements.size()>0){
				Element complexTypeElement = complexTypeElements.get(0);
				List<ParameterBean> inparameters = getParameterFromComplexType(complexTypeElement);
				operationMap.put(operationName, inparameters);
			}
		}
		
		//构建 对象
		List<Element> importElements = ElementUtil.findElements(schemaelement, "import");
		for (Element importElement : importElements) {
		   List<ParameterTypeBean> parameterTypes=getSoapParameterType(importElement,serviceBean);
		   parameterTypeBeans.addAll(parameterTypes);
		}
		
		List<OperationBean> operationBeans=new ArrayList<OperationBean>();
		//遍历方法
		for (Entry<String, List<ParameterBean>> entry : operationMap.entrySet()) {
			String operationName=entry.getKey();
			if(!operationName.contains("Response")){
				
				OperationBean operationBean=new OperationBean();
				operationBean.setOperationName(operationName);
				
				operationBean.setInParameters(entry.getValue());
				
				List<ParameterBean> outParameters = operationMap.get(operationName+"Response");
				operationBean.setOutParameters(outParameters);
				
				operationBeans.add(operationBean);
			}
		}
		
		serviceBean.setOperationBeans(operationBeans);
		serviceBean.setParameterTypes(parameterTypeBeans);
	}

	/**
	 * 构建soap对象
	 * @param importElement
	 * @param serviceBean 
	 * @return
	 */
	private static List<ParameterTypeBean> getSoapParameterType(Element importElement, ServiceBean serviceBean) {
		List<ParameterTypeBean> parameterTypeBeans=new ArrayList<ParameterTypeBean>();
		
		String schemaLocation = importElement.attributeValue("schemaLocation");
		if(!schemaLocation.startsWith("http://")&&!schemaLocation.startsWith("https://")){
			schemaLocation=serviceBean.getHostURL()+schemaLocation;
		}
		serviceBean.getImportWSDL().add(schemaLocation);
		
		InputStream inputStream = HttpClientUtil.send(schemaLocation);
		Document document = DocumentUtil.getDocument(inputStream);
		Element rootElement = ElementUtil.getRootElement(document);
		
		//从 complexType获取对象
		List<Element> complexTypeElements = ElementUtil.findElements(rootElement, "complexType");
		for (Element complexTypeElement : complexTypeElements) {
			ParameterTypeBean parameterTypeBean=new ParameterTypeBean();
			
			parameterTypeBean.setTypeName(complexTypeElement.attributeValue("name"));
			parameterTypeBean.setInstanceClass(complexTypeElement.attributeValue("sdoJava:instanceClass"));
			
			List<BasicTypeBean> basicTypeBeans = getBasicTypeFromComplexType(complexTypeElement);
			parameterTypeBean.setBasicTypeBeans(basicTypeBeans);
			parameterTypeBean.setType("complexType");
			
			parameterTypeBeans.add(parameterTypeBean);
		}
		
		//从 simpleType 获取对象
		List<Element> simpleTypeElements = ElementUtil.findElements(rootElement, "simpleType");
		for (Element simpleTypeElement : simpleTypeElements) {
			ParameterTypeBean parameterTypeBean=new ParameterTypeBean();
			parameterTypeBean.setTypeName(simpleTypeElement.attributeValue("name"));
			
			setBasicTypeFromSimpleType(simpleTypeElement,parameterTypeBean);
			
			parameterTypeBeans.add(parameterTypeBean);
		}
		return parameterTypeBeans;
	}

	/**
	 * 将方法 参数存放在另一个wsdl中
	 * @param element
	 * @param serviceBean
	 */
	public static void parseOperationsFromXSD(Element rootElement, ServiceBean serviceBean) {
		List<String> operations=new ArrayList<String>();
		//方法节点
		List<Element> elements = ElementUtil.findElements(rootElement, "element");
		for (Element element : elements) {
			operations.add(element.attributeValue("name"));
		}
		
		Map<String,List<ParameterBean>> operationMap=new HashMap<String,List<ParameterBean>>();
		
		List<ParameterTypeBean> parameterTypeBeans=new ArrayList<ParameterTypeBean>();
		
		List<Element> complexTypeElements = ElementUtil.findElements(rootElement, "complexType");
		for (Element complexTypeElement : complexTypeElements) {
			String name = complexTypeElement.attributeValue("name");
			
			//包含这个方法
			if(operations.contains(name)){
				
			    List<ParameterBean> inParameterBeans=getParameterFromComplexType(complexTypeElement);
			    
			    operationMap.put(name, inParameterBeans);
			}
			//xsd定义的 对象
			else{
				ParameterTypeBean parameterTypeBean=new ParameterTypeBean();
				parameterTypeBean.setTypeName(name);
				
				List<BasicTypeBean> basicTypeBeans=getBasicTypeFromComplexType(complexTypeElement);
				
				parameterTypeBean.setBasicTypeBeans(basicTypeBeans);
				
				parameterTypeBeans.add(parameterTypeBean);
			}
		}
		
		List<OperationBean> operationBeans=new ArrayList<OperationBean>();
		//遍历方法
		for (String operationName : operations) {
			if(operationName.contains("Response")){
				continue;
			}
			OperationBean operation=new OperationBean();
			operation.setOperationName(operationName);
			
			List<ParameterBean> inParameters = operationMap.get(operationName);
			operation.setInParameters(inParameters);
			
			List<ParameterBean> outParameters = operationMap.get(operationName+"Response");
			operation.setOutParameters(outParameters);
			
			operationBeans.add(operation);
		}
		
		serviceBean.setOperationBeans(operationBeans);
		serviceBean.setParameterTypes(parameterTypeBeans);
	}

	/**
	 * xsd 从complexTypeElement获取参数
	 * @param complexTypeElement
	 * @return
	 */
	private static List<ParameterBean> getParameterFromComplexType(Element complexTypeElement) {
		List<ParameterBean> inParameterBeans=new ArrayList<ParameterBean>();
		
		List<Element> sequenceElements = ElementUtil.findElements(complexTypeElement, "sequence");
		if(sequenceElements.size()>0){
			Element sequenceElement = sequenceElements.get(0);
			List<Element> elements = ElementUtil.findElements(sequenceElement, "element");
			for (Element element : elements) {
				ParameterBean parameterBean=new ParameterBean();
				parameterBean.setParameterName(element.attributeValue("name"));
				String type = element.attributeValue("type");
				if(type!=null){
					String[] types = type.split(":");
					if(types.length>1){
						parameterBean.setParameterType(types[1]);
					}else{
						parameterBean.setParameterType(type);
					}
				}
				parameterBean.setMaxOccurs(element.attributeValue("maxOccurs"));
				parameterBean.setMinOccurs(element.attributeValue("minOccurs"));
				parameterBean.setNillable(element.attributeValue("nillable"));
				inParameterBeans.add(parameterBean);
			}
		}
		return inParameterBeans;
	}

	/**
	 * 从xsd中获取 对象
	 * @param complexTypeElement
	 * @return
	 */
	private static List<BasicTypeBean> getBasicTypeFromComplexType(Element complexTypeElement) {
		List<BasicTypeBean> basicTypeBeans=new ArrayList<BasicTypeBean>();
		
		List<Element> sequenceElements = ElementUtil.findElements(complexTypeElement, "sequence");
		if(sequenceElements.size()>0){
			Element sequenceElement = sequenceElements.get(0);
			List<Element> elements = ElementUtil.findElements(sequenceElement, "element");
			for (Element element : elements) {
				BasicTypeBean basicTypeBean=new BasicTypeBean();
				basicTypeBean.setBasicTypeName(element.attributeValue("name"));
				String type = element.attributeValue("type");
				if(type!=null){
					String[] types = type.split(":");
					if(types.length>1){
						basicTypeBean.setBasicType(types[1]);
					}else{
						basicTypeBean.setBasicType(type);
					}
				}
				basicTypeBean.setMaxOccurs(element.attributeValue("maxOccurs"));
				basicTypeBean.setMinOccurs(element.attributeValue("minOccurs"));
				basicTypeBean.setNillable(element.attributeValue("nillable"));
				basicTypeBeans.add(basicTypeBean);
			}
		}
		return basicTypeBeans;
	}
	
	/**
	 * 从simpleType 获取对象
	 * @param simpleTypeElement
	 * @param parameterTypeBean 
	 * @return
	 */
	private static void setBasicTypeFromSimpleType(Element simpleTypeElement, ParameterTypeBean parameterTypeBean) {
		
		List<Element> restrictionElements = ElementUtil.findElements(simpleTypeElement, "restriction");
		if(restrictionElements.size()>0){
			Element restrictionElement = restrictionElements.get(0);
			
			//String base = restrictionElement.attributeValue("name");
			parameterTypeBean.setType("simpleType");
			
			List<String> values=new ArrayList<String>();
			List<Element> enumerationElements = ElementUtil.findElements(restrictionElement, "enumeration");
			for (Element enumerationElement : enumerationElements) {
				values.add(enumerationElement.attributeValue("value"));
			}
			parameterTypeBean.setValues(values);
		}
	}
}
