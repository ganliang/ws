package com.kingbase.wsdl.parser;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import com.kingbase.wsdl.bean.BindingBean;
import com.kingbase.wsdl.bean.OperationBean;
import com.kingbase.wsdl.exceptions.WSDLServiceException;
import com.kingbase.wsdl.utils.ElementUtil;

public class BindingParse {

	private static final String BINDING="binding";
	private static final String OPERATION="operation";
	/**
	 * 解析 binding
	 * @param element 根节点
	 * @return
	 */
	public static List<BindingBean> parse(Element element){
		if(element==null){
			throw new WSDLServiceException("节点不能为空");
		}
		if(!element.isRootElement()){
			throw new WSDLServiceException("binding节点存在于根节点");
		}
		List<Element> bindingElements = ElementUtil.findElements(element, BINDING);
		if(bindingElements.size()==0){
			throw new IllegalArgumentException("WSDL格式不正确,缺少binding节点");
		}
		List<BindingBean> bindingBeans=new ArrayList<BindingBean>();
		//遍历binding节点
		for (Element bindingElement : bindingElements) {
			BindingBean bindingBean=parseBinding(bindingElement);
			
			bindingBeans.add(bindingBean);
		}
		return bindingBeans;
	}
	
	/**
	 * 解析每一binding
	 * @param bindingElement binding元素
	 * @return
	 */
	private static BindingBean parseBinding(Element bindingElement) {
		BindingBean bindingBean=new BindingBean();
		
		String bindingName = bindingElement.attributeValue("name");
		bindingBean.setBindingName(bindingName);
		
		String bindingType = bindingElement.attributeValue("type");
		if(bindingType!=null){
			String[] types = bindingType.split(":");
			bindingBean.setBindingType(types[1]);
		}
		
		//获取binding下的方法
		List<Element> operationElements = ElementUtil.findElements(bindingElement, OPERATION);
		List<OperationBean> operationBeans=new ArrayList<OperationBean>();
		
		for (Element operationElement : operationElements) {
			OperationBean operationBean=new OperationBean();
			
			String operationName = operationElement.attributeValue("name");
			operationBean.setOperationName(operationName);
			
			//获取SOAPAction
			List<Element> elements = ElementUtil.findElements(operationElement, OPERATION);
			if(elements.size()>0){
				Element element = elements.get(0);
				String soapAction=element.attributeValue("soapAction");
				operationBean.setSoapAction(soapAction);
				
				String style=element.attributeValue("style");
				operationBean.setStyle(style);
			}
			operationBeans.add(operationBean);
		}
		
		bindingBean.setOperationBeans(operationBeans);
		return bindingBean;
	}
}
