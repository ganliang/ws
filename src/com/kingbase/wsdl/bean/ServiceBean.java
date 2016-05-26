package com.kingbase.wsdl.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务实体
 * @author ganliang
 */
public class ServiceBean {

	public static final String SERVICE="service";//服务节点
	
	private String documentation;//wsdl简介
	
	private String hostURL;//
	
	private String targetNamespace;//目标命名空间
	
	private String endpointURI;//发布的wsdl地址
	
	private String serviceName;//服务名称
	
	private String wsdlType;//soap xsd
	
	//binding
	private List<BindingBean> bindingBeans=new ArrayList<BindingBean>();
	
	//port
	private List<PortTypeBean> PortTypeBeans=new ArrayList<PortTypeBean>();
	
	//方法参数
	private List<OperationBean> operationBeans=new ArrayList<OperationBean>();
	
	//服务包含的参数对象
	private List<ParameterTypeBean> parameterTypes=new ArrayList<ParameterTypeBean>();

	//wsdl包含的import文件
	private List<String> importWSDL=new ArrayList<String>();
	
	public String getDocumentation() {
		return documentation;
	}

	public void setDocumentation(String documentation) {
		this.documentation = documentation;
	}

	public String getHostURL() {
		return hostURL;
	}

	public void setHostURL(String hostURL) {
		this.hostURL = hostURL;
	}

	public String getTargetNamespace() {
		return targetNamespace;
	}

	public void setTargetNamespace(String targetNamespace) {
		this.targetNamespace = targetNamespace;
	}

	public String getEndpointURI() {
		return endpointURI;
	}

	public void setEndpointURI(String endpointURI) {
		this.endpointURI = endpointURI;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getWsdlType() {
		return wsdlType;
	}

	public void setWsdlType(String wsdlType) {
		this.wsdlType = wsdlType;
	}

	public List<BindingBean> getBindingBeans() {
		return bindingBeans;
	}

	public void setBindingBeans(List<BindingBean> bindingBeans) {
		this.bindingBeans = bindingBeans;
	}

	public List<PortTypeBean> getPortTypeBeans() {
		return PortTypeBeans;
	}

	public void setPortTypeBeans(List<PortTypeBean> portTypeBeans) {
		PortTypeBeans = portTypeBeans;
	}

	public List<OperationBean> getOperationBeans() {
		return operationBeans;
	}

	public void setOperationBeans(List<OperationBean> operationBeans) {
		this.operationBeans = operationBeans;
	}

	public List<ParameterTypeBean> getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(List<ParameterTypeBean> parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public List<String> getImportWSDL() {
		return importWSDL;
	}

	public void setImportWSDL(List<String> importWSDL) {
		this.importWSDL = importWSDL;
	}

	@Override
	public String toString() {
		return "ServiceBean [documentation=" + documentation + ", hostURL=" + hostURL + ", targetNamespace="
				+ targetNamespace + ", endpointURI=" + endpointURI + ", serviceName=" + serviceName + ", wsdlType="
				+ wsdlType + ", bindingBeans=" + bindingBeans + ", PortTypeBeans=" + PortTypeBeans + ", operationBeans="
				+ operationBeans + ", parameterTypes=" + parameterTypes + ", importWSDL=" + importWSDL + "]";
	}
}
