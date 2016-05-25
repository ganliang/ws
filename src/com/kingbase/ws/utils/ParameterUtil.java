package com.kingbase.ws.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.kingbase.ws.bean.BindingBean;
import com.kingbase.ws.bean.OperationBean;
import com.kingbase.ws.bean.ParameterBean;
import com.kingbase.ws.bean.ParameterTypeBean;
import com.kingbase.ws.bean.ParameterTypeBean.BasicTypeBean;
import com.kingbase.ws.bean.ServiceBean;

public class ParameterUtil {

	private static final String SOAP_BODY_PREFIX="<?xml version=\"1.0\" encoding=\"UTF-8\"?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Header></soapenv:Header><soapenv:Body>";
	
	private static final String SOAP_BODY_SUFFEX="</soapenv:Body></soapenv:Envelope>";
	
	/**
	 * 打印多个services
	 * @param serviceBeans
	 * @return
	 */
	public static String printServices(List<ServiceBean> serviceBeans){
		if(serviceBeans==null||serviceBeans.size()==0){
			return "[]";
		}
		StringBuilder serviceBuilder=new StringBuilder("[");
		Iterator<ServiceBean> iterator = serviceBeans.iterator();
		while(iterator.hasNext()){
			ServiceBean serviceBean = iterator.next();
			
			String printService = printService(serviceBean);
			
			serviceBuilder.append(printService);
		    if(iterator.hasNext()){
		    	serviceBuilder.append(",");
		    }
		}
		serviceBuilder.append("]");
		return serviceBuilder.toString();
	}
	
	/**
	 * 打印单个服务
	 * @param serviceBean
	 * @return
	 */
	public static String printService(ServiceBean serviceBean){
		//选择 binding
		BindingBean bindingBean = selectBindingBean(serviceBean);
		
		List<OperationBean> operations = bindingBean.getOperations();
		String operation=printOperations(operations,serviceBean.getServiceName());
		String servceBean="{\"text\":\""+serviceBean.getServiceName()+"\",\"children\":"+operation+"}";
		return servceBean;
	}
	
	/**
	 * 选择 binding
	 * @param serviceBean
	 * @return
	 */
	private static BindingBean selectBindingBean(ServiceBean serviceBean){
		List<BindingBean> bindingBeans = serviceBean.getBindingBean();
		BindingBean bindingBean=null;
		//当仅有一个binding的时候 
		if(bindingBeans.size()==1){
			bindingBean= bindingBeans.get(0);
		}else{
			for (BindingBean binding : bindingBeans) {
				if(binding.getName().equals(serviceBean.getServiceName()+"Soap")){
					bindingBean=binding;
					break;
				}
			}
			if(bindingBean==null){
				bindingBean= bindingBeans.get(0);
			}
		}
		return bindingBean;
	}
	/**
	 * 获取方法
	 * @param operations
	 * @return
	 */
	public static String printOperations(List<OperationBean> operations,String serviceName) {
		if(operations==null||operations.size()==0){
			return "[]";
		}
		StringBuilder operationBuilder=new StringBuilder("[");
		
		Iterator<OperationBean> iterator = operations.iterator();
		while(iterator.hasNext()){
			OperationBean operationBean = iterator.next();
			operationBuilder.append("{\"text\":\""+operationBean.getName()+"\",\"parentName\":\""+serviceName+"\"}");
			if(iterator.hasNext()){
				operationBuilder.append(",");
			}
		}
		operationBuilder.append("]");
		return operationBuilder.toString();
	}

	/**
	 * 获取 输入参数
	 * @param request 
	 */
	public static Map<String, Object> getInParameter(ServiceBean serviceBean,String methodName) {
		if(serviceBean==null||methodName==null||"".equals(methodName)){
			throw new IllegalArgumentException();
		}
		BindingBean bindingBean = selectBindingBean(serviceBean);
		List<OperationBean> operations = bindingBean.getOperations();
		
		Map<String, Object> parameterMap=new HashMap<String, Object>();
		for (OperationBean operationBean : operations) {
			if(operationBean.getName().equals(methodName)){
				List<ParameterBean> inParameters = operationBean.getInParameters();
				if(inParameters==null){
					continue;
				}
				for (ParameterBean parameterBean : inParameters) {
					String parameterType = parameterBean.getParameterType();
					Object parameter = null;
					//如果参数类型不是基本类型
					if(parameterType!=null&&!"string".equals(parameterType)){
						List<BasicTypeBean> basicTypeBeans = getBasicTypeBean(serviceBean, parameterType);
						Map<String,Object> map=new HashMap<String,Object>();
						for (BasicTypeBean basicTypeBean : basicTypeBeans) {
							map.put(basicTypeBean.getBasicTypeName(), "?");
						}
						parameter=map;
					}else{
						parameter="?";
					}
					parameterMap.put(parameterBean.getParameterName(), parameter);
				}
				break;
			}
		}
	    return parameterMap;
	}
	
	/**
	 * 获取方法的输入参数
	 * @param serviceBean
	 * @param methodName
	 * @return
	 */
	public static String getInParameter2(ServiceBean serviceBean,String methodName){
		if(serviceBean==null||methodName==null||"".equals(methodName)){
			throw new IllegalArgumentException();
		}
		BindingBean bindingBean = selectBindingBean(serviceBean);
		List<OperationBean> operations = bindingBean.getOperations();
		
		StringBuilder parameterBuilder=new StringBuilder();
		
		OperationBean operation = getOperation(operations, methodName);
		if(operation==null){
			throw new IllegalArgumentException("方法【"+methodName+"】不存在");
		}
		

		String xmlns="xmlns";
		if("xsd".equalsIgnoreCase(serviceBean.getWsdlType())){
			methodName="xsd:"+methodName;
			xmlns="xmlns:xsd";
		}
		
		parameterBuilder.append("<"+methodName+" "+xmlns+"=\""+serviceBean.getTargetNamespace()+"\">");
		//遍历 输入参数
		List<ParameterBean> inParameters = operation.getInParameters();
		if(inParameters!=null){
			for (ParameterBean parameterBean : inParameters) {
				String parameterType = parameterBean.getParameterType();
				String parameterName = parameterBean.getParameterName();
				
				recursionParameter(serviceBean,parameterName,parameterType,parameterBuilder);
			}
		}
		
		parameterBuilder.append("</"+methodName+">");
		return parameterBuilder.toString();
	}
	
	
	private static void recursionParameter(ServiceBean serviceBean, String parameterName,
			String parameterType, StringBuilder parameterBuilder) {
		//基本类型
		if(parameterType==null||"".equals(parameterType)||"string".equals(parameterType)
				||"decimal".equals(parameterType)||"int".equals(parameterType)||"boolean".equals(parameterType)){
			parameterBuilder.append("<"+parameterName+">?</"+parameterName+">");
		}else{
			List<BasicTypeBean> basicTypeBeans = getBasicTypeBean(serviceBean, parameterType);
			if(basicTypeBeans==null){
				throw new IllegalArgumentException("解析出错,不存在类型【"+parameterType+"】");
			}
			parameterBuilder.append("<"+parameterName+">");
			//遍历类型
			for (BasicTypeBean basicTypeBean : basicTypeBeans) {
				if(basicTypeBean.getBasicType().equals(parameterType)){
					parameterBuilder.append("<"+parameterName+">?</"+parameterName+">");
					return;
				}
				recursionParameter(serviceBean, basicTypeBean.getBasicTypeName(), basicTypeBean.getBasicType(), parameterBuilder);
			}
			parameterBuilder.append("</"+parameterName+">");
		}
	}

	/**
	 * 找到调用的方法
	 * @param operations
	 * @param methodName
	 * @return
	 */
	public static OperationBean getOperation(List<OperationBean> operations,String methodName){
		for (OperationBean operationBean : operations) {
			if(operationBean.getName().equals(methodName)){
				return operationBean;
			}
		}
		return null;
	}
	
	/**
	 * 获取所有的基本类型
	 * @param serviceBean
	 * @param parameterType
	 * @return
	 */
	public static List<BasicTypeBean> getBasicTypeBean(ServiceBean serviceBean,String parameterType){
		//获取参数实体
		List<ParameterTypeBean> parameterTypes = serviceBean.getParameterTypes();
		for (ParameterTypeBean typeBean : parameterTypes) {
			//找到参数类型
			if(parameterType.equals(typeBean.getTypeName())){
				return typeBean.getBasicTypeBeans();
			}
		}
		return null;
	}
	
	/**
	 * 获取 输入参数
	 * @param request 
	 * @throws DocumentException 
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getInParameter(String parameterXML) throws DocumentException {
		if(parameterXML==null||"".equals(parameterXML)){
			throw new IllegalArgumentException();
		}
		
		Document document = DocumentHelper.parseText(parameterXML);
		Element rootElement = document.getRootElement();
		List<Element> elements = rootElement.elements();
		
		Map<String,Object> parameterMap=new HashMap<String,Object>();
		for (Element element : elements) {
			parameterMap.put(element.getName(), element.getTextTrim());
		}
		return parameterMap;
	}
}
