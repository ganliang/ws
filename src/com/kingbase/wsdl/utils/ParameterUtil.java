package com.kingbase.wsdl.utils;

import java.util.Iterator;
import java.util.List;

import com.kingbase.wsdl.bean.OperationBean;
import com.kingbase.wsdl.bean.ParameterBean;
import com.kingbase.wsdl.bean.ParameterTypeBean;
import com.kingbase.wsdl.bean.ParameterTypeBean.BasicTypeBean;
import com.kingbase.wsdl.bean.ServiceBean;

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
		List<OperationBean> operations = serviceBean.getOperationBeans();
		
		if(operations==null||operations.size()==0){
			return "[]";
		}
		StringBuilder operationBuilder=new StringBuilder("[");
		
		Iterator<OperationBean> iterator = operations.iterator();
		while(iterator.hasNext()){
			OperationBean operationBean = iterator.next();
			operationBuilder.append("{\"text\":\""+operationBean.getOperationName()+"\",\"parentName\":\""+serviceBean.getServiceName()+"\"}");
			if(iterator.hasNext()){
				operationBuilder.append(",");
			}
		}
		operationBuilder.append("]");
		
		String operationJSON="{\"text\":\""+serviceBean.getServiceName()+"\",\"children\":"+operationBuilder.toString()+"}";
		return operationJSON;
	}
	
	
	/**
	 * 获取方法的输入参数
	 * @param serviceBean
	 * @param methodName
	 * @return
	 */
	public static String getInParameter(ServiceBean serviceBean,String methodName){
		if(serviceBean==null||methodName==null||"".equals(methodName)){
			throw new IllegalArgumentException();
		}
		
		StringBuilder parameterBuilder=new StringBuilder();
		
		OperationBean operation = getOperation(serviceBean, methodName);
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
	
	/**
	 * 递归 获取参数
	 * @param serviceBean
	 * @param parameterName
	 * @param parameterType
	 * @param parameterBuilder
	 */
	private static void recursionParameter(ServiceBean serviceBean, String parameterName,
			String parameterType, StringBuilder parameterBuilder) {
		//基本类型
		if(ParameterTypeBean.basicTypes.contains(parameterType)){
			parameterBuilder.append("<"+parameterName+">?</"+parameterName+">");
		}
		//用户自定义的对象 类型
		else{
			List<BasicTypeBean> basicTypeBeans = getBasicTypeBean(serviceBean, parameterType);
			if(basicTypeBeans==null){
				throw new IllegalArgumentException("解析出错,不存在类型【"+parameterType+"】");
			}
			parameterBuilder.append("<"+parameterName+">");
			//遍历类型
			for (BasicTypeBean basicTypeBean : basicTypeBeans) {
				if(basicTypeBean.getBasicType().equals(parameterType)){
					parameterBuilder.append("<"+basicTypeBean.getBasicTypeName()+">?</"+basicTypeBean.getBasicTypeName()+">");
					continue;
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
	public static OperationBean getOperation(ServiceBean serviceBean,String methodName){
		//获取方法
		List<OperationBean> operationBeans = serviceBean.getOperationBeans();
		
		for (OperationBean operationBean : operationBeans) {
			if(operationBean.getOperationName().equals(methodName)){
				return operationBean;
			}
		}
		return null;
	}
	
	/**
	 * 自定义类型 下的类型
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
	
}
