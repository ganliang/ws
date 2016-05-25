package com.kingbase.wsdl.parser;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import com.kingbase.wsdl.bean.OperationBean;
import com.kingbase.wsdl.bean.PortTypeBean;
import com.kingbase.wsdl.bean.ServiceBean;
import com.kingbase.wsdl.exceptions.WSDLServiceException;
import com.kingbase.wsdl.utils.ElementUtil;

public class PortTypeParse {

	private static final String PORTTYPE="portType";
	private static final String OPERATION="operation";
	private static final String DOCUMENTATION="documentation";
	
	/**
	 * 解析portType
	 * @param element根节点
	 * @return
	 */
	public static void parse(Element element, ServiceBean serviceBean){
		if(element==null){
			throw new WSDLServiceException("节点不能为空");
		}
		if(!element.isRootElement()){
			throw new WSDLServiceException("portType节点存在于根节点");
		}
		
		List<Element> portTypeElements = ElementUtil.findElements(element, PORTTYPE);
		if(portTypeElements.size()==0){
			throw new IllegalArgumentException("WSDL格式不正确,缺少portType节点");
		}
		//遍历portType元素
		List<PortTypeBean> portTypeBeans=new ArrayList<PortTypeBean>();
		for (Element portTypeElement : portTypeElements) {
			PortTypeBean portTypeBean=parsePortType(portTypeElement);
			
			portTypeBeans.add(portTypeBean);
		}
		
		serviceBean.setPortTypeBeans(portTypeBeans);
	}

	/**
	 * 解析portType元素
	 * @param portTypeElement
	 * @return
	 */
	private static PortTypeBean parsePortType(Element portTypeElement) {
		PortTypeBean portTypeBean=new PortTypeBean();
		
		String portName = portTypeElement.attributeValue("name");
		portTypeBean.setPortName(portName);
		
		//获取portType下的方法
		List<Element> operationElements = ElementUtil.findElements(portTypeElement, OPERATION);
		List<OperationBean> operationBeans=new ArrayList<OperationBean>();
		
		for (Element operationElement : operationElements) {
			OperationBean operationBean=new OperationBean();
			
			String operationName = operationElement.attributeValue("name");
			operationBean.setOperationName(operationName);
			
			//获取 documentation
			List<Element> elements = ElementUtil.findElements(operationElement, DOCUMENTATION);
			if(elements.size()>0){
				Element element = elements.get(0);
				String documentation = element.getText();
				operationBean.setDocumentation(documentation);
			}
			operationBeans.add(operationBean);
		}
		portTypeBean.setOperationBeans(operationBeans);
		return portTypeBean;
	}

}
