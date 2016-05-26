package com.kingbase.wsdl.parser;

import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Element;

import com.kingbase.wsdl.bean.ServiceBean;
import com.kingbase.wsdl.exceptions.WSDLServiceException;
import com.kingbase.wsdl.utils.ElementUtil;

/**
 * 解析service
 * @author ganliang
 */
public class ServiceParse {

	private static final String DOCUMENTATION="documentation";//服务文档描述
	private static final String SERVICE="service";//服务节点
	
	/**
	 * 解析 service
	 * @param element 
	 * @param wsdl 
	 * @param ServiceBean 
	 */
	public static ServiceBean parse(Element element, String wsdl){
		if(element==null){
			throw new WSDLServiceException("节点不能为空");
		}
		if(!element.isRootElement()){
			throw new WSDLServiceException("service节点存在于根节点");
		}
		ServiceBean serviceBean =new ServiceBean();
		//设置命名空间
		String targetNameSpace = getTargetNameSpace(element);
		serviceBean.setTargetNamespace(targetNameSpace);//命名空间
		
		//获取serices节点
		List<Element> serviceElements = ElementUtil.findElements(element, SERVICE);
		if(serviceElements.size()==0){
			throw new WSDLServiceException("WSDL格式不正确,不存在services节点");
		}
		Element serviceElement = serviceElements.get(0);
		//获取服务名称
		String serviceName = serviceElement.attributeValue("name");
		serviceBean.setServiceName(serviceName);
		
		//设置服务描述
		String documentation=getDocumentation(serviceElement);
		serviceBean.setDocumentation(documentation);
		
		//设置服务host
		int indexOf = wsdl.indexOf("/", "https://".length());
		serviceBean.setHostURL(wsdl.substring(0, indexOf));
		serviceBean.setEndpointURI(wsdl);
		
		serviceBean.getImportWSDL().add(wsdl);
		return serviceBean;
	}
	
	/**
	 * 获取服务的目标命名空间
	 * @param element 服务节点
	 * @return
	 */
	private static String getTargetNameSpace(Element element) {
		Attribute targetNamespaceAttribute = element.attribute("targetNamespace");
		return targetNamespaceAttribute.getValue();
	}
	
	/**
	 * 获取服务描述
	 * @param element
	 * @return
	 */
	private static String getDocumentation(Element element) {
		//查询 documentation 节点
		List<Element> documentations = ElementUtil.findElements(element, DOCUMENTATION);
		if(documentations.size()>0){
			Element documentation = documentations.get(0);
			return documentation.getText();
		}
		return null;
	}
}
