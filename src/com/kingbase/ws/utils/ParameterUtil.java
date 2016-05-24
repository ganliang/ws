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
import com.kingbase.ws.bean.ServiceBean;

public class ParameterUtil {

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
					parameterMap.put(parameterBean.getParameterName(), "?");
				}
				break;
			}
		}
	    return parameterMap;
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
