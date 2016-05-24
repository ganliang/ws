package com.kingbase.ws.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.wsdl.WSDLException;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import com.kingbase.ws.bean.BindingBean;
import com.kingbase.ws.bean.OperationBean;
import com.kingbase.ws.bean.ParameterBean;
import com.kingbase.ws.bean.ServiceBean;
import com.kingbase.ws.utils.DocumentUtil;
import com.kingbase.ws.utils.HttpClientUtil;
import com.kingbase.ws.utils.ParameterUtil;

/**
 * 解析wsdl
 * @author ganliang
 */
public class SOAPParser {

	private static final Map<String,List<ParameterBean>> parameters=new HashMap<String,List<ParameterBean>>();
	private static final Map<String,Map<String,String>> portTypes=new HashMap<String,Map<String,String>>();
	private String wsdlType="";
	/**
	 * 解析wsdl文件流
	 * @param inputStream
	 * @return
	 * @throws WSDLException 
	 */
	public ServiceBean parse(InputStream inputStream){
		//获取文档
		Document document = DocumentUtil.getDocument(inputStream);
		Element rootElement = document.getRootElement();
		
		ServiceBean serviceBean=new ServiceBean();
		//serviceBean.setEndpointURI(wsdllocation);//发布url
		
		Attribute targetNamespaceAttribute = rootElement.attribute("targetNamespace");
		serviceBean.setTargetNamespace(targetNamespaceAttribute.getValue());//命名空间
		
		//构建参数
		buildParameters(rootElement);
		//获取所有的port
		buildPortTypes(rootElement);
		
		//获取所有的binding
		List<BindingBean> bindingBeans=buildBindings(rootElement);
		
		
		//获取服务
		buildServices(rootElement,serviceBean);
		
		serviceBean.setBindingBean(bindingBeans);
		serviceBean.setWsdlType(wsdlType);
		return serviceBean;		
	}
	
	/**
	 * 解析wsdl
	 * @throws WSDLException 
	 * @throws IOException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 */
	public ServiceBean parse(String wsdllocation) throws SecurityException, IllegalArgumentException, IOException {
		//获取流
		InputStream inputStream = HttpClientUtil.send(wsdllocation);
		return parse(inputStream);
	}

	/**
	 * 构建 service
	 * @param rootElement
	 * @param serviceBean
	 */
	@SuppressWarnings("unchecked")
	private void buildServices(Element rootElement, ServiceBean serviceBean) {
		List<Element> serviceElements = getComponentElement(rootElement, "service");
		if(serviceElements.size()==0){
			throw new IllegalArgumentException("wsdl不存在service");
		}
		Element serviceElement = serviceElements.get(0);
		String serviceName=serviceElement.attribute("name").getValue();
		String xsdServiceName=serviceName.substring(0, serviceName.length()-"Service".length());
		if(xsdServiceName.indexOf("Service")>-1){
			serviceName=xsdServiceName;
		}
		serviceBean.setServiceName(serviceName);
		
		List<Element> elements = serviceElement.elements();
		
		for (Element element : elements) {
			if(element.getName().contains("documentation")){
				serviceBean.setDocumentation(element.getText());
			}else{
				List<Element> list = element.elements();
				if(list.size()>0){
					Element ele = list.get(0);
					String location = ele.attributeValue("location");
					serviceBean.setEndpointURI(location+"?wsdl");
				}
				break;
			}
		}
	}
	
	/**
	 * 构建方法
	 * @param rootElement
	 */
	private void buildParameters(Element rootElement) {
		List<Element> componentElement = getComponentElement(rootElement, "types");
		if(componentElement.size()==0){
			throw new IllegalArgumentException("wsdl不存在types");
		}
		Element typesElement = componentElement.get(0);
		List<Element> schemaElements = getComponentElement(typesElement, "schema");
		if(schemaElements.size()==0){
			throw new IllegalArgumentException("wsdl不存在types");
		}
		Element schemaElement = schemaElements.get(0);
		List<Element> importElements = getComponentElement(schemaElement, "import");
		//如果不存在导入scheme 则是soap格式的wsdl文件
		if(importElements.size()==0){
			buildParametersFromSOAP(schemaElement);
			wsdlType="soap";
		}
		//如果存在 则types存在于import 文件
		else{
			Element element = importElements.get(0);
			String schemaLocation = element.attribute("schemaLocation").getValue();
			buildParametersFromXSD(schemaLocation);
			wsdlType="xsd";
		}
	}
	
	/**
	 * 构建soap的方法
	 * @param schemaLocation
	 * @throws WSDLException 
	 */
	private void buildParametersFromXSD(String schemaLocation){
		InputStream inputStream = HttpClientUtil.send(schemaLocation);
		try {
			Document document = DocumentUtil.getDocument(inputStream);
			Element rootElement = document.getRootElement();
			List<Element> complexTypeElements = getComponentElement(rootElement, "complexType");
			for (Element element : complexTypeElements) {
				String operationName=element.attribute("name").getValue();
				
				List<Element> sequenceElements=getComponentElement(element, "sequence");
				
				if(sequenceElements.size()>0){
					List<ParameterBean> parameterBeans = getParameters(sequenceElements.get(0));
					parameters.put(operationName, parameterBeans);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 构建xsd方法
	 * @param schemaElement
	 */
	private void buildParametersFromSOAP(Element schemaElement) {
		List<Element> elements = getComponentElement(schemaElement, "element");
		for (Element element : elements) {
			String operationName=element.attribute("name").getValue();
			
			List<Element> complexTypeElement = getComponentElement(element, "complexType");
			if(complexTypeElement.size()>0){
				List<Element> sequenceElements = getComponentElement(complexTypeElement.get(0), "sequence");
				if(sequenceElements.size()>0){
					List<ParameterBean> parameterBeans = getParameters(sequenceElements.get(0));
					parameters.put(operationName, parameterBeans);
				}
			}
		}
	}
	
	/**
	 * 获取参数集合
	 * @param sequenceElement
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ParameterBean> getParameters(Element sequenceElement){
		List<Element> eles= getComponentElement(sequenceElement, "element");
		List<ParameterBean> parameterBeans=new ArrayList<ParameterBean>();
		for (Element ele : eles) {
			ParameterBean parameterBean=new ParameterBean();
			
			List<Attribute> attributes = ele.attributes();
			for (Attribute attribute : attributes) {
				String name = attribute.getName();
				String value = attribute.getValue();
				if("name".equals(name)){
					parameterBean.setParameterName(value);
				}else if("type".equals(name)){
					parameterBean.setParameterType(removePrefix(value, ":"));
				}else if("minOccurs".equals(name)){
					parameterBean.setMinOccurs(value);
				}else if("maxOccurs".equals(name)){
					parameterBean.setMaxOccurs(value);
				}
			}
			parameterBeans.add(parameterBean);
		}
		return parameterBeans;
	}

	/**
	 * 获取绑定
	 * @param rootElement
	 * @return
	 */
	private List<BindingBean> buildBindings(Element rootElement) {
		List<BindingBean> beans=new ArrayList<BindingBean>();
		
		List<Element> bindings = getComponentElement(rootElement, "binding");
		for (Element binding : bindings) {
			BindingBean bindingBean=new BindingBean();
			bindingBean.setName(binding.attribute("name").getValue());
			String type = binding.attribute("type").getValue();
			type = removePrefix(type, ":");
			bindingBean.setType(type);
			
			List<OperationBean> operationBeans=getOperations(binding,type);
			bindingBean.setOperations(operationBeans);
			beans.add(bindingBean);
		}
		return beans;
	}

	/**
	 * 获取方法
	 * @param binding
	 * @param type 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<OperationBean> getOperations(Element binding, String portType) {
		List<OperationBean> operationBeans=new ArrayList<OperationBean>();
		Map<String, String> portTypeMap = portTypes.get(portType);
		
		//获取方法
		List<Element> elements = binding.elements();
		for (Element element : elements) {
			if(element.getName().contains("operation")){
				String operationName=element.attribute("name").getValue();
				OperationBean operationBean = new OperationBean();
				operationBean.setName(operationName);
				
				//获取soapAction
				List<Element> eles = element.elements();
				for (Element ele : eles) {
					if(ele.getName().contains("operation")){
						operationBean.setSoapAction(ele.attributeValue("soapAction"));
						operationBean.setStyle(ele.attributeValue("style"));
						break;
					}
				}
				//获取方法的注释
				if(portTypeMap!=null){
					String documentation = portTypeMap.get(operationName);
					operationBean.setDocumentation(documentation);
				}
				
				List<ParameterBean> inParameters = parameters.get(operationName);
				List<ParameterBean> outParameters = parameters.get(operationName+"Response");
				
				operationBean.setInParameters(inParameters);
				operationBean.setOutParameters(outParameters);
				
				operationBeans.add(operationBean);
			}
		}
		return operationBeans;
	}

	/**
	 * 获取所有的port
	 * @param rootElement
	 * @return
	 */
	private void buildPortTypes(Element rootElement) {
		List<Element> portTypeElements = getComponentElement(rootElement, "portType");
		if(portTypeElements.size()==0){
			throw new IllegalAccessError("wsdl不存在 portType");
		}
		//遍历
		for (Element portTypeElement : portTypeElements) {
			String portName=portTypeElement.attributeValue("name");
			//获取方法
			List<Element> operationElements = getComponentElement(portTypeElement, "operation");
			if(operationElements.size()==0){
				continue;
			}
			
			Map<String,String> map=new HashMap<String,String>();
			for (Element operationElement : operationElements) {
				String operationName=operationElement.attributeValue("name");
				List<Element> documentationElements = getComponentElement(operationElement, "documentation");
				if(documentationElements.size()==0){
					continue;
				}
				String documentation = documentationElements.get(0).getText();
				map.put(operationName, documentation);
			}
			portTypes.put(portName, map);
		}
	}
	/**
	 * 找到节点
	 * @param rootElement
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Element> getComponentElement(Element rootElement,String name){
		List<Element> list=new ArrayList<Element>();
		
		List<Element> elements = rootElement.elements();
		for (Element element : elements) {
			if(element.getName().contains(name)){
				list.add(element);
			}
		}
		return list;
	}

	/**
	 * 打印
	 * @param serviceBean
	 */
	public static void print(ServiceBean serviceBean){
		System.out.println("服务名称 "+serviceBean.getServiceName());
		List<BindingBean> bindingBean = serviceBean.getBindingBean();
		for (BindingBean binding : bindingBean) {
			System.out.println("--->>>"+binding);
			List<OperationBean> operations = binding.getOperations();
			for (OperationBean operationBean : operations) {
				System.out.println(operationBean);
			}
			System.out.println();
			System.out.println();
		}
		System.out.println("url  "+serviceBean.getEndpointURI());
	}
	
	/**
	 * 去掉字符串的前缀
	 * @param value
	 * @return
	 */
	public static String removePrefix(String type,String prefix){
		if(type==null){
			return "";
		}
		String[] strings = type.split(prefix);
		if(strings.length>1){
			return strings[1];
		}else{
			return type;
		}	
	}
	
	public static void main(String[] args)
			throws SecurityException, IllegalArgumentException, IOException {
		String wsdllocation = "http://www.webxml.com.cn/webservices/ChinaStockSmallImageWS.asmx?wsdl";
		//String wsdllocation = "http://192.168.8.144:9999/services/helloWord?wsdl";
		SOAPParser parser = new SOAPParser();

		ServiceBean serviceBean = parser.parse(wsdllocation);
		print(serviceBean);
		
		List<ServiceBean> serviceBeans=new ArrayList<ServiceBean>();
		serviceBeans.add(serviceBean);
		
		String services = ParameterUtil.printServices(serviceBeans);
		System.out.println(services);
		
		String service = ParameterUtil.printService(serviceBean);
		System.out.println(service);

		
	}
}
