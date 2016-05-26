package com.kingbase.wsdl.parser;

import java.io.InputStream;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.kingbase.wsdl.bean.BindingBean;
import com.kingbase.wsdl.bean.OperationBean;
import com.kingbase.wsdl.bean.PortTypeBean;
import com.kingbase.wsdl.bean.ServiceBean;
import com.kingbase.wsdl.utils.DocumentUtil;
import com.kingbase.wsdl.utils.ElementUtil;
import com.kingbase.wsdl.utils.HttpClientUtil;

/**
 * 解析wsdl
 * @author ganliang
 */
public class SOAPParser {
	
	private static final String IMPORT="import";

	/**
	 * 解析wsdl
	 * @param wsdl
	 */
	public ServiceBean parser(String wsdl){
		InputStream inputStream = HttpClientUtil.send(wsdl);
		return parser(inputStream,wsdl);
	}
	
	/**
	 * 解析wsdl
	 * @param inputStream
	 * @param wsdl 
	 */
	public ServiceBean parser(InputStream inputStream, String wsdl){
		//获取文档
		Document document = DocumentUtil.getDocument(inputStream);
		
		//获取根节点
		Element rootElement = ElementUtil.getRootElement(document);
		
		//解析service
		ServiceBean serviceBean = ServiceParse.parse(rootElement,wsdl);
		
		//解析binding
		List<BindingBean> bindingBeans = BindingParse.parse(rootElement);
		serviceBean.setBindingBeans(bindingBeans);
		
		//解析Types、Message、PortType
		parserTMPT(rootElement, serviceBean);
		
		reConOperations(serviceBean);
		return serviceBean;
	}
	
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
		serviceBean.getImportWSDL().add(location);
		
		//发送请求 
		InputStream inputStream = HttpClientUtil.send(location);
		
		Document document = DocumentUtil.getDocument(inputStream);
		
		Element rootElement = ElementUtil.getRootElement(document);
		
		parserTMPTFromLocal(rootElement, serviceBean);
	}
	
	/**
	 * 将方法注释 soapAction放在operations中
	 * @param serviceBean
	 */
	private void reConOperations(ServiceBean serviceBean) {
		
		List<OperationBean> operationBeans = serviceBean.getOperationBeans();
		for (OperationBean operationBean : operationBeans) {
			String operationName = operationBean.getOperationName();
			
			//设置文档说明
			String documentation=getDocumentation(serviceBean,operationName);
			operationBean.setDocumentation(documentation);
			
			//设置soapAction
			String soapAction=getSOAPAction(serviceBean,operationName);
			operationBean.setSoapAction(soapAction);
		}
	}

	/**
	 * 获取方法的 说明
	 * @param serviceBean
	 * @param operationName
	 * @return
	 */
	private String getDocumentation(ServiceBean serviceBean, String operationName) {
		List<PortTypeBean> portTypeBeans = serviceBean.getPortTypeBeans();
		if(portTypeBeans.size()==0){
			throw new IllegalArgumentException("WSDL解析异常");
		}
		PortTypeBean portTypeBean = portTypeBeans.get(0);
		List<OperationBean> operationBeans = portTypeBean.getOperationBeans();
		
		for (OperationBean operationBean : operationBeans) {
			if(operationBean.getOperationName().equals(operationName)){
				return operationBean.getDocumentation();
			}
		}
		return null;
	}
	
	/**
	 * 获取方法的soapAction
	 * @param serviceBean
	 * @param operationName
	 * @return
	 */
	private String getSOAPAction(ServiceBean serviceBean, String operationName) {
		List<BindingBean> bindingBeans = serviceBean.getBindingBeans();
		if(bindingBeans.size()==0){
			throw new IllegalArgumentException("WSDL解析异常");
		}
		BindingBean bindingBean = bindingBeans.get(0);
		
		List<OperationBean> operationBeans = bindingBean.getOperationBeans();
		for (OperationBean operationBean : operationBeans) {
			if(operationBean.getOperationName().equals(operationName)){
				return operationBean.getSoapAction();
			}
		}
		return null;
	}
}
